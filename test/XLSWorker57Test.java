

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
public class XLSWorker57Test {

    public XLSWorker57Test() {
        Svod svod = Svod.getInstance();
        try {
            svod.loadFiles("Реестр сумм пенсийне полученных пенсионером в связи с переездом (Прил. 57)");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker57Test.class.getName()).log(Level.SEVERE, null, ex);
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
