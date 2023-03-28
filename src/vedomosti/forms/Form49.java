package vedomosti.forms;

import java.math.BigDecimal;
import service.LoggingService;
import vedomosti.common.Form;

/**
 * PrilInd = 7 Form = 49 "РЕЕСТРДОХ"
 *
 * @author kneretin
 */
public class Form49 extends Form implements Form49Interface {

    protected String debitorName;

    public Form49() {
        this.chunks = new int[]{9, 2, 200, 15};
        this.summs = new BigDecimal[1];
        this.totalSumPosition = 0;
        this.type = "РЕЕСТРДОХ";
        this.primaryKey = kod;
    }

    /**
     * Переопределен метод Form.parse
     *
     * @param str
     */
    public void parse(String str) {
        int start = 0;
        int summsIndex = 0;
        for (int i = 0; i < chunks.length; i++) {
            int end = start + chunks[i];
            String part = str.substring(start, end);
            if (i == 0) {
                if (part.equals(type) == false) {
                    LoggingService.writeLog("ERROR - wrong pril expected: " + type + "  - but read: " + part,"debug");
                }
            }
            if (i == 1) {
                this.kod = part;
                this.primaryKey = this.kod;
            }
            if (i == 2) {
                this.debitorName = part;
            }
            if (i == 3) {
                try {
                    summs[summsIndex] = new BigDecimal(part);
                } catch (NumberFormatException nfe) {
                    LoggingService.writeLog("ERROR - try to get currency from: " + part,"debug");
                }
                summsIndex++;
            }
            start = end;
        }
    }

    @Override
    public String toString() {
        String out = type + kod + debitorName;
        out += formFormat.extendString(summs[0], 15);
        return out;
    }

    public String getDebitorName() {
        return debitorName;
    }

}
