package graph.clustering.kmeans;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.GraphFactory;
import graph.clustering.Utility;
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

    public static int masterNumber;
    public static double squareError;
    public static int disbandonedKeywords = 0;

    public static final int MAX_ITERATION = 10000;
    public static final double MAX_ERROR = 0.05;
    public static final int MAX_REALLOC_COUNT = 0;
    public static final int MAX_MEMBER_COUNT = 15;
    public static final int MIN_MEMBER_COUNT = 2;

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords){
        categories = new ArrayList<>(GraphFactory.rootKeywordVertices.size());

        ClusteringInitializer.categoriesBasedInitializer(inputKeywords, categories, GraphFactory.keywordVertices);

        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i).categoryIndex == 1) { // Alle Keywords der Kategorie Ba liegen in der 1. Ebene und haben keinerlei Querverbindungen miteinander => Kein Vektor kann erstellt werden.
                Cluster cluster = new Cluster();
                for(int j = 0; j < categories.get(i).categoryMembers.size(); j++){
                    cluster.memberVertices.add(categories.get(i).categoryMembers.get(j));
                }
                categories.get(i).clusters.add(cluster);
                //categories.get(i).clusters.get(0).averageSquaredDistance = calculateAverageSquareDistance(masterNumber, categories.get(i).clusters.get(0));

                System.out.println("==============================");
                System.out.println(GraphFactory.rootKeywordVertices.get(categories.get(i).categoryIndex).name + ". cluster");
                for(int j = 0; j < categories.get(i).clusters.get(0).memberVertices.size(); j++){
                    System.out.println(categories.get(i).clusters.get(0).memberVertices.get(j).name);
                }
                System.out.println();

                continue;
            }

            System.out.println("==============================");
            System.out.println(GraphFactory.rootKeywordVertices.get(categories.get(i).categoryMembers.get(0).dominantCategory).name + " Clustering: ");
            masterNumber = categories.get(i).categoryMembers.get(0).masterSimilarityVector.size();

            if(categories.get(i).categoryMembers.size() < 6) {
                Cluster cluster = new Cluster();
                for(int j = 0; j < categories.get(i).categoryMembers.size(); j++){
                    cluster.memberVertices.add(categories.get(i).categoryMembers.get(j));
                }
                categories.get(i).clusters.add(cluster);
                categories.get(i).clusters.get(0).averageSquaredDistance = calculateAverageSquareDistance(masterNumber, categories.get(i).clusters.get(0));
                continue;
            }

            do {
                performKMeans(MAX_ITERATION, MAX_REALLOC_COUNT, i);
                } while (splitCluster(categories.get(i)));

            flushEmptyCluster(categories.get(i));
            // Print
            for(int k = 0; k < categories.get(i).clusters.size(); k++){
                System.out.println(categories.get(i).clusters.get(k).grandMaster.name + ". cluster, Average Squared Distance: " + Double.toString(categories.get(i).clusters.get(k).averageSquaredDistance));
                for(int j = 0; j < categories.get(i).clusters.get(k).memberVertices.size(); j++){
                    System.out.println(categories.get(i).clusters.get(k).memberVertices.get(j).name + " x " + Integer.toString(categories.get(i).clusters.get(k).memberVertices.get(j).duplicateCount));
                }
                System.out.println();
            }
        }
    }

    private static void performKMeans(int maxIteration, int maxRealloc, int i){
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;
        ArrayList<KeywordVertex> currentCategoryMembers = categories.get(i).categoryMembers;
        ArrayList<Cluster> currentCategoryClusters = categories.get(i).clusters;


        for(int j = 0; j < currentCategoryMembers.size(); j++){
            Cluster nearestCluster = null;
            try {
                nearestCluster = nearestCentroid(currentCategoryMembers.get(j).masterSimilarityVector, currentCategoryClusters);
            } catch (NullPointerException e){
                System.out.println("PKM Null");
            }
            nearestCluster.memberVertices.add(currentCategoryMembers.get(j));
            currentCategoryMembers.get(j).originCluster = nearestCluster;
        }


        while(iteration < maxIteration && reallocCount > maxRealloc){
            reallocCount = 0;
            flushEmptyCluster(categories.get(i));
            recentralizeCentroids(masterNumber, currentCategoryClusters);
            for(int j = 0; j < currentCategoryMembers.size(); j++){
                KeywordVertex currentCategoryMember = currentCategoryMembers.get(j);

                Cluster nearestCluster = nearestCentroid(currentCategoryMember.masterSimilarityVector, currentCategoryClusters);
                if(!nearestCluster.memberVertices.contains(currentCategoryMember)){
                    Cluster currentOriginCluster = currentCategoryMember.originCluster;

                    nearestCluster.memberVertices.add(currentCategoryMember);
                    currentOriginCluster.memberVertices.remove(currentCategoryMember);
                    currentCategoryMember.originCluster = nearestCluster;
                    reallocCount++;
                }
            }
            iteration++;
        }
        //flushEmptyCluster(categories.get(i));
        setGrandMaster(categories.get(i));

        //categories.get(i).categoryMembers = new ArrayList<>();
        for(int j = 0; j < categories.get(i).clusters.size(); j++){
            categories.get(i).clusters.get(j).averageSquaredDistance = calculateAverageSquareDistance(masterNumber, categories.get(i).clusters.get(j));
            categories.get(i).clusters.get(j).isClosed = true;
        }
    }

    private static void setGrandMaster(Category category){
        for(int j = 0; j < category.clusters.size(); j++){
            Cluster currentCluster = category.clusters.get(j);
            int masterIndex;
            if((masterIndex = Utility.findIndexOfMinEntry(currentCluster.masterSimilarityCentroid, currentCluster)) != -1){
                currentCluster.grandMaster = category.masterVertices.get(masterIndex);
            } else {
                currentCluster.grandMaster = GraphFactory.rootKeywordVertices.get(category.categoryIndex);
            }
        }
    }

    private static void eliminateOutstanders(Category category){
        int clusterCount = category.clusters.size();
        for(int i = 0; i < clusterCount; i++){
            Cluster currentCluster = category.clusters.get(i);
            if(currentCluster.memberVertices.size() < MIN_MEMBER_COUNT && currentCluster.memberVertices.get(currentCluster.memberVertices.size() - 1).duplicateCount < 2){
                category.clusters.remove(currentCluster);
                i--;
                clusterCount--;
                disbandonedKeywords++;
            }
        }
    }

    private static void flushEmptyCluster(Category category){
        int clusterCount = category.clusters.size();
        for(int i = 0; i < clusterCount; i++){
            if(category.clusters.get(i).memberVertices.size() == 0){
                category.clusters.remove(category.clusters.get(i));
                i--;
                clusterCount--;
            }
        }
    }
    private static boolean splitCluster(Category category){
        eliminateOutstanders(category);
        //flushEmptyCluster(category);
        for(int i = 0; i < category.clusters.size(); i++){
            Cluster currentCluster = category.clusters.get(i);
            if(currentCluster.memberVertices.size() > 20 && currentCluster.averageSquaredDistance > 0.00){
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                ClusteringInitializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            } else if(currentCluster.averageSquaredDistance > MAX_ERROR){
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                ClusteringInitializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            } else if(Utility.findIndexForName(currentCluster.grandMaster.name) != -1 && currentCluster.averageSquaredDistance > 0.0){
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                ClusteringInitializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            }
        }
        return false;
    }

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords,
                                                    ArrayList<RootKeywordVertex> rootKeywordVertices){

        clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters, k currently equals the number of categories.
        ClusteringInitializer.categoriesBasedInitializer(rootKeywordVertices);
        masterNumber = rootKeywordVertices.size();

        //ClusteringInitializer.kMeansPPInitializer(10, inputKeywords, clusters);

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).categorySimilarityVector, clusters);
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < MAX_ITERATION && reallocCount > MAX_REALLOC_COUNT){
            reallocCount = 0;
            recentralizeCentroids(masterNumber, clusters);
            for(int i = 0; i < inputKeywords.size(); i++){
                Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).categorySimilarityVector, clusters);
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
            clusters.get(i).averageSquaredDistance = calculateAverageSquareDistance(masterNumber, clusters.get(i));
        }
        squareError = calculateSquareErrorFORGYStyle(masterNumber);


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
        ClusteringInitializer.kMeansPPInitializer(10, inputKeywords, clusters);
        masterNumber = inputKeywords.get(0).pathLengthVector.size();

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).pathLengthVector, clusters);
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < MAX_ITERATION && reallocCount > MAX_REALLOC_COUNT){
            reallocCount = 0;
            recentralizeCentroids(masterNumber, clusters);
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
            clusters.get(i).averageSquaredDistance = calculateAverageSquareDistance(masterNumber, clusters.get(i));
        }
        squareError = calculateSquareErrorFORGYStyle(masterNumber);


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
    private static Cluster nearestCentroid(Vector<Double> currentMasterSimilarityVector,
                                           ArrayList<Cluster> currentClusters){
        double minError = Double.MAX_VALUE;
        Cluster nearestCluster = null;
        for(int i = 0; i < currentClusters.size(); i++){

            double e = 0;
                try {
                    e = euclideanDistance(currentMasterSimilarityVector, currentClusters.get(i).masterSimilarityCentroid);
                } catch (NullPointerException er){
                    System.out.println("nearest Null");
                }

                if (minError > e) {
                    minError = e;
                    nearestCluster = currentClusters.get(i);
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

    private static void recentralizeCentroids(int dimension, ArrayList<Cluster> clusters){
        for(int j = 0; j < dimension; j++){
            for(int k = 0; k < clusters.size(); k++){
                double entry = 0;
                for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                    entry += clusters.get(k).memberVertices.get(i).masterSimilarityVector.get(j);
                }
                if(clusters.get(k).memberVertices.size() == 0) {
                    System.out.println("Division durch null");
                    continue;
                }
                entry = entry / clusters.get(k).memberVertices.size();
                clusters.get(k).masterSimilarityCentroid.set(j, entry);
            }
        }

    }

    // Calculates the error between given vertex and given cluster.
    public static double calculateError(KeywordVertex inputVertex, Vector<Double> inputCentroid){
        return euclideanDistance(inputVertex.categorySimilarityVector, inputCentroid);
    }

    public static double calculateError(KeywordVertex inputVertex, SparseDoubleMatrix1D inputCentroid){
        return euclideanDistance(inputVertex.pathLengthVector, inputCentroid);
    }

    public static double euclideanDistance(Vector<Double> a, Vector<Double> b){
        double distance = 0;
        for(int i = 0; i < a.size(); i++){
            try {
                distance += Math.pow((a.get(i) - b.get(i)), 2);
            } catch (NullPointerException e){
                System.out.println("a " + Integer.toString(a.size()));
                System.out.println("b " + Integer.toString(b.size()));
            }
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
        for(int i = 0; i < k.memberVertices.size(); i++){
            double xij = k.memberVertices.get(i).masterSimilarityVector.get(j);
            double mj = k.masterSimilarityCentroid.get(j);
            variance += Math.pow((xij - mj), 2);
        }
        return variance;
    }
}
