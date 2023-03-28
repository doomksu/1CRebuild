package vedomosti.XLSWorkers;

import java.math.BigDecimal;
import vedomosti.common.XLSWorker;
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
import vedomosti.formats.Format23;
import vedomosti.forms.Form30Interface;

/**
 * Класс для записи 30х форм в xls файлы
 *
 * @author kneretin
 */
public class XLSWorker30 extends XLSWorker {

    private final int FIRST_ROW_NUM = 24;

    public XLSWorker30(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        super(_wb, _banks, _params);
    }

    protected void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName) {
        currentSheet = wb.cloneSheet(wb.getSheetIndex(cloneFromSheet));
        wb.setSheetName(wb.getSheetIndex(currentSheet), newSheetName);
        HashMap<String, String> orgParams = org.getParams();
        //header
        getCellFromReference("A3").setCellValue("за " + day + " " + month + " " + year + " г.");
        getCellFromReference("O3").setCellValue(day + "." + monthNum + "." + year);
        getCellFromReference("A2").setCellValue("Расчетная ведомость №___" + orgParams.get("PrilNumber") + "____");
        getCellFromReference("O4").setCellValue(params.get("KTOPFR"));
        getCellFromReference("O5").setCellValue(params.get("KSP"));
        getCellFromReference("O7").setCellValue(params.get("OKEI"));

        if (params.get("RegNumNameOrg").length() > 14) {
            getCellFromReference("E4").setCellValue(params.get("RegNumNameOrg").substring(0, 15));
            getCellFromReference("F4").setCellValue(params.get("RegNumNameOrg").substring(15));
        } else {
            getCellFromReference("E4").setCellValue(params.get("RegNumNameOrg"));
        }
        getCellFromReference("E5").setCellValue(params.get("NameStPodr"));
        getCellFromReference("E9").setCellValue(orgParams.get("OrgDostName"));
        getCellFromReference("E10").setCellValue(orgParams.get("OrgDostAdr"));
        getCellFromReference("A14").setCellValue(orgParams.get("OrgPolName"));
        getCellFromReference("E14").setCellValue(orgParams.get("OrgPolINN"));
        getCellFromReference("H14").setCellValue(orgParams.get("OrgPoltKPP"));
        getCellFromReference("M14").setCellValue(orgParams.get("OrgPolBank"));
        getCellFromReference("A18").setCellValue(orgParams.get("BankName"));
        getCellFromReference("E18").setCellValue(orgParams.get("BankBIK"));
        getCellFromReference("J18").setCellValue(orgParams.get("BankKorSch"));
        //end header
        // сдвиг строк в таблице для внесения выплат
        int paymentRowCount = org.getPaymentsCount();
        currentSheet.shiftRows(FIRST_ROW_NUM, currentSheet.getLastRowNum(), paymentRowCount, true, true);
        Iterator<String> kodsIt = org.getPaymentCodsIterator();

        int strIndex = 0;

        Form30Interface totalForm = null;    //итоговая строка по организации
        //записываем строки по датам и подитоги по датам 
        while (kodsIt.hasNext()) {
            String kod = kodsIt.next();
            Form30Interface totalByDateForm = null;  //итоговая строка текущей даты 
            for (Form form : org.getPaymentsByCod(kod)) {
                Form30Interface f30 = (Form30Interface) form;
                if (f30.isTotalByDateString()) {
                    totalByDateForm = f30;
                } else {
                    if (f30.isTotalString()) {
                        totalForm = f30;
                    } else {
                        //пишем строки по текущей дате
                        writeFormToRow(f30, strIndex);
                        f30.resetPrimaryKeyToKod();
                        strIndex++;
                    }
                }
            }
            //пишем итоговую строку по текущей дате
            if (totalByDateForm != null) {
                writeFormToRow(totalByDateForm, strIndex);
                totalByDateForm.resetPrimaryKeyToKod();
                strIndex++;
                totalByDateForm = null;
            }
        }
        //пишем итоговую строку по организации
        if (totalForm != null) {
            writeFormToRow(totalForm, strIndex);
            totalForm.resetPrimaryKeyToKod();
            strIndex++;
            totalForm = null;
        }

        //записали даты и переставили первичныйе ключи по 30 приложению на код выплаты -  переходим к разделению по кодам
        //теперь можно пройти по кодам и выписать статистику по ним
        currentSheet.shiftRows(FIRST_ROW_NUM + strIndex, currentSheet.getLastRowNum(), paymentRowCount, true, true);    //сдвининем строки 

        //Изменяем ключи выплатного контейнера на коды выплат
        org.reorgPaymentContainer();
        kodsIt = org.getPaymentCodsIterator();
        boolean firstKod = true;
        while (kodsIt.hasNext()) {
            String kod = kodsIt.next();
            BigDecimal summByCode = new BigDecimal(0);
            String f_kod = "";
            //проходим по всем кодам  - сумируем
            for (Form form : org.getPaymentsByCod(kod)) {
                Form30Interface f30 = (Form30Interface) form;
                summByCode = summByCode.add(f30.getTotalSumm());
                f_kod = f30.getKbk();
                f30.resetPrimaryKeyToDate();
            }
            if (kod.equals(Format23.TOTAL_PAYMENT_KOD) == false) {
                writeF30ByKod(f_kod, summByCode, firstKod, strIndex);
                strIndex++;
                firstKod = false;
            }
        }

        //Изменяем ключи выплатного контейнера на даты доставки
        org.reorgPaymentContainer();
        strIndex++;
    }

    private void writeF30ByKod(String f_kod, BigDecimal summByCode, boolean firstKod, int strIndex) {
        int columnNum = 6;
        CellRangeAddress cra = null;
        Cell c = null;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }
        if (firstKod == true) {
            c = prepareCell(r, columnNum);//G-H
            c.setCellValue("в том числе по коду расходов БК");
            cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
            mergeCells(cra);
            makeCellFullBordered(cra, currentSheet);
        }
        columnNum += 2;
        c = prepareCell(r, columnNum);//I-L 4:next 9
        c.setCellValue(f_kod);
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 3);
        mergeCells(cra);
        makeCellFullBordered(cra, currentSheet);
        columnNum += 4;

        c = prepareCell(r, columnNum);//M 1:next 12 - пропуск
        makeCellFullBordered(c, currentSheet);
        columnNum += 1;

        c = prepareCell(r, columnNum);//N-O 2:next 10
        c.setCellValue(summByCode.toPlainString().replace(".", ","));
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        makeCellFullBordered(cra, currentSheet);
        mergeCells(cra);

    }

    private void writeFormToRow(Form30Interface form, int strIndex) {
        int columnNum = 0;
        Row r = currentSheet.getRow(FIRST_ROW_NUM + strIndex);
        if (currentSheet.getRow(FIRST_ROW_NUM + strIndex) == null) {
            r = currentSheet.createRow(FIRST_ROW_NUM + strIndex);
        }
        CellRangeAddress cra = null;
        Cell c = prepareCell(r, columnNum);//A
        c.setCellValue(form.getDate());
        makeCellFullBordered(c, currentSheet);
        columnNum++;

        c = prepareCell(r, columnNum);//B-C next 3
        if (form.isTotalByDateString() == false) {
            c.setCellValue(form.getKod());
        }
        c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        c.getCellStyle().setWrapText(true);
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        mergeCells(cra);
        makeCellFullBordered(cra, currentSheet);
        columnNum += 2;

        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 4);
        c = prepareCell(r, columnNum);//D-H 5: next 8
        if (form.isTotalByDateString()) {
            c.setCellValue("Итого по дате:");
        } else {
            c.setCellValue(SettingsReader.getInstance().getSTPaymentName(form.getKod()));
        }
        if (SettingsReader.getInstance().getSTPaymentName(form.getKod()).length() > 30) {
            r.setHeight((short) (r.getHeight() * 4));
        }
        mergeCells(cra);
        c.getCellStyle().setAlignment(HorizontalAlignment.LEFT);
        makeCellFullBordered(cra, currentSheet);
        columnNum += 5;

        c = prepareCell(r, columnNum);//I-L 4:next 9
        c.setCellValue(form.getKbk().isEmpty() ? "X" : form.getKbk());
        c.getCellStyle().setAlignment(HorizontalAlignment.CENTER);
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 3);
        mergeCells(cra);
        makeCellFullBordered(cra, currentSheet);
        columnNum += 4;

        c = prepareCell(r, columnNum);//M 1:next 12
        c.setCellValue(form.getPersonByDateCount());
        makeCellFullBordered(c, currentSheet);
        columnNum += 1;

        c = prepareCell(r, columnNum);//N-O 2:next 10
        c.setCellValue(form.getSumms()[0].toPlainString().replace(".", ","));
        cra = new CellRangeAddress(r.getRowNum(), r.getRowNum(), columnNum, columnNum + 1);
        makeCellFullBordered(cra, currentSheet);
        mergeCells(cra);

    }

}
