package vedomosti.XLSWorkers;

import vedomosti.common.XLSWorker;
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

/**
 * Класс для записи 36х форм в xls файлы
 *
 * @author kneretin
 */
public class XLSWorker57 extends XLSWorker {

    private final int FIRST_ROW_NUM = 17;

    public XLSWorker57(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
        setLastMonthDate();
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("A5").setCellValue("за " + month + " " + year + " г.");
        getCellFromReference("H5").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A2").setCellValue("Реестр № ___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("H6").setCellValue(params.get("KTOPFR"));
        getCellFromReference("H7").setCellValue(params.get("KSP"));
        getCellFromReference("H8").setCellValue(params.get("OKEI"));

        getCellFromReference("C6").setCellValue(params.get("RegNumNameOrg"));
        getCellFromReference("C7").setCellValue(params.get("NameStPodr"));

        getCellFromReference("C11").setCellValue(params.get("OrgDostName"));
        getCellFromReference("C12").setCellValue(params.get("OrgDostINN"));

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

    private void writeFormToRow(Form form, int strIndex) {
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

        c = prepareCell(r, columnNum);//B
        c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));   //Цепочка получения имени выплаты
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4.5));
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum++;

        c = prepareCell(r, columnNum); //C
        c.setCellValue(form.getKbk());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum++;

        c = prepareCell(r, columnNum); //D
        c.setCellValue(form.getSumms()[0].toPlainString().replace(".", ","));
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum++;
        //--
        c = prepareCell(r, columnNum); //E-F
        c.setCellValue(form.getSumms()[1].toPlainString().replace(".", ","));
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), c.getColumnIndex(), c.getColumnIndex() + 1);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.RIGHT);
        mergeCells(cra);
        columnNum += 2;
        //----
        c = prepareCell(r, columnNum); //G-H
        c.setCellValue(form.getSumms()[2].toPlainString().replace(".", ","));
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), c.getColumnIndex(), c.getColumnIndex() + 1);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.RIGHT);
        mergeCells(cra);
        columnNum += 2;

    }

}
