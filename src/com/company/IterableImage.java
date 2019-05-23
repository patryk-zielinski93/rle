package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class IterableImage implements Iterable<Color> {
    private BufferedImage image;
    private int width;
    private int height;

    IterableImage(BufferedImage img) {
        image = img;
        width = img.getWidth();
        height = img.getHeight();
    }

    @Override
    public Iterator<Color> iterator() {
        return new Itr();
    }

    private final class Itr implements Iterator<Color> {
        private int x = 0, y = 0;

        @Override
        public boolean hasNext() {
            return x < width && y < height;
        }

        @Override
        public Color next() {
            Color color = new Color(image.getRGB(x, y));

            x += 1;
            if (x >= width) {
                x = 0;
                y += 1;
            }

            return color;
        }
    }
}
