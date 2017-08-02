/*
 * Copyright (C) 2017  Lucia Bressan <lucyluz333@gmial.com>,
 *                     Franco Pellegrini <francogpellegrini@gmail.com>,
 *                     Renzo Bianchini <renzobianchini85@gmail.com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ar.edu.unrc.coeus.pig;

import ar.edu.unrc.coeus.tdlearning.interfaces.IAction;
import ar.edu.unrc.coeus.tdlearning.interfaces.IProblemToTrain;
import ar.edu.unrc.coeus.tdlearning.interfaces.IState;
import ar.edu.unrc.coeus.tdlearning.interfaces.IStatePerceptron;
import ar.edu.unrc.coeus.tdlearning.learning.TDLambdaLearning;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;


/**
 *
 **/
public
class Game
        implements IProblemToTrain {
    private static final List< IAction > LIST_OF_ALL_POSSIBLE_ACTIONS = Arrays.asList(RollDicesAction.ROLL1DICE,
            RollDicesAction.ROLL2DICES,
            RollDicesAction.ROLL3DICES,
            RollDicesAction.ROLL4DICES,
            RollDicesAction.ROLL5DICES,
            RollDicesAction.ROLL6DICES,
            RollDicesAction.ROLL7DICES,
            RollDicesAction.ROLL8DICES,
            RollDicesAction.ROLL9DICES,
            RollDicesAction.ROLL10DICES);
    private static final double          MAX_REWARD                   = 1000;
    private final EncogInterface                 encogInterface;
    private final Function< GameState, Integer > player1Brain;
    private final Function< GameState, Integer > player2Brain;
    private final Random                         random;
    private       GameState                      currentGameState;

    public
    Game(
            final PlayerType player1Type,
            final PlayerType player2Type,
            final EncogInterface encogInterface
    ) {
        random = new Random();
        this.encogInterface = encogInterface;
        currentGameState = new GameState();
        player1Brain = setPlayerType(player1Type);
        player2Brain = setPlayerType(player2Type);
    }

    public static
    void main( final String[] args ) {
        if ( args[0].contains("Humans") ) {
            final Game pig = new Game(PlayerType.HUMAN, PlayerType.HUMAN, null);
            pig.play(true);
        } else if ( args[0].contains("HumanVsRandom") ) {
            final int  humanPlayer = Integer.parseInt(args[1]);
            final Game pig;
            switch ( humanPlayer ) {
                case 1:
                    pig = new Game(PlayerType.HUMAN, PlayerType.RANDOM, null);
                    break;
                case 2:
                    pig = new Game(PlayerType.RANDOM, PlayerType.HUMAN, null);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown human player position. Usage: ./pig HumanVsRandom (1|2)");
            }
            pig.play(true);
        } else if ( args[0].contains("TrainRandom") ) {
            final Game pig1 = new Game(PlayerType.PERCEPTRON, PlayerType.RANDOM, null);
            pig1.train();
            final Game pig2 = new Game(PlayerType.RANDOM, PlayerType.PERCEPTRON, null);
            pig2.train();
            //TODO continuar!
        }
    }

    /**
     * Tira los dados de 1 a {@code dicesToRoll} veces.
     *
     * @param dicesToRoll cantidad de dados a tirar.
     * @param random      a utilizar para la generación de números al azar.
     * @param show        true si debe mostrar por terminal los resultados.
     *
     * @return resultado de sumar todas las tiradas de dados, a excepción que se saque algún 1, lo cual retorna un puntaje de 0.
     */
    private static
    int rollDices(
            final int dicesToRoll,
            final Random random,
            final boolean show
    ) {
        if ( show ) {
            System.out.printf("Tiradas = ");
        }
        int total = 0;
        for ( int i = 0; i < dicesToRoll; i++ ) {
            final int diceValue = TDLambdaLearning.randomBetween(1, 10, random);
            if ( show ) {
                System.out.print(diceValue + ", ");
            }
            total += diceValue;
            if ( diceValue == 1 ) {
                if ( show ) {
                    System.out.println();
                }
                return 0;
            }
        }
        if ( show ) {
            System.out.println();
            System.out.println("Resultados de la tirada: " + total);
        }
        return total;
    }

    /**
     * @return entero del 1 al 10 introducido por teclado.
     */
    @SuppressWarnings( "IOResourceOpenedButNotSafelyClosed" )
    private static
    int userInput() {
        int                  value = 0;
        final BufferedReader br    = new BufferedReader(new InputStreamReader(System.in));
        while ( ( value < 1 ) || ( value > 10 ) ) {
            try {
                final String s = br.readLine();
                value = Integer.parseInt(s);
            } catch ( final NumberFormatException ignored ) {
                value = 0;
            } catch ( final IOException e ) {
                e.printStackTrace();
                value = 0;
            }
            if ( ( value < 1 ) || ( value > 10 ) ) {
                System.out.println("Error: Debe introducir un numero del 1 al 10: ");
            }
        }
        return value;
    }

    @Override
    public
    boolean canExploreThisTurn( final long currentTurn ) {
        return false;
    }

    @Override
    public
    IState computeAfterState(
            final IState turnInitialState,
            final IAction action
    ) {
        //copiamos el estado inicial para calcular el after state.
        final GameState newGameState = (GameState) turnInitialState.getCopy();
        //nuestro partial score es la cantidad de dados arrojados en el turno.
        newGameState.setDicesToRoll(( (RollDicesAction) action ).getNumVal());
        return newGameState;
    }

    @Override
    public
    IState computeNextTurnStateFromAfterState( final IState afterState ) {
        final GameState finalGameState = (GameState) afterState.getCopy();
        //Computamos todas las acciones estocásticas (incluyendo las del enemigo)
        if ( finalGameState.isPlayer1() ) {
            //acciones estocásticas del jugador 1
            finalGameState.addPlayer1Score(rollDices(finalGameState.getDicesToRoll(), random, false));
            if ( !finalGameState.isTerminalState() ) {
                //acciones del jugador 2, consideradas como acciones estocásticas del jugador 1
                finalGameState.swapPlayers();
                finalGameState.addPlayer2Score(rollDices(player1Brain.apply(finalGameState), random, false));
                finalGameState.swapPlayers();
            }
        } else {
            //acciones estocásticas del jugador 2
            finalGameState.addPlayer2Score(rollDices(finalGameState.getDicesToRoll(), random, false));
            if ( !finalGameState.isTerminalState() ) {
                //acciones del jugador 1, consideradas como acciones estocásticas del jugador 2
                finalGameState.swapPlayers();
                finalGameState.addPlayer1Score(rollDices(player1Brain.apply(finalGameState), random, false));
                finalGameState.swapPlayers();
            }
        }
        return finalGameState;
    }

    @Override
    public
    Double computeNumericRepresentationFor(
            final Object[] output
    ) {
        return (Double) output[0];
    }

    @Override
    public
    double deNormalizeValueFromPerceptronOutput( final Object value ) {
        return encogInterface.deNormalizeOutput((double) value);
    }

    @Override
    public
    Object[] evaluateStateWithPerceptron( final IState state ) {
        //creamos las entradas de la red neuronal
        final double[] inputs     = new double[encogInterface.getNeuronQuantityInLayer()[0]];
        IntStream      inputLayer = IntStream.range(0, encogInterface.getNeuronQuantityInLayer()[0]);
        inputLayer = encogInterface.isConcurrentInputEnabled() ? inputLayer.parallel() : inputLayer.sequential();
        inputLayer.forEach(index -> inputs[index] = ( (IStatePerceptron) state ).translateToPerceptronInput(index));

        //cargamos la entrada a la red
        final MLData   inputData  = new BasicMLData(inputs);
        final MLData   output     = encogInterface.getNeuralNetwork().compute(inputData);
        final Double[] out        = new Double[output.getData().length];
        final int      outputSize = output.size();
        for ( int i = 0; i < outputSize; i++ ) {
            out[i] = output.getData()[i];
        }
        return out;
    }

    @Override
    public
    IState initialize() {
        currentGameState.reset();
        return currentGameState;
    }

    @Override
    public
    List< IAction > listAllPossibleActions( final IState turnInitialState ) {
        return LIST_OF_ALL_POSSIBLE_ACTIONS;
    }

    @Override
    public
    double normalizeValueToPerceptronOutput( final Object value ) {
        if ( (Double) value > MAX_REWARD ) {
            throw new IllegalArgumentException("value no puede ser mayor a MAX_REWARD=" + MAX_REWARD);
        }
        return encogInterface.normalizeOutput((Double) value);
    }

    private
    void play( final boolean show ) {
        System.out.println("Hola Jugamos al Game!!!");
        final Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        while ( !currentGameState.isTerminalState() ) {
            if ( show ) {
                if ( currentGameState.isPlayer1() ) {
                    System.out.println("======== Turno del jugador 1 ========");
                } else {
                    System.out.println("======== Turno del jugador 2 ========");
                }
                System.out.println("¿Cuántos dados desea tirar (1 a 10)?: ");
            }
            if ( currentGameState.isPlayer1() ) {
                // entrada del jugador 1
                currentGameState.setDicesToRoll(player1Brain.apply(currentGameState));
                // tiramos dados y calculamos puntaje
                currentGameState.addPlayer1Score(rollDices(currentGameState.getDicesToRoll(), random, show));
                if ( show ) {
                    System.out.println("* Puntaje Total = " + currentGameState.getPlayer1Score() + " *");
                }
            } else {
                // entrada del jugador 2
                currentGameState.setDicesToRoll(player2Brain.apply(currentGameState));
                // tiramos dados y calculamos puntaje
                currentGameState.addPlayer2Score(rollDices(currentGameState.getDicesToRoll(), random, show));
                if ( show ) {
                    System.out.println("* Puntaje Total = " + currentGameState.getPlayer2Score() + " *");
                }
            }

            // cambiamos de jugador si no se gana el juego
            if ( !currentGameState.isTerminalState() ) {
                currentGameState.swapPlayers();
            }
        }
        if ( show ) {
            System.out.println("==========================================");
            if ( currentGameState.isPlayer1() ) {
                System.out.println("Gana el jugador 1 con " + currentGameState.getPlayer1Score() + " puntos");
            } else {
                System.out.println("Gana el jugador 2 con " + currentGameState.getPlayer2Score() + " puntos");
            }
        }
    }

    @Override
    public
    void setCurrentState( final IState nextTurnState ) {
        currentGameState = (GameState) nextTurnState;
    }

    private
    Function< GameState, Integer > setPlayerType( final PlayerType playerType ) {
        switch ( playerType ) {
            case RANDOM:
                return ( gameState ) -> TDLambdaLearning.randomBetween(1, 10, random);
            case HUMAN:
                return ( gameState ) -> userInput();
            case GREEDY:
                return ( gameState ) -> 10;
            case PERCEPTRON:
                //return (state) -> 10;
                throw new UnsupportedOperationException("Not implemented yet");
            default:
                throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    private
    void train() {

    }

    public
    enum PlayerType {
        HUMAN,
        RANDOM,
        GREEDY,
        PERCEPTRON
    }

    @Override
    public
    String toString() {
        return "Game{" + "encogInterface=" + encogInterface + ", player1Brain=" + player1Brain + ", player2Brain=" + player2Brain +
               ", currentGameState=" + currentGameState + '}';
    }
}
