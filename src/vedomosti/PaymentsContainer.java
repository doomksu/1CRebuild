package vedomosti;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import service.LoggingService;
import vedomosti.common.Form;
import vedomosti.forms.Form30;
import vedomosti.forms.Form30Interface;

/**
 * У каждой формы в строке есть код и КБК (для 36 формы они могут повторяться)
 *
 * @author kneretin
 */
class PaymentsContainer implements Serializable {

    private TreeMap<String, ArrayList<Form>> payments;
    private Form totalRow;
    private boolean isAppendableContainer;  //суммируется ли этот контейнер

    public PaymentsContainer() {
        payments = new TreeMap<>();
        isAppendableContainer = true;
    }

    /**
     * Добавить строку ведомости к сохраненным
     *
     * @param kod
     * @param form
     */
    public void addPayment(String kod, Form form) {
        if (payments.containsKey(kod) == false) {
            payments.put(kod, new ArrayList<>());
        }
        payments.get(kod).add(form);

        if (form.isTotalString()) {
            if (this.totalRow == null) {//один раз добавляем итоговую строку
                this.totalRow = form;
            } else {
                this.totalRow.appendFromForm(form);
            }
        }
        //проверям что добавляется форма суммируемая
        if (isAppendableContainer == true && form.isAppendable() == false) {
            isAppendableContainer = false;
        }
    }

    /**
     * Объединить с другим контейнером -добавлено отличае для 30 формы - в
     * идеале требуется рефакторинг и переработка объединения, отделение логики
     * конкретного приложения от выплатного контейнера
     *
     * @param container
     */
    public void appendOrgPayments(PaymentsContainer container) {
        for (String kod : container.payments.keySet()) {
            if (payments.containsKey(kod) == false) {
                //у нас такой выплаты нет - записываем
                payments.put(kod, container.payments.get(kod));
            } else {
                if (payments.get(kod).get(0).isAppendable()) {
                    // у форм которые допускают сложение в массиве должна быть 1 запись 
                    // - кроме 30 - там по коду-дате строка с кодом выплаты и итог - минимум 2 строки
                    //полагаю что 30  суммируются по датам 
                    if (payments.get(kod).get(0) instanceof Form30Interface) {
                        for (Form outerForm : container.payments.get(kod)) {
                            Form30 outerForm30 = (Form30) outerForm;
//                            LoggingService.writeLog(" - will append payment by day: "
//                                    + outerForm30.getDate()
//                                    + " kod: " + outerForm.getKod()
//                                    + " total: " + outerForm.getTotalSumm(), "debug");

                            boolean found = false;
                            for (Form innerForm : payments.get(kod)) {
                                Form30 innerForm30 = (Form30) innerForm;
                                if (outerForm.isSameKBK(innerForm) && outerForm30.getDate().equals(innerForm30.getDate())) {
                                    innerForm.appendFromForm(outerForm);
                                    found = true;
                                }
                            }
                            if (found == false) {//если не нашли внутри даты по КБК то добавляем и суммируем в строку итога
                                payments.get(kod).add(outerForm);
                            }
                        }
                    } else {
                        LoggingService.writeLog("not any type of 30Interface", "debug");
                        payments.get(kod).get(0).appendFromForm(container.payments.get(kod).get(0));
                    }

                } else {
                    //у форм с несколькими записями по одному КБК (как у 36 пофамильной формы) не суммируем строки а добавляем 
                    for (Form _form : container.payments.get(kod)) {
                        addPayment(kod, _form);
                    }
                }
            }
        }
    }

    /**
     * Тестовый метод записи информации по выплатам в консоль
     */
    public void sout() {
        String total = "";
        for (String kod : payments.keySet()) {
            for (Form form : payments.get(kod)) {
                if (form.isTotalString()) {
                    total = form.toString();
                } else {
                    LoggingService.writeLog(form.toString(), "debug");
                }
            }
        }
        LoggingService.writeLog(total, "debug");
    }

    public String getPaymentsContainerDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (String string : payments.keySet()) {
            sb.append(!first ? ", " : "").append(string);
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Получить строки для текстового свода по организации
     *
     * @return
     */
    Collection<String> getStrings() {
        ArrayList<String> strs = new ArrayList<>();
        String total = "";
        for (String kod : payments.keySet()) {
            Form subtotalForm = null;
            for (Form form : payments.get(kod)) {
                //Форма может быть итоговой по организации  - откладываем пишем после всех строк организации
                if (form.isTotalString()) {
                    total = form.toString() + "\n";
                } else {
                    //Форма может быть итоговой по отдельному коду в организации (для некоторых приложений) - откладываем пишем после всех строк по этому коду
                    if (form.isSubtotalString()) {
                        subtotalForm = form;
                    } else {
                        strs.add(form.toString() + "\n");
                    }
                }
            }
            if (subtotalForm != null) {
                strs.add(subtotalForm.toString() + "\n");
            }
        }
        strs.add(total);
        return strs;
    }

    /**
     *
     * Возвращает итератор по ключам выплат
     *
     * @return
     */
    public Iterator<String> getPaymentCodsIterator() {
        return this.payments.keySet().iterator();
    }

    /**
     * Возвращает ArrayList форм по ключу - коду. После внутреннего объединения
     * по организации, по каждому коду должна быть только одна запись выплаты
     *
     * @param kod
     * @return
     */
    public ArrayList<Form> getPaymentsByCod(String kod) {
        return this.payments.get(kod);
    }

    public int getSize() {
        int size = 0;
        for (String string : payments.keySet()) {
            size += payments.get(string).size();
        }
        return size;
    }

    /**
     * Получить количество кодов - разных видов выплат в контейнере организации
     *
     * @return
     */
    public int getKodsSize() {
        return payments.keySet().size();
    }

    /**
     * Получить итоговую строку выплат
     *
     * @return
     */
    public Form getTotalString() {
        for (String kod : payments.keySet()) {
            for (Form form : payments.get(kod)) {
                if (form.isTotalString()) {
//                    LoggingService.writeLog("Container total string: " + form.getDescription(), "debug");
                    return form;
                }
            }
        }
        return null;
    }

    /**
     * Получить сумму характеризующую организацию (индивидуально для разных
     * форм)
     *
     * @return
     */
    public BigDecimal getTotalSumm() {
        if (getTotalString() == null) {
            LoggingService.writeLog(">>getTotalString() will return null; payments.keySet().size(): " + payments.keySet().size(), "debug");
        }
        boolean hasTotalString = false;
        for (String kod : payments.keySet()) {
            for (Form form : payments.get(kod)) {
                if (form.isTotalString()) {
                    hasTotalString = true;
                }
            }
        }
        if (getTotalString() != null) {
            return getTotalString().getTotalSumm();
        }
        LoggingService.writeLog(">>hasTotalString: " + hasTotalString, "debug");
        return null;
    }

}
