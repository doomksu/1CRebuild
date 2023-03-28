package vedomosti.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import service.LoggingService;
import vedomosti.Organization;
import vedomosti.OrganizationsGroup;

/**
 * Абстрактный класс XLS обработчика
 *
 * @author kneretin
 */
public abstract class XLSWorker {

    protected Sheet currentSheet;
    protected final String LIST_SHEET_NAME = "Лист1";
    protected final String LIST_SVOD_NAME = "СВОД";//Имя листа для свода "СВОД" + (n)
    protected final String LIST_OF_LOADED_NAME = "СписокЗагрузка";
    protected final String LIST_OF_SVOD_NAME = "СписокСВОД";//Имя листа для списка свода "СписокСВОД"
    protected final String STR_TOTAL_BY_FILE = " итоговая сумма  по файлу ";
    protected final String STR_TOTAL_BY_BANK = " итоговая сумма по банкам ";
    protected final String STR_TOTAL_BY_SVOD = "Итого СВОД";
    private final String[] monthNames = {"январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"};
    protected String day;
    /**
     * Имя месяца вычисленного при формировании ведомости
     */
    protected String month;
    /**
     * Номер месяца вычисленного при формировании ведомости
     */
    protected String monthNum;
    protected String year;
    protected Workbook wb;
    protected OrganizationsGroup orgGroup;
    protected Sheet sample;
    protected HashMap<String, String> params;
    private GregorianCalendar gc;
    protected HSSFCellStyle borderedStyle;
    protected HSSFCellStyle borderedStyleLEFT;
    protected HSSFCellStyle borderedStyleRIGHT;
    protected HSSFCellStyle borderedStyleCENTER;
    protected HSSFCellStyle borderedStyleCENTER_CENTER;
    protected HSSFCellStyle borderedStyleRIGHT_NUMBERFORMAT;
    private File fileOut;

    protected abstract void writeOrganization(Organization org, Sheet cloneFromSheet, String newSheetName);

    public XLSWorker(Workbook _wb, OrganizationsGroup _banks, HashMap<String, String> _params) {
        LoggingService.writeLog("create common XLSWorker", "debug");
        wb = _wb;
        orgGroup = _banks;
        params = _params;
        gc = new GregorianCalendar();
        day = String.valueOf(gc.get(Calendar.DAY_OF_MONTH));
//        LoggingService.writeLog(">> month num: "+gc.get(Calendar.MONTH), "debug");
        month = monthNames[gc.get(Calendar.MONTH)];
        monthNum = (gc.get(Calendar.MONTH) + 1) < 10
                ? "0" + String.valueOf(gc.get(Calendar.MONTH) + 1)
                : String.valueOf(gc.get(Calendar.MONTH) + 1);
        year = String.valueOf(gc.get(Calendar.YEAR));

        borderedStyle = (HSSFCellStyle) wb.createCellStyle();
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setWrapText(true);

        borderedStyleLEFT = (HSSFCellStyle) wb.createCellStyle();
        borderedStyleLEFT.cloneStyleFrom(borderedStyle);
        borderedStyleLEFT.setAlignment(HorizontalAlignment.LEFT);

        borderedStyleRIGHT = (HSSFCellStyle) wb.createCellStyle();
        borderedStyleRIGHT.cloneStyleFrom(borderedStyle);
        borderedStyleRIGHT.setAlignment(HorizontalAlignment.RIGHT);

        borderedStyleCENTER = (HSSFCellStyle) wb.createCellStyle();
        borderedStyleCENTER.cloneStyleFrom(borderedStyle);
        borderedStyleCENTER.setAlignment(HorizontalAlignment.CENTER);

        borderedStyleCENTER_CENTER = (HSSFCellStyle) wb.createCellStyle();
        borderedStyleCENTER_CENTER.cloneStyleFrom(borderedStyle);
        borderedStyleCENTER_CENTER.setAlignment(HorizontalAlignment.CENTER);
        borderedStyleCENTER_CENTER.setVerticalAlignment(VerticalAlignment.CENTER);

        borderedStyleRIGHT_NUMBERFORMAT = (HSSFCellStyle) wb.createCellStyle();
        borderedStyleRIGHT_NUMBERFORMAT.cloneStyleFrom(borderedStyleRIGHT);
        borderedStyleRIGHT_NUMBERFORMAT.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    }

