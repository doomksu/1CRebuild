package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * ПЕРЕППОГАШ PrilInd = 5 Form = 51
 *
 * @author kneretin
 */
public class Form51 extends Form {

    public Form51() {
        this.chunks = new int[]{10, 8, 23, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[7];
        this.totalSumPosition = 0;
        this.type = "ПЕРЕППОГАШ";
        this.primaryKey = kod;
    }

}
