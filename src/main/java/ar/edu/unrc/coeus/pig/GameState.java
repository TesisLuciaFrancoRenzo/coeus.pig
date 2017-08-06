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

import static ar.edu.unrc.coeus.pig.Game.FIRST_DICES_TO_ROLL_INDEX;
import static ar.edu.unrc.coeus.pig.Game.MAX_DICES_TO_ROLL;

/**
 *
 */
public final
class GameState
        implements IStatePerceptron {

    /**
     * Usad para saber cuantos dados hay que tirar. También dados Es usado como recompensa parcial.
     */
    private int     dicesToRoll;
    private boolean isPlayer1Turn;
    private int     player1Score;
    private int     player1TotalReward;
    private int     player2Score;
    private int     player2TotalReward;

    public
    GameState() {
        reset();
    }

    public
    GameState(
            final int dicesToRoll,
            final boolean isPlayer1Turn,
            final int player1Score,
            final int player2Score,
            final int player1TotalReward,
            final int player2TotalReward
    ) {
        this.dicesToRoll = dicesToRoll;
        this.isPlayer1Turn = isPlayer1Turn;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.player1TotalReward = player1TotalReward;
        this.player2TotalReward = player2TotalReward;
    }

    public
    void addPlayer1Score( final int score ) {
        player1Score += score;
    }

    public
    void addPlayer1TotalReward( final int dicesToRoll ) {
        player1TotalReward += dicesToRoll;
    }

    public
    void addPlayer2Score( final int score ) {
        player2Score += score;
    }

    public
    void addPlayer2TotalReward( final int dicesToRoll ) {
        player2TotalReward += dicesToRoll;
    }

    @Override
    public
    IState getCopy() {
        return new GameState(dicesToRoll, isPlayer1Turn, player1Score, player2Score, player1TotalReward, player2TotalReward);
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
    int getPlayer1TotalReward() {
        return player1TotalReward;
    }

    public
    int getPlayer2Score() {
        return player2Score;
    }

    public
    int getPlayer2TotalReward() {
        return player2TotalReward;
    }

    @Override
    public
    double getStateReward( final int outputNeuron ) {
        return dicesToRoll;
    }

    /**
     * @return 0 = Sin ganador, 1 = jugador 1, 2 = jugador 2.
     */
    public
    int getWinner() {
        if ( isTerminalState() ) {
            return ( player1Score >= 100 ) ? 1 : 2;
        } else {
            return 0;
        }
    }

    public
    boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    @Override
    public
    boolean isTerminalState() {
        return ( player1Score >= 100 ) || ( player2Score >= 100 );
    }

    public
    void reset() {
        isPlayer1Turn = true;
        dicesToRoll = 0;
        player1Score = 0;
        player2Score = 0;
        player1TotalReward = 0;
        player2TotalReward = 0;
    }

    public
    void swapPlayers() {
        isPlayer1Turn = !isPlayer1Turn;
        dicesToRoll = 0;
    }

    @Override
    public
    String toString() {
        return "GameState{" + "dicesToRoll=" + dicesToRoll + ", isPlayer1Turn=" + isPlayer1Turn + ", player1Score=" + player1Score +
               ", player1TotalReward=" + player1TotalReward + ", player2Score=" + player2Score + ", player2TotalReward=" + player2TotalReward + '}';
    }

    @Override
    public
    Double translateToPerceptronInput( final int neuronIndex ) {
        if ( neuronIndex <= ( ( FIRST_DICES_TO_ROLL_INDEX + MAX_DICES_TO_ROLL ) - 1 ) ) {
            assert dicesToRoll > 0;
            return ( ( neuronIndex - FIRST_DICES_TO_ROLL_INDEX ) == ( dicesToRoll - 1 ) ) ? 1.0d : 0.0d;
        }
        throw new IllegalStateException("unrecognized neuron number " + neuronIndex);
    }
}
