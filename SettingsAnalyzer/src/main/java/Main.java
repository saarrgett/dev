import appHandler.menu;
import excel.ExcelHandler;
import objects.SystemSettingDiffs;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by saar on 2/12/17.
 */
public class Main {
    public static String scrum = "scrum1"; // The scrum from where the system settings will be taken from
    public static String fileName = "tryout.xlsx"; //The name of the file. it will be saved under the user folder

    public static void main(String[] args) {

        if (args.length==0){
            args = menu.getParameters();
            scrum = args[0];
            fileName = args[1];
        } else {
            if (args.length==1) {
                scrum = "scrum" + menu.validateScrumParameter(args[0]);
                fileName = menu.validateFileName("");
            } else {

                if (args.length==2) {
                    scrum = "scrum" + menu.validateScrumParameter(args[0]);
                    fileName = menu.validateFileName(args[1]);
                }
            }
        }

        System.out.println("analayzing: "+scrum);
        System.out.println("filename: "+fileName);

        try {
            SystemSettingDiffs systemSettingDiffs = new SystemSettingDiffs(scrum);
            HashMap<Object, HashMap<Object, ArrayList<Object>>> map = systemSettingDiffs.getDiffsFromAllCountries();
            new ExcelHandler(fileName).createExcelSpreadSheetOfAllDiffs(map);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
