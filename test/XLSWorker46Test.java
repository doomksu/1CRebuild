

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
public class XLSWorker46Test {
    
    public XLSWorker46Test() {
        try {
            Svod svod = Svod.getInstance();
            svod.loadFiles("Ведомость по переплатам по вине ПФР (Прил. 46)");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker46Test.class.getName()).log(Level.SEVERE, null, ex);
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
