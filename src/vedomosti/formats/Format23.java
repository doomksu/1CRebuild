package vedomosti.formats;

import java.io.File;
import java.util.HashMap;
import service.LoggingService;
import settings.SettingsReader;
import vedomosti.common.Format;
import vedomosti.common.Form;
import vedomosti.forms.Form30;
import vedomosti.forms.Form32;
import vedomosti.forms.Form34;
import vedomosti.forms.Form35;
import vedomosti.forms.Form36;
import vedomosti.forms.Form46;
import vedomosti.forms.Form49;
import vedomosti.forms.Form51;
import vedomosti.forms.Form57;
import vedomosti.forms.Form59;
import vedomosti.forms.Form65;
import vedomosti.forms.Form69;
import vedomosti.forms.Form71;
import vedomosti.forms.Form73;
import vedomosti.forms.Form77;

/**
 *
 * @author kneretin
 */
public class Format23 extends Format {

    protected final String FORMAT_NAME = "23.1";

    protected byte VERSION = 0;
    protected byte ORGANIZATION = 1;

    public Format23(String _header) {
        header = _header;
        ORGChunksLenght = new int[]{11, 2, 1, 1, 10, 6, 3, 20, 2, 4, 250, 200, 15, 15, 250, 15, 15, 20, 20, 100, 15, 20, 20, 30, 10, 10, 20, 150, 1, 2, 100, 130, 3};
        ORGChunksNames = new String[]{"", "", "", "", "PrilDate", "PrilNumber", "", "", "", "", "OrgDostName", "OrgDostAdr",
            "OrgDostINN", "OrgDostKPP", "OrgPolName", "OrgPolINN", "OrgPoltKPP", "OrgPolBank", "OrgPolOKATO", "BankName", "BankBIK",
            "BankKorSch", "KODDohBK", "VidOper", "SrokPlat", "", "", "", "", "", "", "", ""};
        VERChunksName = new String[]{"", "NumVerFile", "RegNumNameOrg", "NameStPodr", "KTOPFR", "OKPO", "", "KSP", "OKEI"};
        VERchunksLenght = new int[]{6, 10, 14, 200, 10, 18, 100, 10, 3};
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
                + upToLength(getFormatName(), 10);
        temp = upToLength(SettingsReader.getInstance().getValue("TREGIONN"), 3)
                + "-"
                + upToLength(SettingsReader.getInstance().getValue("TRAIONN"), 3)
                + "-"
                + SettingsReader.getInstance().getValue("TREGNPFR");
        temp = upToLength(temp, VERchunksLenght[2]);
        comp += temp;
        comp += upToLength(SettingsReader.getInstance().getValue("TORGNAME"), VERchunksLenght[3]);
        comp += upToLength(SettingsReader.getInstance().getValue("TKTOPFR"), VERchunksLenght[4]);
        comp += upToLength(SettingsReader.getInstance().getValue("TOKPO"), VERchunksLenght[5]);
        comp += upToLength(SettingsReader.getInstance().getValue("TSTRUKTNAME"), VERchunksLenght[6]);
        comp += upToLength(SettingsReader.getInstance().getValue("TKSP"), VERchunksLenght[7]);
        comp += upToLength(SettingsReader.getInstance().getValue("TOKEY"), VERchunksLenght[8]);
        return comp;
    }

    protected HashMap<String, String> getValues(byte type, String str) {
        int[] length = {};
        String[] names = {};
        if (type == VERSION) {
            length = VERchunksLenght;
            names = VERChunksName;
        }
        if (type == ORGANIZATION) {
            length = ORGChunksLenght;
            names = ORGChunksNames;
        }
        HashMap<String, String> params = new HashMap<>();
        int start = 0;

        for (int i = 0; i < length.length; i++) {
            int end = start + length[i];
            String part = str.substring(start, end);
            params.put(names[i], part);
            start = end;
        }
        return params;
    }

    @Override
    public HashMap<String, String> getOrgValues(String str) {
        return getValues(ORGANIZATION, str);
    }

    @Override
    public HashMap<String, String> getVerValues(String str) {
        return getValues(VERSION, str);
    }

    @Override
    public Form makeForm() {
        Form form = null;
        switch (formNumber) {
            case 30:
                form = new Form30();
                break;
            case 32:
                form = new Form32();
                break;
            case 34:
                form = new Form34();
                break;
            case 35:
                form = new Form35();
                break;
            case 36:
                form = new Form36();
                break;
            case 46:
                form = new Form46();
                break;
            case 49:
                form = new Form49();
                break;
            case 51:
                form = new Form51();
                break;
            case 57:
                form = new Form57();
                break;
            case 59:
                form = new Form59();
                break;
            case 65:
                form = new Form65();
                break;
            case 69:
                form = new Form69();
                break;
            case 71:
                form = new Form71();
                break;
            case 73:
                form = new Form73();
                break;
            case 77:
                form = new Form77();
                break;
        }
        if (form != null) {
            form.setFormFormat(produceFormFormat(form));
        }
        return form;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }

    /**
     * Для этого формата не применяется
     *
     * @param newforamatName
     * @return
     */
    @Override
    public String createHeaderString(String newforamatName) {
        return "";
    }

    @Override
    protected FormFormat produceFormFormat(Form form) {
        return new FormFormat23(form);
    }

    @Override
    public File getXLSTemplate(int form) {
        File selfXLS = getXLSTemplateHeirarcy(form, XLSFolder, XLSPostfix);
        if (selfXLS.isFile() == false) {
            LoggingService.writeLog("cant find XLS tempale: " + form + " in format: " + this.getFormatName(),"debug");
            return super.getXLSTemplate(form);
        }
        return selfXLS;
    }

}
