package vedomosti.format23_1;

import vedomosti.formats.Format23;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import service.LoggingService;
import vedomosti.common.Format;

/**
 *
 * @author kneretin
 */
public class FormFormat23_1Test {

    public FormFormat23_1Test() {
        Format f = new Format23("header");
        HashMap<String, String> vals__ = f.getOrgValues("ОРГАНИЗАЦИЯ06602017/11/02140352                       112017Среднерусский банк ПАО Сбербанк 	                                                                                                                                                                                                                         г.Москва, ул.Вавилова, д.19                                                                                                                                                                             7707083893     773643002      УФПС Московской области - филиал ФГУП \"Почта России\"	                                                                                                                                                                                                     7724261610     502702001      40504810400060000002                    БАНК ВТБ (ПАО)                                                                                      044525187      30101810700000000187                    01                            2017/11/02                                                                                                                                                                                    П                                                                                                                                                                                                                                        049");
        HashMap<String, String> valn__ = f.getOrgValues("ОРГАНИЗАЦИЯ06302017/11/02140354                       112017Среднерусский банк ПАО Сбербанк 	                                                                                                                                                                                                                         г.Москва, ул.Вавилова, д.19                                                                                                                                                                             7707083893     773643002      Среднерусский банк ПАО Сбербанк 	                                                                                                                                                                                                                         7707083893     773643002      47422810240009921000                    ПАО СБЕРБАНК                                                                                        044525225      30101810400000000225                    01                            2017/11/02                                                                                                                                                                                    Б                                                                                                                                                                                                                                        049");
        LoggingService.writeLog("done ", "debug");
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
     * Test of isTotalString method, of class FormFormat23_1.
     */
    @Test
    public void testIsTotalString() {
    }

    /**
     * Test of toString method, of class FormFormat23_1.
     */
    @Test
    public void testToString() {
    }

    /**
     * Test of parse method, of class FormFormat23_1.
     */
    @Test
    public void testParse() throws Exception {
    }

    /**
     * Test of parsePartToBigDecimal method, of class FormFormat23_1.
     */
    @Test
    public void testParsePartToBigDecimal() {
    }

    /**
     * Test of getTotalSumm method, of class FormFormat23_1.
     */
    @Test
    public void testGetTotalSumm() {
    }

}
