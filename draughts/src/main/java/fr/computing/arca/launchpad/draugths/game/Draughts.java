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

public class Draughts {

    private Board board = new Board();
    private DraughtsListener draughtsListener;
    private Piece currentPlayer = PLAYER_1;
    private boolean lastMoveHasCaught = false;
    private int playerOneNbPieces = 0;
    private int playerTwoNbPieces = 0;

    public Draughts(DraughtsListener draughtsListener) {
        this.draughtsListener = draughtsListener;
        initBoard();
    }

    private void initBoard() {
        initBoardForPlayer1();
        initBoardForPlayer2();
    }

    private void initBoardForPlayer1() {
        for (int y = 0; y < 3; y++) {
            for (int x = y % 2; x < 8; x += 2) {
                addPiece(new Coordinates(x, y), PLAYER_1);
                playerOneNbPieces++;
            }
        }
    }

    private void initBoardForPlayer2() {
        for (int y = 7; y > 4; y--) {
            for (int x = y % 2; x < 8; x += 2) {
                addPiece(new Coordinates(x, y), PLAYER_2);
                playerTwoNbPieces++;
            }
        }
    }

    private void addPiece(Coordinates coord, Piece piece) {
        board.setPieceAt(coord, piece);
        draughtsListener.onPieceAdded(coord, piece);
    }

    public boolean move(Coordinates from, Coordinates to) {
        boolean canMove = canMove(from, to);
        boolean canCatch = !canMove && canCatch(from, to);

        if (canMove || canCatch) {
            changeTurn(from);
            movePiece(from, to);
            lastMoveHasCaught = canCatch;
        }
        if (canCatch) {
            catchPiece(from, to);
        }
        return canMove || canCatch;
    }

    private void changeTurn(Coordinates from) {
        currentPlayer = board.getPieceAt(from).other();
    }

    private boolean canMove(Coordinates from, Coordinates to) {
        return from.rangeXWith(to) == 1 && from.rangeYWith(to) == 1
                && board.squareIsEmpty(to)
                && pieceIsOwnedByCurrentPlayer(from);
    }

    private void movePiece(Coordinates from, Coordinates to) {
        board.setPieceAt(to, board.getPieceAt(from));
        board.removePieceAt(from);
        draughtsListener.onPieceMoved(from, to, board.getPieceAt(to));
    }

    private boolean canCatch(Coordinates from, Coordinates to) {
        Coordinates between = from.between(to);
        return from.rangeXWith(to) == 2 && from.rangeYWith(to) == 2
                && board.squareIsEmpty(to)
                && board.getPieceAt(between) == board.getPieceAt(from).other()
                && (pieceIsOwnedByCurrentPlayer(from) || lastMoveHasCaught);
    }

    private void catchPiece(Coordinates from, Coordinates to) {
        Coordinates between = from.between(to);
        Piece piece = board.getPieceAt(between);
        board.removePieceAt(between);
        draughtsListener.onPieceCatched(between, piece);
        countCaughtPieces(piece);
        checkWinner();
    }

    private void countCaughtPieces(Piece piece) {
        if (piece == PLAYER_1) {
            playerOneNbPieces--;
        } else {
            playerTwoNbPieces--;
        }
    }

    private void checkWinner() {
        if (playerOneNbPieces == 0) {
            draughtsListener.onWinner(PLAYER_2);
        }
        if (playerTwoNbPieces == 0) {
            draughtsListener.onWinner(PLAYER_1);
        }
    }

    private boolean pieceIsOwnedByCurrentPlayer(Coordinates coord) {
        return currentPlayer == board.getPieceAt(coord);
    }

    @Override
    public String toString() {
        return board.toString();
    }

    public boolean isAPieceAt(Coordinates coord) {
        return board.isAPieceAt(coord);
    }

    public Piece getPieceAt(Coordinates coord) {
        return board.getPieceAt(coord);
    }
}
