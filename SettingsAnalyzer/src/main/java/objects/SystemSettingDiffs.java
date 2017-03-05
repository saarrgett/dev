package objects;

import db.DBHandler;
import db.ResultSetConverter;
import files_handlers.excel.csv.CsvHandler;
import files_handlers.excel.excel.ExcelComparer;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The class is used for finding the diffs between the environments
 * Created by Rachamim on 2/20/17.
 */
public class SystemSettingDiffs {

    //Data members
    String scrum;
    private String url = "jdbc:mysql://scrum-rds.gett.qa:3306/";
    private String replicaCloneProductionUrl = "jdbc:mysql://gt-country-cloned-db.gtforge.com/gettaxi_country_production";
    private String replicaCloneProductionUser = "automation";
    private String replicaCloneProductionPassword = "Auto!@2016";
    private String query = "Select * from ";
    private HashMap<Object, HashMap<Object, ArrayList<Object>>> diffsMaps;
    private String noSuchKeyStr = "No such key";

    //C'tor
    public SystemSettingDiffs(String scrum) {
        this.scrum = scrum;
        diffsMaps = new HashMap<Object, HashMap<Object, ArrayList<Object>>>();
    }

    /**
     * The function get the diffs from all countries by calling the getDiffsFromSpecificCountry with different parameters each time.
     * @return Hash map contains all the diffs from all the countries
     * @throws Exception
     */
    public HashMap<Object, HashMap<Object, ArrayList<Object>>> getDiffsFromAllCountries() throws Exception {
        getDiffsFromSpecificCountry("il", replicaCloneProductionUrl.replace("country", "il"), this.url + "il" + scrum, "il" + scrum, "il" + scrum, "system_settings");
        getDiffsFromSpecificCountry("ru", replicaCloneProductionUrl.replace("country", "ru"), this.url + "ru" + scrum, "ru" + scrum, "ru" + scrum, "system_settings");
        getDiffsFromSpecificCountry("uk", replicaCloneProductionUrl.replace("country", "uk"), this.url + "uk" + scrum, "uk" + scrum, "uk" + scrum, "system_settings");
        getDiffsFromSpecificCountry("us", replicaCloneProductionUrl.replace("country", "us"), this.url + "us" + scrum, "us" + scrum, "us" + scrum, "system_settings");
        return diffsMaps;
    }

    /**
     * Thr function connects to the Scrum and production DB, query and get settings key values.
     * It then compares them and get the differences. It goes through them and stores them in a (key, (scrumVal, productionVal)) structure.
     * @param country The country which is currently checked
     * @param productionUrl The url of the production, which we are connecting to
     * @param scrumUrl The url of the current scrum, which we are connecting to
     * @param username The user name used for the connection
     * @param password The password used for the connection
     * @throws Exception
     */
    private void getDiffsFromSpecificCountry(String country, String productionUrl, String scrumUrl, String username, String password, String table) throws Exception {
        //Connecting to the database of the current scrum and getting all the results according to the query
        DBHandler dbHandler = new DBHandler(scrumUrl, username, password);
        dbHandler.connect();
        ResultSet resultSet = dbHandler.executeQuery(query + table);
        Object[][] scrumObjMatrix = new ResultSetConverter().convertRStoMatrix(resultSet);

        //Connecting to the database of the production and getting all the results according to the query
        dbHandler = new DBHandler(productionUrl, replicaCloneProductionUser, replicaCloneProductionPassword);
        dbHandler.connect();
        resultSet = dbHandler.executeQuery(query + table);
        Object[][] productionObjMatrix = new ResultSetConverter().convertRStoMatrix(resultSet);

        //obtaining the white list
        ArrayList<String> whiteList = new CsvHandler().getWhiteListValues();
        List<List<Object>> diffFromScrumToProduction = new ExcelComparer().returnDiffsBetweenExcels(scrumObjMatrix, productionObjMatrix);
        List<List<Object>> diffFromProductionToScrum = new ExcelComparer().returnDiffsBetweenExcels(productionObjMatrix, scrumObjMatrix);

        //Initializing the diffs map
        HashMap<Object, ArrayList<Object>> diffsMap = new HashMap<Object, ArrayList<Object>>();

        updateAllTheDiffsOfValuesBetweenProductionToScrum(diffFromProductionToScrum, diffFromScrumToProduction, diffsMap, whiteList);

        updateAllTheDiffsOfKeysBetweenProductionToScrum(diffFromProductionToScrum, diffFromScrumToProduction, diffsMap, whiteList, "Scrum");

        updateAllTheDiffsOfKeysBetweenProductionToScrum(diffFromScrumToProduction, diffFromProductionToScrum, diffsMap, whiteList, "Production");

        diffsMaps.put(country, diffsMap);
    }

    /**
     * The function update all the diffs between Production to the Scrum
     * @param diffFromProductionToScrum List with diffs from production to scrum
     * @param diffFromScrumToProduction List with diffs from scrum to production
     * @param diffsMap A map contains all the diffs according key and list of values
     * @param whiteList
     */
    private void updateAllTheDiffsOfValuesBetweenProductionToScrum(List<List<Object>> diffFromProductionToScrum, List<List<Object>> diffFromScrumToProduction, HashMap<Object, ArrayList<Object>> diffsMap, ArrayList<String> whiteList) {
        //Taking all the diffs and put them in a (key, (scrumVal, productionVal)) structure
        for (List<Object> diffListOfProduction : diffFromProductionToScrum) {
            Object productionCurrentKey = diffListOfProduction.get(4);
            ArrayList<Object> values = new ArrayList<Object>();

            //The key is part of the white list, no need to consider it
            if(whiteList.contains(productionCurrentKey.toString()))
                continue;

            values.add(diffListOfProduction.get(5));

            for (List<Object> diffListOfScrum : diffFromScrumToProduction) {
                Object scrumCurrentKey = diffListOfScrum.get(4);
                if (productionCurrentKey.equals(scrumCurrentKey))
                    values.add(diffListOfScrum.get(5));
            }
            diffsMap.put(productionCurrentKey, values);
        }
    }

    /**
     * The function updates all the diffs between the keys.
     * for example keys that exist in production, but not in scrum.
     * Important - it compares from the first to the second.
     * It checks if there are keys in the first that are missing in the second.
     * @param env1 scrum\production
     * @param env2 scrum\production
     * @param diffsMap The Diffs contained in a map object.
     * @param whiteList A white list contains all the keys to ignore from.
     */
    private void updateAllTheDiffsOfKeysBetweenProductionToScrum(List<List<Object>> env1, List<List<Object>> env2, HashMap<Object, ArrayList<Object>> diffsMap, ArrayList<String> whiteList, String envStr) {
        //Going over all the keys of the production and checking if there is a missing key in the scrum env
        for(List<Object> diffListOfEnv1 : env1)
        {
            Object productionCurrentKey = diffListOfEnv1.get(4);
            boolean foundKey = false;

            if(whiteList.contains(productionCurrentKey.toString()))
                continue;

            for (List<Object> diffListOfEnv2 : env2) {
                Object scrumCurrentKey = diffListOfEnv2.get(4);
                if (productionCurrentKey.equals(scrumCurrentKey))
                {
                    foundKey = true;
                    break;
                }
            }

            if(!foundKey)
            {
                ArrayList<Object> values = new ArrayList<Object>();
                values.add(noSuchKeyStr + " in " + envStr);
                diffsMap.put(productionCurrentKey, values);
            }
        }
    }
}
