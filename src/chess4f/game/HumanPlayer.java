/*
   Copyright (C) 2015 Adam Zimny (adamzimny@gmail.com)
   This file is part of Chess4f.

    Chess4f is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Chess4f is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Chess4f.  If not, see <http://www.gnu.org/licenses/>.
 */
package chess4f.game;

import chess4f.domain.ChessBoard;
import chess4f.domain.Color;
import chess4f.domain.Move;
import chess4f.domain.Piece;

/**
 *
 * @author Adam Zimny<adamzimny@gmail.com>
 */
public class HumanPlayer implements Player {

    Move move = null;
    ChessBoard mainBoard;
    Color color;

    public HumanPlayer(ChessBoard board, Color color) {
        this.mainBoard = board;
        this.color = color;
    }

    @Override
    public synchronized Move makeMove() {
        // check for checkmate or stallmate
        // do this on a board copy not to collide with UI (getAllMoves modifies board temporarily)
        // this should be handled smarter
        boolean canMove = false;
        ChessBoard board = mainBoard.copy();
        for(Piece p : board.getAllPieces(color)) {
            if(!p.getAllMoves(board, true).isEmpty()) {
                canMove = true;
            }
        }
        if(!canMove) {
            return null;
        }

        // wait for human to move
        try {
            this.wait();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        Move m = move;
        move = null;

        return m;
    }

    public synchronized void humanPlayerMoved(Move newMove) {
        if (move != null) {
            throw new RuntimeException("Multiple moves of human player");
        }
        this.move = newMove;
        this.notify();
    }

}
