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
    public static void categoriesBasedInitializer(ArrayList<RootKeywordVertex> rootKeywordVertices){
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            Cluster k = new Cluster(rootKeywordVertices.get(i).categorySimilarityVector);
            ClusterFactory.clusters.add(k);
        }
    }

    public static void categoriesBasedInitializer(ArrayList<KeywordVertex> inputVertices,
                                                  ArrayList<Category> inputCategories,
                                                  ArrayList<KeywordVertex> keywordVertices){

        ArrayList createdCategories = new ArrayList();
        for(int i = 0; i < inputVertices.size(); i++){
            int dominantCategory = inputVertices.get(i).dominantCategory;
            if(!createdCategories.contains(dominantCategory)){
                Category category = new Category(dominantCategory);
                category.addMember(inputVertices.get(i));
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
                        for(int k = 0; k < inputCategories.get(i).categoryMembers.size(); k++){
                        inputCategories.get(i).categoryMembers.get(k).masterSimilarityVector.add(inputCategories.get(i).categoryMembers.get(k).pathLengthVector.get(j));
                    }
                }
            }

            if(inputCategories.get(i).categoryMembers.size() > 3){
                kmeansPPInitializer(3, inputCategories.get(i).categoryMembers, inputCategories.get(i).clusters);
            } else {
                kmeansPPInitializer(inputCategories.get(i).categoryMembers.size() / 2, inputCategories.get(i).categoryMembers, inputCategories.get(i).clusters);
            }
        }
    }

    private static boolean checkMasterQualification(KeywordVertex potentialMaster,
                                                    ArrayList<KeywordVertex> memberList,
                                                    Category category){

        for(int i = 0; i < potentialMaster.subordinateList.size(); i++){
            for(int j = 0; j < memberList.size(); j++){
                if(potentialMaster.subordinateList.get(i).equals(memberList.get(j).name)){
                    if(potentialMaster.layer <= category.maxLayer){
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * using K-Means++ algorithm.
     * @param k Wished number of clusters.
     */
    public static void kmeansPPInitializer(int k,
                                           ArrayList<KeywordVertex> inputVertices,
                                           ArrayList<Cluster> clusters){

        int inputVerticesCount = inputVertices.size();
        int createdCentroid = 1;

        //System.out.println("=== Start K-Means++ initialization " + GraphFactory.rootKeywordVertices.get(inputVertices.get(0).dominantCategory).name + " ===");
        // Firstly initialize the first categoryBasedCentroid randomly.
        Cluster first = new Cluster();
        first.masterBasedCentroid = inputVertices.get(inputVerticesCount / 2).masterSimilarityVector;
        clusters.add(first);

        while(createdCentroid < k){
            double maxProbability = 0;
            Vector<Double> farestVertex = null;
            double distanceSum = 0;

            for(int i = 0; i < inputVerticesCount; i++){
                inputVertices.get(i).shortestDistance = Math.pow(shortestDistanceToClosestCentroid(inputVertices.get(i).masterSimilarityVector, clusters), 2);
                distanceSum += inputVertices.get(i).shortestDistance;
            }
            for(int i = 0; i < inputVerticesCount; i++){
                double probability = inputVertices.get(i).shortestDistance / distanceSum;
                if(maxProbability < probability){
                    maxProbability = probability;
                    farestVertex = inputVertices.get(i).masterSimilarityVector;
                }
            }
            Cluster next = new Cluster();
            next.masterBasedCentroid = farestVertex;
            clusters.add(next);
            createdCentroid++;
        }
        //System.out.println("=== Initialization done ===");
        //System.out.println();
    }

    private static double shortestDistanceToClosestCentroid(Vector<Double> inputVertex,
                                                            ArrayList<Cluster> clusters){

        double shortestDistance = Double.MAX_VALUE;
        for(int i = 0; i < clusters.size(); i++){
            double distance = ClusterFactory.euclideanDistance(inputVertex, clusters.get(i).masterBasedCentroid);
            if(shortestDistance > distance){
                shortestDistance = distance;
            }
        }
        return shortestDistance;
    }

}
