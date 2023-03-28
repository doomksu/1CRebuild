package vedomosti.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import service.LoggingService;
import settings.FoldersInformation;
import settings.SettingsReader;
import vedomosti.Organization;
import vedomosti.OrganizationsGroup;
import vedomosti.XLSWorkers.XLSWorker30;
import vedomosti.XLSWorkers.XLSWorker32;
import vedomosti.XLSWorkers.XLSWorker34;
import vedomosti.XLSWorkers.XLSWorker35;
import vedomosti.XLSWorkers.XLSWorker36;
import vedomosti.XLSWorkers.XLSWorker46;
import vedomosti.XLSWorkers.XLSWorker49;
import vedomosti.XLSWorkers.XLSWorker51;
import vedomosti.XLSWorkers.XLSWorker57;
import vedomosti.XLSWorkers.XLSWorker59;
import vedomosti.XLSWorkers.XLSWorker65;
import vedomosti.XLSWorkers.XLSWorker69;
import vedomosti.XLSWorkers.XLSWorker71;
import vedomosti.XLSWorkers.XLSWorker73;
import vedomosti.XLSWorkers.XLSWorker77;
import vedomosti.formats.FormFormat;
import vedomosti.formats.Format24;
import vedomosti.formats.Format25;

/**
 *
 * @author kneretin
 */
public abstract class Format implements Serializable {

    protected String header;
    protected String[] headerPartsNames;
    protected int[] headerPartsLengths;
    protected File outXLS;
    protected int formNumber;
    protected int[] VERchunksLenght;
    public String[] VERChunksName;
    protected int[] ORGChunksLenght;
    protected String[] ORGChunksNames;
    protected int MAX_DAYLI_COUNT = 9;
    public String XLSFolder = "";
    public String XLSPostfix = "";

    public static final String TOTAL_PAYMENT_KOD = "00000000";

    abstract public HashMap<String, String> getOrgValues(String str);

    abstract public HashMap<String, String> getVerValues(String str);

    abstract public Form makeForm();

    abstract public String getFormatName();

    abstract protected FormFormat produceFormFormat(Form form);

    /**
     * На основании данных из установок получить отформатированную строку
     * заголовка файла
     *
     * @param strar
     * @return
     */
    abstract public String createHeaderString();

    abstract public String createHeaderString(String newforamatName);

    abstract protected HashMap<String, String> getValues(byte type, String str);

    public void setVedomostNumber(int vedomostNumber) {
        formNumber = vedomostNumber;
    }

    public String getHeader() {
        return header;
    }

    public int getFormNumber() {
        return formNumber;
    }

    protected String upToLength(String string, int len) {
        while (string.length() < len) {
            string = string + " ";
        }
        return string;
    }

