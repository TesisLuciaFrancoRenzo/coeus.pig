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

public
enum Action
        implements IAction {
    ROLL1DICE(1),
    ROLL2DICES(2),
    ROLL3DICES(3),
    ROLL4DICES(4),
    ROLL5DICES(5),
    ROLL6DICES(6),
    ROLL7DICES(7),
    ROLL8DICES(8),
    ROLL9DICES(9),
    ROLL10DICES(10);

    private int numVal;

    Action( int numVal ) {
        this.numVal = numVal;
    }

    public
    int getNumVal() {
        return numVal;
    }
}

