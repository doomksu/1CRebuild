package vedomosti.forms.format24;

import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.common.FormParseException;
import vedomosti.formats.Format23;
import vedomosti.formats.Format24;
import vedomosti.forms.Form30;
import vedomosti.forms.Form30Interface;

/**
 * РЕЕСТРДОСТ PrilInd = 3 Form = 30
 *
 * @author kneretin
 */
public class Form30_24 extends Form30 implements Form30Interface {

//    private String date;
//    private int personByDateCount = 0;


    public boolean isSameKOD(Form30_24 form, boolean checkDate) {
        if (checkDate == true) {
            if (form.getDate().equals(date)) {
                return super.isSameKOD(form);
            } else {
                return false;
            }
        }
        return super.isSameKOD(form);
    }


    /**
     * Переопределен метод получения строки формы 30
     *
     * @return
     */
    @Override
    public String toString() {
        String out
                = (type == null ? "" : type) + Format24.DELIMETR
                + (date == null ? "" : date) + Format24.DELIMETR
                + (kod == null ? "" : kod) + Format24.DELIMETR
                + (kbk == null ? "" : kbk) + Format24.DELIMETR
                + personByDateCount + Format24.DELIMETR
                + bdToString(summs[0]) + Format24.DELIMETR;
//                + (summs[0] == null ? "0" : summs[0].toPlainString().replace(",", ".")) + Format24_1.DELIMETR;
        return out;
    }

    /**
     * Переопределен метод Form.parse Здесь уточняется дата доставки
     *
     * @param str
     * @throws vedomosti.common.FormParseException
     */
    public void parse(String str) throws FormParseException {
        int summsIndex = 0;
        String[] parts = str.split("\\" + Format24.DELIMETR);
        for (int i = 0; i < chunks.length; i++) {
            String part = parts[i];
            if (i == 0) {
                if (part.equals(type) == false) {
                    throw new FormParseException("Неправильный тип приложения, должен быть:  " + type + " а получен: " + part);
                }
            }
            if (i == 1) {
                this.date = part;
                this.primaryKey = this.date;
            }
            if (i == 2) {
                this.kod = part;
            }
            if (i == 3) {
                this.kbk = part;
            }
            if (i == 4) {
                try {
                    this.personByDateCount = Integer.parseInt(part);
                } catch (NumberFormatException nfex) {
                    LoggingService.writeLog("ERROR - cant parse count of person by date: " + part,"debug");
                }
            }
            if (i > 4) {
                boolean negative = false;
                if (part.contains("-")) {
                    part = part.replace("-", "");
                    negative = true;
                }
                if (part.trim().length() == 0) {
                    part = "0";
                }
                try {
                    if (negative) {
                        summs[summsIndex] = new BigDecimal(part).negate();
                    } else {
                        summs[summsIndex] = new BigDecimal(part);
                    }
                } catch (NumberFormatException nfe) {
                    LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                }
                summsIndex++;
            }
        }
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public int getPersonByDateCount() {
        return personByDateCount;
    }

    @Override
    public void resetPrimaryKeyToKod() {
        primaryKey = kod;
    }

    @Override
    public void resetPrimaryKeyToDate() {
        primaryKey = date;
    }

    @Override
    public boolean isSubtotalString() {
        return isTotalByDateString();
    }

    @Override
    public boolean isTotalString() {
        if (kod.equals(Format23.TOTAL_PAYMENT_KOD) == true && date.isEmpty() == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isTotalByDateString() {
        if (kod.equals(Format23.TOTAL_PAYMENT_KOD) == true && date.isEmpty() == false) {
            return true;
        }
        return false;
    }

//    @Override
//    public void appendFromForm30(Form form) {
//        Form30_24 form30 = (Form30_24) form;
//        for (int i = 0; i < summs.length; i++) {
//            if (form.getSumms()[i] != null) {
//                getSumms()[i] = getSumms()[i].add(form30.getSumms()[i]);
//            }
//            personByDateCount += form30.getPersonByDateCount();
//        }
//    }
}
