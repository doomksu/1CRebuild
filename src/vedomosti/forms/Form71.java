package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * НАСЛЕДВЫП PrilInd = 12 Form = 71
 *
 * @author kneretin
 */
public class Form71 extends Form {

    public Form71() {
        this.chunks = new int[]{9, 8, 23, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[7];
        this.totalSumPosition = 0;
        this.type = "НАСЛЕДВЫП";
        this.primaryKey = kod;
    }

}
