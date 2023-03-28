package vedomosti.forms.format24;

import java.math.BigDecimal;
import java.math.BigInteger;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.formats.Format24;
import vedomosti.forms.Form49;
import vedomosti.forms.Form49Interface;

/**
 * РЕЕСТРДОХ PrilInd = 7 Form = 49
 *
 * @author kneretin
 */
public class Form49_24 extends Form49 implements Form49Interface {

    public Form49_24() {
        Form model = new Form49();
        model.copyParams(this);
    }

    /**
     * Переопределен метод Form.parse
     *
     * @param str
     */
    @Override
    public void parse(String str) {
        int summsIndex = 0;
        String[] parts = str.split("\\" + Format24.DELIMETR);
        for (int i = 0; i < parts.length; i++) {
            String part = "";
            if (i < parts.length) {
                if (parts[i] != null) {
                    part = parts[i];
                }
            }
            if (i == 0) {
                if (part.equals(type) == false) {
                    LoggingService.writeLog("ERROR - wrong pril expected: " + type + "  - but read: " + part,"debug");
                }
            }
            if (i == 1) {
                this.kod = part;
            }
            if (i == 2) {
                this.debitorName = part;
                this.primaryKey = this.debitorName;
            }
            if (i >= 3) {
                BigDecimal res = null;
                boolean negative = false;
                if (part.contains("-")) {
                    part = part.replace("-", "");
                    negative = true;
                }
                if (part.isEmpty()) {
                    part = "0";
                }
                try {
                    res = new BigDecimal(part);
                    if (negative) {
                        summs[summsIndex] = res.negate();
                    } else {
                        summs[summsIndex] = res;
                    }
                } catch (NumberFormatException nfe) {
                    LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                }
                summsIndex++;
            }
        }
        for (int i = summsIndex; i < summs.length; i++) {
            summs[i] = new BigDecimal(BigInteger.ZERO);
        }
    }

}
