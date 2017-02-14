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
    public static String url = "jdbc:mysql://scrum-rds.gett.qa:3306/ilscrum1";
    public static String username = "ilscrum1";
    public static String password = "ilscrum1";
    public static String sheetName = "try";
    public static String query = "Select * from system_settings";
    public static String file1Str = "/Users/saar/test.xlsx";
    public static String file2Str = "/Users/saar/test2.xlsx";



    public static void main(String[] args) {
        try {
            //Connection to DB and getting results according to query
            DBHandler dbHandler = new DBHandler(url, username, password);
            dbHandler.connect();
            ResultSet resultSet = dbHandler.executeQuery(query);
            Object[][] objMatrix = new ResultSetConverter().convertRStoMatrix(resultSet);

            //Creating excel from data and comparing them
            new ExcelHandler(file1Str, sheetName).createExcelSpreadSheet(objMatrix);
            new ExcelHandler(file2Str, sheetName).createExcelSpreadSheet(objMatrix);
            Object[][] matrix = new ExcelHandler(file1Str, sheetName).readFromExcel();
            Object[][] matrix2 = new ExcelHandler(file2Str, sheetName).readFromExcel();

            List<List<Object>> diffListOfLists = new ExcelComparer().returnDiffsBetweenExcels(matrix, matrix2);
            for (List<Object> diffList : diffListOfLists) {
                System.out.println("There is a difference at the pair: " + diffList.get(4) + ", " + diffList.get(5));
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
