package vedomosti.XLSWorkers.format26;

import vedomosti.common.XLSWorker;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import settings.SettingsReader;
import vedomosti.Organization;
import vedomosti.OrganizationsGroup;
import vedomosti.common.Form;

/**
 * Класс для записи 32х форм в xls файлы
 *
 * @author kneretin
 */
public class XLSWorker32_26 extends XLSWorker {

    private final int FIRST_ROW_NUM = 35;

    public XLSWorker32_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("A12").setCellValue("\"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("U12").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A10").setCellValue("Расчетная ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("U13").setCellValue(params.get("KTOPFR"));
        getCellFromReference("U14").setCellValue(params.get("KSP"));
        getCellFromReference("U16").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("L13").setCellValue(params.get("Reg                                                                        NumNameOrg").substring(0, 15));
            getCellFromReference("G13").setCellValue(params.get("RegNumNameOrg").substring(15));
        }

        getCellFromReference("G14").setCellValue(params.get("NameStPodr"));
        getCellFromReference("I18").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("I19").setCellValue(orgParams.get("OrgDostAdr"));
        getCellFromReference("A23").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("E23").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("J23").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("N23").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("N23").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));

        getCellFromReference("A29").setCellValue(orgParams.get("BankName"));
        getCellFromReference("E29").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("K29").setCellValue(orgParams.get("BankKorSch"));
        //end header
        // сдвиг строк в таблице для внесения выплат
        int paymentRowCount = org.getPaymentsCount();
//        LoggingService.writeLog("--" + orgParams.get("OrgDostName").trim() + "-- has: " + paymentRowCount,"debug");
//        deleteMergedRange(paymentRowCount);
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
        writeFormToRow(org.getTotalString(), strIndex);
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
        c.getCellStyle().setWrapText(true);
        makeCellFullBordered(c, currentSheet);
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4));
        }
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), c.getColumnIndex(), c.getColumnIndex() + 1);
        mergeCells(cra);
        makeCellFullBordered(cra, currentSheet);
        columnNum += 2;

        c = prepareCell(r, columnNum);//D-E
        c.setCellValue(form.getKbk());
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), c.getColumnIndex(), c.getColumnIndex() + 1);
        mergeCells(cra);
        makeCellFullBordered(cra, currentSheet);
        columnNum += 2;

//        LoggingService.writeLog("will write: "+form.getSumms().length + "  columns" ,"debug");
        for (BigDecimal summ : form.getSumms()) {
            c = prepareCell(r, columnNum);
            c.setCellValue(summ.toPlainString().replace(".", ","));
            columnNum++;
            makeCellFullBordered(c, currentSheet);
        }
//        LoggingService.writeLog("stop form in column: " + columnNum,"debug");
    }

    protected void deleteMergedRange(int i) {
        for (int j = 0; j < currentSheet.getNumMergedRegions(); j++) {
            CellRangeAddress cellRangeAddress = currentSheet.getMergedRegion(j);
            if (cellRangeAddress.getFirstRow() >= 35 && cellRangeAddress.getFirstRow() <= 32 + i) {
                currentSheet.removeMergedRegion(j);
                deleteMergedRange(i);
            }
        }
    }

}
