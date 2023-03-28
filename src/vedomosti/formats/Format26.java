package vedomosti.formats;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import service.LoggingService;
import vedomosti.OrganizationsGroup;
import vedomosti.XLSWorkers.format26.XLSWorker32_26;
import vedomosti.XLSWorkers.format26.XLSWorker34_26;
import vedomosti.XLSWorkers.format26.XLSWorker35_26;
import vedomosti.XLSWorkers.format26.XLSWorker36_26;
import vedomosti.XLSWorkers.format26.XLSWorker46_26;
import vedomosti.XLSWorkers.format26.XLSWorker49_26;
import vedomosti.XLSWorkers.format26.XLSWorker51_26;
import vedomosti.XLSWorkers.format26.XLSWorker57_26;
import vedomosti.XLSWorkers.format26.XLSWorker65_26;
import vedomosti.XLSWorkers.format26.XLSWorker69_26;
import vedomosti.XLSWorkers.format26.XLSWorker71_26;
import vedomosti.common.Form;
import vedomosti.common.XLSWorker;
import vedomosti.forms.foramt26.Form32_26;
import vedomosti.forms.foramt26.Form34_26;
import vedomosti.forms.foramt26.Form35_26;
import vedomosti.forms.foramt26.Form36_26;
import vedomosti.forms.foramt26.Form46_26;
import vedomosti.forms.foramt26.Form49_26;
import vedomosti.forms.foramt26.Form51_26;
import vedomosti.forms.foramt26.Form57_26;
import vedomosti.forms.foramt26.Form65_26;
import vedomosti.forms.foramt26.Form69_26;

/**
 *
 * @author kneretin
 */
public class Format26 extends Format25 {

    public static String FORMAT_NAME = "26.";
    public static String DELIMETR = "|";
    static String XLSFolder = "\\_26";
    static String XLSPostfix = "_26";
    private ArrayList<Integer> forms = new ArrayList<Integer>();

    public Format26(String _header) {
        super(_header);
        ORGChunksNames = new String[]{"", "", "", "", "PrilDate", "PrilNumber", "", "", "", "", "OrgDostName", "OrgDostAdr",
            "OrgDostINN", "OrgDostKPP", "OrgPolName", "OrgPolINN", "OrgPoltKPP", "OrgPolBank",
            "OrgPolOKATO", "BankName", "BankBIK", "BankKorSch", "KODDohBK", "VidOper", "SrokPlat",
            "incomeManager", "budgetName", "incomeType", "orgType", "foregnKey", "causeOfClose",
            "serviceInfo", "raionKodeByKLADR", "deliveryDate"};
        ORGChunksLenght = new int[]{11, 2, 1, 1, 10, 6, 3, 20, 2, 4, 250, 200, 15, 15, 250, 15, 15, 20, 20, 100, 15, 20, 20, 30, 10, 10, 20, 150, 1, 2, 100, 130, 3, 10};
        MAX_DAYLI_COUNT = 99;
        //максимальное количество файлов за день для этого формата увеличено до 99 
        int[] _f = {32, 34, 35, 36, 46, 49, 51, 57, 65, 69, 71};
        for (int i : _f) {
            forms.add(i);
        }
    }

    @Override
    public Form makeForm() {
        Form form = createForm();
        if (form == null) {
            form = super.makeForm();
        }
        form.setFormFormat(produceFormFormat(form));
        return form;
    }

    private Form createForm() {
        Form form = null;
        if (forms.contains(formNumber)) {
            switch (formNumber) {
                case 32:
                    form = new Form32_26();
                    break;
                case 34:
                    form = new Form34_26();
                    break;
                case 35:
                    form = new Form35_26();
                    break;
                case 36:
                    form = new Form36_26();
                    break;
                case 46:
                    form = new Form46_26();
                    break;
                case 49:
                    form = new Form49_26();
                    break;
                case 51:
                    form = new Form51_26();
                    break;
                case 57:
                    form = new Form57_26();
                    break;
                case 65:
                    form = new Form65_26();
                    break;
                case 69:
                    form = new Form69_26();
                    break;
            }
        }
        return form;
    }

    /**
     * Определить номер или букву текущего месяца для имени файла
     *
     * @return
     */
    @Override
    protected String countCurentMonthSign() {
        GregorianCalendar gc = new GregorianCalendar();
        int monthNum = (gc.get(Calendar.MONTH) + 1);
        if (monthNum < 10) {
            return String.valueOf(monthNum);
        } else {
            if (monthNum == 10) {
                return "A";
            }
            if (monthNum == 11) {
                return "B";
            }
            if (monthNum == 12) {
                return "C";
            }
        }
        return null;
    }

    @Override
    public XLSWorker createWorker(OrganizationsGroup banks, int vedomostNumber) {
        LoggingService.writeLog("call to create worker in format: " + getFormatName(), "debug");
        XLSWorker worker = null;
        LoggingService.writeLog("check is one of 26 format form", "debug");
        if (forms.contains(vedomostNumber)) {
            try {
                outXLS = getOutFileForXLSWorker(vedomostNumber);
                InputStream inp = new FileInputStream(outXLS);
                Workbook wb = WorkbookFactory.create(inp);
                switch (formNumber) {
                    case 32:
                        worker = new XLSWorker32_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 34:
                        worker = new XLSWorker34_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 35:
                        worker = new XLSWorker35_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 36:
                        worker = new XLSWorker36_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 46:
                        worker = new XLSWorker46_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 49:
                        worker = new XLSWorker49_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 51:
                        worker = new XLSWorker51_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 57:
                        worker = new XLSWorker57_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 65:
                        worker = new XLSWorker65_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 69:
                        worker = new XLSWorker69_26(wb, banks, getVerValues(getHeader()));
                        break;
                    case 71:
                        worker = new XLSWorker71_26(wb, banks, getVerValues(getHeader()));
                        break;
                }
                if (worker != null) {
                    worker.setOUTXLS(outXLS);
                    return worker;
                }
            } catch (Exception ex) {
                LoggingService.writeLog(ex);
            }
        }
        if (worker == null) {
            return super.createWorker(banks, vedomostNumber);
        }
        return null;
    }

    @Override
    protected FormFormat produceFormFormat(Form form) {
        return new FormFormat26(form);
    }

    @Override
    public File getXLSTemplate(int form) {
        LoggingService.writeLog("call to get template for form: " + form + " in format 26", "debug");
        File selfXLS = getXLSTemplateHeirarcy(form, XLSFolder, XLSPostfix);
        if (selfXLS == null || selfXLS.isFile() == false) {
            LoggingService.writeLog("cant find in this format will ask in super", "debug");
            return super.getXLSTemplate(form);
        }
        return selfXLS;
    }

}
