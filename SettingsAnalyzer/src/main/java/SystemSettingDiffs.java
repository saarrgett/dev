import db.DBHandler;
import db.ResultSetConverter;
import excel.ExcelComparer;
import excel.ExcelHandler;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by Rachamim on 2/20/17.
 */
public class SystemSettingDiffs {
    String scrum;
    private String url = "jdbc:mysql://scrum-rds.gett.qa:3306/";
    private String username = "ilscrum1";
    private String password = "ilscrum1";

    private String replicaCloneProductionUrl = "jdbc:mysql://gt-country-cloned-db.gtforge.com/gettaxi_country_production";
    private String replicaCloneProductionUser = "automation";
    private String replicaCloneProductionPassword = "Auto!@2016";

    private String sheetName = "try";
    private String query = "Select * from system_settings";
    private String file1Str = "/test.xlsx";
    private String file2Str = "/test2.xlsx";

    public SystemSettingDiffs(String scrum) {
        this.scrum = scrum;
    }

    public List<List<Object>> getDiffsFromAllCountries() throws Exception {
        getDiffsFromSpecificCountry("il", this.url+"il"+scrum, "il" + scrum, "il" + scrum, "il" + scrum, "try");
        getDiffsFromSpecificCountry("ru", this.url+"ru"+scrum, "ru" + scrum, "ru" + scrum, "ru" + scrum, "try");
        getDiffsFromSpecificCountry("uk", this.url+"uk"+scrum, "uk" + scrum, "uk" + scrum, "uk" + scrum, "try");
        getDiffsFromSpecificCountry("us",this.url+"us"+scrum, "us" + scrum, "us" + scrum, "us" + scrum, "try");
        return null;
    }

    private List<List<Object>> getDiffsFromSpecificCountry(String country, String scrumUrl, String username, String password, String fileStr, String sheetName) throws Exception {
        String productionUrl = replicaCloneProductionUrl.replace("country", country);

        System.out.println(country);

        DBHandler dbHandler = new DBHandler(scrumUrl, username, password);
        dbHandler.connect();
        ResultSet resultSet = dbHandler.executeQuery(query);
        Object[][] objMatrix = new ResultSetConverter().convertRStoMatrix(resultSet);

        dbHandler = new DBHandler(productionUrl, replicaCloneProductionUser, replicaCloneProductionPassword);
        dbHandler.connect();
        resultSet = dbHandler.executeQuery(query);
        Object[][] productionObjMatrix = new ResultSetConverter().convertRStoMatrix(resultSet);

        //Creating excel from data and comparing them
        new ExcelHandler(file1Str, sheetName).createExcelSpreadSheet(objMatrix);
        new ExcelHandler(file2Str, sheetName).createExcelSpreadSheet(productionObjMatrix);
        Object[][] matrix = new ExcelHandler(file1Str, sheetName).readFromExcel();
        Object[][] matrix2 = new ExcelHandler(file2Str, sheetName).readFromExcel();

        List<List<Object>> diffListOfLists = new ExcelComparer().returnDiffsBetweenExcels(matrix, matrix2);
        for (List<Object> diffList : diffListOfLists) {
            System.out.println("There is a difference at the pair: " + diffList.get(4) + ", " + diffList.get(5));
        }
        return diffListOfLists;
    }
}
