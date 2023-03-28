package vedomosti.formats;

import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.common.FormParseException;

/**
 * Обобщение форм для формата 24 
 *
 * @author kneretin
 */
public class FormFormat24 extends FormFormat23 {

    public FormFormat24(Form form) {
        super(form);
    }

    public boolean isTotalString() {
        if (form.getKod().equals(Format24.TOTAL_PAYMENT_KOD)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String out = form.getType()
                + Format24.DELIMETR
                + form.getKod()
                + Format24.DELIMETR
                + form.getKbk()
                + Format24.DELIMETR;
        for (int i = 0; i < form.getSumms().length; i++) {
            out += bdToString(form.getSumms()[i]) + Format24.DELIMETR;
        }
        return out;
    }

    /**
     * Обобщенный метод считывания и разбора строки формы
     *
     * @param str
     * @throws vedomosti.common.FormParseException
     */
    @Override
    public void parse(String str) throws FormParseException {
        int summsIndex = 0;
        String[] parts = str.split("\\" + Format24.DELIMETR);
        for (int i = 0; i < form.getChunks().length; i++) {
            String part = "";
            if (i < parts.length) {
                if (parts[i] != null) {
                    part = parts[i];
                }
            }
            if (i == 0) {
                if (part.equals(form.getType()) == false) {
                    throw new FormParseException("Неправильный тип приложения, должен быть: " + form.getType() + " - а получен: " + part);
                }
            }
            if (i == 1) {
                form.setKod(part);
                form.setPrimaryKey(part);
            }
            if (i == 2) {
                form.setKbk(part);
            }
            if (i > 2) {
                //проверка на "-" в середине 
                boolean negative = false;
                if (part.contains("-")) {
                    part = part.replace("-", "");
                    negative = true;
                }
                if (part.trim().length() == 0) {
                    part = "0";
                }
                try {
                    BigDecimal dbValue = new BigDecimal(part);
                    if (negative) {
                        form.getSumms()[summsIndex] = dbValue.negate();
                    } else {
                        form.getSumms()[summsIndex] = new BigDecimal(part);
                    }
                } catch (NumberFormatException nfe) {
                    LoggingService.writeLog("number format exception in parse form summsIndex(" + summsIndex + ") :" + str,"debug");
                }
                summsIndex++;
            }
        }
    }
}
