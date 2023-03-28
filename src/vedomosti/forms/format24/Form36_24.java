package vedomosti.forms.format24;

import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.formats.Format24;
import vedomosti.forms.Form36;
import vedomosti.forms.Form36Interface;

/**
 * УДЕРЖАНИЕ PrilInd = 6 Form = 36
 *
 * @author kneretin
 */
public class Form36_24 extends Form36 implements Form36Interface {

    protected String fio;
    protected String postAdr;
    protected String pensionNumber;
    protected String keepingType;
    protected String keepingDocumentName;
    protected String keepingDocumentNumber;
    protected String keepingDocumentDate;
    protected String getterAccount;
    protected String getterAdr;
    protected String getterFio;
    protected String note;
    protected String uin;

    public Form36_24() {
        Form model = new Form36();
        model.copyParams(this);
        this.isAditionalKey = true;
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
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[0] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                    }
                    break;
                case 7:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[1] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                    }
                    break;
                case 8:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[2] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                    }
                    break;
                case 9:
                    this.keepingType = part;
                    break;
                case 10:
                    this.keepingDocumentName = part;
                    break;
                case 11:
                    this.keepingDocumentNumber = part;
                    break;
                case 12:
                    this.keepingDocumentDate = part;
                    break;
                case 13:
                    this.getterAccount = part;
                    break;
                case 14:
                    this.getterAdr = part;
                    break;
                case 15:
                    this.getterFio = part;
                    break;
                case 16:
                    this.note = part;
                    break;
                case 17:
                    this.uin = part;
                    break;
            }
        }
    }

    public String getFio() {
        return fio;
    }

    public String getPostAdr() {
        return postAdr;
    }

    public String getPensionNumber() {
        return pensionNumber;
    }

    public String getKeepingType() {
        return keepingType;
    }

    public String getKeepingDocumentName() {
        return keepingDocumentName;
    }

    public String getKeepingDocumentNumber() {
        return keepingDocumentNumber;
    }

    public String getKeepingDocumentDate() {
        return keepingDocumentDate;
    }

    public String getGetterAccount() {
        return getterAccount;
    }

    public String getGetterAdr() {
        return getterAdr;
    }

    public String getGetterFio() {
        return getterFio;
    }

    public String getNote() {
        return note;
    }

    public String getUin() {
        return uin;
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
    public String getKeepingKod() {
        LoggingService.writeLog(">> ask " + guid + " keepingType 36_24: " + keepingType, "debug");
        return keepingType;
    }

    /**
     * Поле не используется в старом формате
     *
     * @return
     */
    @Override
    public String getInn() {
        return "";
    }

    @Override
    public boolean isFake25Format() {
        return true;
    }

    @Override
    public String formToString() {
        String str = "\r\n\t fio " + fio
                + "\r\n\t postAdr " + postAdr
                + "\r\n\t pensionNumber " + pensionNumber
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
}
