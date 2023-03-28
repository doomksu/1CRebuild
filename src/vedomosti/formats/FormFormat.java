package vedomosti.formats;

import java.math.BigDecimal;
import vedomosti.common.Form;
import vedomosti.common.FormParseException;

/**
 * Описание методов обработки форма по текущему формату
 *
 * @author kneretin
 */
public abstract class FormFormat {

    protected Form form;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public abstract boolean isTotalString();

    public abstract String toString();

    public abstract void parse(String str) throws FormParseException;

    public String bdToString(BigDecimal bd) {
        String result = "";
        if (bd != null) {
            if (bd.compareTo(BigDecimal.ZERO) != 0) {
                result = bd.toPlainString().replace(",", ".");
            }
        }
        return result;
    }

    /**
     * Растянуть строку под формат
     *
     * @param target
     * @param size
     * @return
     */
    public String extendString(String target, int size) {
        int currentSize = target.length();
        for (int i = currentSize; i < size; i++) {
            target += " ";
        }
        return target;
    }

    public String extendString(Integer target, int size) {
        String str = String.valueOf(target);
        int currentSize = str.length();
        for (int i = currentSize; i < size; i++) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * Растянуть строку с суммой под формат
     *
     * @param target
     * @param size
     * @return
     */
    public String extendString(BigDecimal target, int size) {
        if (target == null) {
            target = BigDecimal.valueOf(0.00);
            target.setScale(2);
//            LoggingService.writeLog("plain string from 0: " + target.toPlainString(),"debug");
        }
        String val = target.toPlainString();
        boolean negative = false;
        if (target.signum() == -1) {
            negative = true;
            val = val.replace("-", "");
            size -= 1;      //забирем 1 символ под "-"
        }

        for (int i = val.length(); i < size; i++) {
            val = "0" + val;
        }
        if (negative) {
            val = "-" + val;
        }
        return val;
    }
}
