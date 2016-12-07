package graph.clustering.kmeans;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusterFactory {
    public static ArrayList<Cluster> clusters;

    public static double calculateWithinClusterVariation(int j, Cluster k){
        double variance = 0;
        for(int i = 0; i < k.memberVertices.size(); i++){
            double xij = k.memberVertices.get(i).similarityVector.get(j);
            double mj = k.centroid.get(j);
            variance += Math.pow((xij - mj), 2);
        }
        return Math.sqrt(variance);
    }
}
