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
package chess4f.domain.pieces;

import chess4f.domain.ChessBoard;
import chess4f.domain.Color;
import chess4f.domain.PieceKind;
import chess4f.domain.Position;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public class Pawn extends BasePiece {

    public Pawn(Color color, Position position) {
        super(color, position);
    }

    protected Pawn(Pawn src) {
        super(src);
    }

    @Override
    public Pawn copy() {
        return new Pawn(this);
    }

    @Override
    public PieceKind getKind() {
        return PieceKind.PAWN;
    }

    @Override
    public List<Position> getAllMoves(ChessBoard board, boolean validateCheck) {
        List<Position> moves = new ArrayList<>();

        Integer row = getPosition().getRow();
        Integer col = getPosition().getCol();

        switch (getColor()) {

            case BLACK:
                if (row == 7) {
                    Position p;
                    p = new Position(6, col);
                    addMoveIfFieldEmpty(board, p, moves);
                    if (board.isFieldEmpty(p)) { // can not jump over another piece
                        p = new Position(5, col);
                        addMoveIfFieldEmpty(board, p, moves);
                    }
                } else if (row > 1) {
                    Position p;
                    p = new Position(row - 1, col);
                    addMoveIfFieldEmpty(board, p, moves);
                }

                // capture
                if (row > 1 && col < 8) {
                    addMoveIfCapture(board, new Position(row - 1, col + 1), moves);
                }

                if (row > 1 && col > 1) {
                    addMoveIfCapture(board, new Position(row - 1, col - 1), moves);
                }

                // TODO add en passant
                break;
            case WHITE:
                if (row == 2) {
                    Position p;
                    p = new Position(3, col);
                    addMoveIfFieldEmpty(board, p, moves);
                    if (board.isFieldEmpty(p)) { // can not jump over another piece
                        p = new Position(4, col);
                        addMoveIfFieldEmpty(board, p, moves);
                    }
                } else if (row < 8) {
                    Position p;
                    p = new Position(row + 1, col);
                    addMoveIfFieldEmpty(board, p, moves);
                }

                // capture
                if (row < 8 && col < 8) {
                    addMoveIfCapture(board, new Position(row + 1, col + 1), moves);
                }

                if (row < 8 && col > 1) {
                    addMoveIfCapture(board, new Position(row + 1, col - 1), moves);
                }

                break;

        }

        if (validateCheck) {
            moves = filterOutCausingCheckOnOwnKing(moves, board);
        }

        return moves;
    }

    private void addMoveIfFieldEmpty(ChessBoard board, Position position, List<Position> moves) {
        if (board.isFieldEmpty(position)) {
            moves.add(position);
        }
    }

    private void addMoveIfCapture(ChessBoard board, Position position, List<Position> moves) {
        if (board.getPiece(position) != null && board.getPiece(position).getColor() != getColor()) {
            moves.add(position);
        }
    }

}
