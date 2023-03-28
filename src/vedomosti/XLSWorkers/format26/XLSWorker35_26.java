package vedomosti.XLSWorkers.format26;

import vedomosti.common.XLSWorker;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
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
public class XLSWorker35_26 extends XLSWorker {

    private final int FIRST_ROW_NUM = 29;

    public XLSWorker35_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
        setLastMonthDate();
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("G7").setCellValue("за " + month + " " + year + " г.");
        getCellFromReference("S7").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A5").setCellValue("Расчетная ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("S8").setCellValue(params.get("KTOPFR"));
        getCellFromReference("S9").setCellValue(params.get("KSP"));
        getCellFromReference("S12").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("D8").setCellValue(params.get("RegNumNameOrg").substring(0, 15));
            getCellFromReference("F8").setCellValue(params.get("RegNumNameOrg").substring(15));
        }
        getCellFromReference("E9").setCellValue(params.get("NameStPodr"));
        getCellFromReference("H14").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("H15").setCellValue(orgParams.get("OrgDostAdr"));

        getCellFromReference("A19").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("E19").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("I19").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("R19").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("R19").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        getCellFromReference("A23").setCellValue(orgParams.get("BankName"));
        getCellFromReference("E23").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("J23").setCellValue(orgParams.get("BankKorSch"));
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
        if (org.getPaymentsCount() > 0) {
            writeFormToRow(org.getTotalString(), strIndex);
        }
        strIndex++;
    }

    private void writeFormToRow(Form form, int strIndex) {
        Cell formulaCell1 = null;
        Cell formulaCell2 = null;
        Cell formulaCell3 = null;
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        int columnNum = 0;
        int realSringIndex = FIRST_ROW_NUM + strIndex + 1;
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
        c.setCellStyle(borderedStyleLEFT);
        columnNum += 1;
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4));
        }

        c = prepareCell(r, columnNum);//C
        c.setCellValue(form.getKbk());
        makeCellFullBordered(c, currentSheet);
        columnNum += 1;
        for (BigDecimal summ : form.getSumms()) {
            c = prepareCell(r, columnNum);;
            String summFormula = "";
            if (columnNum == 7) {//здесь пишем сумму
                //гр.9+гр.10+гр.11 +гр.17
                c.setCellType(CellType.FORMULA);
                summFormula = "I" + realSringIndex
                        + "+J" + realSringIndex
                        + "+K" + realSringIndex
                        + "+Q" + realSringIndex;
                c.setCellFormula(summFormula);
                formulaCell1 = c;
            }
            //поле с cуммой 12 + 15 + 16
            if (columnNum == 10) {
                c.setCellType(CellType.FORMULA);
                summFormula = "L" + realSringIndex
                        + "+O" + realSringIndex
                        + "+P" + realSringIndex;
                c.setCellFormula(summFormula);
                formulaCell2 = c;
            }
            
            if (columnNum == 13) {
                c.setCellType(CellType.FORMULA);
                summFormula = "L" + realSringIndex
                        + "+M" + realSringIndex;
                c.setCellFormula(summFormula);
                formulaCell3 = c;
            }
            
            if (summ != null) {
                c.setCellValue(summ.toPlainString().replace(".", ","));
            }else{
                c.setCellValue("0,00");
            }
            
            c.setCellStyle(borderedStyleRIGHT_NUMBERFORMAT);
            columnNum += 1;
        }
        evaluator.evaluateFormulaCellEnum(formulaCell1);
        evaluator.evaluateFormulaCellEnum(formulaCell2);
        evaluator.evaluateFormulaCellEnum(formulaCell3);
    }

    private void deleteMergedRange(int i) {
        for (int j = 0; j < currentSheet.getNumMergedRegions(); j++) {
            CellRangeAddress cellRangeAddress = currentSheet.getMergedRegion(j);
            if (cellRangeAddress.getFirstRow() >= FIRST_ROW_NUM && cellRangeAddress.getFirstRow() <= 32 + i) {
                currentSheet.removeMergedRegion(j);
                deleteMergedRange(i);
            }
        }
    }

}
