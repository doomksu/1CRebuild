package vedomosti.formats;

import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.common.FormParseException;

/**
 * Обобщение форм для 23_1 формата
 *
 * @author kneretin
 */
public class FormFormat23 extends FormFormat {

    public FormFormat23(Form form) {
        this.form = form;
    }

    public boolean isTotalString() {
        if (form.getKod().equals(Format23.TOTAL_PAYMENT_KOD)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String out = form.getType() + form.getKod() + form.getKbk();
        for (int i = 0; i < form.getSumms().length; i++) {
            out += extendString(form.getSumms()[i], form.getChunks()[3 + i]);
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
        int start = 0;
        int summsIndex = 0;
        for (int i = 0; i < form.getChunks().length; i++) {
            int end = start + form.getChunks()[i];
            if (str.length() < end) {
                break;
            }
            String part = str.substring(start, end);
            if (i == 0) {
                if (part.equals(form.getType()) == false) {
                    throw new FormParseException("Неправильный тип приложения, должен быть: "
                            + form.getType() + " - а получен: " + part);
                }
            }
            if (i == 1) {
                form.setKod(part);
                form.setPrimaryKey(form.getKod());
            }
            if (i == 2) {
                form.setKbk(part);
            }
            if (i > 2) {//проверка на "-" в середине 
                boolean negative = false;
                if (part.contains("0-")) {
                    part = part.replace("-", "");
                    negative = true;
                }
                try {
                    BigDecimal dbValue = new BigDecimal(part);
                    if (negative) {
                        form.getSumms()[summsIndex] = dbValue.negate();
                        if ((extendString(form.getSumms()[summsIndex], 15)).equals(part) == false) {
                            LoggingService.writeLog("        not equal summs - read: " 
                                    + part + "\t parse: " 
                                    + (extendString(form.getSumms()[summsIndex], 15)),"debug");
                        }
                    } else {
                        form.getSumms()[summsIndex] = new BigDecimal(part);
                        if ((extendString(form.getSumms()[summsIndex], 15)).equals(part) == false) {
                            LoggingService.writeLog("        not equal summs - read: " 
                                    + part + "\t parse: " + (extendString(form.getSumms()[summsIndex], 15)),"debug");
                        }
                    }
                } catch (NumberFormatException nfe) {
                    LoggingService.writeLog("number format exception in parse form summsIndex(" + summsIndex + ") :" + str,"debug");
                }

                summsIndex++;
            }
            start = end;
        }
    }

}
