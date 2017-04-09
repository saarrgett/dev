package files_handlers.excel.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The class is used for handling with CSV file.
 * for exmaple for reading from a csv file.
 * Created by Rachamim on 2/27/17.
 */
public class CsvHandler {

    //Data members
    String whiteListRelativePath; //The initial value from the whitelist file
    BufferedReader br;
    String csvSeperator; //The separator used to parse the values from the csv file

    //C'tor
    public CsvHandler(String whiteListRelativePath){
        this.whiteListRelativePath = whiteListRelativePath;
        csvSeperator = ";";
    }

    /**
     * Getting the white list, meaning all the keys that can be ignored and won't be reported as a mismatch
     * @return a list of Strings containing all the values in the white list file
     */
    public ArrayList<String> getWhiteListValues(){
        ArrayList<String> allValuesList = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(whiteListRelativePath));
            String allValuesString = br.readLine();
            allValuesList.addAll(Arrays.asList(allValuesString.split(csvSeperator)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allValuesList;
    }
}
