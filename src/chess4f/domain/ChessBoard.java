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
import java.util.List;

/**
 * Board that knows chess rules.
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public class ChessBoard extends Board {

    private Boolean whiteUnderCheck = null;
    private Boolean blackUnderCheck = null;

    public ChessBoard() {
        checkConsistency();
    }

    protected ChessBoard(ChessBoard src) {
        super(src);

        whiteUnderCheck = src.whiteUnderCheck;
        blackUnderCheck = src.blackUnderCheck;

        checkConsistency();
    }

    public ChessBoard copy() {
        return new ChessBoard(this);
    }

    public void resetPieces() {
        whitePieces.clear();
        blackPieces.clear();

        // white
        for (int c = 1; c <= 8; c++) {
            whitePieces.add(new Pawn(Color.WHITE, new Position(2, c)));
        }

        whitePieces.add(new Rook(Color.WHITE, new Position(1, 1)));
        whitePieces.add(new Rook(Color.WHITE, new Position(1, 8)));

        whitePieces.add(new Knight(Color.WHITE, new Position(1, 2)));
        whitePieces.add(new Knight(Color.WHITE, new Position(1, 7)));

        whitePieces.add(new Bishop(Color.WHITE, new Position(1, 3)));
        whitePieces.add(new Bishop(Color.WHITE, new Position(1, 6)));

        whitePieces.add(new Queen(Color.WHITE, new Position(1, 4)));
        whitePieces.add(new King(Color.WHITE, new Position(1, 5)));

        // black
        for (int c = 1; c <= 8; c++) {
            blackPieces.add(new Pawn(Color.BLACK, new Position(7, c)));
        }
        blackPieces.add(new Rook(Color.BLACK, new Position(8, 1)));
        blackPieces.add(new Rook(Color.BLACK, new Position(8, 8)));

        blackPieces.add(new Knight(Color.BLACK, new Position(8, 2)));
        blackPieces.add(new Knight(Color.BLACK, new Position(8, 7)));

        blackPieces.add(new Bishop(Color.BLACK, new Position(8, 3)));
        blackPieces.add(new Bishop(Color.BLACK, new Position(8, 6)));

        blackPieces.add(new Queen(Color.BLACK, new Position(8, 4)));
        blackPieces.add(new King(Color.BLACK, new Position(8, 5)));

        // put pieces on the board
        whitePieces.stream().forEach((p) -> {
            getField(p.getPosition()).setPiece(p);
        });

        blackPieces.stream().forEach((p) -> {
            getField(p.getPosition()).setPiece(p);
        });

    }

    public boolean isMoveLegal(Move move) {
        Piece piece = move.getWhat();

        List<Position> allMoves = piece.getAllMoves(this, true);
        return allMoves.contains(move.getWhere());
    }

    /**
     * Make a move on the board.
     *
     * No validation checking is performed.
     *
     * @param move move to be made
     * @return undo information to take the move back
     */
    public UndoBoard movePiece(Move move) {
        Piece capturedPiece = getPiece(move.getWhere());
        if (capturedPiece != null) {
            // capture
            removePiece(capturedPiece);
        }

        UndoPiece undoPieceB = moveRookIfCastling(move.getWhat(), move.getWhere());

        getField(move.getWhat().getPosition()).setPiece(null);
        getField(move.getWhere()).setPiece(move.getWhat());
        UndoPiece undoPieceA = move.getWhat().move(move.getWhere());

        UndoBoard undoBoard = new UndoBoard(capturedPiece, undoPieceA, undoPieceB, whiteUnderCheck, blackUnderCheck);

        // check status becomes undetermined
        whiteUnderCheck = null;
        blackUnderCheck = null;

        checkConsistency();

        return undoBoard;
    }

    public boolean isUnderCheck(Color kingColor) {

        switch (kingColor) {
            case WHITE:
                if (whiteUnderCheck == null) {
                    whiteUnderCheck = evaluateCheck(Color.WHITE);
                }
                return whiteUnderCheck;

            case BLACK:
                if (blackUnderCheck == null) {
                    blackUnderCheck = evaluateCheck(Color.BLACK);
                }
                return blackUnderCheck;
            default:
                throw new RuntimeException("unknown king color");
        }

    }

    private boolean evaluateCheck(Color kingColor) {
        //LOGGER.info("Checking if "+kingColor.name()+" is under attack");
        List<Piece> opponentPieces = (kingColor == Color.WHITE ? blackPieces : whitePieces);
        List<Piece> myPieces = (kingColor == Color.WHITE ? whitePieces : blackPieces);

        Piece king = myPieces.stream()
                .filter(p -> p.getKind() == PieceKind.KING)
                .findFirst()
                .get();

        Position kingPosition = king.getPosition();

        boolean check = opponentPieces.stream()
                .anyMatch(piece -> {
                    //LOGGER.info("Checking attack from "+piece.toString());
                    boolean attacks = piece.getAllMoves(this, false).stream()
                    .anyMatch(pos -> {
                        //LOGGER.info("  checking attack from "+piece.toString()+" position "+pos);
                        return kingPosition.equals(pos);
                    });

                    return attacks;
                });

        return check;
    }

    private UndoPiece moveRookIfCastling(Piece piece, Position toPosition) {
        // check castling
        if (piece.getKind() != PieceKind.KING
                || Math.abs(piece.getPosition().getCol() - toPosition.getCol()) != 2) {
            return null;
        }

        Piece rook;
        Position newRookPosition;
        Position oldRookPosition;
        if (toPosition.getCol() == 3) {
            // castling with left rook
            rook = getPiece(piece.getPosition().getRow(), 1);
            oldRookPosition = rook.getPosition();
            newRookPosition = new Position(piece.getPosition().getRow(), 4);
        } else {
            // castling with right rook
            assert (toPosition.getCol() == 7);
            rook = getPiece(piece.getPosition().getRow(), 8);
            oldRookPosition = rook.getPosition();
            newRookPosition = new Position(piece.getPosition().getRow(), 6);
        }
        getField(oldRookPosition).setPiece(null);
        getField(newRookPosition).setPiece(rook);
        return rook.move(newRookPosition);
    }

    public void applyUndo(UndoBoard undo) {

        UndoPiece undoPieceA = undo.getUndoPieceA();
        if (undoPieceA != null) { // should never be null
            getField(undoPieceA.getPiece().getPosition()).setPiece(null);
            undoPieceA.getPiece().applyUndo(undoPieceA);
            getField(undoPieceA.getPiece().getPosition()).setPiece(undoPieceA.getPiece());
        }

        // used when undoing castling
        UndoPiece undoPieceB = undo.getUndoPieceB();
        if (undoPieceB != null) {
            getField(undoPieceB.getPiece().getPosition()).setPiece(null);
            undoPieceB.getPiece().applyUndo(undoPieceB);
            getField(undoPieceB.getPiece().getPosition()).setPiece(undoPieceB.getPiece());
        }

        if (undo.getCapturedPiece() != null) {
            Piece capturedPiece = undo.getCapturedPiece();
            addPiece(capturedPiece);
        }

        whiteUnderCheck = undo.getWhiteUnderCheck();
        blackUnderCheck = undo.getBlackUnderCheck();

        checkConsistency();
    }
}
