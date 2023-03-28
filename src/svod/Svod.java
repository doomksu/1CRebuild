package svod;

import vedomosti.VedomostiLoader;
import java.io.File;
import java.util.ArrayList;
import service.LoggingService;
import settings.FoldersInformation;
import settings.SettingsReader;
import vedomosti.common.FormParseException;

/**
 * Главный класс. Хранит состояние
 *
 * @author kneretin
 */
public class Svod {

    private static Svod instance;
    public static String versionNumber = "1.5.21 28.03.2023";
    public static String highestFormatVersionClassName = "Format27";
    private ArrayList<String> messages;
    private boolean doneGood;

    /**
     * Единственный экземпляр СВОДа, и загрузка путей к файлам
     *
     * @return
     */
    public static Svod getInstance() {
        if (instance == null) {
            try {
                LoggingService.getInstance();
                LoggingService.writeLog("LoggingService loaded", "process");
                SettingsReader.getInstance();
                LoggingService.writeLog("Settings readed", "process");
                instance = new Svod();
                LoggingService.writeLog("1C Svod version: " + versionNumber, "process");
                FoldersInformation.initializeFolders();
            } catch (Exception ex) {
                LoggingService.writeLog(ex);
            }
        }
        return instance;
    }

    public Svod() {
        messages = null;
        doneGood = false;
    }

    public File getBootPath() {
        File thisDir = new File(System.getProperty("user.dir"));
        if (thisDir.isDirectory()) {
            return thisDir;
        }
        return null;
    }

    /**
     * Запускает загрузку файлов - VedomostiLoader, запуск объединения файлов и
     * записи свода
     *
     * @param type
     */
    public void loadFiles(String type) throws FormParseException, Exception {
        LoggingService.writeLog("load files in svod :: " + type, "debug");
        VedomostiLoader loader = new VedomostiLoader(type, SettingsReader.getInstance().getValue("PFRFILES"));
        try {
            loader.load();
        } catch (FormParseException fpex) {
            messages = loader.getFileExceptionMessages();
            LoggingService.writeLog("get error while loadFiles", "debug");
            throw fpex;
        }
        if (!loader.isEmptyInputFolder()) {
            loader.mergeFiles();
            loader.writeSVOD();
            doneGood = true;
        }
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public boolean isDoneGood() {
        return doneGood;
    }

}
