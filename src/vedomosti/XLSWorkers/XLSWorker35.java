package vedomosti.XLSWorkers;

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
public class XLSWorker35 extends XLSWorker {

    private final int FIRST_ROW_NUM = 29;

    public XLSWorker35(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("B7").setCellValue("\"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("Q7").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A5").setCellValue("Расчетная ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("Q8").setCellValue(params.get("KTOPFR"));
        getCellFromReference("Q9").setCellValue(params.get("KSP"));
        getCellFromReference("Q11").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("D8").setCellValue(params.get("RegNumNameOrg").substring(0, 15));
            getCellFromReference("F8").setCellValue(params.get("RegNumNameOrg").substring(15));
        }
        getCellFromReference("E9").setCellValue(params.get("NameStPodr"));
        getCellFromReference("H14").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("H15").setCellValue(orgParams.get("OrgDostAdr"));
        getCellFromReference("A19").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("E19").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("H19").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("L19").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("L19").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        getCellFromReference("A23").setCellValue(orgParams.get("BankName"));
        getCellFromReference("E23").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("I23").setCellValue(orgParams.get("BankKorSch"));
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

        int index = 3;
        for (BigDecimal summ : form.getSumms()) {
            c = prepareCell(r, columnNum);;
            String summFormula = "";
            if (index == 6) {//здесь пишем сумму
                c.setCellType(CellType.FORMULA);
                summFormula = "H" + realSringIndex
                        + "+I" + realSringIndex
                        + "+J" + realSringIndex
                        + "+P" + realSringIndex;
                c.setCellFormula(summFormula);
                c.setCellStyle(borderedStyleRIGHT_NUMBERFORMAT);
                formulaCell1 = c;
            } else {
                //поле с cуммой 11 + 14 + 15  = K + N + O 
                if (index == 9) {
                    c.setCellType(CellType.FORMULA);
                    summFormula = "K" + realSringIndex
                            + "+N" + realSringIndex
                            + "+O" + realSringIndex;
                    c.setCellFormula(summFormula);
                    formulaCell2 = c;
                } else {
                    if (summ != null) {
                        c.setCellValue(summ.toPlainString().replace(".", ","));
                    }
                }
            }
            index++;
            c.setCellStyle(borderedStyleRIGHT_NUMBERFORMAT);
            columnNum += 1;
        }
        evaluator.evaluateFormulaCellEnum(formulaCell1);
        evaluator.evaluateFormulaCellEnum(formulaCell2);
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
