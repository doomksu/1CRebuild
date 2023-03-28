package vedomosti.common;

import java.math.BigDecimal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LoggingService;
import vedomosti.forms.Form32;

/**
 *
 * @author kneretin
 */
public class FormTest {

    public FormTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getTotalSumm method, of class Form.
     */
    @Test
    public void testGetTotalSumm() {
    }

    /**
     * Test of parse method, of class Form.
     */
    @Test
    public void testParse() throws Exception {
    }

    /**
     * Test of toString method, of class Form.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of appendFromForm method, of class Form.
     */
    @Test
    public void testAppendFromForm() {
    }

    /**
     * Test of getKod method, of class Form.
     */
    @Test
    public void testGetKod() {
    }

    /**
     * Test of getKbk method, of class Form.
     */
    @Test
    public void testGetKbk() {
    }

    /**
     * Test of getPrimaryKey method, of class Form.
     */
    @Test
    public void testGetPrimaryKey() {
    }

    /**
     * Test of isSameKOD method, of class Form.
     */
    @Test
    public void testIsSameKOD() {
    }

    /**
     * Test of isAppendable method, of class Form.
     */
    @Test
    public void testIsAppendable() {
    }

    /**
     * Test of extendString method, of class Form.
     */
    @Test
    public void testExtendString_String_int() {
    }

    /**
     * Test of extendString method, of class Form.
     */
    @Test
    public void testExtendString_Integer_int() {
    }

    /**
     * Test of extendString method, of class Form.
     */
    @Test
    public void testExtendString_BigDecimal_int() {
        Form32 instance = new Form32();
        String sample = "000000002028.16";
        BigDecimal bdValue = new BigDecimal(sample);
        LoggingService.writeLog("sample: \t" + sample, "debug");
    }

    /**
     * Test of isTotalString method, of class Form.
     */
    @Test
    public void testIsTotalString() {
    }

    /**
     * Test of getSumms method, of class Form.
     */
    @Test
    public void testGetSumms() {
    }

    /**
     * Test of isSubtotalString method, of class Form.
     */
    @Test
    public void testIsSubtotalString() {
    }

    /**
     * Test of isAllNull method, of class Form.
     */
    @Test
    public void testIsAllNull() {
    }

    public class FormImpl extends Form {

        public BigDecimal getTotalSumm() {
            return null;
        }

        public void parse(String str) throws FormParseException {
        }

        public String toString() {
            return "";
        }
    }

}
