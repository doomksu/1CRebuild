import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LoggingService;
import svod.Svod;

/**
 *
 * @author kneretin
 */
public class XLSWorker32Test {

    public XLSWorker32Test() {
        try {
            Svod svod = Svod.getInstance();
            LoggingService.writeLog("call load files from test::","debug");
            svod.loadFiles("Расчетная ведомость по начислению по почтам (Прил. 32) ");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker32Test.class.getName()).log(Level.SEVERE, null, ex);
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
     * Test of writeLoadedFromFile method, of class XLSWorker32.
     */
    @Test
    public void testWriteLoadedFromFile() {

    }

}
