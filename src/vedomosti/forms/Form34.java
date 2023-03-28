package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * НЕОПЛАТА PrilInd = 7 Form = 34
 *
 * @author kneretin
 */
public class Form34 extends Form {

    public Form34() {
        this.chunks = new int[]{8, 8, 23, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[24];
        this.totalSumPosition = 0;
        this.type = "НЕОПЛАТА";
        this.primaryKey = kod;
    }

}
