

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
public class XLSWorker69Test {
    
    public XLSWorker69Test() {
        try {
            Svod svod = Svod.getInstance();
            svod.loadFiles("Ведомость не полученных сумм пенсий (Прил. 69)");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker69Test.class.getName()).log(Level.SEVERE, null, ex);
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
     * Test of writeOrganization method, of class XLSWorker46.
     */
    @Test
    public void testWriteOrganization() {
    }
    
}
