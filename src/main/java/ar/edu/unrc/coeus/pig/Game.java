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
import ar.edu.unrc.coeus.tdlearning.learning.ELearningStyle;
import ar.edu.unrc.coeus.tdlearning.learning.TDLambdaLearning;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
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
    public static final  String          HUMANS                       = "Humans";
    public static final  String          HUMAN_VS_RANDOM              = "HumanVsRandom";
    public static final  String          HUMAN_VS_TRAINED             = "HumanVsTrained";
    public static final  String          SIMULATE_RANDOM              = "SimulateRandom";
    public static final  String          SIMULATE_TRAINED_VS_RANDOM   = "SimulateTrainedVsRandom";
    public static final  String          TRAIN_VS_RANDOM              = "TrainVsRandom";
    public static final  String          USAGE                        = "Usage: ./pig [(Humans)|(TrainRandom)|(HumanVsRandom (1|2))]";
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
    private final PerceptronConfiguration        perceptronConfiguration;
    private final Function< GameState, Integer > player1Brain;
    private final Function< GameState, Integer > player2Brain;
    private final Random                         random;
    private       GameState                      currentGameState;

    public
    Game(
            @NotNull final PlayerType player1Type,
            @NotNull final PlayerType player2Type,
            final PerceptronConfiguration perceptronConfiguration,
            final boolean isAIPlayer1
    ) {
        random = new Random();
        this.perceptronConfiguration = perceptronConfiguration;
        if ( perceptronConfiguration != null ) {
            this.encogInterface = perceptronConfiguration.getEncogInterface();
        } else {
            this.encogInterface = null;
        }
        currentGameState = new GameState(isAIPlayer1);
        player1Brain = setPlayerType(player1Type);
        player2Brain = setPlayerType(player2Type);
    }

    public static
    void main( final String[] args )
            throws Exception {
        if ( args[0] == null ) {
            throw new IllegalArgumentException(USAGE);
        }
        final Game pig1;
        final Game pig2;
        int        gamesToPlay;
        final int  humanPlayer;
        final PerceptronConfiguration config = new PerceptronConfiguration("PerceptronVsRandom",
                new File("../PigPerceptrons/"),
                new ActivationFunction[] { new ActivationTANH() },
                1,
                -1,
                100, -100,
                true,
                new int[] { 332, 1 },
                false,
                ELearningStyle.AFTER_STATE,
                new double[] { 0.0025, 0.0025 },
                0.3,
                false,
                1.0,
                new boolean[] { false, false },
                true,
                false);
        switch ( args[0] ) {
            case HUMANS:
                pig1 = new Game(PlayerType.HUMAN, PlayerType.HUMAN, null, true);
                pig1.play(true);
                break;
            case HUMAN_VS_RANDOM:
                try {
                    humanPlayer = Integer.parseInt(args[1]);
                    switch ( humanPlayer ) {
                        case 1:
                            pig1 = new Game(PlayerType.HUMAN, PlayerType.RANDOM, null, true);
                            break;
                        case 2:
                            pig1 = new Game(PlayerType.RANDOM, PlayerType.HUMAN, null, true);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown human player position. Usage: ./pig " + HUMAN_VS_RANDOM + " (1|2)");
                    }
                    pig1.play(true);
                } catch ( NumberFormatException e ) {
                    throw new IllegalArgumentException("Unknown human player position. Usage: ./pig " + HUMAN_VS_RANDOM + " (1|2)");
                }
                break;
            case HUMAN_VS_TRAINED:
                try {
                    humanPlayer = Integer.parseInt(args[1]);
                    switch ( humanPlayer ) {
                        case 1:
                            pig1 = new Game(PlayerType.HUMAN, PlayerType.PERCEPTRON, config, false);
                            break;
                        case 2:
                            pig1 = new Game(PlayerType.PERCEPTRON, PlayerType.HUMAN, config, true);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown human player position. Usage: ./pig " + HUMAN_VS_TRAINED + " (1|2)");
                    }
                    config.loadTrainedPerceptron();
                    pig1.play(true);
                } catch ( NumberFormatException e ) {
                    throw new IllegalArgumentException("Unknown human player position. Usage: ./pig " + HUMAN_VS_TRAINED + " (1|2)");
                }
                break;
            case TRAIN_VS_RANDOM:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig1 = new Game(PlayerType.PERCEPTRON, PlayerType.RANDOM, config, true);
                    pig2 = new Game(PlayerType.RANDOM, PlayerType.PERCEPTRON, config, false);
                    config.newPerceptronToTrain();
                    train(config, pig1, pig2, gamesToPlay);
                } catch ( NumberFormatException e ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + TRAIN_VS_RANDOM + " \"number\"");
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                break;
            case SIMULATE_TRAINED_VS_RANDOM:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig1 = new Game(PlayerType.PERCEPTRON, PlayerType.RANDOM, config, true);
                    pig2 = new Game(PlayerType.RANDOM, PlayerType.PERCEPTRON, config, false);
                    config.loadTrainedPerceptron();
                    simulate(pig1, pig2, gamesToPlay);
                } catch ( NumberFormatException e ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_TRAINED_VS_RANDOM + " \"number\"");
                }
                break;
            case SIMULATE_RANDOM:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig1 = new Game(PlayerType.RANDOM, PlayerType.RANDOM, null, true);
                    pig2 = new Game(PlayerType.RANDOM, PlayerType.RANDOM, null, false);
                    simulate(pig1, pig2, gamesToPlay);
                } catch ( NumberFormatException e ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_RANDOM + " \"number\"");
                }
                break;
            default:
                throw new IllegalArgumentException(USAGE);
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

    private static
    void simulate(
            final Game pig1,
            final Game pig2,
            final int gamesToPlay
    ) {
        double winRate = 0;

        for ( int i = 1; i <= gamesToPlay; i++ ) {
            pig1.reset();
            pig1.play(false);
            if ( pig1.currentGameState.getWinner() == 1 ) {
                winRate += 1.0d;
            }
            pig2.reset();
            pig2.play(false);
            if ( pig1.currentGameState.getWinner() == 2 ) {
                winRate += 1.0d;
            }
            if ( i % 100 == 0 ) {
                final int percent = (int) ( ( ( i * 1.0d ) / ( gamesToPlay * 1.0d ) ) * 100.0d );
                System.out.println(percent + "%");
            }
        }
        winRate = ( winRate * 100d ) / ( gamesToPlay * 2d );
        System.out.println("** WinRate = " + winRate);
    }

    private static
    void train(
            final PerceptronConfiguration perceptronConfiguration,
            final Game pig1,
            final Game pig2,
            final int gamesToPlay
    )
            throws IOException {
        TDLambdaLearning learningAlgorithm = new TDLambdaLearning(perceptronConfiguration.getEncogInterface(),
                perceptronConfiguration.getLearningStyle(),
                perceptronConfiguration.getAlpha(),
                perceptronConfiguration.getLambda(),
                perceptronConfiguration.isReplaceEligibilityTraces(),
                perceptronConfiguration.getGamma(),
                perceptronConfiguration.getConcurrencyInLayer(),
                new Random(),
                perceptronConfiguration.isCollectStatistics());
        learningAlgorithm.setFixedLearningRate();
        learningAlgorithm.setFixedExplorationRate(0);
        for ( int i = 1; i <= gamesToPlay; i++ ) {
            pig1.reset();
            learningAlgorithm.solveAndTrainOnce(pig1, i);
            pig2.reset();
            learningAlgorithm.solveAndTrainOnce(pig2, i);
            if ( i % 100 == 0 ) {
                final int percent = (int) ( ( ( i * 1.0d ) / ( gamesToPlay * 1.0d ) ) * 100.0d );
                System.out.println(percent + "%");
            }
        }
        perceptronConfiguration.saveTrainedNeuralNetwork();
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
        if ( finalGameState.isAIPlayer1() ) {
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
        if ( show ) {
            System.out.println("Hola Jugamos al Game!!!");
        }
        final Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        while ( !currentGameState.isTerminalState() ) {
            if ( show ) {
                if ( currentGameState.isAIPlayer1() ) {
                    System.out.println("======== Turno del jugador 1 ========");
                } else {
                    System.out.println("======== Turno del jugador 2 ========");
                }
                System.out.println("¿Cuántos dados desea tirar (1 a 10)?: ");
            }
            if ( currentGameState.isAIPlayer1() ) {
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
            if ( currentGameState.isAIPlayer1() ) {
                System.out.println("Gana el jugador 1 con " + currentGameState.getPlayer1Score() + " puntos");
            } else {
                System.out.println("Gana el jugador 2 con " + currentGameState.getPlayer2Score() + " puntos");
            }
        }
    }

    private
    void reset() {
        currentGameState.reset();
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
                return ( gameState ) -> {
                    // evaluamos cada acción aplicada al estado inicial y elegimos la mejor
                    // acción basada en las predicciones del problema
                    return ( (RollDicesAction) TDLambdaLearning.computeBestPossibleAction(this,
                            perceptronConfiguration.getLearningStyle(),
                            gameState,
                            LIST_OF_ALL_POSSIBLE_ACTIONS,
                            perceptronConfiguration.isComputeParallelBestPossibleAction(),
                            random,
                            null).getAction() ).getNumVal();
                };
            default:
                throw new UnsupportedOperationException("Not implemented yet");
        }
    }

    @Override
    public
    String toString() {
        return "Game{" + "encogInterface=" + encogInterface + ", player1Brain=" + player1Brain + ", player2Brain=" + player2Brain +
               ", currentGameState=" + currentGameState + '}';
    }

    public
    enum PlayerType {
        HUMAN,
        RANDOM,
        GREEDY,
        PERCEPTRON
    }
}