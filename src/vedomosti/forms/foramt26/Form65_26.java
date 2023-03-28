package vedomosti.forms.foramt26;

import java.math.BigDecimal;
import vedomosti.forms.Form65;

/**
 * 65 – тип «НЕВКЛДОСТД»
 *
 * @author kneretin
 */
public class Form65_26 extends Form65 {

    public Form65_26() {
        super();
        this.chunks = new int[]{10, 8, 23, 15, 15, 15, 15};
        this.summs = new BigDecimal[4];
    }

}
