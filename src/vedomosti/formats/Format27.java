package vedomosti.formats;

import java.util.ArrayList;

/**
 *
 * @author kneretin
 */
public class Format27 extends Format26 {

    public static String FORMAT_NAME = "27.";
    public static String DELIMETR = "|";
    static String XLSFolder = "\\_27";
    static String XLSPostfix = "_27";
    private ArrayList<Integer> forms = new ArrayList<Integer>();

    public Format27(String _header) {
        super(_header);
        ORGChunksNames = new String[]{"", "", "", "", "PrilDate", "PrilNumber", "", "", "", "", "OrgDostName", "OrgDostAdr",
            "OrgDostINN", "OrgDostKPP", "OrgPolName", "OrgPolINN", "OrgPoltKPP", "OrgPolBank",
            "OrgPolOKATO", "BankName", "BankBIK", "BankKorSch", "KODDohBK", "VidOper", "SrokPlat",
            "incomeManager", "budgetName", "incomeType", "orgType", "foregnKey", "causeOfClose",
            "serviceInfo", "raionKodeByKLADR", "deliveryDate", "kodVidDoh"};
        ORGChunksLenght = new int[]{11, 2, 2, 1, 10, 6, 3, 20, 2, 4, 250, 200, 15, 15, 250, 15, 15, 20, 20, 160, 15, 20, 20, 30, 10, 10, 20, 160, 1, 2, 100, 130, 3, 10, 1};
        MAX_DAYLI_COUNT = 99;
        //максимальное количество файлов за день для этого формата увеличено до 99 
        int[] _f = {32, 34, 35, 36, 46, 49, 51, 57, 65, 69, 71};
        for (int i : _f) {
            forms.add(i);
        }
    }

}