    public File getOutFileForXLSWorker(int vedomostNumber) {
        try {
            File template = getXLSTemplate(vedomostNumber);
            if (template != null && template.isFile()) {
                outXLS = copyXLSSchema(template, vedomostNumber);
                return outXLS;
            }
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
        return null;
    }

    public XLSWorker createWorker(OrganizationsGroup banks, int vedomostNumber) {
        LoggingService.writeLog("will create xls worker for format number: "+ formNumber, "debug");
        XLSWorker worker = null;
        try {
            if (outXLS == null) {
                outXLS = getOutFileForXLSWorker(vedomostNumber);
            }
            InputStream inp = new FileInputStream(outXLS);
            Workbook wb = WorkbookFactory.create(inp);
            switch (formNumber) {
                case 30:
                    worker = new XLSWorker30(wb, banks, getVerValues(getHeader()));
                    break;
                case 32:
                    worker = new XLSWorker32(wb, banks, getVerValues(getHeader()));
                    break;
                case 35:
                    worker = new XLSWorker35(wb, banks, getVerValues(getHeader()));
                    break;
                case 34:
                    worker = new XLSWorker34(wb, banks, getVerValues(getHeader()));
                    break;
                case 36:
                    worker = new XLSWorker36(wb, banks, getVerValues(getHeader()));
                    break;
                case 46:
                    worker = new XLSWorker46(wb, banks, getVerValues(getHeader()));
                    break;
                case 49:
                    worker = new XLSWorker49(wb, banks, getVerValues(getHeader()));
                    break;
                case 51:
                    worker = new XLSWorker51(wb, banks, getVerValues(getHeader()));
                    break;
                case 57:
                    worker = new XLSWorker57(wb, banks, getVerValues(getHeader()));
                    break;
                case 59:
                    worker = new XLSWorker59(wb, banks, getVerValues(getHeader()));
                    break;
                case 65:
                    worker = new XLSWorker65(wb, banks, getVerValues(getHeader()));
                    break;
                case 69:
                    worker = new XLSWorker69(wb, banks, getVerValues(getHeader()));
                    break;
                case 71:
                    worker = new XLSWorker71(wb, banks, getVerValues(getHeader()));
                    break;
                case 73:
                    worker = new XLSWorker73(wb, banks, getVerValues(getHeader()));
                    break;
                case 77:
                    worker = new XLSWorker77(wb, banks, getVerValues(getHeader()));
                    break;
            }
            if (worker != null) {
                worker.setOUTXLS(outXLS);
                return worker;
            }
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
        LoggingService.writeLog("ERROR cant crete worker for this format: " 
                + getFormatName() 
                + "  -form: " + getFormNumber(), "debug");
        return null;
    }

    /**
     * Скоприовать образец ведомости xls и задать ему имя
     *
     * @param form
     */
    private File copyXLSSchema(File template, int pril) {
        if (template.isFile()) {
            if (outXLS == null) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(new Date());
                String date = "";
                if (String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).length() < 2) {
                    date += "0";
                }
                date += gc.get(Calendar.DAY_OF_MONTH);
                if (String.valueOf(gc.get(Calendar.MONTH) + 1).length() < 2) {
                    date += "0";
                }
                date += gc.get(Calendar.MONTH) + 1;
                date += gc.get(Calendar.YEAR) + "_";

                if (String.valueOf(gc.get(Calendar.HOUR_OF_DAY)).length() < 2) {
                    date += "0";
                }
                date += gc.get(Calendar.HOUR_OF_DAY);
                if (String.valueOf(gc.get(Calendar.MINUTE)).length() < 2) {
                    date += "0";
                }
                date += gc.get(Calendar.MINUTE);
                if (String.valueOf(gc.get(Calendar.SECOND)).length() < 2) {
                    date += "0";
                }
                date += gc.get(Calendar.SECOND);
                String folderName = "";     //проверяем сохранение по папкам приложений
                if (SettingsReader.getInstance().isMakeFoldersForPril()) {
                    folderName = SettingsReader.getInstance().getValue("EXCELFILES") + "\\" + pril + "\\";
                    File folder = new File(folderName);
                    folder.mkdirs();
                    outXLS = new File(folderName + "P" + pril + "SVOD_" + date + ".xls");
                } else {
                    outXLS = new File(SettingsReader.getInstance().getValue("EXCELFILES") + folderName + "\\" + "P" + pril + "SVOD_" + date + ".xls");
                }
                try {
                    if (outXLS.exists() == false) {
                        outXLS.createNewFile();
                        Files.copy(template.toPath(), outXLS.toPath(), REPLACE_EXISTING);
                    }
                } catch (IOException ex) {
                    LoggingService.writeLog("ERROR - cant copy schema to out xls", "debug");
                    LoggingService.writeLog(ex);
                }
            }
        } else {
            LoggingService.writeLog(" cant find schema by form: " + pril, "debug");
        }
        return outXLS;
    }

