

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
public class XLSWorker77Test {
    
    public XLSWorker77Test() {
        try {
            Svod svod = Svod.getInstance();
            svod.loadFiles("Ведомость непровомерного получения по счетам карты (Прил. 77)");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker77Test.class.getName()).log(Level.SEVERE, null, ex);
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
