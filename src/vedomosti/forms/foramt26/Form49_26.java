package vedomosti.forms.foramt26;

import java.math.BigDecimal;
import vedomosti.formats.Format26;
import vedomosti.forms.format24.Form49_24;

/**
 * PrilInd = 7 Form = 49 "РЕЕСТРДОХ"
 *
 * @author kneretin
 */
public class Form49_26 extends Form49_24 {

    public Form49_26() {
        this.chunks = new int[]{9, 2, 200, 15, 15, 15, 15, 15};
        this.summs = new BigDecimal[5];
    }

    @Override
    public String toString() {
        String out = type + Format26.DELIMETR + kod + Format26.DELIMETR + debitorName + Format26.DELIMETR;
        
        for (BigDecimal bigDecimal : summs) {
            if (bigDecimal != null) {
                out += bigDecimal.toPlainString() + Format26.DELIMETR;
            } else {
                out += Format26.DELIMETR;
            }
        
        }
        return out;
    }

    public String getDebitorName() {
        return debitorName;
    }

}
