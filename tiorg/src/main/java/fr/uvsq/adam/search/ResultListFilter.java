/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import fr.uvsq.adam.search.ResultConstruction.ResultNodeModel;
import java.util.ArrayList;

/**
 *
 * @author houk
 */
public class ResultListFilter {

    public ResultListFilter() {
    }

    public static void main(ArrayList<ResultNodeModel> resultList) {
        int i = 0;
        for (ResultNodeModel result1 : resultList) {
            for (int j = i + 1; i < resultList.size(); j++) {
                ResultNodeModel result2 = resultList.get(j);
                if (result1.getTypeResult().equalsIgnoreCase("node")) {
                    if (result2.getTypeResult().equalsIgnoreCase("model")) {
                    } else {
                    }
                } else {
                    if (result2.getTypeResult().equalsIgnoreCase("model")) {
                    } else {
                    }
                }
            }
            i++;
        }
    }
}
