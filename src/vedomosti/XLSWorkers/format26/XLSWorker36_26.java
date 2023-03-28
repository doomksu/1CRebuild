package vedomosti.XLSWorkers.format26;

import vedomosti.common.XLSWorker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
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
import vedomosti.forms.foramt26.Form36_26;

/**
 * Класс для записи 36х форм в xls файлы – тип «УДЕРЖАНИЕ»
 *
 * @author kneretin
 */
public class XLSWorker36_26 extends XLSWorker {

    private final int FIRST_ROW_NUM = 35;

    public XLSWorker36_26(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("F12").setCellValue("на \"" + day + "\" " + month + " " + year + " г.");
        getCellFromReference("T12").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("F10").setCellValue("Реестр №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("T13").setCellValue(params.get("KTOPFR"));
        getCellFromReference("T14").setCellValue(params.get("KSP"));
        getCellFromReference("T15").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("F13").setCellValue(params.get("RegNumNameOrg").substring(0, 15));
            getCellFromReference("G13").setCellValue(params.get("RegNumNameOrg").substring(15));
        } else {
            getCellFromReference("F13").setCellValue(params.get("RegNumNameOrg"));
        }
        getCellFromReference("F14").setCellValue(params.get("NameStPodr"));

        getCellFromReference("F17").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("F18").setCellValue(orgParams.get("OrgDostAdr"));

        LoggingService.writeLog("sheet: " + newSheetName + "  -  orgPolName: " + orgParams.get("OrgPolName"), "debug");
        getCellFromReference("D23").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("L23").setCellValue(orgParams.get("BankName"));
        LoggingService.writeLog(""
                + "\r\n\t OrgPolName: " + orgParams.get("OrgPolName")
                + "\r\n\t BankName: " + orgParams.get("BankName"), "debug");

        getCellFromReference("A26").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("C26").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("E26").setCellValue(orgParams.get("OrgPolOKATO"));
        getCellFromReference("H26").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("H26").getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));
        getCellFromReference("K26").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("M26").setCellValue(orgParams.get("BankKorSch"));

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
//        LoggingService.writeLog("will write FORM36:  " + form.getClass().getName(),"debug");
        Form36_26 fform = (Form36_26) form;
//        LoggingService.writeLog(fform.formToString(),"debug");

        int columnNum = 0;
        CellRangeAddress cra = null;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }
        Cell c = prepareCell(r, columnNum);//A
        c.setCellValue(fform.getKod());
        makeCellFullBordered(c, currentSheet);
        c.getCellStyle().setWrapText(true);
        columnNum++;

        c = prepareCell(r, columnNum);//B-C
        c.setCellValue(SettingsReader.getInstance().getSTs().getST(fform.getKod()).getName());   //Цепочка получения имени выплаты
        if (SettingsReader.getInstance().getSTs().getST(fform.getKod()).getName().length() > 30) {
            r.setHeight((short) (r.getHeight() * 4.5));
        }
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER);
        columnNum += 2;

        c = prepareCell(r, columnNum); //D-E
        c.setCellValue(fform.getKbk());
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBorderedAndAlign(cra, currentSheet, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 2;

        c = prepareCell(r, columnNum); //F
        c.setCellValue(fform.getFio());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //G
        c.setCellValue(fform.getPostAdr());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //H
        c.setCellValue(fform.getPensionNumber());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //I
        c.setCellValue(fform.getPensionINN());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //J
        if (fform.getSumms()[0] != null) {
            c.setCellValue(fform.getSumms()[0].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //K
        if (fform.getSumms()[1] != null) {
            c.setCellValue(fform.getSumms()[1].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //L
        if (fform.getSumms()[2] != null) {
            c.setCellValue(fform.getSumms()[2].toPlainString().replace(".", ","));
        } else {
            c.setCellValue(0);
        }
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.RIGHT);
        columnNum += 1;

        c = prepareCell(r, columnNum); //M
        c.setCellValue(keepingTypeToString(fform.getKeepingType()));
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //N
        c.setCellValue(fform.getKeepingDocumentName());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //O
        c.setCellValue(fform.getKeepingDocumentNumber());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //P
        c.setCellValue(fform.getKeepingDocumentDate());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //Q
        c.setCellValue(fform.getGetterAccount());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //R
        c.setCellValue(fform.getGetterAdr());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //S
        c.setCellValue(fform.getGetterFio());
        makeCellBorderedWrapedAlign(c, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        columnNum += 1;

        c = prepareCell(r, columnNum); //T
        c.setCellValue(fform.getUin());
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
