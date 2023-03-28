package vedomosti;

import java.io.File;
import java.util.concurrent.Callable;
import vedomosti.common.FormParseException;

/**
 * Файл ведомости - Умеет читать ведомость и объединять выплатные организации по
 * файлу и вызываться в отдельном потоке
 *
 *
 * @author kneretin
 */
//нужен еще 1 класс в иерархию создающий xls файлы
class VedomostiFile extends VedomostiReader implements Callable<Boolean> {

    private File outXLS;
    private Exception savedException;

    public VedomostiFile(int _vedomostNumber, File _inFile, File _outXLS) {
        super(_vedomostNumber, _inFile);
        outXLS = _outXLS;
        savedException = null;
    }

    @Override
    public Boolean call() throws Exception, FormParseException {
        if (checkFile()) {
            try {
                read();
            } catch (FormParseException fpex) { //получили ошибку чтения форм по файлу, добавляем имя файла и снова выбрасываем
                String fpexMessage = fpex.getMessage();
                fpex = new FormParseException("в файле: " + this.file.getName() + "  " + fpexMessage);
                savedException = fpex;
            }
        }else{
            throw new FormParseException("Имя файла не прошло проверку: "+ file.getName());
        }
                
        return true;
    }

    /**
     * Открыть файл xls, записать загруженные организации
     *
     * @throws Exception
     *
     */
    public int writeLoadedOrganizations(int position) throws Exception {
        if (xlsWorker == null) {
            if (format == null) {
                throw new IllegalArgumentException("Не установлен формат для обработки файлов");
            }
            xlsWorker = format.createWorker(orgsGroup, vedomostNumber);
        }
        xlsWorker.setBanks(orgsGroup);
        position = xlsWorker.writeLoadedFromFile(file, position);
        return position;
    }

    public Exception getSavedException() {
        return savedException;
    }

}
