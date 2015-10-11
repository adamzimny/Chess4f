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

import chess4f.domain.pieces.Pawn;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adam
 */
public class BoardTest {

    public BoardTest() {
    }

    @Test
    public void testCopy() {
        System.out.println("copy");
        ChessBoard board = new ChessBoard();
        board.resetPieces();
        Pawn pawn = (Pawn) board.getPiece(2, 1);

        ChessBoard copy = board.copy();
        assertTrue(copy!=board);
        Pawn pawnCopy = (Pawn) copy.getPiece(2, 1);

        assertTrue(pawn!=pawnCopy);

        board.movePiece(new Move(pawn, new Position(3, 1)));

        // copy should not have moved
        assertEquals(2, pawnCopy.getPosition().getRow());
        assertEquals(1, pawnCopy.getPosition().getCol());
        assertFalse(pawnCopy.hasMoved());

        // make another copy
        ChessBoard copy2 = board.copy();
        Pawn pawnCopy2 = (Pawn) copy2.getPiece(3, 1);
        assertEquals(3, pawnCopy2.getPosition().getRow());
        assertEquals(1, pawnCopy2.getPosition().getCol());
        assertTrue(pawnCopy2.hasMoved());

        // move on copy2, assert that original has not changed
        copy2.movePiece(new Move(pawnCopy2, new Position(4, 1)));
        assertEquals(3, pawn.getPosition().getRow());
    }

}
