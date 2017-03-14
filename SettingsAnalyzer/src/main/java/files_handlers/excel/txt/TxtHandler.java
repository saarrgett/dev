package files_handlers.excel.txt;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The class handles all the txt handling needed for the project
 * Created by Rachamim on 3/13/17.
 */
public class TxtHandler {
    //Data members
    private String fileName;
    private String method;
    private PrintWriter writer;

    //C'tor
    public TxtHandler(String fileName){
        this.fileName = fileName;
        method = "UTF-8";
    }

    /**
     * The function write to txt file
     * @param map The map to get the data from
     * @throws IOException
     */
    public void writeToTxtFile(HashMap<Object, HashMap<Object, ArrayList<Object>>> map) throws IOException {
        //Creating folders - if missing
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();

        writer = new PrintWriter(fileName, "UTF-8");

        Iterator<Object> countryItr = map.keySet().iterator();
        while (countryItr.hasNext()) {
            Object country = countryItr.next();
            HashMap<Object, ArrayList<Object>> currentCountryMap = map.get(country);
            Iterator<Object> keyItr = currentCountryMap.keySet().iterator();

            while (keyItr.hasNext()) {
                Object key = keyItr.next();
                writer.print(country + ";" + key);
                ArrayList<Object> list = currentCountryMap.get(key);
                for (int i = 0; i < list.size(); ++i)
                {
                    writer.print(";" + list.get(i));
                }
                writer.println();
            }
        }
        writer.close();
    }
}
