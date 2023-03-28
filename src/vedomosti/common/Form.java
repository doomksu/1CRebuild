package vedomosti.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import vedomosti.formats.FormFormat;

/**
 *
 * @author kneretin
 */
public abstract class Form implements Serializable {

    protected final String totalPaymentKod = "00000000";

    protected boolean isAppendable = true;
    protected String type;
    /**
     * Массив сумм по строке
     */
    protected BigDecimal[] summs;
    protected int totalSumPosition;
    protected int[] chunks;
    protected String kod;
    protected String kbk;
    protected String primaryKey; //ключ по которому формы сравниваются для суммирования полей при объединении в своде
    protected String aditionalKey = " noAddKey ";
    protected boolean isAditionalKey = false; //Дополнительный ключ для проверки объединения
    protected FormFormat formFormat;

    /**
     * Скопировать парметры из этой формы во входящую
     *
     * @param form
     */
    public void copyParams(Form form) {
        form.chunks = this.chunks;
        form.isAppendable = this.isAppendable;
        form.kbk = this.kbk;
        form.kod = this.kod;
        form.primaryKey = this.primaryKey;
        form.totalSumPosition = this.totalSumPosition;
        form.type = this.type;
        form.summs = this.summs;
    }

    /**
     * Суммируем эту строку с входящей
     *
     * @param form
     */
    public void appendFromForm(Form form) {
        for (int i = 0; i < summs.length; i++) {
            if (form.summs[i] != null && summs[i] != null) {
                summs[i] = summs[i].add(form.summs[i]);
            } else {
                if (form.summs[i] == null) {
                    form.summs[i] = new BigDecimal(BigInteger.ZERO);
                }
                if (summs[i] == null) {
                    summs[i] = new BigDecimal(BigInteger.ZERO);
                }
            }
        }
    }

    /**
     * Проверка соввпадения кода этой формы и входящей
     *
     * @param form
     * @return
     */
    public boolean isSameKOD(Form form) {
        if (form.kod.equals(kod)) {
            return true;
        }
        return false;
    }

    public boolean isSameKBK(Form form) {
        if (kbk == null && form.kbk == null) {
            return true;
        }
        if (form.kbk.equals(kbk)) {
            return true;
        }
        return false;
    }

    public boolean isTotalString() {
        if (kod.equals(totalPaymentKod)) {
            return true;
        }
        return false;
    }

    /**
     * Определить строку формы заполненную только нулями
     *
     * @return
     */
    public boolean isAllNull() {
        for (BigDecimal summ : summs) {
            if (summ != null) {
                if (summ.compareTo(BigDecimal.ZERO) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * У большинства форм нет дополнительных ключей. Стандартное поведение для
     * общего случая
     *
     * @return
     */
    public boolean hasAdditionalKey() {
        return !isTotalString();
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public void setKbk(String kbk) {
        this.kbk = kbk;
    }

    public String getKod() {
        return kod;
    }

    public String getKbk() {
        return kbk;
    }

    public String getType() {
        return type;
    }

    /**
     * Возвращает
     *
     * @return
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isAppendable() {
        return isAppendable;
    }

    public String getAdditionalKey() {
        return aditionalKey;
    }

    public BigDecimal[] getSumms() {
        return summs;
    }

    public int[] getChunks() {
        return chunks;
    }

    public BigDecimal getTotalSumm() {
        return summs[totalSumPosition];
    }

    public void parse(String str) throws FormParseException {
        formFormat.parse(str);
    }

    public String toString() {
        return formFormat.toString();
    }

    public void setFormFormat(FormFormat ff) {
        this.formFormat = ff;
    }

    /**
     * Является ли строка подитогом по кодам выплат для описанной организации.
     * Для большинства приложений таких строк нет. Переопределяется приложениями
     * в которых такие строки есть
     *
     * @return
     */
    public boolean isSubtotalString() {
        return false;
    }

    public String bdToString(BigDecimal bd) {
        String result = "";
        if (bd != null) {
            if (bd.compareTo(BigDecimal.ZERO) != 0) {
                result = bd.toPlainString().replace(",", ".");
            }
        }
        return result;
    }
    public String getDescription() {
        return "description";
    }

}
