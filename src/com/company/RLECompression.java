package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class RLECompression {
    private static byte[] toPrimitives(ArrayList<Byte> bytes) {
        byte[] b = new byte[bytes.toArray().length];
        int i = 0;
        for (Byte by : bytes) {
            b[i++] = by.byteValue();
        }

        return b;
    }

    private static String readMetadata(File file, String s) {
        UserDefinedFileAttributeView view = Files.getFileAttributeView(file.toPath(), UserDefinedFileAttributeView.class);
        ByteBuffer buf = null;
        try {
            buf = ByteBuffer.allocate(view.size(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            view.read(s, buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        buf.flip();
        return Charset.defaultCharset().decode(buf).toString();
    }

    static void encode(BufferedImage img, File outputFile) throws IOException {
        int w = img.getWidth();
        int h = img.getHeight();

        Iterator<Color> itrImg = new IterableImage(img).iterator();
        ColorCounter redCc = new ColorCounter(itrImg);
        ColorCounter greenCc = new ColorCounter(itrImg);
        ColorCounter blueCc = new ColorCounter(itrImg);

        // Zliczanie poszczególnych kolorów
        while (itrImg.hasNext()) {
            Color color = itrImg.next();
            redCc.count(color.getRed());
            greenCc.count(color.getGreen());
            blueCc.count(color.getBlue());
        }

        ArrayList<Byte> bytes = new ArrayList<Byte>(redCc.bytes);
        bytes.addAll(greenCc.bytes);
        bytes.addAll(blueCc.bytes);

        /*System.out.println(redByte & 0xFF);*/

        byte[] toSave = RLECompression.toPrimitives(bytes);

        // Zapisz do pliku
        try (FileOutputStream stream = new FileOutputStream(outputFile)) {
            stream.write(toSave);
        }

        // Utwórz nagłówki pliku dotyczące rozmiaru obrazu i długości poszczególnych kolorów.
        UserDefinedFileAttributeView view = Files.getFileAttributeView(outputFile.toPath(), UserDefinedFileAttributeView.class);
        view.write("user.width", Charset.defaultCharset().encode(w + ""));
        view.write("user.height", Charset.defaultCharset().encode(h + ""));
        view.write("user.RLength", Charset.defaultCharset().encode(redCc.bytes.size() + ""));
        view.write("user.GLength", Charset.defaultCharset().encode(greenCc.bytes.size() + ""));
        view.write("user.BLength", Charset.defaultCharset().encode(blueCc.bytes.size() + ""));
    }

    static void decode(File file, File outputFile) throws IOException {
        // Wczytaj plik
        byte[] fileContent = Files.readAllBytes(file.toPath());
        // Odczytaj wartości z nagłówków
        int width = Integer.valueOf(RLECompression.readMetadata(file, "user.width"));
        int height = Integer.valueOf(RLECompression.readMetadata(file, "user.height"));
        int redLength = Integer.valueOf(RLECompression.readMetadata(file, "user.RLength"));
        int greenLength = Integer.valueOf(RLECompression.readMetadata(file, "user.GLength"));
        int blueLength = Integer.valueOf(RLECompression.readMetadata(file, "user.BLength"));
        // Wydziel poszczególne tablice koloru z pliku
        byte[] redArr = Arrays.copyOfRange(fileContent, 0, redLength);
        byte[] greenArr = Arrays.copyOfRange(fileContent, redLength, redLength + greenLength);
        byte[] blueArr = Arrays.copyOfRange(fileContent, redLength + greenLength, fileContent.length);
        int[] red = new int[redLength];
        int[] green = new int[greenLength];
        int[] blue = new int[blueLength];

        // Konwertuje byte na int (https://stackoverflow.com/questions/15191905/getting-byte-value-for-numbers-between-0-and-255)
        for (int i = 0; i < redLength; i++) {
            red[i] = redArr[i] & 0xFF;
        }

        for (int i = 0; i < greenLength; i++) {
            green[i] = greenArr[i] & 0xFF;
        }

        for (int i = 0; i < blueLength; i++) {
            blue[i] = blueArr[i] & 0xFF;
        }

        // Dekoduje obraz
        ColorExtractor ce = new ColorExtractor(width, height, red, green, blue);

        // Utwórz obraz i przypisz kolory do odpowiadających pixeli
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, ce.colors[i][j].getRGB());
            }
        }

        ImageIO.write(image, "jpg", outputFile);
    }
}
