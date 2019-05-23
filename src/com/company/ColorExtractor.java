package com.company;

import java.awt.*;

public class ColorExtractor {
    Color[][] colors;
    private int width;
    private int height;

    ColorExtractor(int w, int h, int[] r, int[] g, int[] b) {
        colors = new Color[w][h];
        width = w;
        height = h;
        int[][] red = createColorMatrix(r);
        int[][] green = createColorMatrix(g);
        int[][] blue = createColorMatrix(b);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                colors[x][y] = new Color(red[x][y], green[x][y], blue[x][y]);
            }
        }
    }

    private int[][] createColorMatrix(int[] ints) {
        int[][] colorMatrix = new int[width][height];
        int x = 0;
        int y = 0;

        for (int i = 0; i < ints.length; i += 2) {
            int color = ints[i];
            int count = ints[i + 1];

            for (int j = 0; j <= count; j++) {
                if (x >= width) {
                    x = 0;
                    y++;
                }
                colorMatrix[x][y] = color;
                x++;
            }
        }

        return colorMatrix;
    }
}
