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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public enum FileReader {

    INSTANCE;

    /**
     * Build board from text file.
     * @param fileName file name
     * @return board
     */
    public static ChessBoard readBoard(String fileName) {
        FileInputStream fis;
        ChessBoard board = new ChessBoard();
        try {
            fis = new FileInputStream(fileName);

            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;

            while ((line = br.readLine()) != null) {
                Piece piece = parsePiece(line);
                if(piece!=null) {
                    board.addPiece(piece);
                }
            }

            br.close();

        } catch (IOException ex) {
            throw new RuntimeException("Can not read boar", ex);
        }

        return board;
    }

    /**
     * E.g. wQa5 is white Queen on a5
     *
     * @param line
     * @return
     */
    private static Piece parsePiece(String line) {
        line = line.trim();

        if (line.trim().isEmpty()) {
            return null;
        }

        Color color;
        char c = line.charAt(0);
        switch (c) {
            case 'w':
                color = Color.WHITE;
                break;
            case 'b':
                color = Color.BLACK;
                break;
            default:
                throw new RuntimeException("line can not be parsed, invalid color: >" + line + "<");
        }

        PieceKind[] pieceKinds = PieceKind.values();
        PieceKind pieceKind = null;
        for (PieceKind pk : pieceKinds) {
            if (line.charAt(1) == pk.getNotation()) {
                pieceKind = pk;
                break;
            }
        }
        if (pieceKind == null) {
            throw new RuntimeException("line can not be parsed, invalid char: >" + line + "<");
        }

        char col = line.charAt(2);
        if (col < 'a' || col > 'h') {
            throw new RuntimeException("line can not be parsed, invalid column: >" + line + "<");
        }


        char row = line.charAt(3);
        if (row < '1' || row > '8') {
            throw new RuntimeException("line can not be parsed, invalid row: >" + line + "<");
        }

        Piece piece = pieceKind.buildPiece(color, new Position(row-'1'+1, col-'a'+1));
        return piece;
    }
}
