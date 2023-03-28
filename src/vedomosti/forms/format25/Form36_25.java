package vedomosti.forms.format25;

import vedomosti.forms.format24.Form36_24;
import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.formats.Format24;
import vedomosti.forms.Form36;

/**
 * УДЕРЖАНИЕ PrilInd = 6 Form = 36
 *
 * В 25 формате добавлено поле ИНН форма наследует методы обработки от формы 36
 * формата 24
 *
 * @author kneretin
 */
public class Form36_25 extends Form36_24 {

    private String inn;
    private boolean fake25Format = false;

    public Form36_25() {
        Form model = new Form36();
        model.copyParams(this);
        this.isAditionalKey = true;
        this.inn = "";
    }

    public String getInn() {
        return inn;
    }

    public void parse(String str) {
        int partsPices = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.codePointAt(i) == Format24.DELIMETR.codePointAt(0)) {
                partsPices++;
            }
        }
        String[] parts = str.split("\\" + Format24.DELIMETR);
        if (partsPices != 19) {//здесь считаем что файл приведен к формату 25 заменой - на самом деле это 24 формат
            fake25Format = true;
            super.parse(str);
            return;
        } else {
            this.parse25(str);
            return;
        }
//        LoggingService.writeLog("found diff length: " + parts.length + " -- " + str,"debug");
    }

    public void parse25(String str) {
        String[] parts = str.split("\\" + Format24.DELIMETR);
//        LoggingService.writeLog("format 25 count parts: " + parts.length,"debug");
        //определяем итоговую строку - она не содержит ИНН ее разбираем как формат 24
        //в прошлых версиях НВП итоговая строка не содержала ИНН  - сейчас это счтается неправильным и 
        //она его содержит - закоментировал потому что возможен откат и ИНН могут снова убрать
//        if (parts[1].equals(Format24_1.TOTAL_PAYMENT_KOD)) {
//            super.parse(str);
//        } else {
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
                    this.inn = part;
                    break;
                case 7:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[0] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part, "debug");
                    }
                    break;
                case 8:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[1] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part, "debug");
                    }
                    break;
                case 9:
                    try {
                        if (part.trim().length() == 0) {
                            part = "0";
                        }
                        summs[2] = new BigDecimal(part);
                    } catch (NumberFormatException nfe) {
                        LoggingService.writeLog("ERROR - try to get currency from: " + part, "debug");
                    }
                    break;
                case 10:
                    this.keepingType = part;
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
        if (isFake25Format() == false) {
            out += (inn == null ? "" : inn) + Format24.DELIMETR;
        }
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
        LoggingService.writeLog(">> ask " + guid + " keepingType 36_25: " + keepingType, "debug");
        return keepingType;
    }

    @Override
    public boolean isFake25Format() {
        return fake25Format;
    }

}
