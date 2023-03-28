package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * РЕЕСТРВОЗ Form = 63
 *
 * @author kneretin
 */
public class Form63 extends Form {

    public Form63() {
        this.chunks = new int[]{9, 8, 23, 122, 25, 10, 250, 15, 15};
        this.summs = new BigDecimal[3];
        this.totalSumPosition = 0;
        this.type = "РЕЕСТРВОЗ";
        this.primaryKey = kod;
    }

}
