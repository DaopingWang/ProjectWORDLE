package graph.clustering.kmeans;

import graph.clustering.GraphFactory;
import graph.clustering.Utility;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusteringInitializer {
    public static final int MAX_ITERATION = 2;
    public static final int MIN_CLUSTER_SIZE = 3;
    public static final double MIN_INTERCLUSTER_DISTANCE = 0.5;
    public static final double MAX_STANDARD_DEVIATION = 1.5;
    public static final int MAX_PAIR = 3;

    public static void categoriesBasedInitializer(ArrayList<RootKeywordVertex> rootKeywordVertices){
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            Cluster k = new Cluster(rootKeywordVertices.get(i).categorySimilarityVector);
            ClusterFactory.clusters.add(k);
        }
    }

    public static void categoriesBasedInitializer(ArrayList<KeywordVertex> inputVertices,
                                                  ArrayList<Category> inputCategories,
                                                  ArrayList<KeywordVertex> keywordVertices){

        checkDuplicates(inputVertices);

        ArrayList createdCategories = new ArrayList();
        for(int i = 0; i < inputVertices.size(); i++){
            int dominantCategory = inputVertices.get(i).dominantCategory;
            if(!createdCategories.contains(dominantCategory)){
                Category category = new Category(dominantCategory);
                category.addMember(inputVertices.get(i));
                category.maxIter = MAX_ITERATION;
                category.samprm = MIN_CLUSTER_SIZE;
                category.lump = MIN_INTERCLUSTER_DISTANCE;
                category.stdv = MAX_STANDARD_DEVIATION;
                category.maxpair = MAX_PAIR;
                inputCategories.add(category);
                createdCategories.add(dominantCategory);
            } else {
                inputCategories.get(Utility.findIndexForCategoryIndex(inputCategories, dominantCategory)).addMember(inputVertices.get(i));
            }
        }

        for(int i = 0; i < inputCategories.size(); i++){
            for(int j = 0; j < keywordVertices.size(); j++){
                //if((keywordVertices.get(j).dominantCategory == inputCategories.get(i).categoryIndex) && (keywordVertices.get(j).layer <= inputCategories.get(i).maxLayer)){
                if(checkMasterQualification(keywordVertices.get(j), inputCategories.get(i).categoryMembers, inputCategories.get(i))){
                    inputCategories.get(i).masterVertices.add(keywordVertices.get(j)); // Add vertex to master vector of category.
                    for(int k = 0; k < inputCategories.get(i).categoryMembers.size(); k++){
                        inputCategories.get(i).categoryMembers.get(k).masterSimilarityVector.add(inputCategories.get(i).categoryMembers.get(k).pathLengthVector.get(j));
                    }
                }
            }

            // Add root dependency to the last entry to ensure everyone has a mastersimivector
            inputCategories.get(i).masterVertices.add(GraphFactory.rootKeywordVertices.get(inputCategories.get(i).categoryIndex));
            for(int j = 0; j < inputCategories.get(i).categoryMembers.size(); j++){
                inputCategories.get(i).categoryMembers.get(j).masterSimilarityVector.add((double) 1);
            }

            inputCategories.get(i).numClus = Math.max(inputCategories.get(i).categoryMembers.size() / 10, 1);
            kMeansPPInitializer(inputCategories.get(i).numClus, inputCategories.get(i).categoryMembers, inputCategories.get(i).clusters);

        }
    }

    private static boolean checkMasterQualification(KeywordVertex potentialMaster,
                                                    ArrayList<KeywordVertex> memberList,
                                                    Category category){

        for(int i = 0; i < potentialMaster.subordinateList.size(); i++){
            for(int j = 0; j < memberList.size(); j++){
                if(potentialMaster.subordinateList.get(i).equals(memberList.get(j).name) && potentialMaster.dominantCategory == category.categoryIndex){
                    //if(potentialMaster.layer <= category.maxLayer){
                        return true;
                    //}
                }
            }
        }
        return false;
    }

    private static void checkDuplicates(ArrayList<KeywordVertex> inputKeywords){
        for(int i = 0; i < inputKeywords.size(); i++){
            for(int j = i+1; j < inputKeywords.size(); j++){
                if(inputKeywords.get(i).name.equals(inputKeywords.get(j).name)){
                    inputKeywords.get(i).duplicateCount++;
                    inputKeywords.remove(inputKeywords.get(j));
                    j--;
                }
            }
        }
    }

    /**
     * using K-Means++ algorithm.
     * @param k Wished number of clusters.
     */
    public static void kMeansPPInitializer(int k,
                                           ArrayList<KeywordVertex> inputVertices,
                                           ArrayList<Cluster> clusters){

        int inputVerticesCount = inputVertices.size();
        int createdCentroid = 1;

        // Firstly initialize the first categoryBasedCentroid randomly.
        Cluster first = new Cluster();
        first.masterSimilarityCentroid = inputVertices.get(0).masterSimilarityVector;
        clusters.add(first);

        while(createdCentroid < k){
            double maxProbability = -1;
            Vector<Double> farestVector = null;
            KeywordVertex farestKeyword = null;
            double distanceSum = 0;

            for(int i = 0; i < inputVerticesCount; i++){
                inputVertices.get(i).shortestDistance = Math.pow(shortestDistanceToClosestCentroid(inputVertices.get(i).masterSimilarityVector, clusters), 2);
                distanceSum += inputVertices.get(i).shortestDistance;
            }
            for(int i = 0; i < inputVerticesCount; i++){
                double probability = inputVertices.get(i).shortestDistance / distanceSum;
                if(maxProbability < probability){
                    maxProbability = probability;
                    farestVector = inputVertices.get(i).masterSimilarityVector;
                    farestKeyword = inputVertices.get(i);
                }
            }
            Cluster next = new Cluster();
            if(farestVector == null){ // bad way to prevent null mastersimicentroid
                next.masterSimilarityCentroid = clusters.get(clusters.size() - 1).masterSimilarityCentroid;
            } else {
                next.masterSimilarityCentroid = farestVector;
                //next.memberVertices.add(farestKeyword);
            }
            clusters.add(next);
            createdCentroid++;
        }
    }

    private static double shortestDistanceToClosestCentroid(Vector<Double> inputVertex,
                                                            ArrayList<Cluster> clusters){

        double shortestDistance = Double.MAX_VALUE;
        for(int i = 0; i < clusters.size(); i++){
            if(clusters.get(i).isClosed) continue;
            double distance = ClusterFactory.euclideanDistance(inputVertex, clusters.get(i).masterSimilarityCentroid);
            if(shortestDistance > distance){
                shortestDistance = distance;
            }
        }
        return shortestDistance;
    }

}
