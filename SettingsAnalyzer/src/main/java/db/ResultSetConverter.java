package db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by saar on 2/13/17.
 */
public class ResultSetConverter {
    /**
     * the function convert the result set into a matrix
     * @param rs the result set
     * @return A matrix containing the values in the result set
     * @throws Exception
     */
    public Object[][] convertRStoMatrix(ResultSet rs) throws Exception {
        Object tabArray[][];

        int startRow = 0;
        int startCol = 0;
        int ci, cj;
        int endCol = this.ColCount(rs);
        int endRow = this.rowCount(rs);

        if(rs.getFetchSize() == 0)
            return new Object[0][0];

        tabArray = new Object[endRow - startRow - 1][endCol - startCol - 1];
        ci = 0;
        cj = 0;
        rs.beforeFirst();
        for (int i = startRow + 1; i < endRow; i++, ci++) {
            cj = 0;
            try {
                rs.next();// for index 1 it will be in the first row
            } catch (SQLException e1) {
                // can't move next empty row end of RS
                System.out.println(e1.getMessage());
            }
            for (int j = startCol + 1; j < endCol; j++, cj++) {
                try {
                    tabArray[ci][cj] = rs.getObject(j);// /sheet.getCell(j,i).getContents();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return (tabArray);
    }

    /**
     * The function counts the number of cols in the result set
     * @param rs The result set
     * @return the number of cols in the result set
     */
    public int ColCount(ResultSet rs) {
        int colCount = 0;
        try {
            rs.first();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            return colCount = rsMetaData.getColumnCount();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return colCount;
    }

    /**
     * The function counts the number of rows in the result set
     * @param rs The result set
     * @return The number of rows in the result set
     * @throws Exception
     */
    public int rowCount(ResultSet rs) throws Exception {
        int rowCount = 0;
        int counter = 0;
        try {
            // rs.first();
            while (rs.next()) {
                counter++;
            }

            if (rs.next() == false) {
                rowCount = rs.getRow();
                rs.first();
                return counter;
            } else {
                System.out.println("Can't get row count for - RecordSet is empty");
            }

        } catch (SQLException e) {
            System.out.println("Can't get row count for Q: " + rowCount + " " + e.getMessage());
        } finally {
            rs.first();
        }
        return rowCount;
    }

}
