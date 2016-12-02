package graph.clustering;

import java.io.IOException;

/**
 * Created by Wang.Daoping on 02.12.2016.
 */
public class TestMain {

    public static void main(String[] args){
        int testIndex = 1000;

        try {
            GraphFactory.parseGraphFromRawCSV("C:/Users/wang.daoping/Documents/Keyword_Graph.csv");
        } catch (IOException e){
            System.out.println("ERROR: PARSE METHOD COULD NOT FIND RAW FILE");
            e.printStackTrace();
        }
        System.out.println(GraphFactory.keywordVertices.get(testIndex).name + " has edges to ");
        for(int i = 0; i < GraphFactory.keywordVertices.get(testIndex).edgeList.size(); i++){
            System.out.println(GraphFactory.keywordVertices.get(testIndex).edgeList.get(i).getTargetVertexName());
        }

        System.out.println("and direct subordinates:");
        for(int i = 0; i < GraphFactory.keywordVertices.get(testIndex).subordinateList.size(); i++){
            System.out.println(GraphFactory.keywordVertices.get(testIndex).subordinateList.get(i));
        }
        System.out.println("Creating layer csvs...");
        try {
            GraphFactory.createCSVLayersFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
        } catch (IOException e){
            System.out.println("ERROR: CANNOT CREATE LAYERS");
            e.printStackTrace();
        }
    }
}
