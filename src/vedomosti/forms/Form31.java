package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * РЕЕСТРОТОЗ Form = 31
 *
 * @author kneretin
 */
public class Form31 extends Form {

    public Form31() {
        this.chunks = new int[]{10, 8, 23, 15, 15};
        this.summs = new BigDecimal[3];
        this.totalSumPosition = 0;
        this.type = "РЕЕСТРОТОЗ";
        this.primaryKey = kod;
    }

}
