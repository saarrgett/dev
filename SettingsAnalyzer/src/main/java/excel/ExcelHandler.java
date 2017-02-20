package excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 * Created by saar on 2/13/17.
 */
public class ExcelHandler {
    //Data members
    private String fileName;
    private String sheetName;

    //Ct'or
    public ExcelHandler(String fileName, String sheetName) {
        this.fileName = System.getProperty("user.dir") + fileName;
        this.sheetName = sheetName;
    }

    /**
     * The function creates an excel spread sheet from an input of an object matrix.
     * @param rowValues a matrix of objects
     */
    public void createExcelSpreadSheet(Object[][] rowValues) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(sheetName);
            for (int i = 0; i < rowValues.length; ++i) {
                HSSFRow row = sheet.createRow((short) i);
                for (int j = 0; j < rowValues[i].length; ++j) {
                    if (rowValues[i][j] == null)
                        row.createCell(j).setCellValue("");
                    else
                        row.createCell(j).setCellValue(rowValues[i][j].toString());
                }
            }
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * The function returns a 2 dimensions array from the excel file
     * @return 2 dimensions array, containing all the data from the excel
     */
    public Object[][] readFromExcel() {
        try {
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            Workbook workbook;
            workbook = WorkbookFactory.create(inputStream);
            Sheet currentSheet = workbook.getSheet(sheetName);
            int numberOfRows = currentSheet.getPhysicalNumberOfRows();
            int numberOfCols = currentSheet.getRow(0).getPhysicalNumberOfCells();
            Object[][] retMatrix = new Object[numberOfRows][numberOfCols];


            for (int i = 0; i < numberOfRows; ++i)
            {
                Row nextRow = currentSheet.getRow(i);
                for(int j = 0; j < numberOfCols; ++j)
                {
                    DataFormatter formatter = new DataFormatter();
                    Cell cell = nextRow.getCell(j);
                    retMatrix[i][j] = formatter.formatCellValue(cell);
                }
            }

            inputStream.close();
            return retMatrix;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
