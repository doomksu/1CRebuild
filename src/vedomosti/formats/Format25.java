package vedomosti.formats;

import java.io.File;
import java.util.HashMap;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.forms.format25.Form36_25;

/**
 *
 * @author kneretin
 */
public class Format25 extends Format24 {

    public static String FORMAT_NAME = "25.";
    public static String DELIMETR = "|";
    static String XLSFolder = "\\_25";
    static String XLSPostfix = "_25";

    public Format25(String _header) {
        super(_header);
    }

    @Override
    public Form makeForm() {
        Form form = null;
        switch (formNumber) {
            case 36:
                form = new Form36_25();
                form.setFormFormat(produceFormFormat(form));
                break;
            default:
                return super.makeForm();
        }
        return form;
    }

    @Override
    protected FormFormat produceFormFormat(Form form) {
        return new FormFormat25(form);
    }

    @Override
    protected HashMap<String, String> getValues(byte type, String str) {
        return super.getValues(type, str);
    }

    @Override
    public File getXLSTemplate(int form) {
        File selfXLS = getXLSTemplateHeirarcy(form, XLSFolder, XLSPostfix);
        if (selfXLS == null || selfXLS.isFile() == false) {
            LoggingService.writeLog("cant find form: " + form + " in format: " + this.getFormatName(),"debug");
            return super.getXLSTemplate(form);
        }
        return selfXLS;
    }
}
