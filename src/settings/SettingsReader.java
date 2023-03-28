package settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import service.LoggingService;
import settings.SettingsReader.STMap.ST;

/**
 *
 * @author kneretin
 */
public class SettingsReader {

    private static SettingsReader instance = null;
    private SettingsMapper settings;
    private ArrayList<String> vedomostiList;
    private STMap stmap;
    private boolean makeFolderForPril;

    private SettingsReader() {
        readSettings();
        saveSettings(false);
        makeFolderForPril = true;
    }

    public static SettingsReader getInstance() {
        if (instance == null) {
            instance = new SettingsReader();
        }
        return instance;
    }

    /**
     * Записать установки в файл и если они отличаются от записи в файле вернуть
     * true, а если в файл записаны те же значения (значения ек изменились) или
     * произошла ошибка вернуть false
     *
     * @param checkExistingSettings проверять существующий файл на равенство
     * @return result - обновлены ли поля в файле установок
     */
    public boolean saveSettings(boolean checkExistingSettings) {
        SettingsMapper currentSettings = settings.clone();
        SettingsMapper fileWritenSettings = null;
        if (checkExistingSettings == true) {
            readSettings();     //установки обновились до записанных в файл
            fileWritenSettings = settings.clone();
        }
        settings = currentSettings;

        boolean error = false;
        if (settings.isEmpty() == false) {
            JSONObject json = new JSONObject();
            for (String key : settings.getSettings().keySet()) {
                json.put(key, settings.getSettings().get(key));
            }
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FoldersInformation.SETTINGS_FILE), "cp1251"))) {
                String jString = json.toJSONString();
                writer.write(new String(jString));
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                error = true;
                LoggingService.writeLog(ex);
            }
        }
        if (error == false) {
            //ошибки при записи нет, проверяем что запись изменилась
            if (checkExistingSettings == true && settings.isSameSettingsMap(fileWritenSettings) == true) {
                return false;
            }
            return true;
        }
        return error;   //в случае ошибки
    }

    private void readSettings() {
        File tryOld = new File(FoldersInformation.SETTINGS_OLDFILE);
        File tryNew = new File(FoldersInformation.SETTINGS_FILE);
        if (tryOld.exists() && tryNew.exists()) {
            if (tryOld.lastModified() > tryNew.lastModified()) {
                //считываем старый формат и переписываем в JSON
                readOld();
            } else {
                readNew();
            }
        } else {
            if (tryOld.exists()) {
                readOld();
            } else {
                readNew();
            }
        }
        readVedomostiList();
        readSTList();
    }

    private void readOld() {
        File tryOld = new File(FoldersInformation.SETTINGS_OLDFILE);
        String string = "";
        SettingsMapperOld smOld = new SettingsMapperOld();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tryOld), "Cp1251"))) {
            int strIndex = 0;
            while ((string = reader.readLine()) != null) {
                if (smOld.setValue(strIndex, string.trim())) {
                    strIndex++;
                } else {
                    break;
                }
            }
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
        settings = smOld;
        tryOld.delete();    //удаляем старый формат
    }

    private void readNew() {
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(FoldersInformation.SETTINGS_FILE), "Cp1251")));
            settings = new SettingsMapper();
            for (Object key : json.keySet()) {
                String str = (String) json.get(key);
                byte ptext[] = str.getBytes();
                settings.setValue((String) key, new String(ptext));
            }
        } catch (IOException | ParseException ex) {
            LoggingService.writeLog("Error while reading settings file: " + FoldersInformation.SETTINGS_FILE, "debug");
            LoggingService.writeLog(ex);
        }
    }

    public String getValue(String key) {
        if (settings.getValue(key) == null) {
            return "";
        }
        return settings.getValue(key);
    }

    public void setValue(String key, String value) {
        settings.setValue(key, value.trim());
    }

    private void readVedomostiList() {
        this.vedomostiList = new ArrayList<>();
        String string = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FoldersInformation.VEDOMOSTILIST_FILE), "Cp1251"))) {
            while ((string = reader.readLine()) != null) {
                vedomostiList.add(string);
            }
        } catch (IOException ex) {
            LoggingService.writeLog("Error while reading vedomosti list: " + FoldersInformation.VEDOMOSTILIST_FILE, "debug");
            LoggingService.writeLog(ex);
        }
    }

    public int getTodayOutFilesCount(String selected_pril) {
        int vedomostNumber = 0;
        String prilNum = selected_pril.substring(selected_pril.indexOf("Прил."), selected_pril.lastIndexOf(")"));
        prilNum = prilNum.replace("Прил.", "").trim();
        prilNum = prilNum.replace("(", "").trim();
        prilNum = prilNum.replace(")", "").trim();
        vedomostNumber = 0;
        try {
            vedomostNumber = Integer.parseInt(prilNum);
            LoggingService.writeLog("vedomostNumber = " + vedomostNumber, "debug");
        } catch (NumberFormatException nfe) {
            LoggingService.writeLog("ERROR cant calculate prilType: " + prilNum, "debug");
        }
        int number = 0;
        String file_path = SettingsReader.getInstance().getValue("EXCELFILES");
        if (makeFolderForPril) {
            file_path += "\\" + vedomostNumber;
        }
        File outDir = new File(file_path);

        GregorianCalendar gc = new GregorianCalendar();
        String day = String.valueOf(gc.get(Calendar.DAY_OF_MONTH));
        if (day.length() < 2) {
            day = "0" + day;
        }
        String monthNum = String.valueOf(gc.get(Calendar.MONTH) + 1);
        if (monthNum.length() < 2) {
            monthNum = "0" + monthNum;
        }
        if (outDir.isDirectory()) {
            if (outDir.list().length > 0) {
                String newName = settings.settings.get("TREGIONN").trim() + monthNum + day;
                for (String name : outDir.list()) {
                    if (name.startsWith(newName)) {
                        number++;
                    }
                    if (number >= 9) {
                        number = 0;
                    }
                }
            }
        }
//        outFileNumber = number;
        return number;
    }

    public int getTodayOutFilesCount(int prilNumber) {
        int number = 0;
        String file_path = SettingsReader.getInstance().getValue("EXCELFILES");
        if (makeFolderForPril && prilNumber >= 0) {
            file_path += "\\" + prilNumber;
        }
        File outDir = new File(file_path);

        GregorianCalendar gc = new GregorianCalendar();
        String day = String.valueOf(gc.get(Calendar.DAY_OF_MONTH));
        if (day.length() < 2) {
            day = "0" + day;
        }
        String monthNum = String.valueOf(gc.get(Calendar.MONTH) + 1);
        if (monthNum.length() < 2) {
            monthNum = "0" + monthNum;
        }
        if (outDir.isDirectory()) {
            if (outDir.list().length > 0) {
                String newName = settings.settings.get("TREGIONN").trim() + monthNum + day;
                for (String name : outDir.list()) {
                    if (name.startsWith(newName)) {
                        number++;
                    }
                    if (number >= 9) {
                        number = 0;
                    }
                }
            }
        }
//        outFileNumber = number;
        return number;
    }

    public String getCurrentOutFileName(int prilNumber) {
        String newName = "\\";
        GregorianCalendar gc = new GregorianCalendar();
        String day = String.valueOf(gc.get(Calendar.DAY_OF_MONTH));
        if (day.length() < 2) {
            day = "0" + day;
        }
        String monthNum = String.valueOf(gc.get(Calendar.MONTH) + 1);
        if (monthNum.length() < 2) {
            monthNum = "0" + monthNum;
        }
        if (makeFolderForPril) {
            newName += prilNumber + "\\";
        }
        newName += settings.settings.get("TREGIONN").trim()
                + monthNum
                + day
                + getTodayOutFilesCount(prilNumber)
                + "." + settings.settings.get("TRAIONN").trim();
        return newName;
    }

    /**
     * Получить список форм для обработки
     *
     * @return
     */
    public ArrayList<String> getVedomostiList() {
        return vedomostiList;
    }

    /**
     * Получить список кодов
     *
     * @return
     */
    public STMap getSTs() {
        return stmap;
    }

    private void readSTList() {
        stmap = new STMap();
        String string = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FoldersInformation.STKLASS_FILE), "Cp1251"))) {
            while ((string = reader.readLine()) != null) {
                String[] strs = string.split("#");
                if (strs.length == 3) {
                    stmap.createST(strs[0], strs[1], strs[2]);
                } else {
                    if (strs.length == 2) {     //строка общий итог не имее своего КБК
                        stmap.createST(strs[0], strs[1], "");
                    } else {
                        LoggingService.writeLog("unknown string in payments list: " + string, "debug");
                    }
                }
            }
        } catch (IOException ex) {
            LoggingService.writeLog("Error while reading list of payments type: " + FoldersInformation.STKLASS_FILE, "debug");
            LoggingService.writeLog(ex);
        }
    }

    public String getSTPaymentName(String kod) {
        ST st = stmap.getST(kod);
        if (st != null) {
            return st.getName();
        }
        LoggingService.writeLog("ERROR: request name of payment that not in list of payments: " + kod, "debug");
        return "";
    }

    public class STMap {

        private HashMap<String, ST> kods;

        public STMap() {
            kods = new HashMap<>();
        }

        public void putKod(ST st) {
            kods.put(st.kod, st);
        }

        public boolean containKod(String kod) {
            return kods.containsKey(kod);
        }

        public ST getST(String kod) {
            if (containKod(kod)) {
                return kods.get(kod);
            }
            return null;
        }

        public void createST(String kod, String name, String kbk) {
            kods.put(kod, new ST(kod, name, kbk));
        }

        public class ST {

            private String kod;
            private String kbk;
            private String name;

            public ST(String kod, String name, String kbk) {
                this.kod = kod;
                this.kbk = kbk;
                this.name = name;
            }

            public String getKod() {
                return kod;
            }

            public String getKbk() {
                return kbk;
            }

            public String getName() {
                return name;
            }

        }
    }

    public void setMakeFoldersForPril(boolean selected) {
        makeFolderForPril = selected;
    }

    public boolean isMakeFoldersForPril() {
        return makeFolderForPril;
    }

}
