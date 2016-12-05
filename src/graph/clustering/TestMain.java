package graph.clustering;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.io.IOException;

/**
 * Created by Wang.Daoping on 02.12.2016.
 */
public class TestMain {

    public static void main(String[] args){
        try {
            //GraphFactory.parseGraphFromRawCSV("C:/Users/wang.daoping/Documents/Keyword_Graph.csv");
            GraphFactory.readGraphFromParsedCSV("C:/Users/wang.daoping/Documents/rework_layers/ParsedCSV.csv");
        } catch (IOException e){
            System.out.println("ERROR: PARSE METHOD COULD NOT FIND RAW FILE");
            e.printStackTrace();
        }

        int testIndex = GraphFactory.findIndexForName("Adapterrahmen", GraphFactory.keywordVertices);
        System.out.println(GraphFactory.keywordVertices.get(testIndex).name + " in layer " + Integer.toString(GraphFactory.keywordVertices.get(testIndex).layer) + " has edges to ");
        for(int i = 0; i < GraphFactory.keywordVertices.get(testIndex).edgeList.size(); i++){
            System.out.println(GraphFactory.keywordVertices.get(testIndex).edgeList.get(i).getTargetVertexName() + " " + Double.toString(GraphFactory.keywordVertices.get(testIndex).edgeList.get(i).getEdgeWeight()));
        }

        System.out.println();
        System.out.println("Probabilities: ");
        for(int i = 0; i < GraphFactory.keywordVertices.get(testIndex).probabilityList.size(); i++){
            System.out.println(GraphFactory.keywordVertices.get(testIndex).probabilityList.get(i).getTargetVertexName() + " " + Double.toString(GraphFactory.keywordVertices.get(testIndex).probabilityList.get(i).getProbability()));
        }

        System.out.println("Creating layer csvs...");
        try {
            GraphFactory.createPathLengthMatrixFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
            //GraphFactory.createParsedCSVFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
        } catch (IOException e){
            System.out.println("ERROR: CANNOT CREATE LAYERS");
            e.printStackTrace();
        }
    }
}
