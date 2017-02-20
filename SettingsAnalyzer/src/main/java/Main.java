import db.DBHandler;
import db.ResultSetConverter;
import excel.ExcelComparer;
import excel.ExcelHandler;

import java.sql.*;
import java.util.List;

/**
 * Created by saar on 2/12/17.
 */
public class Main {
    public static String url = "scrum1";

    public static void main(String[] args) {
        try {
            SystemSettingDiffs systemSettingDiffs = new SystemSettingDiffs(url);
            systemSettingDiffs.getDiffsFromAllCountries();
            System.out.print("for debugging");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
