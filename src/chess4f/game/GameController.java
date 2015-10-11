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
package chess4f.game;

import chess4f.ai.AiPlayer2;
import chess4f.domain.ChessBoard;
import chess4f.domain.Color;
import chess4f.domain.Move;
import chess4f.domain.UndoBoard;
import chess4f.file.FileWriter;
import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public class GameController {

    public interface MoveListener {

        void onMove(Move move);
    }

    public interface EndOfGameListener {

        /**
         * Called when game has finished, either due to checkmate or stallmate
         *
         * @param winner winner or null if stallmate
         */
        void onEndOfGame(Color winner);
    }

    public enum GameMode {

        HUMAN_VS_AI,
        AI_VS_HUMAN,
        AI_VS_AI,
        HUMAN_VS_HUMAN
    }

    private Player whitePlayer;
    private Player blackPlayer;

    private final ChessBoard board;
    private Color turn = Color.WHITE;

    private final Deque<UndoBoard> undoStack = new LinkedList<>();
    private final String BOARD_LOG_FILE_NAME = "board.log";

    private MoveListener moveListener = null;
    private EndOfGameListener endOfGameListener = null;

    public GameController(GameMode gameMode, ChessBoard board) {
        this.board = board;

        switch (gameMode) {
            case HUMAN_VS_AI:
                whitePlayer = new HumanPlayer(board, Color.WHITE);
                blackPlayer = new AiPlayer2(board, Color.BLACK);
                break;
            case AI_VS_HUMAN:
                whitePlayer = new AiPlayer2(board, Color.WHITE);
                blackPlayer = new HumanPlayer(board, Color.BLACK);
                break;
            case AI_VS_AI:
                whitePlayer = new AiPlayer2(board, Color.WHITE);
                blackPlayer = new AiPlayer2(board, Color.BLACK);
                break;
            case HUMAN_VS_HUMAN:
                whitePlayer = new HumanPlayer(board, Color.WHITE);
                blackPlayer = new HumanPlayer(board, Color.BLACK);
                break;
        }
    }

    /**
     * Start the game.
     *
     * Method starts the game and does not return until the game is finished
     * (you probably want to call this method from a separate thread). During
     * execution listeners are notified about events such as move taken or
     * checkmate.
     */
    public synchronized void play() {

        do {
            Player player = turn == Color.WHITE ? whitePlayer : blackPlayer;

            Move move = player.makeMove();
            if (move == null) {
                if (endOfGameListener != null) {
                    if (board.isUnderCheck(Color.WHITE)) {
                        endOfGameListener.onEndOfGame(Color.BLACK);
                    } else if (board.isUnderCheck(Color.BLACK)) {
                        endOfGameListener.onEndOfGame(Color.WHITE);
                    } else {
                        endOfGameListener.onEndOfGame(null);
                    }
                }

                return;
            }
            UndoBoard undo = board.movePiece(move);
            undoStack.addLast(undo);

            FileWriter.writeBoard(BOARD_LOG_FILE_NAME, board, turn);
            if (moveListener != null) {
                moveListener.onMove(move);
            }

            turn = (turn == Color.WHITE ? Color.BLACK : Color.WHITE);
        } while (true);
    }

    public void undo() {
        UndoBoard undo = undoStack.pollLast();
        if (undo != null) {
            board.applyUndo(undo);
        }
    }

    public void setMoveListener(MoveListener moveListener) {
        this.moveListener = moveListener;
    }

    public void setEndOfGameListener(EndOfGameListener endOfGameListener) {
        this.endOfGameListener = endOfGameListener;
    }

    public Color getTurn() {
        return turn;
    }

    public Player getPlayer(Color color) {
        return color == Color.WHITE ? whitePlayer : blackPlayer;
    }

}
