package vedomosti.forms;

import java.math.BigDecimal;
import java.util.UUID;
import service.LoggingService;
import settings.SettingsReader;
import vedomosti.common.Form;

/**
 * УДЕРЖАНИЕ PrilInd = 6 Form = 36
 *
 * @author kneretin
 */
public class Form36 extends Form implements Form36Interface {

    private String fio;
    private String postAdr;
    private String pensionNumber;
    private String keepingType;
    private String keepingDocumentName;
    private String keepingDocumentNumber;
    private String keepingDocumentDate;
    private String getterAccount;
    private String getterAdr;
    private String getterFio;
    private String note;
    private String uin;
    public static String PERSONAL_PAYMENTS_KEY = "personal";
    public static String ORG_PAYMENTS_KEY = "org";
    public static String SPLIT36 = "SPLIT36";
    protected String guid;

    public Form36() {
        guid = UUID.randomUUID().toString();
        this.chunks = new int[]{9, 8, 23, 122, 200, 10, 15, 15, 15, 2, 40, 20, 10, 25, 200, 122, 210, 25};
        this.summs = new BigDecimal[3];
        this.totalSumPosition = 0;
        this.type = "УДЕРЖАНИЕ";
        this.primaryKey = kod;
        this.isAppendable = false;
        this.isAditionalKey = true;
    }

    /**
     * Обобщенный метод считывания и разбора строки формы
     *
     * @param str
     */
    @Override
    public void parse(String str) {
        int start = 0;
        for (int i = 0; i < chunks.length; i++) {
            int end = start + chunks[i];
            if (str.length() < end) {
                break;
            }
            String part = str.substring(start, end);
            switch (i) {
                case 0:
                    if (part.equals(type) == false) {
                        LoggingService.writeLog("ERROR - wrong pril expected: " + type + "  - but read: " + part, "debug");
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
                        summs[0] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part, "debug");
                    }
                    break;
                case 7:
                    try {
                        summs[1] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part, "debug");
                    }
                    break;
                case 8:
                    try {
                        summs[2] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part, "debug");
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
            start = end;
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
        String out = type
                + kod
                + kbk
                + fio
                + postAdr
                + pensionNumber
                + formFormat.extendString(summs[0], 15)
                + formFormat.extendString(summs[1], 15)
                + formFormat.extendString(summs[2], 15)
                + keepingType
                + keepingDocumentName
                + keepingDocumentNumber
                + keepingDocumentDate
                + getterAccount
                + getterAdr
                + getterFio
                + note
                + uin;
        return out;
    }

    @Override
    public String getKeepingKod() {
        return keepingType;
    }

    @Override
    public boolean hasAdditionalKey() {
        return !isTotalString();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAdditionalKey() {
        String resp = ORG_PAYMENTS_KEY;
        if (getKeepingType() != null && (getKeepingType().trim().equals("1") || getKeepingType().trim().equals("2"))) {
            resp = PERSONAL_PAYMENTS_KEY;
        }
        Boolean doesSplitByAccount = false;
        boolean val = Boolean.valueOf(SettingsReader.getInstance().getValue("split36byAccount"));

        if (val) {
            resp += SPLIT36;
        }
//        LoggingService.writeLog(">>form36  " + guid + "  getAdditionalKey: " + resp + " from  keepingType: " + keepingType, "debug");
        return resp;
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
        //В этом формате не поодерживается 
        return false;
    }

    @Override
    public String formToString() {
        return "form36 formToString()";
    }

}
