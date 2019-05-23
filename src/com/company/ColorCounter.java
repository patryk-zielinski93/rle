package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ColorCounter {
    public int previous = -1;
    public int count = 0;
    public ArrayList<Byte> bytes = new ArrayList<Byte>();
    private Iterator<Color> itr;

    ColorCounter(Iterator<Color> i) {
        itr = i;
    }

    public void count(int c) {
        if ((c == previous || previous == -1) && count < 255 && itr.hasNext()) {
            count++;
            previous = c;
        } else {
            bytes.add((byte) previous);
            bytes.add((byte) count);
            count = 0;
            previous = c;
        }
    }
}
