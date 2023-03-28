
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LoggingService;
import settings.SettingsReader;
import svod.Svod;

/**
 *
 * @author kneretin
 */
public class AllFormsTest {

    private static File incommingFolder;
    private static Svod svod;

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void AllFormsTest() {
        svod = Svod.getInstance();
        incommingFolder = new File("test\\samples");
        if (incommingFolder.isDirectory()) {

            for (File node : incommingFolder.listFiles()) {
                if (node.isDirectory()) {
                    walkDir(node, "");
                }
            }
        }
    }

    private void walkDir(File node, String topName) {
        LoggingService.writeLog("walkNode:: " + node + "\t topName: " + topName, "debug");
        for (File subnode : node.listFiles()) {
            if (subnode.isDirectory()) {
                if (topName.equals("")) {
                    topName = node.getName();
                    walkDir(subnode, topName);
                }
            }
        }
        String nodeName = node.getName();
        SettingsReader.getInstance().setValue("PFRFILES", incommingFolder.getAbsolutePath() + "\\" + (topName.isEmpty() ? nodeName : topName + "\\" + nodeName));
        try {
            svod.loadFiles("(Прил. " + (topName.isEmpty() ? nodeName : topName) + ")");
        } catch (Exception ex) {
            Logger.getLogger(AllFormsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

}
