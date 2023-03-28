

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
public class XLSWorker30Test {

    public XLSWorker30Test() {
    }

    @BeforeClass
    public static void setUpClass()  {
        Svod svod = Svod.getInstance();
        try {
            svod.loadFiles("Реестр доставки пенсий (Прил. 30)");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker30Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
     * Test of writeOrganization method, of class XLSWorker30.
     */
    @Test
    public void testOfAllLoading() {

    }

}
