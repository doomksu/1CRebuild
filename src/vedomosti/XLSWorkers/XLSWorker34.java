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
public class XLSWorker34 extends XLSWorker {

    private final int FIRST_ROW_NUM = 22;

    public XLSWorker34(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("A7").setCellValue("\"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("AC7").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A5").setCellValue("Ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("AC8").setCellValue(params.get("KTOPFR"));
        getCellFromReference("AC9").setCellValue(params.get("KSP"));
        getCellFromReference("AC11").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("F8").setCellValue(params.get("RegNumNameOrg").substring(0, 15) + " " + params.get("RegNumNameOrg").substring(15) + " " + params.get("NameStPodr"));
        } else {
            getCellFromReference("F8").setCellValue(params.get("RegNumNameOrg") + " " + params.get("NameStPodr"));
        }

        getCellFromReference("E13").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("E14").setCellValue(orgParams.get("OrgDostAdr"));

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
        c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));   //Цепочка получения имени выплаты
        c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
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

        for (int i = 0; i < form.getSumms().length - 1; i++) {
            c = prepareCell(r, columnNum);
            c.setCellValue(form.getSumms()[i].toPlainString().replace(".", ","));
            c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
            c.getCellStyle().setWrapText(true);
            columnNum += 1;
            makeCellFullBordered(c, currentSheet);
        }

        c = prepareCell(r, columnNum); //L-S
        c.setCellValue(form.getSumms()[form.getSumms().length - 1].toPlainString().replace(".", ","));
        c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        c.getCellStyle().setWrapText(true);
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 8);
        mergeCells(cra);
        makeCellFullBordered(cra, currentSheet);
        columnNum += 1;
    }

}
