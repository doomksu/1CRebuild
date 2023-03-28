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
import settings.SettingsReader;
import vedomosti.Organization;
import vedomosti.OrganizationsGroup;
import vedomosti.common.Form;
import vedomosti.common.XLSWorker;

/**
 * Класс для записи 65х форм в xls файлы  – тип «НЕВКЛДОСТД» 
 *
 * @author kneretin
 */
public class XLSWorker65_26 extends XLSWorker {

    private final int FIRST_ROW_NUM = 17;

    public XLSWorker65_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    @Override
    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("C8").setCellValue("за " + month + " " + year + " г.");
        getCellFromReference("K8").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("C6").setCellValue("Ведомость №___" + orgParams.get("PrilNumber") + "____");
//        getCellFromReference("G8").setCellValue(params.get("KTOPFR"));
        getCellFromReference("K10").setCellValue(params.get("KSP"));
        getCellFromReference("K13").setCellValue(params.get("OKEI"));
        getCellFromReference("C9").setCellValue(params.get("RegNumNameOrg"));
        getCellFromReference("C10").setCellValue(params.get("NameStPodr"));
        //end header
        // сдвиг строк в таблице для внесения выплат
        int paymentRowCount = org.getPaymentsCount();
        int limit = 65535;
        if ((currentSheet.getLastRowNum() + paymentRowCount) < limit) {
            currentSheet.shiftRows(FIRST_ROW_NUM, currentSheet.getLastRowNum(), paymentRowCount, true, true);
        }
        Iterator<String> kodsIt = org.getPaymentCodsIterator();
        int strIndex = 0;
        while (kodsIt.hasNext()) {
            String kod = kodsIt.next();
            for (Form form : org.getPaymentsByCod(kod)) {
                if (form.isTotalString() == false) {
                    writeFormToRow((Form) form, strIndex);
                    strIndex++;
                }
            }
        }
        if (org.getPaymentsCount() > 0) {
            writeFormToRow((Form) org.getTotalString(), strIndex);
        }
        strIndex++;
    }

    private void writeFormToRow(Form form, int strIndex) {
        int columnNum = 0;
        CellRangeAddress cra = null;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }
        Cell c = prepareCell(r, columnNum);//A
        c.setCellValue(form.getKod());
        makeCellFullBordered(c, currentSheet);
        columnNum += 1;

        c = prepareCell(r, columnNum);//B
        c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));   //Цепочка получения имени выплаты
        c.getCellStyle().setWrapText(true);
        makeCellFullBordered(c, currentSheet);
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4));
        }
        makeCellFullBordered(c, currentSheet);
        columnNum += 1;

        c = prepareCell(r, columnNum);//C-D
        c.setCellValue(form.getKbk());
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), c.getColumnIndex(), c.getColumnIndex() + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 2;

        for (BigDecimal summ : form.getSumms()) {   //E-H
            c = prepareCell(r, columnNum);
            c.setCellValue(summ.toPlainString().replace(".", ","));
            columnNum++;
            makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        }
    }

}
