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
import chess4f.domain.Move;
import chess4f.domain.Position;
import chess4f.domain.UndoBoard;
import chess4f.domain.UndoPiece;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public abstract class BasePiece implements Piece {

    private final Color color;
    private Position position;
    private boolean moved = false;

    public BasePiece(Color color, Position position) {
        this.color = color;
        this.position = position;
    }

    protected BasePiece(BasePiece src) {
        this.color = src.color;
        this.position = src.getPosition().copy();
        this.moved = src.moved;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public UndoPiece move(Position toPosition) {
        UndoPiece undo = new UndoPiece(this, position, moved);
        position = toPosition;
        moved = true;
        return undo;
    }

    @Override
    public boolean hasMoved() {
        return moved;
    }

    @Override
    public String toString() {
        return this.getColor().name() + " " + this.getKind().name();
    }

    protected boolean isCapture(ChessBoard board, Position position) {
        return (board.getPiece(position) != null && board.getPiece(position).getColor() != getColor());
    }

    protected boolean isFieldEmptyOrCapture(ChessBoard board, Position position) {
        return (board.isFieldEmpty(position) || isCapture(board, position));
    }

    private boolean isPositionInsideBoard(Position position) {
        int row = position.getRow();
        int col = position.getCol();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    protected void addMoveIfFieldEmptyOrCapture(ChessBoard board, Position position, List<Position> moves) {
        if (isPositionInsideBoard(position) && isFieldEmptyOrCapture(board, position)) {
            moves.add(position);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.color);
        hash = 47 * hash + Objects.hashCode(this.position);
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
        final BasePiece other = (BasePiece) obj;
        if (this.color != other.color) {
            return false;
        }
        return Objects.equals(this.position, other.position);
    }

    protected List<Position> filterOutCausingCheckOnOwnKing(List<Position> moveTo, ChessBoard board) {
        List<Position> filtered = new ArrayList<>(moveTo.size());
        moveTo.stream()
                .filter(p -> {
                    UndoBoard undo = board.movePiece(new Move(this, p));
                    boolean isUnderCheck = board.isUnderCheck(color);
                    board.applyUndo(undo);
                    return !isUnderCheck;
                })
                .forEach(filtered::add);

        return filtered;
    }

    @Override
    public void applyUndo(UndoPiece undo) {
        assert (this == undo.getPiece());
        this.position = undo.getPosition();
        this.moved = undo.getMoved();
    }

    @Override
    public int getScore() {
        return getKind().getScore();
    }

}
