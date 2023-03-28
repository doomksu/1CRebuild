package vedomosti.XLSWorkers.format26;

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
import settings.SettingsReader;
import settings.SettingsReader.STMap.ST;
import vedomosti.Organization;
import vedomosti.OrganizationsGroup;
import vedomosti.XLSWorkers.XLSWorker34;
import vedomosti.common.Form;

/**
 * Класс для записи 36х форм в xls файлы
 *
 * @author kneretin
 */
public class XLSWorker34_26 extends XLSWorker34 {

    private final int FIRST_ROW_NUM = 22;

    public XLSWorker34_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
        setLastMonthDate();
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("A7").setCellValue("за " + month + " " + year + " г.");
        getCellFromReference("AD7").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A5").setCellValue("Ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("AD8").setCellValue(params.get("KTOPFR"));
        getCellFromReference("AD9").setCellValue(params.get("KSP"));
        getCellFromReference("AD11").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("G8").setCellValue(params.get("RegNumNameOrg").substring(0, 15) + " " + params.get("RegNumNameOrg").substring(15) + " " + params.get("NameStPodr"));
        } else {
            getCellFromReference("G8").setCellValue(params.get("RegNumNameOrg") + " " + params.get("NameStPodr"));
        }

        getCellFromReference("I13").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("I14").setCellValue(orgParams.get("OrgDostAdr"));

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

    private void writeFormToRow(Form form, int strIndex) {
        if (form == null || form.getKod() == null) {
            LoggingService.writeLog("currentSheet.name: " + currentSheet.getSheetName()
                    + " wont write total string form is null: " + (form == null)
            , "debug");
            return;
        }
        int columnNum = 0;
        CellRangeAddress cra = null;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }

        Cell c = prepareCell(r, columnNum);//A
        c.setCellValue(form.getKod());
        makeCellFullBordered(c, currentSheet);
        columnNum++;

        c = prepareCell(r, columnNum);//B-C
        String paymentName = "";

        ST st = SettingsReader.getInstance().getSTs().getST(form.getKod());
        if (st != null) {
            paymentName = st.getName();
        } else {
            LoggingService.writeLog("ERROR: Unknown payment for kod: " + form.getKod(),"debug");
        }
        c.setCellValue(paymentName);   //Цепочка получения имени выплаты
        c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        if (paymentName.length() > 30) {
            r.setHeight((short) (r.getHeight() * 4));
        }
        c.getCellStyle().setWrapText(true);
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        columnNum += 2;
        makeCellFullBordered(cra, currentSheet);

        c = prepareCell(r, columnNum); //D
        c.setCellValue(form.getKbk());
        c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        c.getCellStyle().setWrapText(true);
        columnNum += 1;
        makeCellFullBordered(c, currentSheet);

        for (int i = 0; i < form.getSumms().length; i++) {
            c = prepareCell(r, columnNum);
            c.setCellValue(form.getSumms()[i].toPlainString().replace(".", ","));
            c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
            c.getCellStyle().setWrapText(true);
            columnNum += 1;
            makeCellFullBordered(c, currentSheet);
        }
    }

}
