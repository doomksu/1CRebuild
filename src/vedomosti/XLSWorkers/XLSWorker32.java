package vedomosti.XLSWorkers;

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
public class XLSWorker32 extends XLSWorker {

    private final int FIRST_ROW_NUM = 28;

    public XLSWorker32(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("B7").setCellValue("\"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("S7").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A5").setCellValue("Расчетная ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("S8").setCellValue(params.get("KTOPFR"));
        getCellFromReference("S9").setCellValue(params.get("KSP"));
        getCellFromReference("S11").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("G8").setCellValue(params.get("RegNumNameOrg").substring(0, 15));
            getCellFromReference("H8").setCellValue(params.get("RegNumNameOrg").substring(15));
        } else {
            getCellFromReference("H8").setCellValue(params.get("RegNumNameOrg"));
        }

        getCellFromReference("G9").setCellValue(params.get("NameStPodr"));
        getCellFromReference("H13").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("H14").setCellValue(orgParams.get("OrgDostAdr"));
        getCellFromReference("A18").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("E18").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("H18").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("L18").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("L18").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        getCellFromReference("A22").setCellValue(orgParams.get("BankName"));
        getCellFromReference("E22").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("I22").setCellValue(orgParams.get("BankKorSch"));
        //end header
        // сдвиг строк в таблице для внесения выплат
        int paymentRowCount = org.getPaymentsCount();

        deleteMergedRange(paymentRowCount);
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

        for (BigDecimal summ : form.getSumms()) {
            c = prepareCell(r, columnNum);
            c.setCellValue(summ.toPlainString().replace(".", ","));
            columnNum++;
            makeCellFullBordered(c, currentSheet);
        }
    }

    protected void deleteMergedRange(int i) {
        for (int j = 0; j < currentSheet.getNumMergedRegions(); j++) {
            CellRangeAddress cellRangeAddress = currentSheet.getMergedRegion(j);
            if (cellRangeAddress.getFirstRow() >= 28 && cellRangeAddress.getFirstRow() <= 32 + i) {
                currentSheet.removeMergedRegion(j);
                deleteMergedRange(i);
            }
        }
    }

}
