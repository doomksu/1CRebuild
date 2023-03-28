package vedomosti.forms.format24;

import vedomosti.common.Form;
import vedomosti.forms.Form32;

/**
 * НАЧИСЛЕНИЕ PrilInd = 1 Form = 32
 *
 * @author kneretin
 */
public class Form32_24 extends Form32 {

    public Form32_24() {
        Form model = new Form32();
        model.copyParams(this);
    }

}
