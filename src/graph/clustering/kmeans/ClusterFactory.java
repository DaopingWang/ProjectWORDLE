package graph.clustering.kmeans;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.GraphFactory;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusterFactory {
    public static ArrayList<Cluster> clusters;
    public static ArrayList<Category> categories;

    public static int categoryNumber;
    public static double squareError;

    public static final int MAX_ITERATION = 10000;
    public static final int MAX_ERROR = 20;
    public static final int MAX_REALLOC_COUNT = 1;

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords){
        categories = new ArrayList<>(GraphFactory.rootKeywordVertices.size());
    }

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords,
                                                    ArrayList<RootKeywordVertex> rootKeywordVertices){

        clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters, k currently equals the number of categories.
        ClusteringInitializer.categoriesBasedInitializer(rootKeywordVertices);
        categoryNumber = rootKeywordVertices.size();

        //ClusteringInitializer.kmeansPPInitializer(10, inputKeywords, clusters);

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).similarityVector, clusters);
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < MAX_ITERATION && reallocCount > MAX_REALLOC_COUNT){
            reallocCount = 0;
            recentralizeCentroids(categoryNumber);
            for(int i = 0; i < inputKeywords.size(); i++){
                Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).similarityVector, clusters);
                if(!nearestCluster.memberVertices.contains(inputKeywords.get(i))){
                    nearestCluster.memberVertices.add(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster.memberVertices.remove(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster = nearestCluster;
                    reallocCount++;
                }
            }
            iteration++;
        }

        for(int i = 0; i < clusters.size(); i++){
            clusters.get(i).averageSquaredDistance = calculateAverageSquareDistance(categoryNumber, clusters.get(i));
        }
        squareError = calculateSquareErrorFORGYStyle(categoryNumber);


        // Print
        for(int k = 0; k < clusters.size(); k++){
            System.out.println(Integer.toString(k) + ". cluster, ASD " + Double.toString(clusters.get(k).averageSquaredDistance));
            for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                System.out.println(clusters.get(k).memberVertices.get(i).name);
            }
            System.out.println();
        }

        System.out.println("FORGY Square Error: " + Double.toString(squareError));
        System.out.println("Iterations: " + Integer.toString(iteration));
    }

    public static void performSquareErrorClusteringPP(ArrayList<KeywordVertex> inputKeywords,
                                                      ArrayList<RootKeywordVertex> rootKeywordVertices){

        clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters using K-Means++
        ClusteringInitializer.kmeansPPInitializer(10, inputKeywords, clusters);
        categoryNumber = inputKeywords.get(0).pathLengthVector.size();

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).pathLengthVector, clusters);
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < MAX_ITERATION && reallocCount > MAX_REALLOC_COUNT){
            reallocCount = 0;
            recentralizeCentroids(categoryNumber);
            for(int i = 0; i < inputKeywords.size(); i++){
                Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).pathLengthVector, clusters);
                if(!nearestCluster.memberVertices.contains(inputKeywords.get(i))){
                    nearestCluster.memberVertices.add(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster.memberVertices.remove(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster = nearestCluster;
                    reallocCount++;
                }
            }
            iteration++;
        }

        for(int i = 0; i < clusters.size(); i++){
            clusters.get(i).averageSquaredDistance = calculateAverageSquareDistance(categoryNumber, clusters.get(i));
        }
        squareError = calculateSquareErrorFORGYStyle(categoryNumber);


        // Print
        for(int k = 0; k < clusters.size(); k++){
            System.out.println(Integer.toString(k) + ". cluster, ASD " + Double.toString(clusters.get(k).averageSquaredDistance));
            for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                System.out.println(clusters.get(k).memberVertices.get(i).name);
            }
            System.out.println();
        }

        System.out.println("FORGY Square Error: " + Double.toString(squareError));
        System.out.println("Iterations: " + Integer.toString(iteration));
    }

    // Calculates the sum of errors, returns the nearest cluster for given keyword.
    private static Cluster nearestCentroid(Vector<Double> inputVertex,
                                           ArrayList<Cluster> clusters){
        double minError = Double.MAX_VALUE;
        Cluster nearestCluster = null;
        for(int i = 0; i < clusters.size(); i++){
            double e = euclideanDistance(inputVertex, clusters.get(i).categoryBasedCentroid);
            if(minError > e){
                minError = e;
                nearestCluster = clusters.get(i);
            }
        }
        return nearestCluster;
    }

    public static Cluster nearestCentroid(SparseDoubleMatrix1D inputVertex,
                                          ArrayList<Cluster> clusters){

        double minError = Double.MAX_VALUE;
        Cluster nearestCluster = null;
        for(int i = 0; i < clusters.size(); i++){
            double e = euclideanDistance(inputVertex, clusters.get(i).centroid);
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

    private static double calculateAverageSquareDistance(int dimension, Cluster k){
        int memberCount = k.memberVertices.size();
        double averageSquareDistance = 0;
        for(int j = 0; j < dimension; j++){
            averageSquareDistance += calculateWithinClusterVariation(j, k) / (double) memberCount;
        }
        return Math.sqrt(averageSquareDistance) / dimension;
    }

    private static void recentralizeCentroids(int dimension){
        if(dimension == GraphFactory.rootKeywordVertices.size()){
            for(int j = 0; j < dimension; j++){
                for(int k = 0; k < clusters.size(); k++){
                    double entry = 0;
                    for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                        entry += clusters.get(k).memberVertices.get(i).similarityVector.get(j);
                    }
                    entry = entry / clusters.get(k).memberVertices.size();
                    clusters.get(k).categoryBasedCentroid.set(j, entry);
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
        return euclideanDistance(inputVertex.similarityVector, inputCentroid);
    }

    public static double calculateError(KeywordVertex inputVertex, SparseDoubleMatrix1D inputCentroid){
        return euclideanDistance(inputVertex.pathLengthVector, inputCentroid);
    }

    public static double euclideanDistance(Vector<Double> a, Vector<Double> b){
        double distance = 0;
        for(int i = 0; i < a.size(); i++){
            distance += Math.pow((a.get(i) - b.get(i)), 2);
        }
        return Math.sqrt(distance);
    }

    public static double euclideanDistance(SparseDoubleMatrix1D a, SparseDoubleMatrix1D b){
        double distance = 0;
        for(int i = 0; i < a.size(); i++){
            distance += Math.pow((a.get(i) - b.get(i)), 2);
        }
        if(distance == 0){
            return 0;
        }
        return Math.sqrt(distance);
    }

    public static double calculateWithinClusterVariation(int j, Cluster k){
        double variance = 0;
        if(categoryNumber == GraphFactory.rootKeywordVertices.size()){
            for(int i = 0; i < k.memberVertices.size(); i++){
                double xij = k.memberVertices.get(i).similarityVector.get(j);
                double mj = k.categoryBasedCentroid.get(j);
                variance += Math.pow((xij - mj), 2);
            }
            return variance;
        }
        for(int i = 0; i < k.memberVertices.size(); i++){
            double xij = k.memberVertices.get(i).pathLengthVector.get(j);
            double mj = k.centroid.get(j);
            variance += Math.pow((xij - mj), 2);
        }
        return variance;
    }
}
