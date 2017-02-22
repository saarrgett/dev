package objects;

import db.DBHandler;
import db.ResultSetConverter;
import excel.ExcelComparer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rachamim on 2/20/17.
 */
public class SystemSettingDiffs {
    String scrum;

    private String url = "jdbc:mysql://scrum-rds.gett.qa:3306/";
    private String replicaCloneProductionUrl = "jdbc:mysql://gt-country-cloned-db.gtforge.com/gettaxi_country_production";
    private String replicaCloneProductionUser = "automation";
    private String replicaCloneProductionPassword = "Auto!@2016";
    private String query = "Select * from ";
    HashMap<Object, HashMap<Object,ArrayList<Object>>> diffsMaps;

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
        getDiffsFromSpecificCountry("il", replicaCloneProductionUrl.replace("country", "il"), this.url+"il"+scrum, "il" + scrum, "il" + scrum, "system_settings");
        getDiffsFromSpecificCountry("il", replicaCloneProductionUrl.replace("country", "il"), this.url+"il"+scrum, "il" + scrum, "il" + scrum, "system_settings_interim");
        getDiffsFromSpecificCountry("ru", replicaCloneProductionUrl.replace("country", "ru"), this.url+"ru"+scrum, "ru" + scrum, "ru" + scrum, "system_settings");
        getDiffsFromSpecificCountry("ru", replicaCloneProductionUrl.replace("country", "ru"), this.url+"ru"+scrum, "ru" + scrum, "ru" + scrum, "system_settings_interim");
        getDiffsFromSpecificCountry("uk", replicaCloneProductionUrl.replace("country", "uk"), this.url+"uk"+scrum, "uk" + scrum, "uk" + scrum, "system_settings");
        getDiffsFromSpecificCountry("uk", replicaCloneProductionUrl.replace("country", "uk"), this.url+"uk"+scrum, "uk" + scrum, "uk" + scrum, "system_settings_interim");
        getDiffsFromSpecificCountry("us", replicaCloneProductionUrl.replace("country", "us"),this.url+"us"+scrum, "us" + scrum, "us" + scrum, "system_settings");
        getDiffsFromSpecificCountry("us", replicaCloneProductionUrl.replace("country", "us"),this.url+"us"+scrum, "us" + scrum, "us" + scrum, "system_settings_interim");
        return diffsMaps;
    }

    /**
     * Thr function connects to the Scrum and production DB, query and get settings key values.
     * It then compares them and get the differences. It goes through them and stores them in a (key, (scrumVal, productionVal)) structure.
     * @param country
     * @param productionUrl
     * @param scrumUrl
     * @param username
     * @param password
     * @throws Exception
     */
    private void getDiffsFromSpecificCountry(String country, String productionUrl, String scrumUrl, String username, String password, String table) throws Exception {
        DBHandler dbHandler = new DBHandler(scrumUrl, username, password);
        dbHandler.connect();
        ResultSet resultSet = dbHandler.executeQuery(query + table);
        Object[][] scrumObjMatrix = new ResultSetConverter().convertRStoMatrix(resultSet);

        dbHandler = new DBHandler(productionUrl, replicaCloneProductionUser, replicaCloneProductionPassword);
        dbHandler.connect();
        resultSet = dbHandler.executeQuery(query + table);
        Object[][] productionObjMatrix = new ResultSetConverter().convertRStoMatrix(resultSet);

        List<List<Object>> diffFromScrumToProduction = new ExcelComparer().returnDiffsBetweenExcels(scrumObjMatrix, productionObjMatrix);
        List<List<Object>> diffFromProductionToScrum = new ExcelComparer().returnDiffsBetweenExcels(productionObjMatrix, scrumObjMatrix);

        HashMap<Object, ArrayList<Object>> diffsMap = new HashMap<Object, ArrayList<Object>>();

        //Taking all the diffs and put them in a (key, (scrumVal, productionVal)) structure
        for (List<Object> diffListOfProduction : diffFromProductionToScrum) {
            Object productionCurrentKey = diffListOfProduction.get(4);
            ArrayList<Object> values = new ArrayList<Object>();
            values.add(diffListOfProduction.get(5));

            for (List<Object> diffListOfScrum : diffFromScrumToProduction) {
                Object scrumCurrentKey = diffListOfScrum.get(4);
                if(productionCurrentKey.equals(scrumCurrentKey))
                    values.add(diffListOfScrum.get(5));
            }
            diffsMap.put(productionCurrentKey, values);
        }

        diffsMaps.put(country, diffsMap);
    }
}
