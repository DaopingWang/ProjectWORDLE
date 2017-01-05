/**
 * Created by Wang.Daoping on 05.01.2017.
 */

import graph.clustering.GraphFactory;
import graph.clustering.InputKeywordComparator;
import graph.clustering.Utility;
import graph.clustering.algorithm.ISODATAFactory;
import graph.clustering.algorithm.KMeansFactory;
import graph.clustering.algorithm.process.CoreFunctions;
import graph.clustering.vertex.KeywordVertex;
import graph.visualization.WordleFactory;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static final String CACHE_PATH = "C:/Users/wang.daoping/Documents/project_wordle_cache/";
    public static final String RAW_PATH = "C:/Users/wang.daoping/Documents/project_wordle_raw/";

    public static final Boolean parseRawFile = false;
    public static final Boolean readCacheFile = true;
    public static final Boolean writeCacheFile = false;

    public static final Boolean enableRandomInputGenerator = false;
    public static final Boolean enableKeywordInspector = false;

    public static final int CLUSTERING_MODE = 1;
    public static final String TARGET_WORD = null;

    public static final float WORDLE_SKETCH_WIDTH = 1300;
    public static final float WORDLE_SKETCH_HEIGHT = 700;

    public static void main(String[] args){

        if(parseRawFile){
            try {
                GraphFactory.parseGraphFromRawCSV(RAW_PATH);
            } catch (IOException e){
                System.out.println("RawFileNotFoundError");
                e.printStackTrace();
                System.exit(666);
            }
        } else if(readCacheFile){
            try {
                GraphFactory.readGraphFromParsedCSV(CACHE_PATH);
            } catch (IOException e){
                System.out.println("CacheFileNotFoundError");
                e.printStackTrace();
                System.exit(666);
            }
        }

        if(enableRandomInputGenerator){
            randomizer();
        }

        switch (CLUSTERING_MODE){
            case 1:
                for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                    if(TARGET_WORD != null){
                        if(!GraphFactory.searchKeywords.get(i).name.equals(TARGET_WORD)) {
                            continue;
                        }
                    }
                    Utility.reinitializer(GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
                    System.out.println();
                    System.out.println("*** " + GraphFactory.searchKeywords.get(i).name + " " + Integer.toString(GraphFactory.searchKeywords.get(i).searchResults.size()) + " ***");
                    System.out.println();
                    //KMeansFactory.performSquareErrorClustering(GraphFactory.searchKeywords.get(i).searchResults, GraphFactory.rootKeywordVertices, GraphFactory.keywordVertices);
                    ISODATAFactory.performISODATAClustering(GraphFactory.searchKeywords.get(i).searchResults, i, GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
                }
                for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                    WordleFactory.renderWordle(GraphFactory.searchKeywords.get(i).clusters, GraphFactory.searchKeywords.get(i).countOriginalMembers, GraphFactory.searchKeywords.get(i).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT);
                    try {
                        System.in.read();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                    if(TARGET_WORD != null){
                        if(!GraphFactory.searchKeywords.get(i).name.equals(TARGET_WORD)) {
                            continue;
                        }
                    }
                    Utility.reinitializer(GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
                    System.out.println();
                    System.out.println("*** " + GraphFactory.searchKeywords.get(i).name + " " + Integer.toString(GraphFactory.searchKeywords.get(i).searchResults.size()) + " ***");
                    System.out.println();
                    KMeansFactory.performSquareErrorClustering(GraphFactory.searchKeywords.get(i).searchResults, GraphFactory.rootKeywordVertices, GraphFactory.keywordVertices);
                }
                for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                    WordleFactory.renderWordle(GraphFactory.searchKeywords.get(i).clusters, GraphFactory.searchKeywords.get(i).countOriginalMembers, GraphFactory.searchKeywords.get(i).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT);
                    try {
                        System.in.read();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }

        if(enableKeywordInspector){
            Scanner scanner = new Scanner(System.in);
            System.out.println("PROBABOT:\"Enter keyword: \"");
            String userInput;
            while (!(userInput = scanner.nextLine()).equals("exit")){
                KeywordVertex buffer = InputKeywordComparator.findMostSimilarKeywordOf(userInput, GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
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
        }

        if(writeCacheFile){
            try {
                GraphFactory.createProbabilityCSVFromGraph(CACHE_PATH);
                //GraphFactory.createPathLengthMatrixFromGraph(CACHE_PATH);
                GraphFactory.createSubordinatesCSVFromGraph(CACHE_PATH);
                GraphFactory.createParsedCSVFromGraph(CACHE_PATH);
            } catch (IOException e){
                System.out.println("caching failed");
                e.printStackTrace();
                System.exit(666);
            }
        }
    }

    public static void randomizer(){
        Scanner sc = new Scanner(System.in);
        System.out.println("RANDOMGEN:\"Enter random number\" ");
        String userin;
        while(!(userin = sc.nextLine()).equals("exit")){
            Utility.reinitializer(GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
            GraphFactory.calculateSparseVector(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1));
            KMeansFactory.performSquareErrorClustering(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1), GraphFactory.rootKeywordVertices);
            //ISODATAFactory.performISODATAClustering(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1));
            System.out.println(Integer.toString(CoreFunctions.abandonedKeywords));
            System.out.println();
            System.out.println("RANDOMGEN:\"Enter random number\" ");
        }
    }


}
