package vedomosti;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import service.LoggingService;
import vedomosti.common.Format;
import vedomosti.common.Form;
import vedomosti.common.FormParseException;
import static vedomosti.forms.Form36.SPLIT36;

/**
 * Класс описывающий выплатную организацию и обрабатывающий проходящие через нее
 * выплаты
 *
 * @author kneretin
 */
public class Organization extends Form implements Serializable {

    private final String header;
    private final Format format;
    private PaymentsContainer payments;
    private HashMap<String, String> params;
    private String[] keys = new String[4];
    private String combAdditionalKey; //расчитанный ключ из суммы ключа организации и ключа формы
    private final int prilNumber;
    private final String LOCAL_KEY = "local";

    Organization(String string, Format _format, int prilNumber) {
        format = _format;
        header = string;
        params = format.getOrgValues(string);
        payments = new PaymentsContainer();
        this.prilNumber = prilNumber;
        keys[0] = getUnInnKpp();
        keys[1] = getForeignKey();
    }

    public void parseFormNew(Form form, int lineNumber) throws FormParseException {
//        LoggingService.writeLog(">>parseFormNew check add key - this.keys[2]:  "
//                + this.keys[2] + " form.hasAdditionalKey(): "
//                + form.hasAdditionalKey() 
//                +": "+ form.getAdditionalKey(), "debug");
        if (this.keys[2] == null && form.hasAdditionalKey()) {
            this.keys[2] = form.getAdditionalKey();
        }
        if (form.getAdditionalKey().contains(SPLIT36)) {
            this.keys[3] += params.get("OrgPolBank");
        }
        payments.addPayment(String.valueOf(form.getPrimaryKey()), form);
    }

    public String getUnInnKpp() {
        String s = "";
        if (params.containsKey("OrgDostINN")) {
            s += params.get("OrgDostINN");
        }
        if (params.containsKey("OrgDostKPP")) {
            s += params.get("OrgDostKPP");
        }
        if (params.containsKey("OrgPolINN")) {
            s += params.get("OrgPolINN");
        }
        if (params.containsKey("OrgPoltKPP")) {
            s += params.get("OrgPoltKPP");
        }
        if (prilNumber == 36) {
            s += params.get("KODDohBK");
        }
        return s;
    }

    public String getComplexKey() {
        String key = "";
        for (String subKey : keys) {
            key += (key.isEmpty() ? "" : "-") + subKey;
        }
        return key;
    }

    public String getFileKey() {
        String key = "";
        for (int i = 1; i < keys.length; i++) {
            key += keys[i] + " ";
        }
        return key;
    }

    public String getForeignKey() {
        if (getParams().containsKey("foregnKey")) {
            if (getParams().get("foregnKey") == null) {
                return LOCAL_KEY;
            } else {
                return getParams().get("foregnKey");
            }
        }
        return LOCAL_KEY;
    }

    public void consumeOrganization(Organization org) {
//        LoggingService.writeLog("consumeOrganization", "debug");
//        LoggingService.writeLog("core: " + getOrganizationDescription(), "debug");
//        LoggingService.writeLog("will consume: " + org.getOrganizationDescription(), "debug");
        payments.appendOrgPayments(org.payments);
    }

    public String getOrganizationDescription() {
        StringBuilder descr = new StringBuilder();
        descr.append(this.getName()
                + " payments: " + payments.getSize()
                + " amount: "
                + (payments.getTotalSumm() == null ? "no total amount string" : payments.getTotalSumm().toPlainString()));
        descr.append(" payment kods: \r\n");
        descr.append(payments.getPaymentsContainerDescription());
        return descr.toString();
    }

    /**
     * Перераспределить выплаты по новому ключу
     */
    public void reorgPaymentContainer() {
        PaymentsContainer pc = new PaymentsContainer();
        Iterator<String> kodsIt = getPaymentCodsIterator();
        while (kodsIt.hasNext()) {
            String kod = kodsIt.next();
            for (Form form : getPaymentsByCod(kod)) {
                pc.addPayment(form.getPrimaryKey(), form);
            }
        }
        payments = pc;
    }

    boolean isSameAdditionalKey(Organization outOrg) {
        if (combAdditionalKey != null && outOrg.combAdditionalKey != null) {
            if (combAdditionalKey.equals(outOrg.combAdditionalKey) == false) {
                LoggingService.writeLog("        different keys own: " + combAdditionalKey + "  out: " + outOrg.combAdditionalKey, "debug");
                return false;
            } else {
                LoggingService.writeLog("        same additional keys: " + combAdditionalKey, "debug");
                return false;

            }
        }
        LoggingService.writeLog("do not check keys", "debug");
        return true;
    }

    @Override
    public BigDecimal getTotalSumm() {
        LoggingService.writeLog("getTotalSumm(): " + getTotalSumm(), "debug");
        return getTotalString().getTotalSumm();
    }

    /**
     * Не используется здесь
     *
     * @param str
     * @throws FormParseException
     */
    @Override
    public void parse(String str) throws FormParseException {

    }

    /**
     * Проверить пустая ли организация. Не содержит выплат
     *
     * @return
     */
    boolean isEmptyOrg() {
        boolean ret = (getPaymentsCount() == 0);
        return ret;
    }

    public HashMap<String, String> getParams() {
        return format.getOrgValues(header);
    }

    /**
     * Получить количество выплат в PaymentsContainer
     *
     * @return
     */
    public int getPaymentsCount() {
        return payments.getSize();
    }

    /**
     * Получить количество кодов выплат в PaymentsContainer
     *
     * @return
     */
    public int getPaymentsKodsCount() {
        return payments.getKodsSize();
    }

    /**
     *
     * Возвращает итератор по ключам выплат
     *
     * @return
     */
    public Iterator<String> getPaymentCodsIterator() {
        return this.payments.getPaymentCodsIterator();
    }

    /**
     * Возвращает ArrayList форм по ключу - коду. После внутреннего объединения
     * по организации, по каждому коду должна быть только одна запись выплаты
     *
     * @param kod
     * @return
     */
    public ArrayList<Form> getPaymentsByCod(String kod) {
        return this.payments.getPaymentsByCod(kod);
    }

    /**
     * Получить форму последней строки с итогами
     *
     * @return
     */
    public Form getTotalString() {
        LoggingService.writeLog("Organization: getTotalString", "debug");
        if (payments.getTotalString() == null) {
            return null;
        }
        return payments.getTotalString();
    }

    public String getName() {
        if (params.get("BankName") == null) {
            return "";
        }
        return params.get("BankName");
    }

    /**
     * Получить ключ для разбиения форм в этом выплатном контейнере(организации)
     *
     * @return
     */
    @Override
    public String getAdditionalKey() {
        LoggingService.writeLog(">> ask for org add key", "debug");
        return combAdditionalKey;
    }

    Iterable<String> getStrings() {
        ArrayList<String> strs = new ArrayList<>();
        strs.add(header + "\n");
        strs.addAll(payments.getStrings());
        return strs;
    }

    @Override
    public String toString() {
        String str = "org: \r\n";
        for (String key : params.keySet()) {
            str += " key: " + key + " - value: " + params.get(key) + "\r\n";
        }
        return str;
    }

    public boolean isAcceptForm(Form form) {
        if (form.hasAdditionalKey()) {
            if (keys[2] != null) {
                if (keys[2].equals(form.getAdditionalKey()) == false) {
                    return false;
                }
            }
        }
        return true;
    }

}
