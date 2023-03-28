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
public class XLSWorker73 extends XLSWorker {

    private final int FIRST_ROW_NUM = 25;

    public XLSWorker73(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("B1").setCellValue("Расчетная ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("A3").setCellValue("за \"" + "\" " + month + " " + year + " г.");
        getCellFromReference("R3").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("R4").setCellValue(params.get("KTOPFR"));
        getCellFromReference("R5").setCellValue(params.get("KSP"));
        getCellFromReference("R7").setCellValue(params.get("OKEI"));

        getCellFromReference("D4").setCellValue(params.get("RegNumNameOrg"));
        getCellFromReference("D5").setCellValue(params.get("NameStPodr"));

        getCellFromReference("E9").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("E10").setCellValue(orgParams.get("OrgDostAdr"));

        getCellFromReference("A14").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("A18").setCellValue(orgParams.get("BankName"));

        getCellFromReference("E14").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("H14").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("M14").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("E18").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("J18").setCellValue(orgParams.get("BankKorSch"));

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
        int realSringIndex = FIRST_ROW_NUM + strIndex;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }

        Cell c = prepareCell(r, columnNum);//A
        c.setCellValue(form.getKod());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum++;

        c = prepareCell(r, columnNum);//B - C
        c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));   //Цепочка получения имени выплаты
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4.5));
        }
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 2;

        c = prepareCell(r, columnNum); //D - E
        c.setCellValue(form.getKbk());
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 2;

        for (int i = 0; i < 3; i++) {
            c = prepareCell(r, columnNum);
            c.setCellValue(form.getSumms()[i].toPlainString().replace(".", ","));
            makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
            columnNum += 1;
        }
        for (int i = 3; i < 6; i++) {
            c = prepareCell(r, columnNum);
            c.setCellValue(form.getSumms()[i].toPlainString().replace(".", ","));
            cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
            mergeCells(cra);
            makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
            columnNum += 2;
        }
        c = prepareCell(r, columnNum);
        c.setCellValue(form.getSumms()[6].toPlainString().replace(".", ","));
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum);
        c.setCellValue(form.getSumms()[7].toPlainString().replace(".", ","));
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 2;
        
        c = prepareCell(r, columnNum);
        c.setCellValue(form.getSumms()[8].toPlainString().replace(".", ","));
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;
    }

}
