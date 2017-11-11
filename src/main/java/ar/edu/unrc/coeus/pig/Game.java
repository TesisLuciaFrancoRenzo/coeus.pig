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

import ar.edu.unrc.coeus.tdlearning.interfaces.*;
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
import java.util.Date;
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
    public static final  String          CREATE_LAZY_PERCEPTRON         = "CreateLazyPerceptron";
    public static final  int             FIRST_DICES_TO_ROLL_INDEX      = 0;
    public static final  String          HUMANS                         = "Humans";
    public static final  String          HUMAN_VS_RANDOM                = "HumanVsRandom";
    public static final  double          LAZY_PERCEPTRON_INITIAL_WEIGHT = 0.1d;
    public static final  int             MAX_DICES_TO_ROLL              = 10;
    public static final  int             INPUT_NEURONS                  = MAX_DICES_TO_ROLL;
    public static final  int             MAX_SCORE                      = 159;
    public static final  int             MAX_TOTAL_REWARD               = 250;
    public static final  String          SIMULATE_GREEDY                = "SimulateGreedy";
    public static final  String          SIMULATE_GREEDY_VS_INITIAL     = "SimulateGreedyVsInitial";
    public static final  String          SIMULATE_GREEDY_VS_LAZY        = "SimulateGreedyVsLazy";
    public static final  String          SIMULATE_GREEDY_VS_RANDOM      = "SimulateGreedyVsRandom";
    public static final  String          SIMULATE_GREEDY_VS_TRAINED     = "SimulateGreedyVsTrained";
    public static final  String          SIMULATE_LAZY                  = "SimulateLazy";
    public static final  String          SIMULATE_LAZY_VS_INITIAL       = "SimulateLazyVsInitial";
    public static final  String          SIMULATE_LAZY_VS_TRAINED       = "SimulateLazyVsTrained";
    public static final  String          SIMULATE_RANDOM                = "SimulateRandom";
    public static final  String          SIMULATE_RANDOM_VS_GREEDY      = "SimulateRandomVsGreedy";
    public static final  String          SIMULATE_RANDOM_VS_INITIAL     = "SimulateRandomVsInitial";
    public static final  String          SIMULATE_RANDOM_VS_LAZY        = "SimulateRandomVsLazy";
    public static final  String          SIMULATE_RANDOM_VS_TRAINED     = "SimulateRandomVsTrained";
    public static final  String          SIMULATE_TRAINED               = "SimulateTrained";
    public static final  String          TRAINED_VS_HUMAN               = "TrainedVsHuman";
    public static final  String          TRAIN_ALONE                    = "TrainAlone";
    public static final  String          TRAIN_VS_GREEDY                = "TrainVsGreedy";
    public static final  String          TRAIN_VS_RANDOM                = "TrainVsRandom";
    public static final  String          USAGE                          =
            "Usage: ./pig [(Humans)|(HumanVsRandom (1|2))|(TrainedVsHuman \"number\")|(TrainRandom \"number\")|(TrainVsGreedy \"number\")|" +
            "(SimulateGreedy \"number\")|(SimulateGreedyVsInitial \"number\")|(SimulateGreedyVsLazy \"number\")|(SimulateGreedyVsRandom \"number\")" +
            "|(SimulateGreedyVsTrained \"number\")|(SimulateLazy \"number\")|(SimulateLazyVsInitial \"number\")|(SimulateRandom \"number\")|" +
            "(SimulateRandomVsGreedy \"number\")|(SimulateRandomVsInitial \"number\")|(SimulateRandomVsLazy \"number\")|(SimulateRandomVsTrained " +
            "\"number\")|(TrainAlone \"number\")|(TrainVsGreedy \"number\")|(TrainVsRandom \"number\")]";
    private static final List< IAction > LIST_OF_ALL_POSSIBLE_ACTIONS   = Arrays.asList(RollDicesAction.ROLL1DICE,
            RollDicesAction.ROLL2DICES,
            RollDicesAction.ROLL3DICES,
            RollDicesAction.ROLL4DICES,
            RollDicesAction.ROLL5DICES,
            RollDicesAction.ROLL6DICES,
            RollDicesAction.ROLL7DICES,
            RollDicesAction.ROLL8DICES,
            RollDicesAction.ROLL9DICES,
            RollDicesAction.ROLL10DICES);
    private final EncogInterface                 encogInterface;
    private final PerceptronConfiguration        perceptronConfiguration;
    private final Function< GameState, Integer > player1Brain;
    private final Function< GameState, Integer > player2Brain;
    private final Random                         random;
    private       GameState                      currentGameState;

    public
    Game(
            final @NotNull PlayerType player1Type,
            final @NotNull PlayerType player2Type,
            final PerceptronConfiguration perceptronConfiguration
    ) {
        random = new Random();
        random.setSeed(System.currentTimeMillis());
        this.perceptronConfiguration = perceptronConfiguration;
        encogInterface = ( perceptronConfiguration != null ) ? perceptronConfiguration.getEncogInterface() : null;
        currentGameState = new GameState();
        player1Brain = setPlayerType(player1Type, this);
        player2Brain = setPlayerType(player2Type, this);
    }

    public static
    void main( final String[] args )
            throws IllegalArgumentException, IOException, ClassNotFoundException {
        if ( args[0] == null ) {
            throw new IllegalArgumentException(USAGE);
        }
        final Game pig;
        final int  gamesToPlay;

        final PerceptronConfiguration config = new PerceptronConfiguration("PigPerceptron",
                new File("../PigPerceptrons/"),
                new ActivationFunction[] { new ActivationTANH() },
                1.0,
                -1.0,
                MAX_TOTAL_REWARD,
                -MAX_TOTAL_REWARD,
                false,
                new int[] { INPUT_NEURONS, 1 },
                false,
                ELearningStyle.AFTER_STATE,
                new double[] { 0.0025, 0.0025 },
                0.3d,
                true,
                1.0,
                new boolean[] { false, false },
                false,
                false);
        switch ( args[0] ) {
            case HUMANS:
                pig = new Game(PlayerType.HUMAN, PlayerType.HUMAN, null);
                pig.play(true);
                break;
            case HUMAN_VS_RANDOM:
                try {
                    final int humanPlayer = Integer.parseInt(args[1]);
                    switch ( humanPlayer ) {
                        case 1:
                            pig = new Game(PlayerType.HUMAN, PlayerType.RANDOM, null);
                            break;
                        case 2:
                            pig = new Game(PlayerType.RANDOM, PlayerType.HUMAN, null);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown human player position. Usage: ./pig " + HUMAN_VS_RANDOM + " (1|2)");
                    }
                    pig.play(true);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown human player position. Usage: ./pig " + HUMAN_VS_RANDOM + " (1|2)");
                }
                break;
            case TRAINED_VS_HUMAN:
                try {
                    pig = new Game(PlayerType.HUMAN, PlayerType.PERCEPTRON, config);
                    config.loadTrainedPerceptron();
                    pig.play(true);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown human player position. Usage: ./pig " + TRAINED_VS_HUMAN + " (1|2)");
                }
                break;
            case TRAIN_VS_RANDOM:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.RANDOM, PlayerType.PERCEPTRON, config);
                    config.initPerceptronToTrain();
                    train(config, pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + TRAIN_VS_RANDOM + " \"number\"");
                } catch ( final IOException e ) {
                    e.printStackTrace();
                }
                break;
            case SIMULATE_RANDOM_VS_TRAINED:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.RANDOM, PlayerType.PERCEPTRON, config);
                    config.loadTrainedPerceptron();
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_RANDOM_VS_TRAINED + " \"number\"");
                }
                break;
            case SIMULATE_TRAINED:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.PERCEPTRON, PlayerType.PERCEPTRON, config);
                    config.loadTrainedPerceptron();
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_TRAINED + " \"number\"");
                }
                break;
            case CREATE_LAZY_PERCEPTRON:
                try {
                    config.initPerceptronToTrain();
                    config.getEncogInterface().setWeight(1, 0, FIRST_DICES_TO_ROLL_INDEX, LAZY_PERCEPTRON_INITIAL_WEIGHT);
                    config.saveLazyNeuralNetwork();
                } catch ( final Exception e ) {
                    e.printStackTrace();
                }
                break;
            case TRAIN_ALONE:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.PERCEPTRON, PlayerType.PERCEPTRON, config);
                    config.initPerceptronToTrain();
                    train(config, pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + TRAIN_ALONE + " \"number\"");
                } catch ( final IOException e ) {
                    e.printStackTrace();
                }
                break;
            case TRAIN_VS_GREEDY:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.GREEDY, PlayerType.PERCEPTRON, config);
                    config.initPerceptronToTrain();
                    train(config, pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + TRAIN_VS_GREEDY + " \"number\"");
                } catch ( final IOException e ) {
                    e.printStackTrace();
                }
                break;
            case SIMULATE_GREEDY_VS_TRAINED:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.GREEDY, PlayerType.PERCEPTRON, config);
                    config.loadTrainedPerceptron();
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_GREEDY_VS_TRAINED + " \"number\"");
                }
                break;
            case SIMULATE_LAZY_VS_TRAINED:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.LAZY, PlayerType.PERCEPTRON, config);
                    config.loadTrainedPerceptron();
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_LAZY_VS_TRAINED + " \"number\"");
                }
                break;
            case SIMULATE_RANDOM:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.RANDOM, PlayerType.RANDOM, null);
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_RANDOM + " \"number\"");
                }
                break;
            case SIMULATE_GREEDY_VS_RANDOM:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.GREEDY, PlayerType.RANDOM, null);
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_GREEDY_VS_RANDOM + " \"number\"");
                }
                break;
            case SIMULATE_RANDOM_VS_GREEDY:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.RANDOM, PlayerType.GREEDY, null);
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_RANDOM_VS_GREEDY + " \"number\"");
                }
                break;
            case SIMULATE_RANDOM_VS_LAZY:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.RANDOM, PlayerType.LAZY, null);
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_RANDOM_VS_LAZY + " \"number\"");
                }
                break;
            case SIMULATE_GREEDY_VS_LAZY:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.GREEDY, PlayerType.LAZY, null);
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_GREEDY_VS_LAZY + " \"number\"");
                }
                break;
            case SIMULATE_LAZY:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.LAZY, PlayerType.LAZY, null);
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_LAZY + " \"number\"");
                }
                break;
            case SIMULATE_RANDOM_VS_INITIAL:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.RANDOM, PlayerType.PERCEPTRON, config);
                    config.loadLazyPerceptron();
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_RANDOM_VS_INITIAL + " \"number\"");
                }
                break;
            case SIMULATE_LAZY_VS_INITIAL:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.LAZY, PlayerType.PERCEPTRON, config);
                    config.loadLazyPerceptron();
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_LAZY_VS_INITIAL + " \"number\"");
                }
                break;
            case SIMULATE_GREEDY_VS_INITIAL:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.GREEDY, PlayerType.PERCEPTRON, config);
                    config.loadLazyPerceptron();
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_GREEDY_VS_INITIAL + " \"number\"");
                }
                break;
            case SIMULATE_GREEDY:
                try {
                    gamesToPlay = Integer.parseInt(args[1]);
                    pig = new Game(PlayerType.GREEDY, PlayerType.GREEDY, null);
                    simulate(pig, gamesToPlay);
                } catch ( final NumberFormatException ignored ) {
                    throw new IllegalArgumentException("Unknown games to play. Usage: ./pig " + SIMULATE_GREEDY + " \"number\"");
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
        if ( dicesToRoll <= 0 ) {
            throw new IllegalArgumentException("dicesToRoll tiene que ser un valor mayor o igual a 1");
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
     * Entrena un perceptrón inicializado en Pig, gamesToPlay cantidad de veces.
     *
     * @param pig         juego con un perceptrón inicializado.
     * @param gamesToPlay cantidad de veces que se juega.
     */
    private static
    void simulate(
            final Game pig,
            final int gamesToPlay
    ) {
        int wins           = 0;
        int maxFinalReward = 0;
        for ( int i = 1; i <= gamesToPlay; i++ ) {
            pig.reset();
            pig.play(false);
            if ( pig.currentGameState.getWinner() == 1 ) {
                wins++;
            }
            if ( pig.currentGameState.getPlayer1TotalReward() > maxFinalReward ) {
                maxFinalReward = pig.currentGameState.getPlayer1TotalReward();
            }
            if ( pig.currentGameState.getPlayer2TotalReward() > maxFinalReward ) {
                maxFinalReward = pig.currentGameState.getPlayer2TotalReward();
            }
            if ( ( i % ( gamesToPlay / 100 ) ) == 0 ) {
                final int percent = (int) ( ( ( i * 1.0d ) / ( gamesToPlay * 1.0d ) ) * 100.0d );
                System.out.println(new Date() + " - " + percent + " %");
            }
        }
        final double winRate = ( wins * 100.0d ) / ( gamesToPlay );
        System.out.println(new Date() + " => WinRate = " + winRate + " (" + wins + '/' + ( gamesToPlay ) + ") - maxFinalReward=" + maxFinalReward);
    }

    /**
     * Ejemplo de como invocar Coeus para que entrene un perceptron.
     *
     * @param perceptronConfiguration
     * @param pig                     juego inicializado
     * @param gamesToPlay             cantidad de veces que se juega
     *
     * @throws IOException
     */
    private static
    void train(
            final PerceptronConfiguration perceptronConfiguration,
            final Game pig,
            final int gamesToPlay
    )
            throws IOException {
        final TDLambdaLearning learningAlgorithm = new TDLambdaLearning(perceptronConfiguration.getEncogInterface(),
                perceptronConfiguration.getLearningStyle(),
                perceptronConfiguration.getAlpha(),
                perceptronConfiguration.getLambda(),
                perceptronConfiguration.isReplaceEligibilityTraces(),
                perceptronConfiguration.getGamma(),
                perceptronConfiguration.getConcurrencyInLayer(),
                new Random(),
                perceptronConfiguration.isCollectStatistics());
        learningAlgorithm.setAnnealingLearningRate(10_000);
        learningAlgorithm.setLinearExplorationRate(0.1, 5_000, 0, 10_000);
        long time = System.currentTimeMillis();
        for ( int i = 1; i <= gamesToPlay; i++ ) {
            learningAlgorithm.solveAndTrainOnce(pig, i);
            if ( gamesToPlay > 100 && ( i % ( gamesToPlay / 100 ) ) == 0 ) {
                final int percent = (int) ( ( ( i * 1.0d ) / ( gamesToPlay * 1.0d ) ) * 100.0d );
                System.out.println(new Date() + " - " + percent + " %");
            }
        }
        time = System.currentTimeMillis() - time;
        perceptronConfiguration.saveTrainedNeuralNetwork();
        System.out.println(new Date() + " => Training Finished. Time: " + time + "ms.");
    }

    /**
     * @return entero del 1 al 10 introducido por teclado.
     */
    @SuppressWarnings( { "IOResourceOpenedButNotSafelyClosed", "resource" } )
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
        return true;
    }

    @Override
    public
    IState computeAfterState(
            final IState turnInitialState,
            final IAction action
    ) {
        //copiamos el estado inicial para calcular el after state.
        final GameState newGameState = (GameState) turnInitialState.getCopy();
        assert !newGameState.isPlayer1Turn();

        //nuestro partial score es la cantidad de dados arrojados en el turno.
        newGameState.setDicesToRoll(( (RollDicesAction) action ).getNumVal());
        newGameState.addPlayer2TotalReward(newGameState.getDicesToRoll());
        return newGameState;
    }

    @Override
    public
    IState computeNextTurnStateFromAfterState( final IState afterState ) {
        final GameState finalGameState = (GameState) afterState.getCopy();
        //Computamos todas las acciones estocásticas (incluyendo las del enemigo)
        assert !finalGameState.isPlayer1Turn();

        //acciones estocásticas del jugador 2
        finalGameState.addPlayer2Score(rollDices(finalGameState.getDicesToRoll(), random, false));
        if ( !finalGameState.isTerminalState() ) {
            //acciones del jugador 1, consideradas como acciones estocásticas del jugador 2
            finalGameState.swapPlayers();
            finalGameState.setDicesToRoll(player1Brain.apply(finalGameState));
            finalGameState.addPlayer1TotalReward(finalGameState.getDicesToRoll());
            finalGameState.addPlayer1Score(rollDices(finalGameState.getDicesToRoll(), random, false));
            if ( !finalGameState.isTerminalState() ) {
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
        // Al jugar siempre como jugador 2, el turno inicial del juego para entrenar debe
        // ajustarse simulando el primer jugador
        currentGameState.reset();
        assert currentGameState.isPlayer1Turn();
        currentGameState.setDicesToRoll(player1Brain.apply(currentGameState));
        currentGameState.addPlayer1TotalReward(currentGameState.getDicesToRoll());
        currentGameState.addPlayer1Score(rollDices(currentGameState.getDicesToRoll(), random, false));
        currentGameState.swapPlayers();
        return currentGameState;
    }

    @Override
    public
    List< IAction > listAllPossibleActions( final IState turnInitialState ) {
        //Las acciones a elegir siempre son las mismas sin importar el turno y el estado del juego
        return LIST_OF_ALL_POSSIBLE_ACTIONS;
    }

    @Override
    public
    double normalizeValueToPerceptronOutput( final Object value ) {
        if ( (Double) value > (double) MAX_TOTAL_REWARD ) {
            throw new IllegalArgumentException("value no puede ser mayor a MAX_TOTAL_REWARD=" + MAX_TOTAL_REWARD);
        }
        return encogInterface.normalizeOutput((Double) value);
    }

    private
    void play( final boolean show ) {
        if ( show ) {
            System.out.println("Hola, Juguemos al Easy PIG!!!");
        }
        final Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        while ( !currentGameState.isTerminalState() ) {
            if ( show ) {
                if ( currentGameState.isPlayer1Turn() ) {
                    System.out.println("======== Turno del jugador 1 ========");
                } else {
                    System.out.println("======== Turno del jugador 2 ========");
                }
                System.out.println("¿Cuántos dados desea tirar (1 a 10)?: ");
            }
            if ( currentGameState.isPlayer1Turn() ) {
                // entrada del jugador 1
                currentGameState.setDicesToRoll(player1Brain.apply(currentGameState));
                currentGameState.addPlayer1TotalReward(currentGameState.getDicesToRoll());
                // tiramos dados y calculamos puntaje
                currentGameState.addPlayer1Score(rollDices(currentGameState.getDicesToRoll(), random, show));
                if ( show ) {
                    System.out.println(
                            "* Puntaje Total = " + currentGameState.getPlayer1Score() + " * Tiradas = " + currentGameState.getPlayer1TotalReward());
                }
            } else {
                // entrada del jugador 2
                currentGameState.setDicesToRoll(player2Brain.apply(currentGameState));
                currentGameState.addPlayer2TotalReward(currentGameState.getDicesToRoll());
                // tiramos dados y calculamos puntaje
                currentGameState.addPlayer2Score(rollDices(currentGameState.getDicesToRoll(), random, show));
                if ( show ) {
                    System.out.println(
                            "* Puntaje Total = " + currentGameState.getPlayer2Score() + " * Tiradas = " + currentGameState.getPlayer2TotalReward());
                }
            }

            // cambiamos de jugador si no se gana el juego
            if ( !currentGameState.isTerminalState() ) {
                currentGameState.swapPlayers();
            }
        }
        if ( show ) {
            System.out.println("==========================================");
            if ( currentGameState.isPlayer1Turn() ) {
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
    Function< GameState, Integer > setPlayerType(
            final PlayerType playerType,
            final IProblemRunner problemRunner
    ) {
        switch ( playerType ) {
            case RANDOM:
                return ( gameState ) -> TDLambdaLearning.randomBetween(1, 10, random);
            case HUMAN:
                return ( gameState ) -> userInput();
            case GREEDY:
                return ( gameState ) -> 10;
            case LAZY:
                return ( gameState ) -> 1;
            case PERCEPTRON:
                return ( gameState ) -> {
                    // evaluamos cada acción aplicada al estado inicial y elegimos la mejor
                    // acción basada en las predicciones del problema
                    return ( (RollDicesAction) TDLambdaLearning.computeBestPossibleAction(problemRunner,
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
        return "Game{" + "encogInterface=" + encogInterface + ", perceptronConfiguration=" + perceptronConfiguration + ", player1Brain=" +
               player1Brain + ", player2Brain=" + player2Brain + ", random=" + random + ", currentGameState=" + currentGameState + '}';
    }

    public
    enum PlayerType {
        HUMAN,
        RANDOM,
        GREEDY,
        LAZY,
        PERCEPTRON
    }
}

