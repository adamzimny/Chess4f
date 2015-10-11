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
package chess4f.domain;

import java.util.List;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public interface Piece {

    Piece copy();

    Color getColor();

    Position getPosition();

    /**
     * Moves a piece.
     * Moves a piece and returns an Undo object that can be applied to take this move back.
     * @param position
     * @return
     */
    UndoPiece move(Position position);

    void applyUndo(UndoPiece undo);

    PieceKind getKind();

    /**
     * Get all possible moves of this piece given the situation on the board.
     * If validateCheck is true moves leading to check on own king will be filtered out.
     * If validateCheck is false check validation is not performed and some of the returned
     * moves might be illegal.
     * @param board board
     * @param validateCheck set to true if moves leading to check on own king should be filtered out
     * @return all destination positions for this piece
     */
    List<Position> getAllMoves(ChessBoard board, boolean validateCheck);

    /**
     * Has the piece ever been moved.
     * @return true if has been moved
     */
    boolean hasMoved();

    public int getScore();
}
