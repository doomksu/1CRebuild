package vedomosti.forms.foramt26;

import java.math.BigDecimal;
import vedomosti.forms.Form69;

/**
 *  69 – тип «НЕПОЛУЧ» 
 * @author kneretin
 */
public class Form69_26 extends Form69 {

    public Form69_26() {
        super();
        this.chunks = new int[]{7, 8, 23, 15, 15, 15, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[8];
         this.totalSumPosition = 7;
    }

}
