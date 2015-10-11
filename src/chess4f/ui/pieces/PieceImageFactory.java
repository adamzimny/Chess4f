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
package chess4f.ui.pieces;

import chess4f.domain.Color;
import chess4f.domain.PieceKind;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author Adam Zimny <adamzimny@gmail.com>
 */
public enum PieceImageFactory {

    INSTANCE;

    private final static Map<String, Image> images = new HashMap<>();

    public static Image getImage(PieceKind kind, Color color) {

        String hash = hashKindAndColor(kind, color);
        Image img = images.get(hash);
        if (img == null) {
            img = loadFromFile(kind, color);
            images.put(hash, img);
        }

        return img;
    }

    private static String hashKindAndColor(PieceKind kind, Color color) {
        return kind.name() + "_" + color.name();
    }

    private static Image loadFromFile(PieceKind kind, Color color) {
        try {
            return ImageIO.read(PieceImageFactory.class.getResourceAsStream("/resources/graphics/" + kindAndColorToFileName(kind, color)));
        } catch (IOException ex) {
            throw new RuntimeException("Image not available", ex);
        }
    }

    private static String kindAndColorToFileName(PieceKind kind, Color color) {
        return kind.name().toLowerCase() + "_" + color.name().toLowerCase() + ".png";
    }

}
