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

import ar.edu.unrc.coeus.tdlearning.interfaces.IState;
import ar.edu.unrc.coeus.tdlearning.interfaces.IStatePerceptron;

/**
 *
 */
public
class State
        implements IStatePerceptron {

    private int     diceToRoll;
    private boolean isPlayer1;
    private int     player1Score;
    private int     player2Score;

    @Override
    public
    IState getCopy() {
        return null;
    }

    @Override
    public
    double getStateReward( int outputNeuron ) {
        return 0;
    }

    @Override
    public
    boolean isTerminalState() {
        return false;
    }

    @Override
    public
    Double translateToPerceptronInput( int neuronIndex ) {
        return null;
    }
}
