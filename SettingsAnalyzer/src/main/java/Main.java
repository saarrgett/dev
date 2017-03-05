import files_handlers.excel.excel.ExcelHandler;
import objects.SystemSettingDiffs;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by saar on 2/12/17.
 */
public class Main {
    public static String scrum = "scrum10"; // The scrum from where the system settings will be taken from
    public static String fileName = "tryout.xlsx"; //The name of the file. it will be saved under the user folder

    public static void main(String[] args) {
        try {
            HashMap<Object, HashMap<Object, ArrayList<Object>>> whiteList = new HashMap<Object, HashMap<Object, ArrayList<Object>>>();
            ExcelHandler excelHandler =  new ExcelHandler(fileName);
            whiteList = excelHandler.readFromComplexExcel("res/whitelist.xlsx");
            SystemSettingDiffs systemSettingDiffs = new SystemSettingDiffs(scrum, whiteList);
            HashMap<Object, HashMap<Object, ArrayList<Object>>> map = systemSettingDiffs.getDiffsFromAllCountries();
            excelHandler.createExcelSpreadSheetFromHashMap(map);
            excelHandler.setFileName("res/whitelist.xlsx");
            excelHandler.createExcelSpreadSheetFromHashMap(systemSettingDiffs.getWhiteList());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
