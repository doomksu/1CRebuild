package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * ПЕРЕПСЛЗАН PrilInd = ? Form = 59
 *
 * @author kneretin
 */
public class Form59 extends Form {

    public Form59() {
        this.chunks = new int[]{10, 8, 23, 15, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[8];
        this.totalSumPosition = 0;
        this.type = "ПЕРЕПСЛЗАН";
        this.primaryKey = kod;
    }

}
