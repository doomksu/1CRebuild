package vedomosti;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import service.LoggingService;
import vedomosti.common.FormParseException;
import vedomosti.common.Format;

/**
 *
 * @author kneretin
 */
public class VedomostiLoader extends VedomostiMerger {

    private File incomingFolder;
    private File outXLS;
    private VedomostiFile[] files;
    private ArrayList<String> fileExceptionMessages;
    private boolean done;
    private boolean hasBothFormats;

    /**
     * Передаем имя ведомости для контроля файлов и путь к папке загрузки
     *
     * @param vedomostName
     * @param path
     */
    public VedomostiLoader(String vedomostName, String path) {
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            incomingFolder = folder;
        } else {
            LoggingService.writeLog("ERROR: incoming folder not exists or not directory: " + path, "debug");
        }
        String prilNum = vedomostName.substring(vedomostName.indexOf("Прил."), vedomostName.lastIndexOf(")"));
        prilNum = prilNum.replace("Прил.", "").trim();
        prilNum = prilNum.replace("(", "").trim();
        prilNum = prilNum.replace(")", "").trim();
        vedomostNumber = 0;
        try {
            vedomostNumber = Integer.parseInt(prilNum);
        } catch (NumberFormatException nfe) {
            LoggingService.writeLog("ERROR cant calculate prilType: " + prilNum, "debug");
        }
        done = false;
    }

    /**
     * Создаем поток для каждого файла и считываем их
     */
    public void load() throws Exception {
        if (incomingFolder != null) {
            try {
                int filesCount = incomingFolder.listFiles().length;
                ExecutorService es = Executors.newFixedThreadPool(filesCount);
                files = new VedomostiFile[filesCount];
                if (files.length > 0) {
                    for (int i = 0; i < filesCount; i++) {
                        files[i] = new VedomostiFile(vedomostNumber, incomingFolder.listFiles()[i], outXLS);
                        es.submit(files[i]);
                    }
                    es.shutdown();
                    while (es.isTerminated() == false) {
                        es.awaitTermination(1, TimeUnit.SECONDS);
                    }
                }
            } catch (Exception ex) {
                LoggingService.writeLog(ex);
            }
            LoggingService.writeLog("- all files readed", "debug");
            fileExceptionMessages = new ArrayList<>();
            Format format = null;
            if (files.length > 0) {
                //временная заглушка для сведения файлов в 24 и 25 форматах
                hasBothFormats = false;
                for (VedomostiFile vFile : files) {
                    if (vFile.getSavedException() != null) {
                        LoggingService.writeLog(">> vFile has exception: " + vFile.getFile().getName(), "debug");
                        LoggingService.writeLog(vFile.getSavedException());
                        fileExceptionMessages.add(vFile.getSavedException().getMessage());
                    }
                    if (format == null) {
                        format = vFile.getFormat();
                        this.format = vFile.getFormat();
                    } else {
                        if (format.getFormatName().equals(vFile.getFormat().getFormatName()) == false) {
                            //временная заглушка для сведения файлов в 24 и 25 форматах
                            LoggingService.writeLog(">> format.getFormatName(): "
                                    + format.getFormatName()
                                    + "  vFile.getFormat().getFormatName(): "
                                    + vFile.getFormat().getFormatName()
                                    + " file: " + vFile.getFile().getName(), "debug");
                            if (format.compatibleWith(vFile.getFormat()) == true) {
                                hasBothFormats = true;
                            } else {
                                fileExceptionMessages.add("ОШИБКА: попытка обединиить файлы в разных версиях формата");
                            }
                        }
                    }
                }
            }
            if (fileExceptionMessages.isEmpty() == false) {
                String mes = "";
                for (String str : fileExceptionMessages) {
                    mes += str;
                }
                throw new FormParseException(mes);
            }
            int currentOrgNum = 0;
            if (files.length > 0) {
                for (VedomostiFile vedmostiFile : files) {
                    try {
                        if (vedmostiFile.xlsWorker == null && xlsWorker != null) {
                            vedmostiFile.xlsWorker = xlsWorker;
                        }
                        LoggingService.writeLog("writeLoadedOrganizations", "process");
                        currentOrgNum = vedmostiFile.writeLoadedOrganizations(currentOrgNum);
                        LoggingService.writeLog("writeLoadedOrganizations done", "process");
                        xlsWorker = vedmostiFile.xlsWorker;
                    } catch (Exception ex) {
                        LoggingService.writeLog("ERROR - cant write file to XLS", "debug");
                        LoggingService.writeLog(ex);
                    }
                }
            }
        }
    }

    public ArrayList<String> getFileExceptionMessages() {
        return fileExceptionMessages;
    }

    /**
     * Из загруженных файлов сливаем организации в один список по кодам ИНН_КПП
     */
    public void mergeFiles() {
        fileOrgGroups = new ArrayList<>();
        orgsGroup = new OrganizationsGroup();
//        LoggingService.writeLog(">> VedomostiLoader mergeFiles()", "debug");
        for (VedomostiFile file : files) {
//            LoggingService.writeLog(">> gona merge inside file: " + file.getFile().getName(), "debug");
            orgsGroup.allocateOrgGroup(file.orgsGroup);
        }
//        LoggingService.writeLog(">> full org group:", "debug");
//        orgsGroup.print();
        orgsGroup.merge();
//        LoggingService.writeLog(">> full org group after merge:", "debug");
//        orgsGroup.print();
    }

    public void writeSVOD() {
        try {
            if (xlsWorker == null) {
                LoggingService.writeLog(">> is format==null: " + (format == null ? " true" : " false"), "debug");
                xlsWorker = format.createWorker(orgsGroup, vedomostNumber);
                LoggingService.writeLog("returned xls worker is: " + xlsWorker.getClass().getName(), "debug");
            }
            xlsWorker.setBanks(orgsGroup);
            xlsWorker.writeSvod();
            xlsWorker.done();
            printBanksHandler(hasBothFormats);
            Desktop desc = Desktop.getDesktop();
            try {
                desc.open(xlsWorker.getFileOut());
            } catch (IOException ex) {
                LoggingService.writeLog(ex);
            }
        } catch (Exception ex) {
            LoggingService.writeLog("ERROR - cant write SVOD to XLS", "debug");
            LoggingService.writeLog(ex);
        }
    }

    public boolean isEmptyInputFolder() {
        if (files == null) {
            return true;
        }
        if (files.length == 0) {
            return true;
        }
        return false;
    }

}
