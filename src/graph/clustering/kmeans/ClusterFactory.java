package graph.clustering.kmeans;

import graph.clustering.vertex.KeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusterFactory {
    public static ArrayList<Cluster> clusters;

    public static void performSquareErrorClustering(){
        
    }

    public static double calculateError(KeywordVertex inputVertex, Vector<Double> inputCentroid){
        double error = 0;
        if(inputCentroid.size() == inputVertex.similarityVector.size()){
            for(int i = 0; i < inputVertex.similarityVector.size(); i++){
                error += euclideanDistance(inputVertex.similarityVector.get(i), inputCentroid.get(i));
            }
        } else {
            for(int i = 0; i < inputVertex.pathLengthVector.size(); i++){
                error += euclideanDistance(inputVertex.pathLengthVector.get(i), inputCentroid.get(i));
            }
        }
        return error;
    }

    public static double euclideanDistance(double a, double b){
        return Math.sqrt(Math.pow((a - b), 2));
    }

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
