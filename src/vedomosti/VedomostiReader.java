package vedomosti;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import service.LoggingService;
import svod.Svod;
import vedomosti.common.Form;
import vedomosti.common.FormParseException;
import vedomosti.common.Format;
import vedomosti.formats.Format23;
import vedomosti.formats.Format24;
import vedomosti.formats.Format25;
import vedomosti.formats.Format26;
import vedomosti.formats.Format27;

/**
 * Файл ведомости - Умеет читать ведомость и парсить их параметры
 *
 * @author kneretin
 */
class VedomostiReader extends VedomostiMerger {

    /**
     * Номер ведомости по формату - для определения формы
     */
    protected static final int FILE_NAME_LENGTH = 8;
    protected static final int FILE_EXTENION_LENGTH = 4;
    protected final int vedomostNumber;
    protected final File file;

    public VedomostiReader(int _vedomostNumber, File _file) {
        LoggingService.writeLog("create VedomostiReader with file: " + _file.getAbsolutePath(), "debug");
        format = null;
        vedomostNumber = _vedomostNumber;
        file = _file;
        orgsGroup = new OrganizationsGroup();
    }

    /**
     * Читаем файл - распределяем оранизациии и выплаты
     */
    protected void read() throws FormParseException {
        String string = "";
        String lastOrgString = null;
        Organization org = null;
        LoggingService.writeLog("read file: " + file.getAbsolutePath(), "debug");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "cp866"))) {
            int lineNumber = 0;
            while ((string = reader.readLine()) != null) {
                if (string.contains("ВЕРСИЯ")) {
                    fileHeader = string;
                    boolean formatFound = false;

                    //позже заменить фабрикой
                    if (string.contains("ВЕРСИЯ23")) {
                        format = new Format23(string);
                    } else {
                        String fnumber = "";
                        String className = "";
                        try {
                            if (string.contains("ВЕРСИЯ|")) {
                                fnumber = string.substring(0, string.indexOf(".")).replace("ВЕРСИЯ|", "");
                                className = "vedomosti.formats.Format" + fnumber;
                                Object obj = Class.forName(className).getConstructor(String.class).newInstance(string);
                                if (obj instanceof Format) {
                                    format = (Format) obj;
                                    formatFound = true;
                                    LoggingService.writeLog("made format object for className: " + className, "debug");
                                }
                            }
                        } catch (Exception ex) {
                            LoggingService.writeLog("cant find format class for: " + className + "\r\n will try to make highest version format", "error");
                            LoggingService.writeLog(ex);
                            try {
                                className = "vedomosti.formats." + Svod.highestFormatVersionClassName;
                                Object obj = Class.forName(className).getConstructor(String.class).newInstance(string);
                                if (obj instanceof Format) {
                                    format = (Format) obj;
                                    LoggingService.writeLog("made format object for className: " + className, "debug");
                                    formatFound = true;
                                }
                            } catch (Exception ex1) {
                                LoggingService.writeLog("cant find format class for highest version: " + className + "\r\n will try to make version format manually", "error");
                                LoggingService.writeLog(ex1);
                            }
                        }
                    }
                    if (!formatFound) {
                        LoggingService.writeLog("select format manualy", "debug");
                        if (string.contains("ВЕРСИЯ|24.")) {
                            format = new Format24(string);
                        }
                        if (string.contains("ВЕРСИЯ|25.")) {
                            format = new Format25(string);
                        }
                        if (string.contains("ВЕРСИЯ|26.")) {
                            format = new Format26(string);
                        }
                        if (string.contains("ВЕРСИЯ|27.")) {
                            format = new Format27(string);
                        }
                    }

                    if (format != null) {
                        format.setVedomostNumber(vedomostNumber);
                    }
                } else {
                    if (string.contains("ОРГАНИЗАЦИЯ")) {
                        lastOrgString = string;
                        if (org != null) { //проверяем предыдущую организацию, она уже заполнена и можно посмотреть не пустая ли
                            saveRededOrganization(org);
                        }
                        org = new Organization(string, format, vedomostNumber);
                    } else {
                        try {
                            Form form = format.makeForm();
                            form.parse(string);
                            if (org.isAcceptForm(form) == false) {
                                saveRededOrganization(org);
                                org = new Organization(lastOrgString, format, vedomostNumber);
                            }
                            org.parseFormNew(form, lineNumber);
                        } catch (FormParseException fpex) {
                            throw fpex;
                        }
                    }
                }
                lineNumber++;
            }
            //проверяем последнюю оставшуюся органиацию
            saveRededOrganization(org);
        } catch (IOException ex) {
            LoggingService.writeLog(ex);
        }
        LoggingService.writeLog("- done with reading file: " + file.getAbsolutePath(), "debug");
        orgsGroup.print();
    }

    protected void saveRededOrganization(Organization org) {
        orgsGroup.addOrg(org);
    }

    protected boolean checkFile() {
        String fName = file.getName();
        if (fName.contains(".")) {
            if (fName.contains("_")) {
                String[] parts = fName.split("_");
                if (parts.length > 0 && parts[0].length() == FILE_NAME_LENGTH) {
                    return true;
                }
            }
            if (fName.length() == FILE_NAME_LENGTH + FILE_EXTENION_LENGTH) {
                return true;
            }
        }
        return false;
    }

    public File getFile() {
        return file;
    }

}
