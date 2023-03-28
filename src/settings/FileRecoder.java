package settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import service.LoggingService;

public class FileRecoder {

    public static void recode(File file) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String path = file.getAbsolutePath();
        File temp = new File(path + "temp.txt");
        if (temp.exists() == true) {
            temp.delete();
        }
        String string = "";
        if (file.isFile()) {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "Cp1251"));
                while ((string = reader.readLine()) != null) {
                    writer.write(string);
                    writer.newLine();
                    writer.flush();
                }
                writer.close();
                reader.close();
            } catch (Exception ex) {
                LoggingService.writeLog(ex);
            }
        }
        File oldFile = file;
        oldFile.delete();
        temp.renameTo(oldFile);
    }

    public static void recode(String inFile) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        File in = new File(inFile);
        LoggingService.writeLog("--recode file UTF8 -> cp1251: " + in.getName(), "debug");
        String path = in.getAbsolutePath();
        File temp = new File(path + "temp.txt");
        try {
            if (temp.exists() == true) {
                temp.delete();
            }
            String string = "";
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), "cp1251"));
            while ((string = reader.readLine()) != null) {
                writer.write(string);
                writer.newLine();
                writer.flush();
            }
            writer.close();
            reader.close();
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
        File oldFile = new File(inFile);
        oldFile.delete();
        temp.renameTo(oldFile);
    }
}
