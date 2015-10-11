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

/**
 * Undo for board.
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public class UndoBoard {
    private final Piece capturedPiece;
    private final UndoPiece undoPieceA;
    private final UndoPiece undoPieceB; // we need two in case of castling

    private final Boolean whiteUnderCheck;
    private final Boolean blackUnderCheck;


    public UndoBoard(Piece capturedPiece, UndoPiece undoPieceA, UndoPiece undoPieceB,
            Boolean whiteUnderCheck, Boolean blackUnderCheck) {
        this.capturedPiece = capturedPiece;
        this.undoPieceA = undoPieceA;
        this.undoPieceB = undoPieceB;

        this.whiteUnderCheck = whiteUnderCheck;
        this.blackUnderCheck = blackUnderCheck;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public UndoPiece getUndoPieceA() {
        return undoPieceA;
    }

    public UndoPiece getUndoPieceB() {
        return undoPieceB;
    }

    public Boolean getWhiteUnderCheck() {
        return whiteUnderCheck;
    }

    public Boolean getBlackUnderCheck() {
        return blackUnderCheck;
    }
}
