package graph.clustering;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import graph.clustering.vertex.KeywordVertex;

import java.io.IOException;
import java.util.Scanner;

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

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter keyword: ");
        String userInput;
        while (!(userInput = scanner.nextLine()).equals("EXIT")){
            KeywordVertex buffer = InputKeywordComparator.findMostSimilarKeywordOf(userInput);
            System.out.println("Most similar keyword found: " + buffer.name);

            System.out.println(buffer.name + " in layer " + Integer.toString(buffer.layer) + " has edges to ");
            for(int i = 0; i < buffer.edgeList.size(); i++){
                System.out.println(buffer.edgeList.get(i).getTargetVertexName() + " " + Double.toString(buffer.edgeList.get(i).getEdgeWeight()));
            }

            System.out.println();
            System.out.println("Probabilities: ");
            for(int i = 0; i < buffer.probabilityList.size(); i++){
                System.out.println(buffer.probabilityList.get(i).getTargetVertexName() + " " + Double.toString(buffer.probabilityList.get(i).getProbability()));
            }

            System.out.println("Enter keyword:");
        }


        System.out.println("Creating layer csvs...");
        try {
            GraphFactory.createProbabilityCSVFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
            //GraphFactory.createPathLengthMatrixFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
            //GraphFactory.createParsedCSVFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
        } catch (IOException e){
            System.out.println("ERROR: CANNOT CREATE LAYERS");
            e.printStackTrace();
        }
    }
}
