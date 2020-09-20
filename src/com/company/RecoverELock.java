package com.company;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RecoverELock extends Application {
    private ArrayList<File> files;
    private int fileNum;
    private int fileIterator;

    public static void main(String[] args) {
        //System.out.println(generatePassword("HelloWorld", new byte[16]));
        //createZip();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Recover-E Lock");
        GridPane root = new GridPane();
        Text group = new Text("Recover-E Lock");
        group.setScaleX(2);
        group.setScaleY(2);
        group.setScaleZ(2);

        Button start = new Button("Start");
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fileSelection(stage);
            }
        });
        root.setAlignment(Pos.TOP_CENTER);
        root.setVgap(10);

        start.setAlignment(Pos.CENTER);
        root.add(group, 0, 15);
        root.add(start, 0, 20);
        GridPane.setHalignment(start, HPos.CENTER);
        stage.setScene(new Scene(root, 640, 480));

        stage.show();
    }

    private void fileSelection(Stage stage) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Enter number of files");
        TextField inPath = new TextField("Enter number of files here");
        Button next = new Button("Next");

        next.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    fileNum = Integer.parseInt(inPath.getText());
                    inPath.setPromptText("Enter file path here");
                    popupStage.setTitle("Enter file path");
                    fileIterator = 0;
                    files = new ArrayList<>();
                    next.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            if(fileIterator < fileNum) {
                                try {
                                    File temp = new File(inPath.getText());
                                    if(temp.exists()) {
                                        files.add(temp);
                                    } else {
                                        throw new FileNotFoundException();
                                    }
                                    fileIterator++;
                                } catch (Exception e) {
                                    Popup("Error", "Please enter a valid file path");
                                    e.printStackTrace();
                                }
                                if(fileIterator >= fileNum) {
                                    popupStage.close();
                                    closingScreen(stage);
                                }
                            } else {
                                popupStage.close();
                                closingScreen(stage);
                            }
                        }
                    });
                } catch(Exception e) {
                    Popup("Error", "Please input a valid number");
                }
            }
        });

        GridPane root = new GridPane();
        root.setAlignment(Pos.TOP_CENTER);
        root.setHgap(25);
        root.setVgap(10);
        root.setPadding(new Insets(25,25,25,25));
        root.add(inPath, 0,0);
        root.add(next,0,1);
        popupStage.setScene(new Scene(root, 500,200));
        popupStage.show();
    }

    private void closingScreen(Stage stage) {
        ZipFile lockzip = new ZipFile("Lockdown.zip");

        long key = System.currentTimeMillis();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);

        File timestamp = new File("Timestamp.txt");
        try {
            timestamp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter writer = new FileWriter(timestamp);
            writer.write(time);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        files.add(timestamp);

        File keyFile = new File("key.txt");
        try {
            keyFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileWriter writer = new FileWriter(keyFile);
            writer.write(Long.toString(key));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

        String password = generatePassword(Long.toString(key), new byte[16]);
        lockzip.setPassword(password.toCharArray());
        try {
            lockzip.addFiles(files, parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }

        GridPane root = new GridPane();
        Text group = new Text("Recover-E Lock");
        group.setScaleX(2);
        group.setScaleY(2);
        group.setScaleZ(2);

        Text text = new Text("Lockdown finished");

        root.setAlignment(Pos.TOP_CENTER);
        root.setVgap(10);

        root.add(group, 0, 15);
        root.add(text, 0, 20);
        GridPane.setHalignment(text, HPos.CENTER);
        stage.setScene(new Scene(root, 640, 480));

        stage.show();
    }

    private static String generatePassword(String key, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt);
            byte[] bytes = md.digest(key.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public void Popup(String title, String message) {
        Stage stage = new Stage();
        stage.setTitle(title);
        Text text = new Text(message);
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.add(text, 0, 0);
        stage.setScene(new Scene(pane, 300, 150));
        stage.show();
    }
}
