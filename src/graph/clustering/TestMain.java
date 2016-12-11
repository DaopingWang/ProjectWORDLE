package graph.clustering;

import graph.clustering.kmeans.Cluster;
import graph.clustering.kmeans.ClusterFactory;
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

        //ClusterFactory.performSquareErrorClustering(Utility.randomInputGenerator(60,200), GraphFactory.rootKeywordVertices);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter random number ");
        String userin;
        while(!(userin = sc.nextLine()).equals("exit")){
            Utility.reinitializer(GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
            ClusterFactory.performSquareErrorClustering(Utility.randomInputGenerator(Integer.parseInt(userin), 200));
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter keyword: ");
        String userInput;
        while (!(userInput = scanner.nextLine()).equals("exit")){
            KeywordVertex buffer = InputKeywordComparator.findMostSimilarKeywordOf(userInput);
            System.out.println("Most similar keyword found: " + buffer.name);

            System.out.println(buffer.name + " in layer " + Integer.toString(buffer.layer) + " has edges to ");
            for(int i = 0; i < buffer.edgeList.size(); i++){
                System.out.println(buffer.edgeList.get(i).getTargetVertexName() + " " + Double.toString(buffer.edgeList.get(i).getEdgeWeight()));
            }

            System.out.println();
            System.out.println("Probabilities: ");
            for(int i = 0; i < buffer.categorySimilarityVector.size(); i++){
                System.out.println(buffer.probabilityList.get(i).getTargetVertexName() + " " + Double.toString(buffer.categorySimilarityVector.get(i)));
            }

            System.out.println("Enter keyword:");
        }

/*
        try {
            //GraphFactory.createProbabilityCSVFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
            GraphFactory.createPathLengthMatrixFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
            //GraphFactory.createParsedCSVFromGraph("C:/Users/wang.daoping/Documents/rework_layers/");
        } catch (IOException e){
            System.out.println("ERROR: CANNOT CREATE LAYERS");
            e.printStackTrace();
        }*/

    }
}
