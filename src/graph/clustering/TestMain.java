package graph.clustering;

import graph.clustering.algorithm.KMeansFactory;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.algorithm.process.*;
import graph.visualization.Sketch;
import graph.visualization.WordleRenderer;
import processing.core.PApplet;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Wang.Daoping on 02.12.2016.
 */
public class TestMain {

    public static void main(String[] args){

        try {
            //GraphFactory.parseGraphFromRawCSV("C:/Users/wang.daoping/Documents/Keyword_Graph.csv");
            GraphFactory.readGraphFromParsedCSV("C:/Users/wang.daoping/Documents/project_wordle_cache/ParsedCSV.csv");
        } catch (IOException e){
            System.out.println("ERROR: PARSE METHOD COULD NOT FIND RAW FILE");
            e.printStackTrace();
        }

        //randomizer();

        for(int i = 0; i < GraphFactory.searchExamples.size(); i++){
            //if(!GraphFactory.searchExamples.get(i).name.equals("terumo")) continue;
            Utility.reinitializer(GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
            System.out.println();;
            System.out.println("*** " + GraphFactory.searchExamples.get(i).name + " " + Integer.toString(GraphFactory.searchExamples.get(i).searchResults.size()) + " ***");
            System.out.println();
            KMeansFactory.performSquareErrorClustering(GraphFactory.searchExamples.get(i).searchResults);
            //ISODATAFactory.performISODATAClustering(GraphFactory.searchExamples.get(i).searchResults);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("PROBABOT:\"Enter keyword: \"");
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

        PApplet.main("graph.visualization.Sketch");


/*
        try {
            //GraphFactory.createProbabilityCSVFromGraph("C:/Users/wang.daoping/Documents/project_wordle_cache/");
            //GraphFactory.createPathLengthMatrixFromGraph("C:/Users/wang.daoping/Documents/project_wordle_cache/");
            GraphFactory.createSubordinatesCSVFromGraph("C:/Users/wang.daoping/Documents/project_wordle_cache/");
            //GraphFactory.createParsedCSVFromGraph("C:/Users/wang.daoping/Documents/project_wordle_cache/");
        } catch (IOException e){
            System.out.println("ERROR: CANNOT CREATE LAYERS");
            e.printStackTrace();
        }
*/
    }

    public static void randomizer(){
        Scanner sc = new Scanner(System.in);
        System.out.println("RANDOMGEN:\"Enter random number\" ");
        String userin;
        while(!(userin = sc.nextLine()).equals("exit")){
            Utility.reinitializer(GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
            GraphFactory.calculateSparseVector(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1));
            KMeansFactory.performSquareErrorClustering(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1));
            //ISODATAFactory.performISODATAClustering(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1));
            System.out.println(Integer.toString(CoreFunctions.abandonedKeywords));
            System.out.println();
            System.out.println("RANDOMGEN:\"Enter random number\" ");
        }
    }
}
