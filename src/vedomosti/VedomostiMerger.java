package vedomosti;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import service.LoggingService;
import settings.SettingsReader;
import vedomosti.common.Format;
import vedomosti.common.XLSWorker;
import vedomosti.formats.Format25;

/**
 *
 * @author kneretin
 */
abstract class VedomostiMerger {

    protected OrganizationsGroup orgsGroup;
    protected ArrayList<OrganizationsGroup> fileOrgGroups;
    //в 26 формате у организаций может быть много ключей  - UnInnKpp + МИР 1\0 + признак добавочный ключ формы
    protected String fileHeader;
    protected int vedomostNumber;
    protected Format format;
    protected XLSWorker xlsWorker;
    protected HashSet<String> additionalKeys = new HashSet<>();

    protected void printBanksHandler(boolean hasBothFormats) {
        String formatHeader = "";
        if (hasBothFormats) {
            formatHeader = format.createHeaderString(Format25.FORMAT_NAME);
        } else {
            formatHeader = format.createHeaderString();
        }
        splitOrgsGroupByAdditionalKeys();
        printBanks(formatHeader);
    }

    //todo - if need to split files by local and foreign
    private void splitOrgsGroupByAdditionalKeys() {
        HashSet<String> differentOrgsKeys = orgsGroup.getUniqueAdditionalKeys();
//        LoggingService.writeLog(">>unique keys: " + differentOrgsKeys.size(), "debug");
        fileOrgGroups = new ArrayList<>();
        for (String addKey : differentOrgsKeys) {
            OrganizationsGroup orgsG = new OrganizationsGroup();
            orgsG.fillAll(orgsGroup.getAllOrgsByAdditionalKey(addKey));
            fileOrgGroups.add(orgsG);
        }
//        LoggingService.writeLog(">>fileOrgGroups: " + fileOrgGroups.size(), "debug");
    }

    /**
     * Вывод банков в свод
     *
     * @param formatHeader строка с форматом для файла для 36 формы в пользу
     * орг... 1 - для 36 в пользу частных лиц и алименты
     */
    private void printBanks(String formatHeader) {
        try {
            printOrgGroups();
            for (OrganizationsGroup og : fileOrgGroups) {
                File outFile = new File(SettingsReader.getInstance().getValue("EXCELFILES")
                        + SettingsReader.getInstance().getCurrentOutFileName(vedomostNumber));
                LoggingService.writeLog("write rgGroup", "debug");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "cp866"));
                writer.write(formatHeader + "\n");
                for (Organization org : og.getOrgList()) {
                    for (String string : org.getStrings()) {
                        writer.write(string);
                    }
                    writer.flush();
                }
                writer.close();
            }
        } catch (Exception ex) {
            LoggingService.writeLog(ex);
        }
    }

    public void printOrgGroups() {
        LoggingService.writeLog("print  organization: ", "debug");
        for (OrganizationsGroup og : fileOrgGroups) {
            og.print();
        }
    }

    public Format getFormat() {
        return format;
    }

}
