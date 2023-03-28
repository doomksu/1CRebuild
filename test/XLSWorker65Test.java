

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import svod.Svod;

/**
 *
 * @author kneretin
 */
public class XLSWorker65Test {

    public XLSWorker65Test() {
        Svod svod = Svod.getInstance();
        try {
            svod.loadFiles("Ведомость сумм не включенных в доставочные документы (Прил. 65)");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker51Test.class.getName()).log(Level.SEVERE, null, ex);
        }
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
     * Test of writeOrganization method, of class XLSWorker71.
     */
    @Test
    public void testWriteOrganization() {
    }

}
