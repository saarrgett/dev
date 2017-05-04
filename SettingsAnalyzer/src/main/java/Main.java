import files_handlers.excel.excel.ExcelHandler;
import files_handlers.excel.txt.TxtHandler;
import interface_pack.TerminalConsole;
import javafx.util.Pair;
import objects.RootPathObject;
import objects.SystemSettingDiffs;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by saar on 2/12/17.
 */
public class Main {
    public static String scrum = "scrum2"; // The scrum from where the system settings will be taken from
    public static String rootPath = new RootPathObject().getRootPath();
    public static String fileName = scrum; //The name of the file. it will be saved under the user folder
    public static String whiteListRelativePath = rootPath + "/res/xls/"+scrum+".xlsx"; // The relative path of the white list file

    public static void main(String[] args) {
        try {
            HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>> whiteList;
            fileName = scrum = new TerminalConsole(args).handleUserInput(scrum);
            TxtHandler txtHandler = new TxtHandler(rootPath + "/res/txt/" + fileName + ".txt");
            ExcelHandler excelHandler =  new ExcelHandler(rootPath + "/system_settings_diffs/" + fileName + ".xlsx");
            whiteList = excelHandler.readFromComplexExcel(whiteListRelativePath);
            SystemSettingDiffs systemSettingDiffs = new SystemSettingDiffs( scrum , whiteList);
            HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>> map = systemSettingDiffs.getDiffsFromAllCountries();
            excelHandler.createExcelSpreadSheetFromHashMap(map);
//            txtHandler.writeToTxtFile(map);
            excelHandler.setFileName(whiteListRelativePath);
            excelHandler.createExcelSpreadSheetFromHashMap(systemSettingDiffs.getWhiteList());
            System.out.println("The programme passed successfully");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