    /**
     * Запись СВОД
     *
     */
    public void writeSvod() {
        LoggingService.writeLog("will write svod", "debug");
        sample = wb.getSheet(LIST_SHEET_NAME);  //лист аналогичен пофайловому списку
        BigDecimal fullTotalSumm = new BigDecimal(0);
        int number = 0;
        for (Organization organization : orgGroup.getOrgList()) {
            writeOrganization(organization, sample, LIST_SVOD_NAME + "(" + number + ")");
            String linkToName = LIST_SVOD_NAME + "(" + number + ")";
            String orgTotalSumm = "";
            if (organization.getTotalString() != null) {
                orgTotalSumm = organization.getTotalString().getTotalSumm().toPlainString().replace(".", ",");
                fullTotalSumm = fullTotalSumm.add(organization.getTotalString().getTotalSumm());
            }
            writeStringSchema(wb.getSheet(this.LIST_OF_SVOD_NAME), number, linkToName, organization.getName(), orgTotalSumm, true);
            number++;
        }
        String strFullTotalSumm = fullTotalSumm.toPlainString().replace(".", ",");
        writeStringSchema(wb.getSheet(this.LIST_OF_SVOD_NAME), number, STR_TOTAL_BY_SVOD, STR_TOTAL_BY_BANK, strFullTotalSumm, false);
    }

    /**
     * Добавить строку в Список Загрузка/Список СВОД
     *
     * @param writeTo
     * @param number
     * @param schemaName
     * @param orgName
     * @param totalSumm
     * @param addLink
     */
    protected void writeStringSchema(Sheet writeTo, int number, String schemaName, String orgName, String totalSumm, boolean addLink) {
        currentSheet = writeTo;
        Row r = currentSheet.createRow((number + 1));
        Cell c = r.createCell(0);
        c.setCellValue(schemaName);
        makeCellFullBordered(c, writeTo);
        if (addLink) {
            CellStyle hlink_style = wb.createCellStyle();
            Font hlink_font = wb.createFont();
            hlink_font.setUnderline(Font.U_SINGLE);
            hlink_font.setColor(IndexedColors.BLUE.getIndex());
            hlink_style.setFont(hlink_font);
            hlink_style.setBorderBottom(BorderStyle.THIN);
            hlink_style.setBorderTop(BorderStyle.THIN);
            hlink_style.setBorderLeft(BorderStyle.THIN);
            hlink_style.setBorderRight(BorderStyle.THIN);

            CreationHelper createHelper = wb.getCreationHelper();
            Hyperlink link2 = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
            link2.setAddress("'" + schemaName + "'!A1");
            c.setHyperlink(link2);
            c.setCellStyle(hlink_style);
        }
        c = r.createCell(1);
        c.setCellValue(orgName);
        makeCellFullBordered(c, writeTo);
        c = r.createCell(2);
        c.setCellValue(totalSumm);
        c.setCellStyle(borderedStyleRIGHT_NUMBERFORMAT);
    }

    /**
     * Запись организаций ДО объединения
     *
     * @param file
     * @param orgPositionInList
     * @return
     */
    public int writeLoadedFromFile(File file, int orgPositionInList) {
        sample = wb.getSheet(LIST_SHEET_NAME);
        BigDecimal fullTotalSumm = new BigDecimal(0);
        int number = 0;

        for (Organization organization : orgGroup.getOrgList()) {
            String linkToName = file.getName() + "(" + number + ")";
            writeOrganization(organization, sample, linkToName);
            String orgTotalSumm = "";
            if (organization.getTotalString() != null) {
                orgTotalSumm = organization.getTotalString().getTotalSumm().toPlainString().replace(".", ",");
                fullTotalSumm = fullTotalSumm.add(organization.getTotalString().getTotalSumm());
            }
            writeStringSchema(wb.getSheet(this.LIST_OF_LOADED_NAME), orgPositionInList, linkToName, organization.getName(), orgTotalSumm, true);
            number++;
            orgPositionInList++;
        }
        String strFullTotalSumm = fullTotalSumm.toPlainString().replace(".", ",");
        writeStringSchema(wb.getSheet(this.LIST_OF_LOADED_NAME), orgPositionInList, file.getName(), STR_TOTAL_BY_FILE, strFullTotalSumm, false);
        number++;
        orgPositionInList++;
        try {
            flush();
        } catch (IOException ex) {
            LoggingService.writeLog(ex);
        }
        return orgPositionInList;
    }

    /**
     * Объединить ячейки
     *
     * @param mCra
     */
    protected void mergeCells(CellRangeAddress mCra) {
        boolean merge = true;
        for (int i = 0; i < currentSheet.getNumMergedRegions(); i++) {
            CellRangeAddress cra = currentSheet.getMergedRegion(i);
            if (cra.containsRow(mCra.getFirstRow())) {
                if (cra.containsColumn(mCra.getFirstColumn()) || cra.containsColumn(mCra.getLastColumn())) {
                    merge = false;
                }
            }
        }
        if (merge) {
            currentSheet.addMergedRegion(mCra);
        }
    }

