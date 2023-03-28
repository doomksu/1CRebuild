
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
public class XLSWorker36Test {

    public XLSWorker36Test() throws Exception {
        try {
            Svod svod = Svod.getInstance();
            svod.loadFiles("Реестр по удержаниям (Прил. 36)");
        } catch (Exception ex) {
            Logger.getLogger(XLSWorker36Test.class.getName()).log(Level.SEVERE, null, ex);
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

    @Test
    public void testWriteOrganization() {
    }

}
