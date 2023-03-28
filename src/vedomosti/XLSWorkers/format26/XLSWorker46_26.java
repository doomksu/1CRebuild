package vedomosti.XLSWorkers.format26;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import settings.SettingsReader;
import vedomosti.Organization;
import vedomosti.OrganizationsGroup;
import vedomosti.XLSWorkers.XLSWorker46;
import vedomosti.common.Form;

/**
 * Класс для записи 46х форм в xls файлы – тип «ПЕРЕПЛАТА»
 *
 * @author kneretin
 */
public class XLSWorker46_26 extends XLSWorker46 {

    private final int FIRST_ROW_NUM = 15;

    public XLSWorker46_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
        setLastMonthDate();
    }
    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("C8").setCellValue("за " + month + " " + year + " г.");
        getCellFromReference("K8").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("C5").setCellValue("Ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("K9").setCellValue(params.get("KTOPFR"));
        getCellFromReference("K10").setCellValue(params.get("KSP"));
        getCellFromReference("K12").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("D9").setCellValue(params.get("RegNumNameOrg").substring(0, 15) + " " + params.get("RegNumNameOrg").substring(15) + " " + params.get("NameStPodr"));
        } else {
            getCellFromReference("D9").setCellValue(params.get("RegNumNameOrg") + " " + params.get("NameStPodr"));
        }
        getCellFromReference("D10").setCellValue(params.get(""));

        //end header
        // сдвиг строк в таблице для внесения выплат
        int paymentRowCount = org.getPaymentsCount();
        currentSheet.shiftRows(FIRST_ROW_NUM, currentSheet.getLastRowNum(), paymentRowCount, true, true);
        Iterator<String> kodsIt = org.getPaymentCodsIterator();

        int strIndex = 0;
        while (kodsIt.hasNext()) {
            String kod = kodsIt.next();
            for (Form form : org.getPaymentsByCod(kod)) {
                if (form.isTotalString() == false) {
                    writeFormToRow(form, strIndex);
                    strIndex++;
                }
            }
        }
        if (org.getPaymentsCount() > 0) {
            writeFormToRow(org.getTotalString(), strIndex);
        }
        strIndex++;
    }
    
     protected void writeFormToRow(Form form, int strIndex) {
        int columnNum = 0;
        CellRangeAddress cra = null;
        int realSringIndex = FIRST_ROW_NUM + strIndex;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }

        Cell c = prepareCell(r, columnNum);//A
        c.setCellValue(form.getKod());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum++;

        c = prepareCell(r, columnNum);//B-C
        c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));   //Цепочка получения имени выплаты
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4.5));
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //D-E
        c.setCellValue(form.getKbk());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        for (int i = 0; i < form.getSumms().length; i++) {
            c = prepareCell(r, columnNum);
            c.setCellValue(form.getSumms()[i].toPlainString().replace(".", ","));
            makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
            columnNum += 1;
        }

    }

}
