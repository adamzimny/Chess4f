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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Generic board with pieces.
 *
 * No game logic, can be used for chess, checkers and etc.
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public class Board {

    // [row][col]
    final Field[][] fields;
    protected final List<Piece> whitePieces = new ArrayList<>(16);
    protected final List<Piece> blackPieces = new ArrayList<>(16);
    protected static final Logger LOGGER = Logger.getLogger(Board.class.getName());
    protected static final boolean CHECK_CONSISTENCY = false;

    public Board() {
        fields = new Field[8][];
        for (int r = 0; r < 8; r++) {
            fields[r] = new Field[8];
            for (int c = 0; c < 8; c++) {
                fields[r][c] = new Field((r + c) % 2 == 0 ? Color.BLACK : Color.WHITE);
            }
        }

        checkConsistency();
    }

    protected Board(Board src) {
        fields = new Field[8][];
        for (int r = 0; r < 8; r++) {
            fields[r] = new Field[8];
            for (int c = 0; c < 8; c++) {
                fields[r][c] = new Field((r + c) % 2 == 0 ? Color.BLACK : Color.WHITE);
            }
        }

        for (Piece p : src.whitePieces) {
            Piece copy = p.copy();
            whitePieces.add(copy);
            getField(copy.getPosition()).setPiece(copy);
        }

        for (Piece p : src.blackPieces) {
            Piece copy = p.copy();
            blackPieces.add(copy);
            getField(copy.getPosition()).setPiece(copy);
        }

        checkConsistency();
    }

    public boolean isFieldEmpty(Position p) {
        return getField(p).getPiece() == null;
    }

    public boolean isFieldEmpty(int row, int col) {
        return getField(row, col).getPiece() == null;
    }

    public Color getFieldColor(Position p) {
        return getField(p).getColor();
    }

    public Piece getPiece(int row, int col) {
        return getField(row, col).getPiece();
    }

    public Piece getPiece(Position p) {
        return getField(p).getPiece();
    }

    public List<Piece> getAllPieces(Color color) {
        return Color.WHITE == color ? whitePieces : blackPieces;
    }

    public void addPiece(Piece piece) {
        switch (piece.getColor()) {
            case WHITE:
                whitePieces.add(piece);
                break;
            case BLACK:
                blackPieces.add(piece);
                break;
        }
        getField(piece.getPosition()).setPiece(piece);
    }

    final Field getField(Position p) {
        return fields[p.getRow() - 1][p.getCol() - 1];
    }

    final Field getField(int row, int col) {
        return fields[row - 1][col - 1];
    }

    final void removePiece(Piece capturedPiece) {
        getField(capturedPiece.getPosition()).setPiece(null);
        switch (capturedPiece.getColor()) {
            case WHITE:
                whitePieces.remove(capturedPiece);
                break;
            case BLACK:
                blackPieces.remove(capturedPiece);
                break;
        }
    }

    final void checkConsistency() {

        if (!CHECK_CONSISTENCY) {
            return;
        }

        for (Piece p : whitePieces) {
            if (p != getPiece(p.getPosition())) {
                throw new RuntimeException("Board inconsistent, piece: " + p + " field: " + getField(p.getPosition()));
            }
        }

        for (Piece p : blackPieces) {
            if (p != getPiece(p.getPosition())) {
                throw new RuntimeException("Board inconsistent, piece: " + p + " field: " + getField(p.getPosition()));
            }
        }

        int whitePiecesCount = 0;
        int blackPiecesCount = 0;
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                Piece piece = getPiece(r, c);
                if (piece != null && piece.getColor() == Color.WHITE) {
                    whitePiecesCount++;
                }

                if (piece != null && piece.getColor() == Color.BLACK) {
                    blackPiecesCount++;
                }
            }
        }

        if (whitePiecesCount != whitePieces.size()) {
            throw new RuntimeException("Counted white on the board " + whitePiecesCount + " but list size is " + whitePieces.size());
        }

        if (blackPiecesCount != blackPieces.size()) {
            throw new RuntimeException("Counted black on the board " + blackPiecesCount + " but list size is " + blackPieces.size());
        }
    }
}
