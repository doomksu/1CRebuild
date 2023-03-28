package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * ПЕРЕПЛАТА PrilInd = 4 Form = 46
 *
 * @author kneretin
 */
public class Form46 extends Form {

    public Form46() {
        this.chunks = new int[]{9, 8, 23, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[5];
        this.totalSumPosition = 0;
        this.type = "ПЕРЕПЛАТА";
        this.primaryKey = kod;
    }
}
