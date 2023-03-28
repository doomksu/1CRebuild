package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * ДОСТАВКА PrilInd = 2 Form = 35
 *
 * @author kneretin
 */
public class Form35 extends Form {

    public Form35() {
        this.chunks = new int[]{8, 8, 23, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[15];
        this.totalSumPosition = 0;
        this.type = "ДОСТАВКА";
        this.primaryKey = kod;
    }

}
