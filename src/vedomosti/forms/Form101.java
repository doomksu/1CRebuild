package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * АНАЛСВРЕЗ Form = 101
 *
 * @author kneretin
 */
public class Form101 extends Form {

    public Form101() {
        this.chunks = new int[]{9, 8, 23, 15, 15, 15, 15};
        this.summs = new BigDecimal[3];
        this.totalSumPosition = 0;
        this.type = "АНАЛСВРЕЗ";
        this.primaryKey = kod;
    }

}
