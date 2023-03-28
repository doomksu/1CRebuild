package vedomosti.forms;

import java.math.BigDecimal;

/**
 * Описание требований к 49 форме
 *
 * @author kneretin
 */
public interface Form36Interface {

    public String getKod();

    public BigDecimal[] getSumms();

    public String getFio();

    public String getPostAdr();

    public String getPensionNumber();

    public String getKeepingType();

    public String getKeepingDocumentName();

    public String getKeepingDocumentNumber();

    public String getKeepingDocumentDate();

    public String getGetterAccount();

    public String getGetterAdr();

    public String getGetterFio();

    public String getNote();

    public String getUin();

    public String getKbk();

    public String getKeepingKod();

    public String getInn();

    public boolean isFake25Format();

    public String formToString();

}
