package files_handlers.excel.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class handles with all the Excel file handling.
 * For example: reading and writing to Excel file
 * Created by saar on 2/13/17.
 */
public class ExcelHandler {

    //Data members
    private String fileName;
    private String sheetName;

    //Ct'or
    public ExcelHandler(String fileName, String sheetName) {
        this.fileName = fileName;
        this.sheetName = sheetName;
    }

    //Ct'or
    public ExcelHandler(String fileName) {
        this.fileName = fileName;
        this.sheetName = "settings"; // Not necessarily in use
    }

    //Set file name
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    /**
     * The function creates an files_handlers.excel.excel spread sheet from an input of an object matrix.
     *
     * @param map a matrix of objects
     */
    public void createExcelSpreadSheetFromHashMap(HashMap<Object, HashMap<Object, ArrayList<Object>>> map) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Iterator<Object> countryItr = map.keySet().iterator();
        Sheet sheet = workbook.createSheet(sheetName);

        //Creating titles for the file
        int currentRow = 0;
        Row row = sheet.createRow((short) currentRow);
        row.createCell(0).setCellValue("Country");
        row.createCell(1).setCellValue("Key");
        row.createCell(2).setCellValue("Production");
        row.createCell(3).setCellValue("Scrum");
        currentRow++;

        //For each country adding key - values rows
        while (countryItr.hasNext()) {
            Object country = countryItr.next();
            HashMap<Object, ArrayList<Object>> currentCountryMap = map.get(country);
            Iterator<Object> keyItr = currentCountryMap.keySet().iterator();

            while (keyItr.hasNext()) {
                row = sheet.createRow((short) currentRow);

                Object key = keyItr.next();
                ArrayList<Object> list = currentCountryMap.get(key);
                row.createCell(0).setCellValue(country.toString());
                row.createCell(1).setCellValue(key.toString());
                for (int i = 2; i < list.size() + 2; ++i)
                    row.createCell(i).setCellValue(list.get(i - 2).toString());

                currentRow++;
            }
        }

        //Creating folders - if missing
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();

        //Writing to the file
        FileOutputStream fileOut = new FileOutputStream(fileName);
        workbook.write(fileOut);
        fileOut.close();
    }

    /**
     * The function returns a 2 dimensions array from the files_handlers.excel.excel file
     *
     * @return 2 dimensions array, containing all the data from the files_handlers.excel.excel
     */
    public HashMap<Object, HashMap<Object, ArrayList<Object>>> readFromComplexExcel(String fileName) throws IOException, InvalidFormatException, URISyntaxException {
        FileInputStream inputStream = new FileInputStream(new File(fileName));
        Workbook workbook;
        workbook = WorkbookFactory.create(inputStream);
        Sheet currentSheet = workbook.getSheet(sheetName);
        int numberOfRows = currentSheet.getPhysicalNumberOfRows();
        int numberOfCols = currentSheet.getRow(0).getPhysicalNumberOfCells();
        HashMap<Object, HashMap<Object, ArrayList<Object>>> retMap = new HashMap<Object, HashMap<Object, ArrayList<Object>>>();
        HashMap<Object, ArrayList<Object>> keyMap = new HashMap<Object, ArrayList<Object>>();
        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i < numberOfRows; ++i) {
            Row nextRow = currentSheet.getRow(i);
            Object countryObj = formatter.formatCellValue(nextRow.getCell(0));
            Object keyObj = formatter.formatCellValue(nextRow.getCell(1));

            ArrayList<Object> valuesList = new ArrayList<Object>();
            for (int j = 2; j < numberOfCols; ++j) {
                Cell cell = nextRow.getCell(j);
                valuesList.add(formatter.formatCellValue(cell));
            }
            keyMap.put(keyObj, valuesList);
            retMap.put(countryObj, keyMap);
        }

        inputStream.close();
        return retMap;
    }
}