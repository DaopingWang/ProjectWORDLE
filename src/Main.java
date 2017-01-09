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

    // not ready for wordle renderer
    public static final Boolean enableRandomInputGenerator = false;
    public static final Boolean enableKeywordInspector = false;
    public static final Boolean enableWordleRenderer = true;

    public static final int CLUSTERING_MODE = 1;
    public static final String TARGET_WORD = null;

    public static final float WORDLE_SKETCH_WIDTH = 1300;
    public static final float WORDLE_SKETCH_HEIGHT = 700;

    // ISODATA parameters
    public static final int MAX_ITERATION_ISODATA = 1000;
    public static final int MIN_CLUSTER_SIZE = 1;
    public static final double MIN_INTERCLUSTER_DISTANCE = 0.45;
    public static final double MAX_STANDARD_DEVIATION = 0.1;
    public static final int MAX_PAIR = 3;
    public static final double MAX_ASD = 0.5;

    // Traditional K-Means parameters
    public static final int MAX_ITERATION_K_MEANS = 10000;
    public static final double MAX_ERROR = 0.5;
    public static final int MAX_REALLOC_COUNT = 0;
    public static final int MAX_MEMBER_COUNT = 15;
    public static final int MIN_MEMBER_COUNT = 2;

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
                    ISODATAFactory.performISODATAClustering(GraphFactory.searchKeywords.get(i).searchResults,
                            GraphFactory.searchKeywords.get(i),
                            GraphFactory.keywordVertices,
                            GraphFactory.rootKeywordVertices,
                            MAX_ITERATION_ISODATA,
                            MIN_CLUSTER_SIZE,
                            MIN_INTERCLUSTER_DISTANCE,
                            MAX_STANDARD_DEVIATION,
                            MAX_PAIR,
                            MAX_ASD);
                }
                if (enableWordleRenderer){
                    System.out.println();
                    System.out.println("********************************");
                    System.out.println("\"exit\" terminates the renderer");
                    System.out.println("any other inputs will render the next search result");
                    for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                        WordleFactory.renderWordle(GraphFactory.searchKeywords.get(i).clusters, GraphFactory.searchKeywords.get(i).countOriginalMembers, GraphFactory.searchKeywords.get(i).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT);
                        Scanner scanner = new Scanner(System.in);
                        if(scanner.nextLine().equals("exit")) break;
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
                    KMeansFactory.performSquareErrorClustering(GraphFactory.searchKeywords.get(i).searchResults,
                            GraphFactory.searchKeywords.get(i),
                            GraphFactory.rootKeywordVertices,
                            GraphFactory.keywordVertices,
                            MAX_ITERATION_K_MEANS,
                            MAX_ERROR,
                            MAX_REALLOC_COUNT,
                            MAX_MEMBER_COUNT,
                            MIN_MEMBER_COUNT);
                }
                if (enableWordleRenderer){
                    System.out.println();
                    System.out.println("********************************");
                    System.out.println("\"exit\" terminates the renderer");
                    System.out.println("any other inputs will render the next search result");
                    for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                        WordleFactory.renderWordle(GraphFactory.searchKeywords.get(i).clusters, GraphFactory.searchKeywords.get(i).countOriginalMembers, GraphFactory.searchKeywords.get(i).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT);

                        Scanner scanner = new Scanner(System.in);
                        if(scanner.nextLine().equals("exit")) break;
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

        System.out.println("ProjectWordle closed successfully.");
        System.exit(666);
    }

    public static void randomizer(){
        Scanner sc = new Scanner(System.in);
        System.out.println("RANDOMGEN:\"Enter random number\" ");
        String userin;
        while(!(userin = sc.nextLine()).equals("exit")){
            Utility.reinitializer(GraphFactory.keywordVertices, GraphFactory.rootKeywordVertices);
            GraphFactory.calculateSparseVector(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1));
            KMeansFactory.performSquareErrorClustering(Utility.randomInputGenerator(Integer.parseInt(userin), 500, 1), GraphFactory.rootKeywordVertices);
            System.out.println(Integer.toString(CoreFunctions.abandonedKeywords));
            System.out.println();
            System.out.println("RANDOMGEN:\"Enter random number\" ");
        }
    }


}
