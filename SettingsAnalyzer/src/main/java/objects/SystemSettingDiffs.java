package objects;

import db.DBHandler;
import db.ResultSetConverter;
import files_handlers.excel.excel.ExcelComparer;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.util.*;

/**
 * The class is used for finding the diffs between the environments
 * Created by Rachamim on 2/20/17.
 */
public class SystemSettingDiffs {

    //Data members
    private String scrum;
    private String url = "jdbc:mysql://scrum-rds.gett.qa:3306/";
    private String replicaCloneProductionUrl = "jdbc:mysql://gt-country-cloned-db.gtforge.com/gettaxi_country_production";
    private String replicaCloneProductionUser = "automation";
    private String replicaCloneProductionPassword = "Auto!@2016";
    private String query = "Select * from ";
    private HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>> diffsMaps;


    private HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>> whiteList;
    private String noSuchKeyStr = "No such key";

    //C'tor
    public SystemSettingDiffs(String scrum, HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>> whiteList) {
        this.scrum = scrum;
        this.whiteList = whiteList;
        diffsMaps = new HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>>();
    }

    public HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>> getWhiteList() {
        return whiteList;
    }

    /**
     * The function get the diffs from all countries by calling the getDiffsFromSpecificCountry with different parameters each time.
     *
     * @return Hash map contains all the diffs from all the countries
     * @throws Exception
     */
    public HashMap<Object, HashMap<Pair<Object, Object>, ArrayList<Object>>> getDiffsFromAllCountries() throws Exception {
        getDiffsFromSpecificCountry("il", replicaCloneProductionUrl.replace("country", "il"), this.url + "il" + scrum, "il" + scrum, "il" + scrum, "system_settings");
        getDiffsFromSpecificCountry("ru", replicaCloneProductionUrl.replace("country", "ru"), this.url + "ru" + scrum, "ru" + scrum, "ru" + scrum, "system_settings");
        getDiffsFromSpecificCountry("uk", replicaCloneProductionUrl.replace("country", "uk"), this.url + "uk" + scrum, "uk" + scrum, "uk" + scrum, "system_settings");
        getDiffsFromSpecificCountry("us", replicaCloneProductionUrl.replace("country", "us"), this.url + "us" + scrum, "us" + scrum, "us" + scrum, "system_settings");
        return diffsMaps;
    }

    /**
     * Thr function connects to the Scrum and production DB, query and get settings key values.
     * It then compares them and get the differences. It goes through them and stores them in a (key, (scrumVal, productionVal)) structure.
     *
     * @param country       The country which is currently checked
     * @param productionUrl The url of the production, which we are connecting to
     * @param scrumUrl      The url of the current scrum, which we are connecting to
     * @param username      The user name used for the connection
     * @param password      The password used for the connection
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

        HashMap<Pair<Object, Object>, ArrayList<Object>> currentCountryWhiteList = whiteList.get(country);

        if (currentCountryWhiteList == null)
            currentCountryWhiteList = new HashMap<Pair<Object, Object>, ArrayList<Object>>();

        List<List<Object>> diffFromScrumToProduction = new ExcelComparer().returnDiffsBetweenExcels(scrumObjMatrix, productionObjMatrix);
        List<List<Object>> diffFromProductionToScrum = new ExcelComparer().returnDiffsBetweenExcels(productionObjMatrix, scrumObjMatrix);

        //Initializing the diffs map
        HashMap<Pair<Object, Object>, ArrayList<Object>> diffsMap = new HashMap<Pair<Object, Object>, ArrayList<Object>>();

        //Updating diffs
        updateAllTheDiffsOfValuesBetweenProductionToScrum(diffFromProductionToScrum, diffFromScrumToProduction, diffsMap, currentCountryWhiteList);
        updateAllTheDiffsOfKeysBetweenProductionToScrum(diffFromProductionToScrum, diffFromScrumToProduction, diffsMap, "Scrum", currentCountryWhiteList);
        updateAllTheDiffsOfKeysBetweenProductionToScrum(diffFromScrumToProduction, diffFromProductionToScrum, diffsMap, "Production", currentCountryWhiteList);

        diffsMaps.put(country, diffsMap);
    }

    /**
     * The function update all the diffs between Production to the Scrum
     *
     * @param diffFromProductionToScrum List with diffs from production to scrum
     * @param diffFromScrumToProduction List with diffs from scrum to production
     * @param diffsMap                  A map contains all the diffs according key and list of values
     * @param whiteList                 all the keys to be ignored from
     */
    private void updateAllTheDiffsOfValuesBetweenProductionToScrum(List<List<Object>> diffFromProductionToScrum, List<List<Object>> diffFromScrumToProduction, HashMap<Pair<Object, Object>, ArrayList<Object>> diffsMap, HashMap<Pair<Object, Object>, ArrayList<Object>> whiteList) {
        //Taking all the diffs and put them in a (key, (scrumVal, productionVal)) structure
        for (List<Object> diffListOfProduction : diffFromProductionToScrum) {
            Object productionCurrentModule = diffListOfProduction.get(3);
            Object productionCurrentKey = diffListOfProduction.get(4);
            ArrayList<Object> values = new ArrayList<Object>();

            if (isValueInWhiteListAndUpdateValue(whiteList, productionCurrentKey.toString(), diffListOfProduction, diffFromScrumToProduction))
                continue;

            values.add(diffListOfProduction.get(5));

            for (List<Object> diffListOfScrum : diffFromScrumToProduction) {
                Object scrumCurrentModule = diffListOfScrum.get(3);
                Object scrumCurrentKey = diffListOfScrum.get(4);
                if (productionCurrentKey.equals(scrumCurrentKey) && productionCurrentModule.equals(scrumCurrentModule))
                    values.add(diffListOfScrum.get(5));
            }
            diffsMap.put(new Pair<Object, Object>(productionCurrentModule, productionCurrentKey), values);
        }
    }

