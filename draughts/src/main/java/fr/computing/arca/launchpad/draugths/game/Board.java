/*
 * Copyright 2015 ARCA Computing (http://arca-computing.fr)
 *
 * Licensed under the Creative Commons Zero (CC0) 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://creativecommons.org/publicdomain/zero/1.0/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.computing.arca.launchpad.draugths.game;

import static fr.computing.arca.launchpad.draugths.game.Piece.PLAYER_1;
import static fr.computing.arca.launchpad.draugths.game.Piece.PLAYER_2;

public class Board {
    private Piece[][] board = new Piece[8][8];

    public boolean squareIsEmpty(Coordinates coordinates) {
        return board[coordinates.x][coordinates.y] == null;
    }

    public void removePieceAt(Coordinates coord) {
        board[coord.x][coord.y] = null;
    }

    public void setPieceAt(Coordinates coord, Piece piece) {
        board[coord.x][coord.y] = piece;
    }

    public Piece getPieceAt(Coordinates coord) {
        return board[coord.x][coord.y];
    }

    public boolean isAPieceAt(Coordinates coord) {
        return getPieceAt(coord) != null;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                b.append(colorToChar(board[x][y]));
            }
            b.append("\n");
        }
        return b.toString();
    }

    private char colorToChar(Piece piece) {
        if (PLAYER_1 == piece) {
            return '1';
        }
        if (PLAYER_2 == piece) {
            return '2';
        }
        return '.';
    }
}
