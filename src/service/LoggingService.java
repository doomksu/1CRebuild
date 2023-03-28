package service;

import utils.LoggingOptions;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import settings.FoldersInformation;

/**
 *
 * @author K Neretin
 */
public class LoggingService {

    private static LoggingService instance;
    private LoggingOptions options;
    private FileWriter logWriter;
    private File logFile;
    private ArrayList<String> buffer;

    /**
     * Загружаем сервис логирования. По умолчанию файл дописывается и режимы
     * записи устанавливаются в default
     */
    private LoggingService() {
        buffer = new ArrayList<>();
        logFile = new File(FoldersInformation.LOG_FILE);
        
    }

    public static LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
            instance.setLoggingOptions();
        }
        return instance;
    }

    public void closeLogs() {
        try {
            logWriter.close();
        } catch (IOException ex) {
            LoggingService.writeLog("ERROR: Cant close log file");
        }
        if (buffer.isEmpty() == false) {
            for (String string : buffer) {
                LoggingService.writeLog(string, "debug");
            }
        }
    }

    public static void writeLog(String message) {
        writeLog(message, "default");
    }

    /**
     * Запись StackTrace Exception
     *
     * @param Exception ex
     */
        public static void writeLog(Throwable ex) {
        Throwable printEx = ex;
        String exception = "";
        if (printEx.toString().isEmpty()) {
            exception += printEx.getMessage();
        } else {
            exception += printEx.toString();
        }
        String stackTrace = "\r\n";
        for (StackTraceElement ste : printEx.getStackTrace()) {
            stackTrace += "\t" + ste.toString() + "\r\n";
        }
        writeLog(exception + stackTrace, "error");
        if (printEx.getCause() != null) {
            writeLog(printEx.getCause());
        }
    }

    public static void writeLog(String message, String type) {
        try {
            String logMessage = (getInstance().options.isAddDateTime() ? getDateTimeString() + ":" + type + ":\t" : "")
                    + message.trim();
            if (logMessage.endsWith("\r\n") == false) {
                logMessage += "\r\n";
            }
            if (getInstance().logWriter != null) {
                if (getInstance().options.allowLogType(type)) {
                    getInstance().logWriter.write(logMessage);
                    getInstance().logWriter.flush();
                }
            } else {
                LoggingService.writeLog(logMessage, "debug");
            }
        } catch (IOException ex) {
            LoggingService.writeLog(getDateTimeString() + " : ERROR while write to log: " + message + "  type: " + type);
            LoggingService.writeLog(ex);
        }
    }

    private static String getDateTimeString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(dtf);
    }

    /**
     * Посмотреть установки и опрделить перезаписываем ли логи и какие типы
     * сообщений фильтруются
     */
    public void setLoggingOptions() {
        try {
            LoggingOptions lg = new LoggingOptions();
            logFile.delete();
            redirectLogFile();
            if (getInstance().logWriter == null) {
                logWriter = new FileWriter(logFile, true);
                Thread.sleep(1);
            }
            options = lg;
            if (buffer.isEmpty()) {     //буфер пустой 
            } else {                    //в буфере есть записи
                options.setAddDateTime(false);
                for (String string : buffer) {
                    LoggingService.writeLog(string);
                }
                options.setAddDateTime(true);
                buffer.clear();
            }
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
    }

    private void redirectLogFile() {
        try {
            PrintStream pslog = new PrintStream(logFile);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (Exception ex) {
            LoggingService.writeLog(ex);   //если лог не перенаправлен выводит в консоль
        }
    }

}
