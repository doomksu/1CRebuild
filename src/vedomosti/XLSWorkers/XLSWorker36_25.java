package vedomosti.XLSWorkers;

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
import service.LoggingService;
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
public class XLSWorker36_25 extends XLSWorker36 {

    private final int FIRST_ROW_NUM = 32;

    public XLSWorker36_25(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);

        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("G11").setCellValue("от \"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("H9").setCellValue("Реестр №___" + orgParams.get("PrilNumber") + "____");

        getCellFromReference("T11").setCellValue(day + "." + monthNum + "." + year);
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
        

        getCellFromReference("A24").setCellValue(orgParams.get("OrgPolINN"));
        makeCellBorderedWrapedAlign(getCellFromReference("A24"), HorizontalAlignment.CENTER);
        getCellFromReference("C24").setCellValue(orgParams.get("OrgPoltKPP"));
        makeCellBorderedWrapedAlign(getCellFromReference("C24"), HorizontalAlignment.CENTER);
        getCellFromReference("G24").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("G24").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        getCellFromReference("G24").getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        getCellFromReference("K24").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("M24").setCellValue(orgParams.get("BankKorSch"));
        getCellFromReference("E24").setCellValue(orgParams.get("OrgPolOKATO"));
        makeCellBorderedWrapedAlign(getCellFromReference("E24"), HorizontalAlignment.CENTER);

        LoggingService.writeLog("sheet: " + newSheetName + "  -  orgPolName: " + orgParams.get("OrgPolName"),"debug");        
        getCellFromReference("C21").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("L21").setCellValue(orgParams.get("BankName"));

        getCellFromReference("D26").setCellValue(orgParams.get("KODDohBK"));
        getCellFromReference("J26").setCellValue(orgParams.get("VidOper"));
        getCellFromReference("P26").setCellValue(orgParams.get("SrokPlat"));

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
        int realSringIndex = FIRST_ROW_NUM + strIndex;
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
        if (form.isFake25Format() == false) {
            c = prepareCell(r, columnNum); //H
            c.setCellValue(form.getPensionNumber());
            makeCellBorderedWrapedAlign(c, HorizontalAlignment.LEFT);
            columnNum += 1;
        }

        if (form.isFake25Format() == false) {
            c = prepareCell(r, columnNum); //I - добавлен ИНН
            c.setCellValue(form.getInn());
            makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
            columnNum += 1;
        }

        c = prepareCell(r, columnNum); //J
        if (form.getSumms()[0] != null) {
            c.setCellValue(form.getSumms()[0].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //K
        if (form.getSumms()[1] != null) {
            c.setCellValue(form.getSumms()[1].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //L
        if (form.getSumms()[2] != null) {
            c.setCellValue(form.getSumms()[2].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //M
        c.setCellValue(keepingTypeToString(form.getKeepingType()));
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.LEFT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //N
        c.setCellValue(form.getKeepingDocumentName());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //O
        c.setCellValue(form.getKeepingDocumentNumber());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //P
        c.setCellValue(form.getKeepingDocumentDate());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //Q
        c.setCellValue(form.getGetterAccount());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //R
        c.setCellValue(form.getGetterAdr());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //S
        c.setCellValue(form.getGetterFio());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //T
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
            case "1":  //'     
                type = "Алименты";
                break;
            case "2":  //'     
                type = "В пользу частного лица";
                break;
            case "3":  //'     
                type = "В пользу государства";
                break;
            case "4":  //'     
                type = "В пользу организации";
                break;
            case "5":  //'     
                type = "Перечисление в интернат";
                break;
            case "8":   //'    
                type = "Почтовые расходы по доставке";
                break;
            case "9":    //'   
                type = "Переплата";
                break;
        }
        return type;
    }

}
