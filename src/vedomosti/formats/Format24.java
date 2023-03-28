package vedomosti.formats;

import java.io.File;
import java.util.HashMap;
import service.LoggingService;
import settings.SettingsReader;
import vedomosti.common.Form;
import vedomosti.forms.format24.Form30_24;
import vedomosti.forms.format24.Form36_24;
import vedomosti.forms.format24.Form49_24;

/**
 *
 * @author kneretin
 */
public class Format24 extends Format23 {

    public static String FORMAT_NAME = "24.1";
    public static String DELIMETR = "|";
    static String XLSFolder = "";
    static String XLSPostfix = "";

    public Format24(String _header) {
        super(_header);
        header = _header;
    }

    public String createHeaderString(String newforamatName) {
        String comp = "";
        String temp = "";
        comp += "ВЕРСИЯ"
                + DELIMETR
                + newforamatName
                + DELIMETR;
        temp = SettingsReader.getInstance().getValue("TREGIONN")
                + "-"
                + SettingsReader.getInstance().getValue("TRAIONN")
                + "-"
                + SettingsReader.getInstance().getValue("TREGNPFR");
        comp += temp + DELIMETR;
        comp += SettingsReader.getInstance().getValue("TORGNAME")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TKTOPFR")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TOKPO")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TSTRUKTNAME")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TKSP")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TOKEY")
                + DELIMETR;
        return comp;
    }

    /**
     * На основании данных из установок получить отформатированную строку
     * заголовка файла
     *
     * @param strar
     * @return
     */
    public String createHeaderString() {
        String comp = "";
        String temp = "";
        comp += "ВЕРСИЯ"
                + DELIMETR
                + getFormatName()
                + DELIMETR;
        temp = SettingsReader.getInstance().getValue("TREGIONN")
                + "-"
                + SettingsReader.getInstance().getValue("TRAIONN")
                + "-"
                + SettingsReader.getInstance().getValue("TREGNPFR");
        comp += temp + DELIMETR;
        comp += SettingsReader.getInstance().getValue("TORGNAME")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TKTOPFR")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TOKPO")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TSTRUKTNAME")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TKSP")
                + DELIMETR
                + SettingsReader.getInstance().getValue("TOKEY")
                + DELIMETR;
        return comp;

    }

    protected HashMap<String, String> getValues(byte type, String str) {
        String[] names = {};
        if (type == VERSION) {
            names = this.VERChunksName;
        }
        if (type == ORGANIZATION) {
            names = this.ORGChunksNames;
        }
        HashMap<String, String> params = new HashMap<>();
        String[] parts = str.split("\\" + DELIMETR);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i] == null ? "" : parts[i];
            params.put(names[i], parts[i]);
        }
        return params;
    }

    @Override
    public Form makeForm() {
        Form form = null;
        switch (formNumber) {
            case 30:
                form = new Form30_24();
                break;
            case 36:
                form = new Form36_24();
                break;
            case 49:
                form = new Form49_24();
                break;
            default:
                form = super.makeForm();
        }
        if (form != null) {
            form.setFormFormat(produceFormFormat(form));
        }
        return form;
    }

    @Override
    public String getFormatName() {
        return getValues(VERSION, header).get("NumVerFile");
    }

    @Override
    protected FormFormat produceFormFormat(Form form) {
        return new FormFormat24(form);
    }

    @Override
    public File getXLSTemplate(int form) {
        File selfXLS = getXLSTemplateHeirarcy(form, XLSFolder, XLSPostfix);
        if (selfXLS == null || selfXLS.isFile() == false) {
            LoggingService.writeLog("cant find XLS template: " + form + " in format: " + this.getFormatName(),"debug");
            return super.getXLSTemplate(form);
        }
        return selfXLS;
    }
}
