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
import ar.edu.unrc.coeus.tdlearning.interfaces.IActor;
import ar.edu.unrc.coeus.tdlearning.interfaces.IProblemToTrain;
import ar.edu.unrc.coeus.tdlearning.interfaces.IState;

import java.util.List;


/**
 * Entradas a la red: <br> 1- puntaje total del enemigo (Máximo 105). <br> 2- puntaje total del jugador actual (Máximo 105). <br> 3- cantidad de veces
 * tirados el dado en este turno (En el peor caso se lanza 50 veces el dado, con un valor de 2 puntos). <br> 4- Puntaje acumulado en el turno
 * actual (Máximo 105). <br>Objetivo: el que anote 100 o más puntos gana.<br> Detalles:<br> Haga que los jugadores lancen el dado para determinar el
 * orden de juego. El puntaje más bajo va primero.<br> El primer jugador tira el dado y suma los números después de cada tirada. Pueden dejar de tirar
 * en cualquier momento y terminar el turno.<br> El jugador pierde todos los puntos del turno cuando se tira un 1.<br> Si el primer jugador llega a
 * 100 puntos en su primer turno, el otro jugador (s) puede tomar su turno para tratar de lograr una mejor puntuación.<br>
 **/
public
class Pig
        implements IProblemToTrain {

    public static
    void main( String[] args ) {

        System.out.println("Hola Jugamos al Pig!!!");
    }

    @Override
    public
    boolean canExploreThisTurn( long currentTurn ) {
        return false;
    }

    @Override
    public
    IState computeAfterState(
            IState turnInitialState,
            IAction action
    ) {
        return null;
    }

    @Override
    public
    IState computeNextTurnStateFromAfterState( IState afterState ) {
        return null;
    }

    @Override
    public
    Double computeNumericRepresentationFor(
            Object[] output,
            IActor actor
    ) {
        return null;
    }

    @Override
    public
    double deNormalizeValueFromPerceptronOutput( Object value ) {
        return 0;
    }

    @Override
    public
    Object[] evaluateBoardWithPerceptron( IState state ) {
        return new Object[0];
    }

    @Override
    public
    IActor getActorToTrain() {
        return null;
    }

    @Override
    public
    IState initialize( IActor actor ) {
        return null;
    }

    @Override
    public
    List< IAction > listAllPossibleActions( IState turnInitialState ) {
        return null;
    }

    @Override
    public
    double normalizeValueToPerceptronOutput( Object value ) {
        return 0;
    }

    @Override
    public
    void setCurrentState( IState nextTurnState ) {

    }
}
