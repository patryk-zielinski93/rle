package com.company;

import sun.security.ssl.Debug;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCompression extends JFrame {
    private JButton pickFileButton;
    private JPanel rootPanel;
    private JLabel pickedFile;

    public FileCompression() {
        add(rootPanel);
        setTitle("Kompresja plików");
        setSize(400, 500);

        pickFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = jfc.showOpenDialog(null);
                // int returnValue = jfc.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    String filename = selectedFile.getName();
                    String extension = filename.substring(filename.lastIndexOf("."));
                    pickedFile.setText("Wybrany plik: " + filename);
                    File outputFile;
                    if (extension.equals(".rle")) {
                        String path = selectedFile.getAbsolutePath();
                        outputFile = new File(path.substring(0, path.length() - 4) + ".jpg");

                        try {
                            RLECompression.decode(selectedFile, outputFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        outputFile = new File(selectedFile.getAbsolutePath() + ".rle");
                        try {
                            RLECompression.encode(ImageIO.read(selectedFile), outputFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

}
