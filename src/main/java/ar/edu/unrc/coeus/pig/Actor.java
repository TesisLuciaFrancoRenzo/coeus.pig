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

import ar.edu.unrc.coeus.tdlearning.interfaces.IActor;

public
class Actor
        implements IActor {
    private int    currentScore;
    private String name;
    private int    score;
    private int    throwsAmount;


    /**
     * Se inicializan todos los campos con valor cero.
     *
     * @param name Nombre del jugador
     */
    public
    Actor( String name ) {
        this.name = name;
        this.score = 0;
        this.throwsAmount = 0;
        this.currentScore = 0;
    }

    /**
     * @param currentScore valor a sumar al puntaje actual
     */
    public
    void addCurrentScore( int currentScore ) {
        this.currentScore += currentScore;
    }

    /**
     * @param score nuevo puntaje
     */
    public
    void addScore( int score ) {
        this.score += score;
    }

    /**
     * Aumenta la cantidad de tiradas en 1 valor.
     */
    public
    void addThrowsAmount() {
        this.throwsAmount++;
    }

    /**
     * @return el puntaje corriente del turno actual
     */
    public
    int getCurrentScore() {
        return currentScore;
    }

    /**
     * @return el nombre del jugador
     */
    public
    String getName() {
        return name;
    }

    /**
     * @return el puntaje total del juego
     */
    public
    int getScore() {
        return score;
    }

    /**
     * @return la cantidad de tiradas del turno actual
     */
    public
    int getThrowsAmount() {
        return throwsAmount;
    }

    @Override
    public
    String toString() {
        return "Actor{" + "name='" + name + '\'' + ", score=" + score + ", throwsAmount=" + throwsAmount + ", currentScore=" + currentScore + '}';
    }
}

