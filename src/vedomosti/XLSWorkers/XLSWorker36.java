package vedomosti.XLSWorkers;

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
import vedomosti.forms.Form36Interface;

/**
 * Класс для записи 36х форм в xls файлы
 *
 * @author kneretin
 */
public class XLSWorker36 extends XLSWorker {

    private final int FIRST_ROW_NUM = 32;

    public XLSWorker36(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }


    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("G11").setCellValue("от \"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("T11").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("H9").setCellValue("Реестр №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("T12").setCellValue(params.get("KTOPFR"));
        getCellFromReference("T13").setCellValue(params.get("KSP"));
        getCellFromReference("T14").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("D12").setCellValue(params.get("RegNumNameOrg").substring(0, 15));
            getCellFromReference("E13").setCellValue(params.get("RegNumNameOrg").substring(15));
        } else {
            getCellFromReference("D12").setCellValue(params.get("RegNumNameOrg"));
        }
        getCellFromReference("D13").setCellValue(params.get("NameStPodr"));
        
        getCellFromReference("E16").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("E17").setCellValue(orgParams.get("OrgDostAdr"));
        getCellFromReference("C21").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("K21").setCellValue(orgParams.get("BankName"));
        
        getCellFromReference("A24").setCellValue(orgParams.get("OrgPolINN"));
        makeCellBorderedWrapedAlign(getCellFromReference("A24"), HorizontalAlignment.CENTER);
        getCellFromReference("C24").setCellValue(orgParams.get("OrgPoltKPP"));
        makeCellBorderedWrapedAlign(getCellFromReference("C24"), HorizontalAlignment.CENTER);
        getCellFromReference("E24").setCellValue(orgParams.get("OrgPolOKATO"));

        
        getCellFromReference("G24").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("G24").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        getCellFromReference("J24").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("L24").setCellValue(orgParams.get("BankKorSch"));
        
        getCellFromReference("D26").setCellValue(orgParams.get("KODDohBK"));
        getCellFromReference("I26").setCellValue(orgParams.get("VidOper"));
        getCellFromReference("P26").setCellValue(orgParams.get("SrokPlat"));

        // end header
        // сдвиг строк в таблице для внесения выплат
        int paymentRowCount = org.getPaymentsCount();
        currentSheet.shiftRows(FIRST_ROW_NUM, currentSheet.getLastRowNum(), paymentRowCount, true, true);
        Iterator<String> kodsIt = org.getPaymentCodsIterator();

        int strIndex = 0;
        while (kodsIt.hasNext()) {
            String kod = kodsIt.next();
            for (Form form : org.getPaymentsByCod(kod)) {
                if (form.isTotalString() == false) {
                    writeFormToRow((Form36Interface) form, strIndex);
                    strIndex++;
                }
            }
        }
        if (org.getPaymentsCount() > 0) {
            writeFormToRow((Form36Interface) org.getTotalString(), strIndex);
        }
        strIndex++;
    }

    private void writeFormToRow(Form36Interface form, int strIndex) {
        int columnNum = 0;
        CellRangeAddress cra = null;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }
        Cell c = prepareCell(r, columnNum);//A
        c.setCellValue(form.getKod());
        makeCellFullBordered(c, currentSheet);
        c.getCellStyle().setWrapText(true);
        columnNum++;

        c = prepareCell(r, columnNum);//B-C
        c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));   //Цепочка получения имени выплаты
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4.5));
        }
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 2;

        c = prepareCell(r, columnNum); //D-E
        c.setCellValue(form.getKbk());
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 2;

        c = prepareCell(r, columnNum); //F
        c.setCellValue(form.getFio());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.LEFT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //G
        c.setCellValue(form.getPostAdr());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //H
        c.setCellValue(form.getPensionNumber());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //I
        if (form.getSumms()[0] != null) {
            c.setCellValue(form.getSumms()[0].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //J
        if (form.getSumms()[1] != null) {
            c.setCellValue(form.getSumms()[1].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //K
        if (form.getSumms()[2] != null) {
            c.setCellValue(form.getSumms()[2].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //L
        c.setCellValue(keepingTypeToString(form.getKeepingType()));
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.LEFT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //M
        c.setCellValue(form.getKeepingDocumentName());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //N
        c.setCellValue(form.getKeepingDocumentNumber());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //O
        c.setCellValue(form.getKeepingDocumentDate());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //P
        c.setCellValue(form.getGetterAccount());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //Q
        c.setCellValue(form.getGetterAdr());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //R
        c.setCellValue(form.getGetterFio());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.LEFT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //S
        c.setCellValue(form.getUin());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

    }

    private String keepingTypeToString(String keepingType) {
        String type = "";
        if (keepingType == null) {
            return "";
        }
        keepingType = keepingType.replace("0", "");
        switch (keepingType) {
            case "1":  //'     1  Алименты
                type = "Алименты";
                break;
            case "2":  //'     2  В пользу частного лица
                type = "В пользу частного лица";
                break;
            case "3":  //'     3  В пользу государства
                type = "В пользу государства";
                break;
            case "4":  //'     4  В пользу организации
                type = "В пользу организации";
                break;
            case "5":  //'     5  Перечисление в интернат
                type = "Перечисление в интернат";
                break;
            case "8":   //'     8  Почтовые расходы по доставке
                type = "Почтовые расходы по доставке";
                break;
            case "9":    //'     9  Переплата
                type = "Переплата";
                break;
        }
        return type;
    }

}
