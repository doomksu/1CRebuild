package vedomosti.XLSWorkers;

import vedomosti.common.XLSWorker;
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

/**
 * Класс для записи 59х форм в xls файлы
 *
 * @author kneretin
 */
public class XLSWorker59 extends XLSWorker {

    private final int FIRST_ROW_NUM = 20;

    public XLSWorker59(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    @Override
    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("A8").setCellValue("за " + month + " " + year + " г.");
        getCellFromReference("K6").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A5").setCellValue("Ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("K7").setCellValue(params.get("KTOPFR"));
        getCellFromReference("K8").setCellValue(params.get("KSP"));
        getCellFromReference("K10").setCellValue(params.get("OKEI"));

        getCellFromReference("C10").setCellValue(params.get("RegNumNameOrg"));
        getCellFromReference("C11").setCellValue(params.get("NameStPodr"));

        getCellFromReference("D15").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("D16").setCellValue(orgParams.get("OrgDostAdr"));
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
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum);//B
        c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));   //Цепочка получения имени выплаты
        c.getCellStyle().setWrapText(true);
        makeCellFullBordered(c, currentSheet);
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4));
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum);//C
        c.setCellValue(form.getKbk());
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), c.getColumnIndex(), c.getColumnIndex() + 1);
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        for (BigDecimal summ : form.getSumms()) {   //D-K
            c = prepareCell(r, columnNum);
            c.setCellValue(summ.toPlainString().replace(".", ","));
            columnNum++;
            makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        }
    }

}
