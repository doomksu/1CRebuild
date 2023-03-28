package vedomosti.forms.foramt26;

import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.formats.Format24;
import vedomosti.forms.format25.Form36_25;

/**
 * УДЕРЖАНИЕ PrilInd = 6 Form = 36
 *
 * @author kneretin
 */
public class Form36_26 extends Form36_25 {

    private String pensionINN;
    private boolean fake25Format = false;

    public Form36_26() {
        super();
        this.chunks = new int[]{9, 8, 23, 122, 200, 10, 12, 15, 15, 15, 2, 40, 25, 10, 25, 200, 122, 210, 25};
        this.summs = new BigDecimal[3];
    }

    /**
     * Обобщенный метод считывания и разбора строки формы
     *
     * @param str
     */
    public void parse(String str) {
        String[] parts = str.split("\\" + Format24.DELIMETR);
        for (int i = 0; i < parts.length; i++) {
            String part = "";
            if (i < parts.length) {
                if (parts[i] != null) {
                    part = parts[i];
                }
            }
            switch (i) {
                case 0:
                    if (part.equals(type) == false) {
                        LoggingService.writeLog("ERROR - wrong pril expected: " + type + "  - but read: " + part,"debug");
                    }
                    break;
                case 1:
                    this.kod = part;
                    this.primaryKey = this.kod;
                    break;
                case 2:
                    this.kbk = part;
                    break;
                case 3:
                    this.fio = part;
                    break;
                case 4:
                    this.postAdr = part;
                    break;
                case 5:
                    this.pensionNumber = part;
                    break;
                case 6:
                    this.pensionINN = part;
                case 7:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[0] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                    }
                    break;
                case 8:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[1] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                    }
                    break;
                case 9:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[2] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                    }
                    break;
                case 10:
                    this.keepingType = part;
//                    LoggingService.writeLog(">> set keepingType 36_26: "+ keepingType, "debug");
                    break;
                case 11:
                    this.keepingDocumentName = part;
                    break;
                case 12:
                    this.keepingDocumentNumber = part;
                    break;
                case 13:
                    this.keepingDocumentDate = part;
                    break;
                case 14:
                    this.getterAccount = part;
                    break;
                case 15:
                    this.getterAdr = part;
                    break;
                case 16:
                    this.getterFio = part;
                    break;
                case 17:
                    this.note = part;
                    break;
                case 18:
                    this.uin = part;
                    break;
            }
        }
    }

    /**
     * Переопределен метод получения строки формы 36
     *
     * @return
     */
    @Override
    public String toString() {
        String out = type + Format24.DELIMETR
                + kod
                + Format24.DELIMETR
                + (kbk == null ? "" : kbk)
                + Format24.DELIMETR
                + (fio == null ? "" : fio)
                + Format24.DELIMETR
                + (postAdr == null ? "" : postAdr)
                + Format24.DELIMETR
                + (pensionNumber == null ? "" : pensionNumber)
                + Format24.DELIMETR
                + (pensionINN == null ? "" : pensionINN)
                + Format24.DELIMETR;

        String temp = "";
        temp += bdToString(summs[0]) + Format24.DELIMETR;
        temp += bdToString(summs[1]) + Format24.DELIMETR;
        temp += bdToString(summs[2]) + Format24.DELIMETR;
        out += temp;
        out += (keepingType == null ? "" : keepingType) + Format24.DELIMETR
                + (keepingDocumentName == null ? "" : keepingDocumentName) + Format24.DELIMETR
                + (keepingDocumentNumber == null ? "" : keepingDocumentNumber) + Format24.DELIMETR
                + (keepingDocumentDate == null ? "" : keepingDocumentDate) + Format24.DELIMETR
                + (getterAccount == null ? "" : getterAccount) + Format24.DELIMETR
                + (getterAdr == null ? "" : getterAdr) + Format24.DELIMETR
                + (getterFio == null ? "" : getterFio) + Format24.DELIMETR
                + (note == null ? "" : note) + Format24.DELIMETR
                + (uin == null ? "" : uin) + Format24.DELIMETR;
        return out;
    }

    @Override
    public String formToString() {
        String str = "\r\n\t fio " + fio
                + "\r\n\t postAdr " + postAdr
                + "\r\n\t pensionNumber " + pensionNumber
                + "\r\n\t pensionINN " + pensionINN
                + "\r\n\t keepingType " + keepingType
                + "\r\n\t keepingDocumentName " + keepingDocumentName
                + "\r\n\t keepingDocumentNumber " + keepingDocumentNumber
                + "\r\n\t keepingDocumentDate " + keepingDocumentDate
                + "\r\n\t getterAccount " + getterAccount
                + "\r\n\t getterAdr " + getterAdr
                + "\r\n\t getterFio " + getterFio
                + "\r\n\t note " + note
                + "\r\n\t uin " + uin;
        return str;
    }

    public String getPensionINN() {
        return pensionINN;
    }
}
