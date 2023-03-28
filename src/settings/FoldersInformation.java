package settings;

import java.io.File;
import java.lang.reflect.Field;
import service.LoggingService;

/**
 *
 * @author kneretin
 */
public class FoldersInformation {

    public static String BOOT_PATH;
    public static String INPUT_PATH;
    public static String OUTPUT_PATH;
    public static String TEMP_FILE;
    public static String LOG_FILE;
    public static String ERR_FILE;
    public static String SETTINGS_FILE;
    public static String SETTINGS_OLDFILE;
    public static String VEDOMOSTILIST_FILE;
    public static String XLS_PATH;
    public static String STKLASS_FILE;

    static {
        String here = new File(".").getAbsolutePath();
        int lastSeparator = here.lastIndexOf("\\");
        String pathToFolder = here.substring(0, lastSeparator);
        BOOT_PATH = pathToFolder;
        INPUT_PATH = BOOT_PATH + "\\";
        OUTPUT_PATH = BOOT_PATH + "\\out\\";
        LOG_FILE = BOOT_PATH + "\\" + "log.txt";
        ERR_FILE = BOOT_PATH + "\\" + "err.txt";
        SETTINGS_FILE = BOOT_PATH + "\\" + "setup.json";
        SETTINGS_OLDFILE = BOOT_PATH + "\\" + "setup.s1c";
        VEDOMOSTILIST_FILE = BOOT_PATH + "\\" + "Vedomosti1C.s1c";
        STKLASS_FILE = BOOT_PATH + "\\" + "STKlass.s1c";
        XLS_PATH = BOOT_PATH + "\\" + "XLS";
    }

    public FoldersInformation() throws ClassNotFoundException {
        Class c = Class.forName("utils.FoldersInformation");
        Field[] declaredFields = c.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && java.lang.reflect.Modifier.isPrivate(field.getModifiers()) == false) {
                try {
                    LoggingService.writeLog("-- " + field.getName() + " : " + (String) field.get(null),"debug");
                } catch (Exception ex) {
                    LoggingService.writeLog(ex);
                }
            }
        }
    }

    public static void initializeFolders() throws ClassNotFoundException {
        Class c = Class.forName("settings.FoldersInformation");
        Field[] declaredFields = c.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && java.lang.reflect.Modifier.isPrivate(field.getModifiers()) == false) {
                try {
                    if (field.getName().contains("_PATH")) {      //нашли папку - создаем
                        File directory = new File((String) field.get(null));
                        if (directory.exists() == false || directory.isDirectory() == false) {
                            if (directory.mkdirs() == false) {
                                LoggingService.writeLog("-- cant create:  " + directory.getAbsolutePath(),"debug");
                            }
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    LoggingService.writeLog(ex);
                }
            }
        }
    }

}
