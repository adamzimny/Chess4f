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
public class Bishop extends BasePiece {

    public Bishop(Color color, Position position) {
        super(color, position);
    }

    protected Bishop(Bishop src) {
        super(src);
    }

    @Override
    public Bishop copy() {
        return new Bishop(this);
    }

    @Override
    public PieceKind getKind() {
        return PieceKind.BISHOP;
    }

    @Override
    public List<Position> getAllMoves(ChessBoard board, boolean validateCheck) {
        List<Position> moves = new ArrayList<>();

        int r, c;

        r = getPosition().getRow();
        c = getPosition().getCol();
        Position p;
        while (r < 8 && c < 8) {
            r++;
            c++;

            p = new Position(r, c);
            addMoveIfFieldEmptyOrCapture(board, p, moves);
            if (!board.isFieldEmpty(p)) {
                break;
            }
        }

        r = getPosition().getRow();
        c = getPosition().getCol();
        while (r < 8 && c > 1) {
            r++;
            c--;

            p = new Position(r, c);
            addMoveIfFieldEmptyOrCapture(board, p, moves);
            if (!board.isFieldEmpty(p)) {
                break;
            }
        }

        r = getPosition().getRow();
        c = getPosition().getCol();
        while (r > 1 && c < 8) {
            r--;
            c++;

            p = new Position(r, c);
            addMoveIfFieldEmptyOrCapture(board, p, moves);
            if (!board.isFieldEmpty(p)) {
                break;
            }
        }

        r = getPosition().getRow();
        c = getPosition().getCol();
        while (r > 1 && c > 1) {
            r--;
            c--;

            p = new Position(r, c);
            addMoveIfFieldEmptyOrCapture(board, p, moves);
            if (!board.isFieldEmpty(p)) {
                break;
            }
        }

        if (validateCheck) {
            moves = filterOutCausingCheckOnOwnKing(moves, board);
        }

        return moves;
    }
}
