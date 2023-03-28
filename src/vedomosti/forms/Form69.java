package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * НЕПОЛУЧ PrilInd = 11 Form = 69
 *
 * @author kneretin
 */
public class Form69 extends Form {

    public Form69() {
        this.chunks = new int[]{7, 8, 23, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[7];
        this.totalSumPosition = 6;
        this.type = "НЕПОЛУЧ";
        this.primaryKey = kod;
    }

}
