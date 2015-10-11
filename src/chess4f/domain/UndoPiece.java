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
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public class UndoPiece {
    final Piece piece;
    final Position position;
    final boolean moved;

    public UndoPiece(Piece piece, Position position, boolean moved) {
        this.piece = piece;
        this.position = position;
        this.moved = moved;
    }

    public Position getPosition() {
        return position;
    }

    public boolean getMoved() {
        return moved;
    }

    public Piece getPiece() {
        return piece;
    }
}
