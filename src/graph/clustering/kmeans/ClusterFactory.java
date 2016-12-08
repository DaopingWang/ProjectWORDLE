package graph.clustering.kmeans;

import graph.clustering.GraphFactory;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusterFactory {
    public static ArrayList<Cluster> clusters;
    public static int categoryNumber;
    public static double squareError;

    public static final int MAX_ITERATION = 5000;
    public static final int MAX_ERROR = 20;
    public static final int MAX_REALLOC_COUNT = 2;

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords,
                                                    ArrayList<RootKeywordVertex> rootKeywordVertices){

        clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters, k currently equals the number of categories.
        ClusteringInitializer.categoriesBasedInitializer(rootKeywordVertices);
        categoryNumber = rootKeywordVertices.size();

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = nearestCentroid(inputKeywords.get(i));
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < MAX_ITERATION && reallocCount > MAX_REALLOC_COUNT){
            recentralizeCentroids(rootKeywordVertices.size());
            for(int i = 0; i < inputKeywords.size(); i++){
                Cluster nearestCluster = nearestCentroid(inputKeywords.get(i));
                if(!nearestCluster.memberVertices.contains(inputKeywords.get(i))){
                    nearestCluster.memberVertices.add(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster.memberVertices.remove(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster = nearestCluster;
                    reallocCount++;
                }
            }
            iteration++;
        }

        squareError = calculateSquareErrorFORGYStyle(categoryNumber);


        // Print
        for(int i = 0; i < clusters.size(); i++){
            System.out.println(GraphFactory.rootKeywordVertices.get(i).name + ". cluster:");
            for(int j = 0; j < clusters.get(i).memberVertices.size(); j++){
                System.out.println(clusters.get(i).memberVertices.get(j).name);
            }
            System.out.println();
        }

        System.out.println("Square Error: " + Double.toString(squareError));
    }

    // Calculates the sum of errors, returns the nearest cluster for given keyword.
    private static Cluster nearestCentroid(KeywordVertex inputVertex){
        double minError = Double.MAX_VALUE;
        Cluster nearestCluster = null;
        for(int i = 0; i < clusters.size(); i++){
            double e = calculateError(inputVertex, clusters.get(i).centroid);
            if(minError > e){
                minError = e;
                nearestCluster = clusters.get(i);
            }
        }
        return nearestCluster;
    }

    private static double calculateSquareErrorFORGYStyle(int dimension){
        double withinClusterVariation = 0;
        for(int k = 0; k < clusters.size(); k++){
            for(int j = 0; j < dimension; j++){
                withinClusterVariation += calculateWithinClusterVariation(j, clusters.get(k));
            }
        }
        return withinClusterVariation;
    }

    private static void recentralizeCentroids(int dimension){
        if(dimension == categoryNumber){
            for(int j = 0; j < dimension; j++){
                for(int k = 0; k < clusters.size(); k++){
                    double entry = 0;
                    for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                        entry += clusters.get(k).memberVertices.get(i).similarityVector.get(j);
                    }
                    entry = entry / clusters.get(k).memberVertices.size();
                    clusters.get(k).centroid.set(j, entry);
                }
            }
        } else {
            for(int j = 0; j < dimension; j++){
                for(int k = 0; k < clusters.size(); k++){
                    double entry = 0;
                    for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                        entry += clusters.get(k).memberVertices.get(i).pathLengthVector.get(j);
                    }
                    entry = entry / clusters.get(k).memberVertices.size();
                    clusters.get(k).centroid.set(j, entry);
                }
            }
        }
    }

    // Calculates the error between given vertex and given cluster.
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
        return Math.sqrt(error);
    }

    // Returns the Euclidean distance between a and b.
    public static double euclideanDistance(double a, double b){
        return Math.pow((a - b), 2);
    }

    public static double calculateWithinClusterVariation(int j, Cluster k){
        double variance = 0;
        for(int i = 0; i < k.memberVertices.size(); i++){
            double xij = k.memberVertices.get(i).similarityVector.get(j);
            double mj = k.centroid.get(j);
            variance += Math.pow((xij - mj), 2);
        }
        return variance;
    }
}