    /**
     * The function updates the values in the white list, in case the current key is in the white list.
     * If there was a change of the value in the database, then the key and values will be removed from the white list.
     * @param countryWhiteList The white list of specific country
     * @param currentKey The current key - checked if it is in the white list
     * @param diffList List containing the differences of specific environment
     * @param diffFromEnvs List Of lists of differences
     * @return true if the key is found in the white list, false if it wasn't found or if .
     */
    private boolean isValueInWhiteListAndUpdateValue(HashMap<Pair<Object, Object>, ArrayList<Object>> countryWhiteList, String currentKey, List<Object> diffList, List<List<Object>> diffFromEnvs) {
        ArrayList<Object> values = new ArrayList<Object>();

        //Updating the values of the key in the white list
        if (countryWhiteList.keySet().contains(currentKey)) {
            for (List<Object> diffListOfScrum : diffFromEnvs) {
                Object envCurrentKey = diffListOfScrum.get(4);
                if (currentKey.equals(envCurrentKey))
                {
                    ArrayList<Object> whiteListValues = countryWhiteList.get(currentKey);

                    //If the values are not equal, then it means that the value at the database was changed. Need to be reexamined, so it is removed from the white list.
                    if(whiteListValues.size() > 1 && (!diffListOfScrum.get(5).equals(whiteListValues.get(0)) && (!diffListOfScrum.get(5).equals(whiteListValues.get(1)))))
                    {
                        countryWhiteList.remove(currentKey);
                        return false;
                    }

                    //If the values are not equal, then it means that the value at the database was changed. Need to be reexamined, so it is removed from the white list.
                    if(whiteListValues.size() > 1 && (!diffList.get(5).equals(whiteListValues.get(0)) && (!diffList.get(5).equals(whiteListValues.get(1)))))
                    {
                        countryWhiteList.remove(currentKey);
                        return false;
                    }

                    //If the values are not equal, then it means that the value at the database was changed. Need to be reexamined, so it is removed from the white list.
                    if(whiteListValues.size() == 1 && (!diffList.get(5).equals(whiteListValues.get(0)) && (!diffListOfScrum.get(5).equals(whiteListValues.get(0)))))
                    {
                        countryWhiteList.remove(currentKey);
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    /**
     * The function updates all the diffs between the keys.
     * for example keys that exist in production, but not in scrum.
     * Important - it compares from the first to the second.
     * It checks if there are keys in the first that are missing in the second.
     *  @param env1      scrum\production
     * @param env2      scrum\production
     * @param diffsMap  The Diffs contained in a map object.
     * @param whiteList Contains all the keys to be ignored from
     */
    private void updateAllTheDiffsOfKeysBetweenProductionToScrum(List<List<Object>> env1, List<List<Object>> env2, HashMap<Pair<Object, Object>, ArrayList<Object>> diffsMap, String envStr, HashMap<Pair<Object, Object>, ArrayList<Object>> whiteList) {
        //Going over all the keys of the production and checking if there is a missing key in the scrum env
        int index = 0;
        for (List<Object> diffListOfEnv1 : env1) {
            index++;

            Object productionCurrentKey = diffListOfEnv1.get(4);
            boolean foundKey = false;

            if (isValueInWhiteListAndUpdateValue(whiteList, productionCurrentKey.toString(), diffListOfEnv1, env2))
                continue;

            for (List<Object> diffListOfEnv2 : env2) {
                Object scrumCurrentKey = diffListOfEnv2.get(4);
                if (productionCurrentKey.equals(scrumCurrentKey)) {
                    foundKey = true;
                    break;
                }
            }

            if (!foundKey) {
                ArrayList<Object> values = new ArrayList<Object>();
                values.add(noSuchKeyStr + " in " + envStr);
                diffsMap.put(productionCurrentKey, values);
            }
        }
    }
}
