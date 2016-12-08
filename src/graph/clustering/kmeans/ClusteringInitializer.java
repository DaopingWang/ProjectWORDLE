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

        // Firstly initialize the first categoryBasedCentroid randomly.
        Cluster first = new Cluster();
        first.centroid = inputVertices.get(inputVerticesCount / 2).pathLengthVector;
        clusters.add(first);

        while(createdCentroid < k){
            double maxProbability = 0;
            SparseDoubleMatrix1D farestVertex;
            double distance = 0;
            double distanceSum = 0;
            double probability = 0;

            for(int i = 0; i < inputVerticesCount; i++){
                for(int j = 0; j < createdCentroid; j++){
                    distance = ClusterFactory.calculateError(inputVertices.get(i), clusters.get(j).centroid);
                    distanceSum += ClusterFactory.calculateError(inputVertices.get(i), clusters.get(j).centroid);
                }
            }
        }
    }

}
