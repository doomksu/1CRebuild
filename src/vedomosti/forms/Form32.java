package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;
import vedomosti.formats.FormFormat23;

/**
 * НАЧИСЛЕНИЕ PrilInd = 1 Form = 32
 *
 * @author kneretin
 */
public class Form32 extends Form {

    public Form32() {
        this.chunks = new int[]{10, 8, 23, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[14];
        this.totalSumPosition = 13;
        this.type = "НАЧИСЛЕНИЕ";
        this.primaryKey = kod;
        formFormat = new FormFormat23(this);
    }

}
