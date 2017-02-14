package excel;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by saar on 2/13/17.
 */
public class ExcelComparer {
    /**
     * The function returns the diff between 2 matrix
     * @param excelMatrix1
     * @param excelMatrix2
     * @return A list of objects, each object contains a diff between the matrix
     */
    public List<List<Object>> returnDiffsBetweenExcels(Object[][] excelMatrix1, Object[][] excelMatrix2){
        List<List<Object>> list1 = convertMatrixToLists(excelMatrix1);
        List<List<Object>> list2 = convertMatrixToLists(excelMatrix2);
        List<List<Object>> listOfDiffs = new ArrayList<List<Object>>();

        findDiffBetweenLists(list1, list2, listOfDiffs);
        findDiffBetweenLists(list2, list1, listOfDiffs);
        return listOfDiffs;
    }

    /**
     * The function find the diffs between 2 lists.
     * @param list1 First list
     * @param list2 Second list
     * @param listOfDiffs List containing all the diffs.
     */
    private void findDiffBetweenLists(List<List<Object>> list1, List<List<Object>> list2, List<List<Object>> listOfDiffs)
    {
        for(int i = 0; i < list1.size(); ++i)
        {
            boolean isEqual = false;

            for(int j = 0; j < list2.size(); ++j)
            {
                isEqual = list1.get(i).get(4).equals(list2.get(j).get(4)) && list1.get(i).get(5).equals(list2.get(j).get(5));

                if(isEqual)
                    break;
            }

            if(!isEqual)
                listOfDiffs.add(list1.get(i));
        }
    }

    /**
     * The function coverts a matrix to a list of lists of objects.
     * @param matrix The matrix to convert
     * @return List of Lists of objects
     */
    private List<List<Object>> convertMatrixToLists(Object[][] matrix){
        List<List<Object>> retLists = new ArrayList<List<Object>>();
        for(int i = 0; i < matrix.length; ++i)
        {
            List currentRowList = new ArrayList<Object>();
            for(int j = 0; j < matrix[i].length;++j){
                currentRowList.add(matrix[i][j]);
            }
            retLists.add(currentRowList);
        }
        return retLists;
    }
}
