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
package chess4f.ai;

import chess4f.domain.Color;
import chess4f.domain.Move;
import java.util.LinkedList;

/**
 *
 * @author adam
 */
class Path {

    private int blackScore = 0;
    private int whiteScore = 0;
    private int length = 0;
    private Move firstMove = null;

    private final static boolean DEBUG = false;

    private LinkedList<Move> moves;

    Path(Path path) {
        if (DEBUG) {
            moves = new LinkedList<>(path.moves);
        }
        blackScore = path.blackScore;
        whiteScore = path.whiteScore;
        length = path.length;
        firstMove = path.firstMove;
    }

    Path() {
        if (DEBUG) {
            moves = new LinkedList<>();
        }
    }

    void addScore(Color color, int score) {
        if (color == Color.WHITE) {
            whiteScore += score;
        } else {
            blackScore += score;
        }
    }

    public int getBlackScore() {
        return blackScore;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getScoreDistance() {
        return Math.abs(blackScore - whiteScore);
    }

    public int getPathScore(Color color) {
        switch (color) {
            case WHITE:
                return whiteScore - blackScore;
            case BLACK:
                return blackScore - whiteScore;
            default:
                throw new RuntimeException("Unknown color");
        }
    }

    public void add(Move move) {
        if (DEBUG) {
            moves.add(move);
        }
        if (firstMove == null) {
            firstMove = move;
        }
        length++;
    }

    public Move getFirst() {
        return firstMove;
    }

    @Override
    public String toString() {
        if (DEBUG) {
            return "Path{" + "blackScore=" + blackScore + ", whiteScore=" + whiteScore + ", " + moves.toString() + '}';
        } else {
            return "Path{" + "blackScore=" + blackScore + ", whiteScore=" + whiteScore + '}';
        }
    }

    int getLength() {
        return length;
    }

}
