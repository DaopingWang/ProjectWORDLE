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

/**
 * The "console" of this program. In this class, various parameters can be set according to your preferences.
 */
public class Main {
    /**
     * Local path of the folder that contains the following cache files: SubordinatesCSV.csv, ParsedCSV.csv, ProbabilityCSV.csv, Keyword_Graph.csv, Keywords_Artikel.csv, Suchen_example.csv.
     */
    public static final String CACHE_PATH = "C:/Users/wang.daoping/Documents/project_wordle_cache/";

    /**
     * Local path of the folder that contains the following raw csv files: Keyword_Graph.csv, Keywords_Artikel.csv, Suchen_example.csv.
     */
    public static final String RAW_PATH = "C:/Users/wang.daoping/Documents/project_wordle_raw/";

    /**
     * Set to true if you do not have cache files. The program will start parsing from the beginning.
     */
    public static final Boolean parseRawFile = false;

    /**
     * Set to true if you have cache files. Note that only one of parseRawFile and readCacheFile can be true.
     */
    public static final Boolean readCacheFile = true;

    /**
     * Set to true if you want save the parsing results as cache.
     */
    public static final Boolean writeCacheFile = false;

    // not ready for wordle renderer
    public static final Boolean enableRandomInputGenerator = false;
    /**
     * Set to true if you want to look up the assignment probabilities of the keywords.
     */
    public static final Boolean enableKeywordInspector = true;

    /**
     * Set to true if you want to render the wordles.
     */
    public static final Boolean enableWordleRenderer = true;

    /**
     * 1 for ISODATA algorithms, 2 for traditional K-means.
     */
    public static final int CLUSTERING_MODE = 1;

    /**
     * Enter the name of the particular search query you want to look up. If set to null, all search queries will be clustered.
     */
    public static final String TARGET_WORD = null;

    /**
     * Enter the number of clusters that you want each keyword set to be clustered. Set to -1 if you want ISODATA to adjust k for you.
     */
    public static final int RENDER_BEST_K_CLUS_ONLY = -1;

    /**
     * Width of the panel.
     */
    public static final float WORDLE_SKETCH_WIDTH = 1300;

    /**
     * Height of the panel.
     */
    public static final float WORDLE_SKETCH_HEIGHT = 700;

    // ISODATA parameters
    public static final int MAX_ITERATION_ISODATA = 1000;
    public static final int MIN_CLUSTER_SIZE = 1;
    public static final double MIN_INTERCLUSTER_DISTANCE = 0.4;
    public static final double MAX_STANDARD_DEVIATION = 0.3;
    public static final int MAX_PAIR = 1;
    public static final double MAX_ASD = 1;
    public static final int MIN_NUMBER_CLUSTER_ISODATA = 2;

    // Traditional K-Means parameters
    public static final int MAX_ITERATION_K_MEANS = 10000;
    public static final double MAX_ERROR = 0.5;
    public static final int MAX_REALLOC_COUNT = 0;
    public static final int MAX_MEMBER_COUNT = 15;
    public static final int MIN_MEMBER_COUNT = 2;
    public static final int MIN_NUMBER_CLUSTER_K_MEANS = 2;


    public static void main(String[] args){
        int index = -1;

        if(parseRawFile){
            try {
                GraphFactory.parseGraphFromRawCSV(RAW_PATH);
            } catch (IOException e){
                System.out.println("RawFileNotFoundError");
                e.printStackTrace();
                System.exit(666);
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
                System.out.println(Integer.toString(GraphFactory.searchKeywords.size()));
                for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                    if(TARGET_WORD != null){
                        if(!GraphFactory.searchKeywords.get(i).name.equals(TARGET_WORD)) {
                            continue;
                        }
                    }
                    index = i;
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
                            MAX_ASD,
                            MIN_NUMBER_CLUSTER_ISODATA);
                }
                if (enableWordleRenderer){
                    System.out.println();
                    System.out.println("********************************");

                    if(TARGET_WORD != null){
                        WordleFactory.renderWordle(GraphFactory.searchKeywords.get(index).clusters, GraphFactory.searchKeywords.get(index).countOriginalMembers, GraphFactory.searchKeywords.get(index).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT, RENDER_BEST_K_CLUS_ONLY, GraphFactory.rootKeywordVertices);
                        System.out.println("Enter anything to continue");
                        try {
                            System.in.read();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                        break;
                    }

                    System.out.println("\"exit\" terminates the renderer");
                    System.out.println("any other inputs will render the next search result");
                    for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                        WordleFactory.renderWordle(GraphFactory.searchKeywords.get(i).clusters, GraphFactory.searchKeywords.get(i).countOriginalMembers, GraphFactory.searchKeywords.get(i).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT, RENDER_BEST_K_CLUS_ONLY, GraphFactory.rootKeywordVertices);
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
                            MIN_MEMBER_COUNT,
                            MIN_NUMBER_CLUSTER_K_MEANS);
                }
                if (enableWordleRenderer){
                    System.out.println();
                    System.out.println("********************************");

                    if(TARGET_WORD != null){
                        WordleFactory.renderWordle(GraphFactory.searchKeywords.get(index).clusters, GraphFactory.searchKeywords.get(index).countOriginalMembers, GraphFactory.searchKeywords.get(index).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT, RENDER_BEST_K_CLUS_ONLY, GraphFactory.rootKeywordVertices);
                        System.out.println("Enter anything to continue");
                        try {
                            System.in.read();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                        break;
                    }

                    System.out.println("\"exit\" terminates the renderer");
                    System.out.println("any other inputs will render the next search result");
                    for(int i = 0; i < GraphFactory.searchKeywords.size(); i++){
                        WordleFactory.renderWordle(GraphFactory.searchKeywords.get(i).clusters, GraphFactory.searchKeywords.get(i).countOriginalMembers, GraphFactory.searchKeywords.get(i).name, WORDLE_SKETCH_WIDTH, WORDLE_SKETCH_HEIGHT, RENDER_BEST_K_CLUS_ONLY, GraphFactory.rootKeywordVertices);

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
