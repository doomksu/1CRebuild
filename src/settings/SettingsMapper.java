package settings;

import java.util.HashMap;
import service.LoggingService;

/**
 *
 * @author kneretin
 */
public class SettingsMapper {

    protected HashMap<String, String> settings;
    protected final String[] settingsKeyset = {"PFRFILES",
        "EXCELFILES",
        "CHKSUBDIR",
        "TREGIONN",
        "TRAIONN",
        "TREGNPFR",
        "TORGNAME",
        "TKTOPFR",
        "TOKPO",
        "TKSP",
        "TOKEY",
        "TSTRUKTNAME",
        "OPTPRIM",
        "TPRIM",
        "TFILENUM",
        "TNOMVED",
        "XLSFILES",
        "split36byAccount"};

    public SettingsMapper() {
        settings = new HashMap<>();
        for (String key : settingsKeyset) {
            settings.put(key, "");
        }
    }

    public void setValue(String key, String value) {
        if (key.equals("PFRFILES") || key.equals("EXCELFILES")) {
            value = value.replace("?", "");
        }
        settings.put(key, value);
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public String getValue(String key) {
        if (settings.containsKey(key)) {
            return settings.get(key);
        }
        LoggingService.writeLog("try to get unexistable key: " + key,"debug");
        return "";
    }

    boolean isEmpty() {
        return settings.isEmpty();
    }

    /**
     * Сравнить параметры установок
     *
     * @param outMap
     * @return
     */
    public boolean isSameSettingsMap(SettingsMapper outMap) {
        for (String key : settingsKeyset) {
            if (settings.get(key).equals(outMap.settings.get(key)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Клонирование установок - нужно для сравнения при сохранении
     *
     * @return
     */
    public SettingsMapper clone() {
        SettingsMapper newOne = new SettingsMapper();
        for (String key : settingsKeyset) {
            newOne.settings.put(key, settings.get(key));
        }
        return newOne;
    }

}