    /**
     * Получить ячейку по текстовому адресу
     *
     * @param adr
     * @return
     */
    protected Cell getCellFromReference(String adr) {
        if (currentSheet != null) {
            CellReference cRef = new CellReference(adr);
            Row row = currentSheet.getRow(cRef.getRow());
            Cell cell = row.getCell(cRef.getCol());
            return cell;
        }
        return null;
    }

    /**
     * Задать рамки и позиционирование текста (если указано) у всего региона
     * ячеек
     *
     * @param cra
     * @param sheet
     * @param al
     */
    protected void alterCellStyle(CellRangeAddress cra, Sheet sheet, HorizontalAlignment al) {
        for (int i = cra.getFirstRow(); i <= cra.getLastRow(); i++) {
            Row row = sheet.getRow(i);
            for (int j = cra.getFirstColumn(); j <= cra.getLastColumn(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                if (al != null) {
                    makeCellBorderedWrapedAlign(cell, al);
                } else {
                    makeCellFullBordered(cell, sheet);
                }
            }
        }
    }

    protected void alterCellStyle(CellRangeAddress cra, Sheet sheet, HorizontalAlignment al, VerticalAlignment val) {
        for (int i = cra.getFirstRow(); i <= cra.getLastRow(); i++) {
            Row row = sheet.getRow(i);
            for (int j = cra.getFirstColumn(); j <= cra.getLastColumn(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                }
                if (al != null) {
                    makeCellBorderedWrapedAlign(cell, al, val);
                } else {
                    makeCellFullBordered(cell, sheet);
                }
            }
        }
    }

    protected void makeCellBorderedWrapedAlign(Cell c, HorizontalAlignment al) {
        if (al == HorizontalAlignment.CENTER) {
            c.setCellStyle(borderedStyleCENTER);
        }
        if (al == HorizontalAlignment.LEFT) {
            c.setCellStyle(borderedStyleLEFT);
        }
        if (al == HorizontalAlignment.RIGHT) {
            c.setCellStyle(borderedStyleRIGHT);
        }
        if (al == null) {
            c.setCellStyle(borderedStyle);
        }
    }

    protected void makeCellBorderedWrapedAlign(Cell c, HorizontalAlignment al, VerticalAlignment val) {
        if (val == VerticalAlignment.CENTER) {
            c.setCellStyle(borderedStyleCENTER_CENTER);
        } else {
            makeCellBorderedWrapedAlign(c, al);
        }
    }

    protected void setLastMonthDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int mNUm = gc.get(Calendar.MONTH);
        if (mNUm == 0) {
            mNUm = 11;
            year = String.valueOf(gc.get(Calendar.YEAR) - 1);
        } else {
            mNUm = mNUm - 1;
        }
        month = monthNames[mNUm];
    }

    public void done() throws IOException {
        wb.removeSheetAt(wb.getSheetIndex("Лист1"));
        FileOutputStream outStream = new FileOutputStream(fileOut);
        wb.write(outStream);
        outStream.close();
    }

    public void flush() throws IOException {
        FileOutputStream outStream = new FileOutputStream(fileOut);
        wb.write(outStream);
        outStream.close();
    }

    public CellStyle cloneStyleFromCell(Cell cell) {
        CellStyle newCellStyle = wb.createCellStyle();
        newCellStyle.cloneStyleFrom(cell.getCellStyle());
        return newCellStyle;
    }

    public File getFileOut() {
        return fileOut;
    }

    public void setOUTXLS(File outXLS) {
        fileOut = outXLS;
    }

    public void setBanks(OrganizationsGroup banks) {
        this.orgGroup = banks;
    }

    /**
     * Задать рамки у всего региона ячеек
     *
     * @param cra
     * @param sheet
     */
    protected void makeCellFullBordered(CellRangeAddress cra, Sheet sheet) {
        alterCellStyle(cra, sheet, null);
    }

    /**
     * Задать рамки и позиционирование текста у всего региона ячеек
     *
     * @param cra
     * @param sheet
     * @param al
     */
    protected void makeCellFullBorderedAndAlign(CellRangeAddress cra, Sheet sheet, HorizontalAlignment al) {
        alterCellStyle(cra, sheet, al);
    }

    protected void makeCellFullBorderedAndAlign(CellRangeAddress cra, Sheet sheet, HorizontalAlignment al, VerticalAlignment val) {
        alterCellStyle(cra, sheet, al, val);
    }

    protected Cell prepareCell(Row r, int cellIndex) {
        return r.getCell(cellIndex) == null ? r.createCell(cellIndex) : r.getCell(cellIndex);
    }

    /**
     * Выставить у ячейки все границы
     *
     * @param c
     * @param sheet
     */
    protected void makeCellFullBordered(Cell c, Sheet sheet) {
        c.setCellStyle(borderedStyle);
    }

}
