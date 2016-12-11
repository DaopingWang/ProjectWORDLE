package graph.clustering.kmeans;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
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
                                                  ArrayList<KeywordVertex> keywordVertices,
                                                  ArrayList<RootKeywordVertex> rootKeywordVertices,
                                                  ArrayList missingCategories){

        ArrayList createdCategories = new ArrayList();
        for(int i = 0; i < inputVertices.size(); i++){
            int dominantCategory = inputVertices.get(i).dominantCategory;
            if(!createdCategories.contains(dominantCategory)){
                Category category = new Category(dominantCategory);
                category.addMember(inputVertices.get(i));
                inputCategories.add(inputVertices.get(i).dominantCategory, category);
                createdCategories.add(dominantCategory);
            } else {
                inputCategories.get(dominantCategory).addMember(inputVertices.get(i));
            }
        }

        for(int i = 0; i < rootKeywordVertices.size(); i++){
            if(inputCategories.get(i) == null){
                missingCategories.add(i);
            } else{
                for(int j = 0; j < keywordVertices.size(); j++){
                    if((keywordVertices.get(j).dominantCategory == inputCategories.get(i).categoryIndex) && (keywordVertices.get(j).layer <= inputCategories.get(i).maxLayer)){
                        for(int k = 0; k < inputCategories.get(i).categoryMembers.size(); k++){
                            inputCategories.get(i).categoryMembers.get(k).masterSimilarityVector.add(inputCategories.get(i).categoryMembers.get(k).pathLengthVector.get(j));
                        }
                    }
                }

                kmeansPPInitializer(5, inputCategories.get(i).categoryMembers, inputCategories.get(i).clusters);
            }
        }



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

        System.out.println("=== Start K-Means++ initialization ===");
        // Firstly initialize the first categoryBasedCentroid randomly.
        Cluster first = new Cluster();
        first.categoryBasedCentroid = inputVertices.get(inputVerticesCount / 2).masterSimilarityVector;
        clusters.add(first);

        while(createdCentroid < k){
            double maxProbability = 0;
            //SparseDoubleMatrix1D farestVertex = null;
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
        System.out.println("=== Initialization done ===");
    }

    private static double shortestDistanceToClosestCentroid(SparseDoubleMatrix1D inputVertex,
                                                            ArrayList<Cluster> clusters){

        double shortestDistance = Double.MAX_VALUE;
        for(int i = 0; i < clusters.size(); i++){
            double distance = ClusterFactory.euclideanDistance(inputVertex, clusters.get(i).centroid);
            if(shortestDistance > distance){
                shortestDistance = distance;
            }
        }
        return shortestDistance;
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
