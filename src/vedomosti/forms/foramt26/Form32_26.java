package vedomosti.forms.foramt26;

import java.math.BigDecimal;
import vedomosti.forms.Form32;

/**
 * НАЧИСЛЕНИЕ PrilInd = 1 Form = 32
 * @author kneretin
 */
public class Form32_26 extends Form32 {

    public Form32_26() {
        super();
        this.chunks = new int[]{10, 8, 23, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[16];
        this.totalSumPosition = 15;
    }
}
