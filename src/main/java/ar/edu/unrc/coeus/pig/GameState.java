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

    /**
     * neuronas 320-329
     */
    private int     dicesToRoll;
    /**
     * neuronas 330-331
     */
    private boolean isAIPlayer1;
    /**
     * neuronas 0-159
     */
    private int     player1Score;
    /**
     * neuronas 160-319
     */
    private int     player2Score;

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
            final int player2Score
    ) {
        this.dicesToRoll = dicesToRoll;
        this.isAIPlayer1 = isAIPlayer1;
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
        return new GameState(dicesToRoll, isAIPlayer1, player1Score, player2Score);
    }

    public
    int getDicesToRoll() {
        return dicesToRoll;
    }

    public
    void setDicesToRoll( final int dicesToRoll ) {
        this.dicesToRoll = dicesToRoll;
    }

    /**
     * @return 0 = Sin ganador, o puntaje del perdedor
     */
    public
    int getLooserScore() {
        if ( isTerminalState() ) {
            return ( player1Score >= 100 ) ? player2Score : player1Score;
        } else {
            return 0;
        }
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

    /**
     * @return 0 = Sin ganador, o puntaje del ganador
     */
    public
    int getWinnerScore() {
        if ( isTerminalState() ) {
            return ( player1Score >= 100 ) ? player1Score : player2Score;
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
        if ( ( neuronIndex >= 320 ) && ( neuronIndex <= 329 ) ) {
            return ( ( neuronIndex - 320 ) == dicesToRoll ) ? 1d : 0d;
        }
        if ( neuronIndex == 330 ) {
            return isAIPlayer1 ? 1d : 0d;
        }
        if ( neuronIndex == 331 ) {
            return isAIPlayer1 ? 0d : 1d;
        }
        throw new IllegalStateException("unrecognized neuron number " + neuronIndex);
    }
}
