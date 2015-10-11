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
public class Position {
    int row;
    int col;

    public Position(int row, int col) {
        assert(row>=1 && row<=8);
        assert(col>=1 && col<=8);
        this.row = row;
        this.col = col;
    }

    protected Position(Position src) {
        this.row = src.row;
        this.col = src.col;
    }

    public Position copy() {
        return new Position(this);
    }

    /**
     * Numbers as on the board, whites start from row 1 and 2.
     * @return row (1..8)
     */
    public int getRow() {
        return row;
    }

    /**
     * As on the board, just replace A->1, B->2, ...
     * @return col (1..8)
     */
    public int getCol() {
        return col;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.row;
        hash = 29 * hash + this.col;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Position other = (Position) obj;
        if (this.row != other.row) {
            return false;
        }
        return this.col == other.col;
    }

    @Override
    public String toString() {
        return "Position "+(char)(col-1+'a')+""+row;
    }


}
