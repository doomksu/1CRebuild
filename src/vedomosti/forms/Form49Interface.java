package vedomosti.forms;

import java.math.BigDecimal;


/**
 * Описание требований к 49 форме
 *
 * @author kneretin
 */
public interface Form49Interface{

    public String getDebitorName();
    public String getKod();
    public BigDecimal[] getSumms();
    
}
