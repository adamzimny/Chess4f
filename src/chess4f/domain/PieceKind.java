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

import chess4f.domain.pieces.Bishop;
import chess4f.domain.pieces.King;
import chess4f.domain.pieces.Knight;
import chess4f.domain.pieces.Pawn;
import chess4f.domain.pieces.Queen;
import chess4f.domain.pieces.Rook;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public enum PieceKind {
    QUEEN(100, 'Q') {

        @Override
        public Piece buildPiece(Color color, Position position) {
            return new Queen(color, position);
        }
    },
    KING(0, 'K') {

        @Override
        public Piece buildPiece(Color color, Position position) {
            return new King(color, position);
        }
    },
    PAWN(20, 'P') {

        @Override
        public Piece buildPiece(Color color, Position position) {
            return new Pawn(color, position);
        }
    },
    KNIGHT(60, 'N') {

        @Override
        public Piece buildPiece(Color color, Position position) {
            return new Knight(color, position);
        }
    },
    BISHOP(60, 'B') {

        @Override
        public Piece buildPiece(Color color, Position position) {
            return new Bishop(color, position);
        }
    },
    ROOK(80, 'R') {

        @Override
        public Piece buildPiece(Color color, Position position) {
            return new Rook(color, position);
        }
    };

    private final int score;
    private final char notation;

    PieceKind(int score, char notation) {
        this.score = score;
        this.notation = notation;
    }

    public int getScore() {
        return score;
    }

    public char getNotation() {
        return notation;
    }

    public abstract Piece buildPiece(Color color, Position position);

}
