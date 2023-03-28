package vedomosti.forms;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Описание требований к 30 форме
 *
 * @author kneretin
 */
public interface Form30Interface {

    final static String ATOM_totalKod = "01010000";
    final static String ATOM_subKod1 = "01010100";
    final static String ATOM_subKod2 = "01010200";
    
    final static String BIRTH_AND_RISE_TOTAL = "51020000";
    final static String BIRTH_AND_RISE_LOW_INCOME = "51020100";
    final static String BIRTH_AND_RISE_PREGNANT = "51020200";
    final static List<String> BIRTH_AND_RISE_CODES = Arrays.asList(BIRTH_AND_RISE_TOTAL, BIRTH_AND_RISE_LOW_INCOME,BIRTH_AND_RISE_PREGNANT);

    public String getKod();

    public BigDecimal[] getSumms();

    public boolean isTotalByDateString();

    public String getDate();

    public String getKbk();

    public int getPersonByDateCount();

    public void resetPrimaryKeyToKod();

    public boolean isTotalString();

    public BigDecimal getTotalSumm();

    public void resetPrimaryKeyToDate();

//    public void appendFromForm30(Form form);

}
