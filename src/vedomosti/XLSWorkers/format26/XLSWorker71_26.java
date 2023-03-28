package vedomosti.XLSWorkers.format26;

import vedomosti.XLSWorkers.*;
import vedomosti.common.XLSWorker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
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
public class XLSWorker71_26 extends XLSWorker {

    private final int FIRST_ROW_NUM = 35;

    public XLSWorker71_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("A12").setCellValue("\"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("M12").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A10").setCellValue("Расчетная ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("M13").setCellValue(params.get("KTOPFR"));
        getCellFromReference("M14").setCellValue(params.get("KSP"));
        getCellFromReference("M16").setCellValue(params.get("OKEI"));

        getCellFromReference("E13").setCellValue(params.get("RegNumNameOrg"));

        getCellFromReference("E14").setCellValue(params.get("NameStPodr"));
        getCellFromReference("G18").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("G19").setCellValue(orgParams.get("OrgDostAdr"));
        getCellFromReference("A23").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("E23").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("H23").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("M23").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("M23").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        getCellFromReference("A29").setCellValue(orgParams.get("BankName"));
        getCellFromReference("E29").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("H29").setCellValue(orgParams.get("BankKorSch"));

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
        int realSringIndex = FIRST_ROW_NUM + strIndex;
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
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);

        c = prepareCell(r, columnNum); //D-E
        c.setCellValue(form.getKbk());
        c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        c.getCellStyle().setWrapText(true);
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        columnNum += 2;
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);

        c = prepareCell(r, columnNum); //F-G
        c.setCellValue(form.getSumms()[0].toPlainString().replace(".", ","));
        c.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
        c.getCellStyle().setWrapText(true);
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        columnNum += 2;
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        
        for (int i = 1; i < form.getSumms().length; i++) {
            c = prepareCell(r, columnNum);
            c.setCellValue(form.getSumms()[i].toPlainString().replace(".", ","));
            c.getCellStyle().setAlignment(HorizontalAlignment.RIGHT);
            c.getCellStyle().setWrapText(true);
            columnNum += 1;
            makeCellFullBordered(c, currentSheet);
        }

//        c = prepareCell(r, columnNum); //L-S
//        c.setCellValue(form.getSumms()[form.getSumms().length - 1].toPlainString().replace(".", ","));
//        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 7);
//        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.RIGHT);
//        mergeCells(cra);
//        columnNum += 1;
    }

}
