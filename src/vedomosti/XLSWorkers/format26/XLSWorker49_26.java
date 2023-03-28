package vedomosti.XLSWorkers.format26;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import service.LoggingService;
import vedomosti.Organization;
import vedomosti.OrganizationsGroup;
import vedomosti.common.Form;
import vedomosti.common.XLSWorker;
import vedomosti.forms.Form49Interface;
import vedomosti.forms.foramt26.Form49_26;

/**
 * Класс для записи 49х форм в xls файлы тип «РЕЕСТРДОХ»
 *
 * @author kneretin
 */
public class XLSWorker49_26 extends XLSWorker {

    private final int FIRST_ROW_NUM = 20;

    public XLSWorker49_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("E8").setCellValue("за " + month + " " + year + " г.");
        getCellFromReference("L8").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("L9").setCellValue(params.get("KTOPFR"));
        getCellFromReference("L10").setCellValue(params.get("KSP"));
        getCellFromReference("L12").setCellValue(params.get("OKEI"));
        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("E11").setCellValue(params.get("RegNumNameOrg").substring(0, 15) + " " + params.get("RegNumNameOrg").substring(15) + " " + params.get("NameStPodr"));
        } else {
            getCellFromReference("E11").setCellValue(params.get("RegNumNameOrg") + " " + params.get("NameStPodr"));
        }
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
                    writeFormToRow((Form49Interface) form, strIndex);
                    strIndex++;
                }
            }
        }
        strIndex++;
    }

    private void writeFormToRow(Form49Interface form, int strIndex) {
        Form49_26 fform = (Form49_26) form;
        int columnNum = 0;
        CellRangeAddress cra = null;
        int realSringIndex = FIRST_ROW_NUM + strIndex;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }

        Cell c = prepareCell(r, columnNum);//A-C
        c.setCellValue(fform.getKod());
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 2);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 3;

        c = prepareCell(r, columnNum);//D-G
        c.setCellValue(fform.getDebitorName());

        if (fform.getDebitorName().length() > 30) {
            r.setHeight((short) (r.getHeight() * 4.5));
        }
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 3);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 4;

        int summIndex = 0;
        for (BigDecimal summ : fform.getSumms()) {
            c = prepareCell(r, columnNum);
            if (summ == null) {
                LoggingService.writeLog("summ in pos: " + summIndex + "  is null" + fform.toString(),"debug");
            }
            summIndex++;
            c.setCellValue(summ.toPlainString().replace(".", ","));
            columnNum++;
            makeCellFullBordered(c, currentSheet);
        }
    }

}
