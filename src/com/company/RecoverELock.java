package com.company;

import javafx.application.Application;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RecoverELock extends Application {

    public static void main(String[] args) {
        //System.out.println(generatePassword("HelloWorld", new byte[16]));
        createZip();
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    private static void createZip() {
        ZipFile testzip = new ZipFile("testzipfile.zip");
        System.out.println("Made zip");

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        parameters.setEncryptFiles(true);
        parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        System.out.println("Set params");
        testzip.setPassword("HelloWorld".toCharArray());
        try {
            testzip.addFile(new File("C:\\Users\\Varun\\Desktop\\zippy.txt"), parameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        System.out.println("Added file");

        System.out.println("Done");
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
}
