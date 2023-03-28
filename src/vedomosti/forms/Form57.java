package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * РЕЕСТРПЕР PrilInd = 9 Form = 57
 *
 * @author kneretin
 */
public class Form57 extends Form {

    public Form57() {
        this.chunks = new int[]{9, 8, 23, 15, 15, 15};
        this.summs = new BigDecimal[3];
        this.totalSumPosition = 0;
        this.type = "РЕЕСТРПЕР";
        this.primaryKey = kod;
    }

}
