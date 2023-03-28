package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * НЕВКЛДОСТД PrilInd = 11 Form = 69
 *
 * @author kneretin
 */
public class Form65 extends Form {

    public Form65() {
        this.chunks = new int[]{10, 8, 23, 15, 15, 15};
        this.summs = new BigDecimal[3];
        this.totalSumPosition = 0;
        this.type = "НЕВКЛДОСТД";
        this.primaryKey = kod;
    }

}
