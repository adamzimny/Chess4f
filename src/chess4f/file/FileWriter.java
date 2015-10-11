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
package chess4f.file;

import chess4f.domain.ChessBoard;
import chess4f.domain.Color;
import chess4f.domain.Piece;
import chess4f.domain.PieceKind;
import chess4f.domain.Position;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public enum FileWriter {

    INSTANCE;

    /**
     * Append board to file
     *
     * @param fileName file name
     * @param board
     * @param whoMadeLastMove
     */
    public static void writeBoard(String fileName, ChessBoard board, Color whoMadeLastMove) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fileName, true);

            //Construct BufferedReader from InputStreamReader
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            bw.append("=== "+new Date()+", last move made by "+whoMadeLastMove.name()+" ===\n");
            board.getAllPieces(Color.WHITE).stream().forEach((p) -> {
                addPiece(p, bw);
            });
            bw.newLine();
            board.getAllPieces(Color.BLACK).stream().forEach((p) -> {
                addPiece(p, bw);
            });
            bw.newLine();

            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(FileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    * E.g. wQa5 is white Queen on a5
    */
    private static void addPiece(Piece piece, BufferedWriter bw) {
        char kind = piece.getKind().getNotation();
        char column = (char) (piece.getPosition().getCol() - 1 + 'a');
        char row = (char) (piece.getPosition().getRow() - 1 + '1');
        char color = piece.getColor()==Color.WHITE ? 'w' : 'b';

        String pieceString = ""+color+kind+column+row;
        try {
            bw.append(pieceString+"\n");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
