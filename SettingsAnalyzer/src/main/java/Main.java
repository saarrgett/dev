import excel.ExcelHandler;
import objects.SystemSettingDiffs;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by saar on 2/12/17.
 */
public class Main {
    public static String scrum = "scrum1";

    public static void main(String[] args) {
        try {
            SystemSettingDiffs systemSettingDiffs = new SystemSettingDiffs(scrum);
            HashMap<Object, HashMap<Object, ArrayList<Object>>> map = systemSettingDiffs.getDiffsFromAllCountries();
            new ExcelHandler("tryout.xlsx", "try").createExcelSpreadSheetOfAllDiffs(map);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
