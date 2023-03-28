package vedomosti.forms;

import java.math.BigDecimal;
import vedomosti.common.Form;

/**
 * ПЕРЕППОГАШ PrilInd = 5 Form = 51
 *
 * @author kneretin
 */
public class Form77 extends Form {

    public Form77() {
        this.chunks = new int[]{9, 8, 23, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[5];
        this.totalSumPosition = 0;
        this.type = "ПЕРЕПБАНК";
        this.primaryKey = kod;
    }

}
