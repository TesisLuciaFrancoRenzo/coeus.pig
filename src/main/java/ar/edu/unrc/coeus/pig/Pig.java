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
import ar.edu.unrc.coeus.tdlearning.learning.TDLambdaLearning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 *
 **/
public
class Pig
        implements IProblemToTrain {

    public static final List< IAction > listOfAllPossibleActions = Arrays.asList(Action.ROLL1DICE,
            Action.ROLL2DICES,
            Action.ROLL3DICES,
            Action.ROLL4DICES,
            Action.ROLL5DICES,
            Action.ROLL6DICES,
            Action.ROLL7DICES,
            Action.ROLL8DICES,
            Action.ROLL9DICES,
            Action.ROLL10DICES);
    private final State currentState;

    public
    Pig() {
        currentState = new State();
    }

    public static
    void main( final String[] args ) {
        if ( args[0].contains("humans") ) {
            final Pig pig = new Pig();
            pig.playHumanVsHuman();
        }
    }

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
    private static
    int userInput() {
        while ( true ) {
            try ( BufferedReader br = new BufferedReader(new InputStreamReader(System.in)) ) {
                final String s     = br.readLine();
                final int    value = Integer.parseInt(s);
                if ( ( value < 1 ) || ( value > 10 ) ) {throw new Exception("Incorrect input value");}
                return value;
            } catch ( Exception e ) {
                System.out.println("Error: Debe introducir un numero del 1 al 10");
            }
        }
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
        State newState = (State) turnInitialState.getCopy();
        //nuestro partial score es la cantidad de dados arrojados en el turno.
        newState.setDicesToRoll(( (Action) action ).getNumVal());
        return newState;
    }

    @Override
    public
    IState computeNextTurnStateFromAfterState( final IState afterState ) {
        return null;
    }

    @Override
    public
    Double computeNumericRepresentationFor(
            final Object[] output
    ) {
        return null;
    }

    @Override
    public
    double deNormalizeValueFromPerceptronOutput( final Object value ) {
        return (double) 0;
    }

    @Override
    public
    Object[] evaluateStateWithPerceptron( final IState state ) {
        return new Object[0];
    }

    @Override
    public
    IState initialize() {
        return null;
    }

    @Override
    public
    List< IAction > listAllPossibleActions( final IState turnInitialState ) {
        return listOfAllPossibleActions;
    }

    @Override
    public
    double normalizeValueToPerceptronOutput( final Object value ) {
        return (double) 0;
    }

    private
    void playHumanVsHuman() {
        System.out.println("Hola Jugamos al Pig!!! Humano vs Humano");
        final Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        while ( !currentState.isTerminalState() ) {
            if ( currentState.isPlayer1() ) {
                System.out.println("======== Turno del jugador 1 ========");
            } else {
                System.out.println("======== Turno del jugador 2 ========");
            }
            System.out.println("¿Cuántos dados desea tirar (1 a 10)?");
            // entrada del jugador
            currentState.setDicesToRoll(userInput());
            if ( currentState.isPlayer1() ) {
                currentState.addPlayer1Score(rollDices(currentState.getDicesToRoll(), random, true));
                System.out.println("* Puntaje Total = " + currentState.getPlayer1Score() + " *");
            } else {
                currentState.addPlayer2Score(rollDices(currentState.getDicesToRoll(), random, true));
                System.out.println("* Puntaje Total = " + currentState.getPlayer2Score() + " *");
            }

            // cambiamos de jugador si no se gana el juego
            if ( !currentState.isTerminalState() ) {
                currentState.swapPlayers();
            }
        }
        System.out.println("==========================================");
        if ( currentState.isPlayer1() ) {
            System.out.println("Gana el jugador 1 con " + currentState.getPlayer1Score() + " puntos");
        } else {
            System.out.println("Gana el jugador 2 con " + currentState.getPlayer2Score() + " puntos");
        }
    }

    public
    void reset() {
        currentState.reset();
    }

    @Override
    public
    void setCurrentState( final IState nextTurnState ) {

    }
}
