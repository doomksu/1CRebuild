package svod;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author kneretin
 */
public class SvodTest {

    public SvodTest() {
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
    public void svodByTest() {
        Svod svod = Svod.getInstance();
        try {
//            svod.loadFiles("Расчетная ведомость по доставке (Прил. 35)");
//            svod.loadFiles("Расчетная ведомость по начислению по почтам (Прил. 32)"); 
//            svod.loadFiles("Расчетная ведомость по начислению по банкам (Прил. 32)"); 
//            svod.loadFiles("Расчетная вед. по начислению по разовым поручениям(32)"); 
//            svod.loadFiles("Расч. вед. по начисл. пособий на погребение (Прил. 32)");
            svod.loadFiles("Реестр доставки пенсий (Прил. 30)");
//            svod.loadFiles("Расчетная ведомость по доставке (Прил. 35)");
//            svod.loadFiles("Реестр по удержаниям (Прил. 36)");
//            svod.loadFiles("Ведомость по переплатам по вине пенсионеров (Прил. 51)");
//            svod.loadFiles("Ведомость по переплатам по вине ПФР (Прил. 46)");
//            svod.loadFiles("Ведомость сумм неоплаты пенсий (Прил. 34)");
//            svod.loadFiles("Ведомость не полученных сумм пенсий (Прил. 69)");
//            svod.loadFiles("Ведомость по выплате наследникам (Прил. 71)");
//            svod.loadFiles("Ведомость по доставке наследникам (Прил. 73)");
//            svod.loadFiles("Реестр сумм пенсийне полученных пенсионером в связи с переездом (Прил. 57)");
//            svod.loadFiles("Ведомость сумм не включенных в доставочные документы (Прил. 65)");
//            svod.loadFiles("Ведомость выявленных и погашенных переп., по предложению службы занятости (по вине пенсионера) (Прил. 59)");
//            svod.loadFiles("Реестр для начисления доходов  администр. ПФР (Прил.49)");
//            svod.loadFiles("Ведомость непровомерного получения по счетам карты (Прил. 77)");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
