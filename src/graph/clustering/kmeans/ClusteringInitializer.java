package graph.clustering.kmeans;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.GraphFactory;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusteringInitializer {
    public static void categoriesBasedInitializer(ArrayList<RootKeywordVertex> rootKeywordVertices){
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            Cluster k = new Cluster(rootKeywordVertices.get(i).similarityVector);
            ClusterFactory.clusters.add(k);
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
        first.centroid = inputVertices.get(inputVerticesCount / 2).pathLengthVector;
        clusters.add(first);

        while(createdCentroid < k){
            double maxProbability = 0;
            SparseDoubleMatrix1D farestVertex = null;
            double distanceSum = 0;

            for(int i = 0; i < inputVerticesCount; i++){
                inputVertices.get(i).shortestDistance = Math.pow(shortestDistanceToClosestCentroid(inputVertices.get(i).pathLengthVector, clusters), 2);
                distanceSum += inputVertices.get(i).shortestDistance;
            }
            for(int i = 0; i < inputVerticesCount; i++){
                double probability = inputVertices.get(i).shortestDistance / distanceSum;
                if(maxProbability < probability){
                    maxProbability = probability;
                    farestVertex = inputVertices.get(i).pathLengthVector;
                }
            }
            Cluster next = new Cluster();
            next.centroid = farestVertex;
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

}
