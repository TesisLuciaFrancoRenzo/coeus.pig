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
    /**
     * neuronas 320-321
     */
    private boolean isAIPlayer1;
    /**
     * neuronas 0-159
     */
    private int     player1Score;
    private int     player1TotalReward;
    /**
     * neuronas 160-319
     */
    private int     player2Score;
    private int     player2TotalReward;

    public
    GameState(
            final boolean isAIPlayer1
    ) {
        reset();
        this.isAIPlayer1 = isAIPlayer1;
    }

    public
    GameState(
            final int dicesToRoll,
            final boolean isAIPlayer1,
            final int player1Score,
            final int player2Score,
            final int player1TotalReward,
            final int player2TotalReward
    ) {
        this.dicesToRoll = dicesToRoll;
        this.isAIPlayer1 = isAIPlayer1;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.player1TotalReward = player1TotalReward;
        this.player2TotalReward = player2TotalReward;
    }

    public
    void addPlayer1Score( final int score ) {
        player1Score += score;
        player1TotalReward += dicesToRoll;
    }

    public
    void addPlayer2Score( final int score ) {
        player2Score += score;
        player2TotalReward += dicesToRoll;
    }

    @Override
    public
    IState getCopy() {
        return new GameState(dicesToRoll, isAIPlayer1, player1Score, player2Score, player1TotalReward, player2TotalReward);
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
    boolean isAIPlayer1() {
        return isAIPlayer1;
    }

    @Override
    public
    boolean isTerminalState() {
        return ( player1Score >= 100 ) || ( player2Score >= 100 );
    }

    public
    void reset() {
        dicesToRoll = 0;
        player1Score = 0;
        player2Score = 0;
        player1TotalReward = 0;
        player2TotalReward = 0;
    }

    public
    void swapPlayers() {
        isAIPlayer1 = !isAIPlayer1;
    }

    @Override
    public
    String toString() {
        return "GameState{" + "dicesToRoll=" + dicesToRoll + ", isAIPlayer1=" + isAIPlayer1 + ", player1Score=" + player1Score + ", player2Score=" +
               player2Score + '}';
    }

    @Override
    public
    Double translateToPerceptronInput( final int neuronIndex ) {
        if ( neuronIndex <= 159 ) {
            return ( neuronIndex == player1Score ) ? 1d : 0d;
        }
        if ( ( neuronIndex >= 160 ) && ( neuronIndex <= 319 ) ) {
            return ( ( neuronIndex - 160 ) == player2Score ) ? 1d : 0d;
        }
        if ( neuronIndex == 320 ) {
            return isAIPlayer1 ? 1d : 0d;
        }
        if ( neuronIndex == 321 ) {
            return isAIPlayer1 ? 0d : 1d;
        }
        throw new IllegalStateException("unrecognized neuron number " + neuronIndex);
    }
}
