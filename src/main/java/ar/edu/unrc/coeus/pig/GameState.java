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
public final
class GameState
        implements IStatePerceptron {

    private int     dicesToRoll;
    private boolean isPlayer1;
    private int     player1Score;
    private int     player2Score;

    public
    GameState() {
        reset();
    }

    public
    GameState(
            final int dicesToRoll,
            final boolean isPlayer1,
            final int player1Score,
            final int player2Score
    ) {
        this.dicesToRoll = dicesToRoll;
        this.isPlayer1 = isPlayer1;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public
    void addPlayer1Score( final int score ) {
        player1Score += score;
    }

    public
    void addPlayer2Score( final int score ) {
        player2Score += score;
    }

    @Override
    public
    IState getCopy() {
        return new GameState(dicesToRoll, isPlayer1, player1Score, player2Score);
    }

    public
    int getDicesToRoll() {
        return dicesToRoll;
    }

    public
    void setDicesToRoll( final int dicesToRoll ) {
        this.dicesToRoll = dicesToRoll;
    }

    public
    int getPlayer1Score() {
        return player1Score;
    }

    public
    int getPlayer2Score() {
        return player2Score;
    }

    @Override
    public
    double getStateReward( final int outputNeuron ) {
        return dicesToRoll;
    }

    public
    boolean isPlayer1() {
        return isPlayer1;
    }

    @Override
    public
    boolean isTerminalState() {
        return ( player1Score >= 100 ) || ( player2Score >= 100 );
    }

    public
    void reset() {
        dicesToRoll = 0;
        isPlayer1 = true;
        player1Score = 0;
        player2Score = 0;
    }

    public
    void swapPlayers() {
        isPlayer1 = !isPlayer1;
    }

    @Override
    public
    String toString() {
        return "GameState{" + "dicesToRoll=" + dicesToRoll + ", isPlayer1=" + isPlayer1 + ", player1Score=" + player1Score + ", player2Score=" +
               player2Score + '}';
    }

    @Override
    public
    Double translateToPerceptronInput( final int neuronIndex ) {
        return null;
    }
}
