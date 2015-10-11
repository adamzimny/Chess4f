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

import chess4f.domain.Piece;
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
public class King extends BasePiece {

    public King(Color color, Position position) {
        super(color, position);
    }

    public King(King src) {
        super(src);
    }

    @Override
    public King copy() {
        return new King(this);
    }

    @Override
    public PieceKind getKind() {
        return PieceKind.KING;
    }

    @Override
    public List<Position> getAllMoves(ChessBoard board, boolean validateCheck) {
        List<Position> moves = new ArrayList<>();

        addRegularMoves(board, moves);

        addCastlingMoves(board, moves, validateCheck);

        if (validateCheck) {
            moves = filterOutCausingCheckOnOwnKing(moves, board);
        }

        return moves;
    }

    private void addRegularMoves(ChessBoard board, List<Position> moves) {
        int row = getPosition().getRow();
        int col = getPosition().getCol();

        addMoveIfFieldEmptyOrCapture(board, new Position(row, col + 1), moves);
        addMoveIfFieldEmptyOrCapture(board, new Position(row + 1, col), moves);
        addMoveIfFieldEmptyOrCapture(board, new Position(row + 1, col + 1), moves);
        addMoveIfFieldEmptyOrCapture(board, new Position(row, col - 1), moves);
        addMoveIfFieldEmptyOrCapture(board, new Position(row - 1, col), moves);
        addMoveIfFieldEmptyOrCapture(board, new Position(row - 1, col - 1), moves);
        addMoveIfFieldEmptyOrCapture(board, new Position(row + 1, col - 1), moves);
        addMoveIfFieldEmptyOrCapture(board, new Position(row - 1, col + 1), moves);
    }

    private void addCastlingMoves(ChessBoard board, List<Position> moves, boolean validateCheck) {
        if(hasMoved()) {
            return;
        }

        if(validateCheck && board.isUnderCheck(getColor())) {
            return;
        }

        int row = (getColor()==Color.WHITE) ? 1 : 8;
        Position rookAPosition = new Position(row, 1);
        Position rookBPosition = new Position(row, 8);

        Piece rookA = board.getPiece(rookAPosition);
        Piece rookB = board.getPiece(rookBPosition);
        if(rookA!=null && !rookA.hasMoved()) {
            // no pieces between rook and king
            if(board.isFieldEmpty(row, 2) && board.isFieldEmpty(row, 3) && board.isFieldEmpty(row, 4)) {
                moves.add(new Position(row, 3));
            }
        }

        if(rookB!=null && !rookB.hasMoved()) {
            // no pieces between rook and king
            if(board.isFieldEmpty(row, 6) && board.isFieldEmpty(row, 7)) {
                moves.add(new Position(row, 7));
            }
        }
    }
}
