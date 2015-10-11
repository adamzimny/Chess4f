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

import chess4f.game.Player;
import chess4f.domain.ChessBoard;
import chess4f.domain.Color;
import chess4f.domain.Move;
import chess4f.domain.Position;
import chess4f.domain.Piece;
import chess4f.domain.UndoBoard;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public class AiPlayer2 implements Player {

    private static final Logger LOGGER = Logger.getLogger(AiPlayer2.class.getName());
    private final ChessBoard mainBoard;
    private final Color color;
    private final Random random = new Random(2);

    /**
     * Minimal depth level of analysis, each possible path will be analyzed at
     * least up to this depth.
     */
    private static final int MIN_ANALYSIS_DEPTH = 3;

    /**
     * Maximal depth level of analysis, analysis will never go deeper than given
     * level.
     */
    private static final int MAX_ANALYSIS_DEPTH = 7;

    private static final int CHECKMATE_OR_STALLMATE_SCORE = 10000;
    private static final int CHECK_SCORE = 10;

    public AiPlayer2(ChessBoard board, Color color) {
        this.mainBoard = board;
        this.color = color;
    }

    @Override
    public Move makeMove() {

        // use board copy for analysis so that in case gui is refreshed it does not reflect current
        // state of thinking
        ChessBoard board = mainBoard.copy();

        List<Path> bestEffortPaths = buildBestEffortPaths(0, MIN_ANALYSIS_DEPTH, new Path(), color, board);
        if (bestEffortPaths.isEmpty()) {
            // checkmate or stallmate
            return null;
        }

        Path bestPath = findBestScoredAndShortestPath(bestEffortPaths, color);
        Move move = bestPath.getFirst();

        // translate move back to original board
        Move moveOnMainBoard = new Move(mainBoard.getPiece(move.getWhat().getPosition()), move.getWhere());
        return moveOnMainBoard;
    }

    /**
     * Returns a list of paths in which each player took a move with his best
     * effort (move with the best score).
     *
     * @param currentLevel current level of analysis depth
     * @param maxDepthLevel how deep player can analyze
     * @param path path on top of which player adds his move
     * @param player player who takes the move
     * @return list of best effort paths
     */
    List<Path> buildBestEffortPaths(int currentLevel, int maxDepthLevel, Path path, Color player, ChessBoard board) {

        List<Move> moves = getAllMoves(player, board);

        if (moves.isEmpty()) {
            // will be interpreted as check mate or stallmate
            return new ArrayList<>();
        }

        if (currentLevel >= maxDepthLevel - 1) { // -1 because we will go one level further anyway
            return buildBestEffortPathsEndRecursion(moves, path, player, board);
        } else {
            return buildBestEffortPathsContinueRecursion(moves, path, player, board, maxDepthLevel, currentLevel);
        }
    }

    private List<Path> buildBestEffortPathsContinueRecursion(List<Move> moves, Path path, Color player,
            ChessBoard board, int maxDepthLevel, int currentLevel) {

        List<Path> bestEffortPathsForAllMoves = new LinkedList<>();

        // walk through all my possible moves
        for (Move move : moves) {
            Piece captured = board.getPiece(move.getWhere());
            UndoBoard undo = board.movePiece(move);
            Path newPath = new Path(path);
            newPath.add(move);

            // points for me if I made a check
            boolean check = board.isUnderCheck(player.opponent());
            if (check) {
                newPath.addScore(player, CHECK_SCORE);
            }

            int newMaxDepthLevel = calculateNewMaxDepthLevel(maxDepthLevel, captured, check);

            List<Path> bestEffortPathsForThisMove
                    = buildBestEffortPaths(currentLevel + 1, newMaxDepthLevel, newPath,
                            player.opponent(), board);

            // opponent can not move
            if (bestEffortPathsForThisMove.isEmpty()) {
                // check mate or stalemate, opponent can not move
                addScoreForCheckMateOrStalmate(board, newPath, player);
                bestEffortPathsForAllMoves.add(newPath);
            } else {
                if (captured != null) {
                    for (Path p : bestEffortPathsForThisMove) {
                        // use currentLevel as a small penalty, so that paths that score earlier are
                        // promoted over those that score later
                        p.addScore(player, captured.getScore() - currentLevel);
                    }
                }

                bestEffortPathsForAllMoves.addAll(bestEffortPathsForThisMove);
            }

            board.applyUndo(undo);
        }

        List<Path> bestPaths = findBestScoredPaths(bestEffortPathsForAllMoves, player);
        return bestPaths;
    }

    private List<Path> buildBestEffortPathsEndRecursion(List<Move> moves, Path path, final Color player, ChessBoard board) {
        // take those that have the best capture
        int bestScoreSoFar = Integer.MIN_VALUE;
        List<Path> newPaths = new LinkedList<>();

        for (Move move : moves) {
            Piece captured = board.getPiece(move.getWhere());

            Path newPath = new Path(path);
            newPath.add(move);
            if (captured != null) {
                newPath.addScore(player, captured.getScore());
            }

            if (newPath.getPathScore(player) > bestScoreSoFar) {
                bestScoreSoFar = newPath.getPathScore(player);
            }

            newPaths.add(newPath);
        }

        // take only best moves
        final int finalBestScore = bestScoreSoFar;
        List<Path> bestPaths = newPaths.stream()
                .filter(p -> p.getPathScore(player) == finalBestScore)
                .collect(Collectors.toList());

        return bestPaths;
    }

    /**
     * Get all possibilities for next move.
     *
     * @return list of all moves
     */
    private List<Move> getAllMoves(Color player, ChessBoard board) {
        List<Move> moves = new ArrayList<>();
        List<Piece> myPieces = board.getAllPieces(player);
        for (Piece piece : myPieces) {
            List<Position> pieceMoves = piece.getAllMoves(board, true);
            for (Position position : pieceMoves) {
                moves.add(new Move(piece, position));
            }
        }
        return moves;
    }

    private Path findBestScoredAndShortestPath(List<Path> paths, final Color player) {
        int bestScoreSoFar = paths.get(0).getPathScore(player);
        Path bestPathSoFar = paths.get(0);

        for (Path p : paths) {
            // uncomment to see thinking
            //    LOGGER.info(p.toString());
            if (p.getPathScore(player) > bestScoreSoFar) {
                bestPathSoFar = p;
                bestScoreSoFar = p.getPathScore(player);
            } else if (p.getPathScore(player) == bestScoreSoFar && p.getLength() < bestPathSoFar.getLength()) {
                bestPathSoFar = p;
            }
        }

        // collect all paths with score == best score and of minimal length
        final int bestPathMinLength = bestPathSoFar.getLength();
        final int finalBestScore = bestScoreSoFar;
        List<Path> bestAndShortestPaths = paths.stream()
                .filter(p -> p.getPathScore(player) == finalBestScore
                        && p.getLength() == bestPathMinLength)
                .collect(Collectors.toList());

        Path result = pickRandomPath(bestAndShortestPaths);
        LOGGER.info("Chosen path " + result);
        return result;
    }

    private List<Path> findBestScoredPaths(List<Path> paths, Color player) {
        int bestScore = paths.stream()
                .mapToInt(p -> p.getPathScore(player))
                .max()
                .getAsInt();

        List<Path> bestPaths = paths.stream()
                .filter(p -> p.getPathScore(player) == bestScore)
                .collect(Collectors.toList());

        return bestPaths;
    }

    private int calculateNewMaxDepthLevel(int maxDepthLevel, Piece captured, boolean check) {
        int newMaxDepthLevel = maxDepthLevel;
        if (captured != null || check) {
            newMaxDepthLevel++;
        } else {
            newMaxDepthLevel--;
        }

        if (newMaxDepthLevel < MIN_ANALYSIS_DEPTH) {
            newMaxDepthLevel = MIN_ANALYSIS_DEPTH;
        }

        if (newMaxDepthLevel > MAX_ANALYSIS_DEPTH) {
            newMaxDepthLevel = MAX_ANALYSIS_DEPTH;
        }

        //LOGGER.info("New max depth "+newMaxDepthLevel);
        return newMaxDepthLevel;
    }

    private Path pickRandomPath(List<Path> paths) {
        return paths.get(random.nextInt(paths.size()));
    }

    private void addScoreForCheckMateOrStalmate(ChessBoard board, Path path, Color player) {
        if (board.isUnderCheck(player.opponent())) {
            // check and mate, good for me
            LOGGER.info("I am " + player + ", checkmate possible: " + path);
            path.addScore(player, CHECKMATE_OR_STALLMATE_SCORE);
        } else {
            if (getAllPiecesStrength(board, player) > getAllPiecesStrength(board, player.opponent())) {
                // I am winning, do not want a draw, points to my oppponent
                LOGGER.info("I am " + player + ", stall possible, trying to avoid it");
                path.addScore(player.opponent(), CHECKMATE_OR_STALLMATE_SCORE);
            } else {
                // I am loosing, draw is a good choice, points for me
                LOGGER.info("I am " + player + ", stall possible, trying to make it");
                path.addScore(player, CHECKMATE_OR_STALLMATE_SCORE);
            }
        }
    }

    private int getAllPiecesStrength(ChessBoard board, Color player) {
        return board.getAllPieces(player).stream()
                .mapToInt(piece -> piece.getKind().getScore())
                .sum();
    }

}