    /**
     * Временная заглушка для сведения фвйлов 24 и 25 форматов - проверяет
     * соместимость форматов 24 и 25
     *
     * @param format
     * @return
     */
    public boolean compatibleWith(Format format) {
        if (getFormatName().equals(Format25.FORMAT_NAME) == true) {
            if (format.getFormatName().equals(Format24.FORMAT_NAME)) {
                return true;
            }
        } else {
            if (format.getFormatName().equals(Format25.FORMAT_NAME)) {
                return true;
            }
        }
        return false;
    }

    public String createOutFileName(int vedomostNumber) {
        GregorianCalendar gc = new GregorianCalendar();
        String day = String.valueOf(gc.get(Calendar.DAY_OF_MONTH));
        if (day.length() < 2) {
            day = "0" + day;
        }
        String name = SettingsReader.getInstance().getValue("EXCELFILES")
                + "\\"
                + SettingsReader.getInstance().getValue("TREGIONN").trim()
                + countCurentMonthSign()
                + day
                + getTodayOutFilesCount(vedomostNumber)
                + "."
                + SettingsReader.getInstance().getValue("TRAIONN").trim();
        return name;
    }

    protected int getTodayOutFilesCount(int prilNumber) {
        int number = 0;
        String file_path = SettingsReader.getInstance().getValue("EXCELFILES");
        if (SettingsReader.getInstance().isMakeFoldersForPril() && prilNumber >= 0) {
            file_path += "\\" + prilNumber;
        }
        File outDir = new File(file_path);
        GregorianCalendar gc = new GregorianCalendar();
        String day = String.valueOf(gc.get(Calendar.DAY_OF_MONTH));
        if (day.length() < 2) {
            day = "0" + day;
        }
        String monthNum = countCurentMonthSign();
        if (outDir.isDirectory()) {
            if (outDir.list().length > 0) {
                String newName = SettingsReader.getInstance().getValue("TREGIONN").trim() + monthNum + day;
                for (String name : outDir.list()) {
                    if (name.startsWith(newName)) {
                        number++;
                    }
                    if (number >= MAX_DAYLI_COUNT) {
                        number = 0;
                    }
                }
            }
        }
        return number;
    }

    /**
     * Определить номер или букву текущего месяца для имени файла
     *
     * @return
     */
    protected String countCurentMonthSign() {
        GregorianCalendar gc = new GregorianCalendar();
        int monthNum = (gc.get(Calendar.MONTH) + 1);
        String month = String.valueOf(monthNum);
        if (month.length() < 2) {
            month = "0" + month;
        }
        return null;
    }

    public File getXLSTemplate(int form) {
//        LoggingService.writeLog("call getXLSTemplateHeirarcy with: " + XLSFolder + "  " + XLSPostfix,"debug");
        return getXLSTemplateHeirarcy(form, XLSFolder, XLSPostfix);
    }

    protected File getXLSTemplateHeirarcy(int form, String subFolder, String postFix) {
        File xlsFolder = new File(FoldersInformation.XLS_PATH + subFolder);
        LoggingService.writeLog("search xls in folder: " + FoldersInformation.XLS_PATH + subFolder, "debug");
        if (xlsFolder.exists()) {
            File selectedXLSExemple = new File(xlsFolder.getAbsolutePath() + "\\Pril" + form + postFix + ".xls");
            LoggingService.writeLog("format folder exists - search xls file: " + xlsFolder.getAbsolutePath() + "\\Pril" + form + postFix + ".xls", "debug");
            if (selectedXLSExemple.isFile()) {
                return selectedXLSExemple;
            }
        }
        return null;
    }

    public File getOutXLS() {
        return outXLS;
    }

    public void print() {
        LoggingService.writeLog("-> format: " + getFormatName(), "debug");
        LoggingService.writeLog("-> outFile: " + (outXLS == null ? "null" : outXLS.getAbsolutePath()), "debug");
    }

    public String getAdditionalKeyForOrganiation(Organization org) {
        if (org.getParams().containsKey("foregnKey")) {
            if (org.getParams().get("foregnKey") == null) {
                return " local ";
            } else {
                return org.getParams().get("foregnKey");
            }
        } else {
            return " local ";
        }
    }

}
