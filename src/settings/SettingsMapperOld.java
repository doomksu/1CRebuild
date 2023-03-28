package settings;

import service.LoggingService;

/**
 * Класс для работы со старым формат построчной записи установок. Расширяет
 * общий класс для работы с установками для конвертации
 *
 * @author kneretin
 */
public class SettingsMapperOld extends SettingsMapper {

    public SettingsMapperOld() {
        super();
    }

    /**
     * Записать значение в установки, если установки заполнены возпащает false
     * иначе true
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setValue(int key, String value) {
        if (key <= settingsKeyset.length) {
            settings.put(settingsKeyset[key], value);
            LoggingService.writeLog("try to put in  " + settingsKeyset[key] + " : " + value, "debug");
            return true;
        }
        return false;
    }

}
