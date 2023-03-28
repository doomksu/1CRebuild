package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * НАСЛЕДДОСТ PrilInd = 13 Form = 73
 *
 * @author kneretin
 */
public class Form73 extends Form {

    public Form73() {
        this.chunks = new int[]{10, 8, 23, 15, 15, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[9];
        this.totalSumPosition = 0;
        this.type = "НАСЛЕДДОСТ";
        this.primaryKey = kod;
    }

}
