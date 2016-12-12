package graph.clustering;

import graph.clustering.kmeans.Category;
import graph.clustering.kmeans.Cluster;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class Utility {
    public static KeywordVertex findVertexForName(String inputName, ArrayList<KeywordVertex> inputList){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return inputList.get(i);
            }
        }
        //System.out.println("ERROR: VERTEX NOT FOUND FOR " + inputName);
        return null;
    }

    public static RootKeywordVertex findVertexForName(String inputName){
        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            if(GraphFactory.rootKeywordVertices.get(i).name.equals(inputName)){
                return GraphFactory.rootKeywordVertices.get(i);
            }
        }
        //System.out.println("ERROR: VERTEX NOT FOUND FOR " + inputName);
        return null;
    }

    public static boolean isRootKeyword(String inputName){
        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            if(inputName.equals(GraphFactory.rootKeywordVertices.get(i).name)){
                return true;
            }
        }
        return false;
    }

    public static int findIndexForName(String inputName, ArrayList<KeywordVertex> inputList){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return i;
            }
        }
        //System.out.println("ERROR: INDEX NOT FOUND FOR " + inputName);
        return -1;
    }

    public static int findIndexForName(String inputName){
        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            if(GraphFactory.rootKeywordVertices.get(i).name.equals(inputName)){
                return i;
            }
        }
        return -1;
    }

    static int processPercentage(int i, int count){
        int fivePercent = count / 20;
        if(i % fivePercent == 0){
            return 5 * (i / fivePercent);
        }
        return 0;
    }

    public static ArrayList<KeywordVertex> randomInputGenerator(int rand, int num){
        ArrayList<KeywordVertex> vertices = new ArrayList<>();
        for(int i = 0; i < GraphFactory.keywordVertices.size(); i++){
            if(i % rand == 0){
                vertices.add(GraphFactory.keywordVertices.get(i));
            }
            if(vertices.size() >= num) break;
        }
        return vertices;
    }

    public static int findIndexForCategoryIndex(ArrayList<Category> categories,
                                                int categoryIndex){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i).categoryIndex == categoryIndex){
                return i;
            }
        }
        return -1;
    }

    public static void reinitializer(ArrayList<KeywordVertex> keywordVertices,
                                     ArrayList<RootKeywordVertex> rootKeywordVertices){

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).masterSimilarityVector = new Vector<>();
            keywordVertices.get(i).categorySimilarityVector = new Vector<>();
        }
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            rootKeywordVertices.get(i).masterSimilarityVector = new Vector<>();
            rootKeywordVertices.get(i).categorySimilarityVector = new Vector<>();
        }
    }

    public static int findIndexOfMinEntry(Vector<Double> masterSimilarityVector, Cluster cluster){
        double min = Double.MAX_VALUE;
            int index = -1;
            for(int i = 0; i < masterSimilarityVector.size(); i++){
                if((masterSimilarityVector.get(i) < min) && (masterSimilarityVector.get(i) != (double) 0) && (checkConnectivity(i, cluster))) {
                    min = masterSimilarityVector.get(i);
                    index = i;
                }
            }
            return index;

    }

    public static boolean checkConnectivity(int index, Cluster cluster){
        for(int i = 0; i < cluster.memberVertices.size(); i++){
            if(cluster.memberVertices.get(i).masterSimilarityVector.get(index) == 0){
                return false;
            }
        }
        return true;
    }
}
