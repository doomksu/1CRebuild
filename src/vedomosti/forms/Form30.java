package vedomosti.forms;

import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.common.FormParseException;
import vedomosti.formats.FormFormat23;
import vedomosti.formats.Format23;

/**
 * РЕЕСТРДОСТ PrilInd = 3 Form = 30
 *
 * @author kneretin
 */
public class Form30 extends Form implements Form30Interface {

    protected String date;
    protected int personByDateCount = 0;

    public Form30() {
        this.chunks = new int[]{10, 2, 8, 23, 15, 15};
        this.summs = new BigDecimal[1];
        this.totalSumPosition = 0;
        this.type = "РЕЕСТРДОСТ";
        this.primaryKey = date;
        this.isAppendable = true;
        formFormat = new FormFormat23(this);
    }

    public boolean isSameKOD(Form30 form, boolean checkDate) {
        if (checkDate == true) {
            if (form.getDate().equals(date)) {
                return super.isSameKOD(form);
            } else {
                return false;
            }
        }
        return super.isSameKOD(form);
    }

    public String getDescription() {
        return "day: " + date + " kbk: " + kbk + " count: " + personByDateCount + " summ: " + summs[0];
    }

    /**
     * Суммирование с другой 30 формой - реализация интерфейса
     *
     * @param form
     */
    @Override
    public void appendFromForm(Form form) {
//        LoggingService.writeLog("this: " + getDescription(), "debug");
        Form30 outer30 = (Form30) form;
//        LoggingService.writeLog("\tadd: " + outer30.getDescription(), "debug");

        Form30 form30 = (Form30) form;
        for (int i = 0; i < summs.length; i++) {
            if (form.getSumms()[i] != null) {
                getSumms()[i] = getSumms()[i].add(form30.getSumms()[i]);
            }
        }
        personByDateCount += form30.getPersonByDateCount();
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
    public boolean isTotalString() {
        if (kod.equals(Format23.TOTAL_PAYMENT_KOD) == true && date.equals("00") == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isTotalByDateString() {
        if (kod.equals(Format23.TOTAL_PAYMENT_KOD) == true && date.equals("00") == false) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSubtotalString() {
        return isTotalByDateString();
    }

    /**
     * Переопределен метод Form.parse Здесь уточняется дата доставки
     *
     * @param str
     * @throws vedomosti.common.FormParseException
     */
    @Override
    public void parse(String str) throws FormParseException {
        int start = 0;
        int summsIndex = 0;
        for (int i = 0; i < chunks.length; i++) {
            int end = start + chunks[i];
            String part = str.substring(start, end);
            if (i == 0) {
                if (part.equals(type) == false) {
                    LoggingService.writeLog("ERROR - wrong pril expected: " + type + "  - but read: " + part, "debug");
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
                    LoggingService.writeLog("ERROR - cant parse count of person by date: " + part, "debug");
                }
            }
            if (i > 4) {
                boolean negative = false;
                if (part.contains("0-")) {
                    part = part.replace("-", "");
                    negative = true;
                }
                try {
                    if (negative) {
                        summs[summsIndex] = new BigDecimal(part).negate();
                    } else {
                        summs[summsIndex] = new BigDecimal(part);
                    }
                } catch (NumberFormatException nfe) {
                    LoggingService.writeLog("ERROR - try to get currency from: " + part, "debug");
                }
                summsIndex++;
            }
            start = end;
        }
    }

    public void resetPrimaryKeyToDate() {
        primaryKey = date;
    }

    public void resetPrimaryKeyToKod() {
        primaryKey = kod;
    }

    @Override
    public boolean isSameKBK(Form form) {
        if (kbk == null && form.getKbk() == null) {
            return true;
        }
        if (form.getKbk().equals(kbk)) {
            if (kod.equals(ATOM_totalKod)
                    || kod.equals(ATOM_subKod1)
                    || kod.equals(ATOM_subKod2)) {
                return false;
            }

            if (BIRTH_AND_RISE_CODES.contains(form.getKod())) {
//                LoggingService.writeLog("this is one of BIRTH_AND_RISE: " + getDescription(), "debug");
//                LoggingService.writeLog("\ttry to add: "+ form.getDescription(), "debug");
                return kod.equals(form.getKod());
            }
            return true;
        }
        return false;
    }

}
