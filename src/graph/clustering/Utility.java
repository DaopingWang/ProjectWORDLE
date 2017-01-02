package graph.clustering;

import graph.clustering.algorithm.process.Category;
import graph.clustering.algorithm.process.Cluster;
import graph.clustering.vertex.Article;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;
import graph.clustering.vertex.SearchKeyword;

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

    public static KeywordVertex findVertexForArticleNum(String inputArticle, ArrayList<Article> articles){
        for(int i = 0; i < articles.size(); i++){
            if(articles.get(i).articleNumber.equals(inputArticle)){
                return articles.get(i).correspondingKeyword;
            }
        }
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

    public static int findSearchKeywordIndexForName(String inputName, ArrayList<SearchKeyword> searchKeywords){
        for(int i = 0; i < searchKeywords.size(); i++){
            if(searchKeywords.get(i).name.equals(inputName)){
                return i;
            }
        }
        return -1;
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

    public static ArrayList<KeywordVertex> randomInputGenerator(int rand, int num, int duplicate){
        ArrayList<KeywordVertex> vertices = new ArrayList<>();
        for(int i = rand; i < GraphFactory.keywordVertices.size(); i++){
            if(i % rand == 0 && i >= rand){
                int j = 0;
                while(j < duplicate){
                    vertices.add(GraphFactory.keywordVertices.get(i));
                    j++;
                }
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
            //keywordVertices.get(i).categorySimilarityVector = new Vector<>();
        }
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            rootKeywordVertices.get(i).masterSimilarityVector = new Vector<>();
            //rootKeywordVertices.get(i).categorySimilarityVector = new Vector<>();
        }
    }

    public static int findIndexOfMinEntry(Vector<Double> masterSimilarityVector, Cluster cluster){
        double min = Double.MAX_VALUE;
            int index = -1;
            for(int i = 0; i < masterSimilarityVector.size() - 1; i++){
                if((masterSimilarityVector.get(i) != 1) && (masterSimilarityVector.get(i) < min) /*&& (masterSimilarityVector.get(i) != 0)*/ && (checkConnectivity(i, cluster))) {
                    min = masterSimilarityVector.get(i);
                    index = i;
                }
            }
            if(index == -1) {
                index = masterSimilarityVector.size() - 1;
                return index;
            } else {
                return index;
            }

    }

    public static int clusterMemberCounter(Cluster cluster){
        int count = 0;
        for(int i = 0; i < cluster.memberVertices.size(); i++){
            for(int j = 0; j < cluster.memberVertices.get(i).duplicateCount; j++){
                count++;
            }
        }
        return count;
    }

    public static int categoryMemberCounter(Category category){
        int count = 0;
        for(int i = 0; i < category.clusters.size(); i++){
            count += clusterMemberCounter(category.clusters.get(i));
        }
        return count;
    }

    public static boolean checkConnectivity(int index, Cluster cluster){
        for(int i = 0; i < cluster.memberVertices.size(); i++){
            if(cluster.memberVertices.get(i).masterSimilarityVector.get(index) == 0){
                return false;
            }
        }
        return true;
    }

    public static int cardinality(Vector<Double> vector){
        int cardinality = 0;
        for(int i = 0; i < vector.size(); i++){
            if(vector.get(i) == (double) 0) cardinality++;
        }
        return cardinality;
    }
}
