package graph.clustering.algorithm.process;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.GraphFactory;
import graph.clustering.Utility;
import graph.clustering.algorithm.ISODATAFactory;
import graph.clustering.algorithm.KMeansFactory;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;
import graph.clustering.vertex.Vertex;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */

/**
 * This class contains methods shared by both the traditional K-Means and the modified ISODATA
 * clustering algorithm.
 */
public class CoreFunctions {
    public static ArrayList<Cluster> clusters;
    public static ArrayList<Category> categories;

    public static int vectorSpaceDimension;
    public static double squareError;
    public static int abandonedKeywords;
    public static int searchExampleCount;
    public static double dropRate;

    public static boolean isoclusMode;
    public static boolean clusterSuspended;

    public static Vector<int[]> calculateInterclusterDistances(Category currentCategory){
        double currentDistance;
        int mergeCount = 0;
        Vector<int[]> minPairVector = new Vector<>();

        for(int i = 0; i < currentCategory.clusters.size(); i++){
            Vector<Double> currentIDVector = currentCategory.clusters.get(i).interclusterDistance;
            for(int j = 0; j < currentCategory .clusters.size(); j++){
                if(i == j){
                    currentIDVector.add(Double.MAX_VALUE);
                    continue;
                }
                currentDistance = euclideanDistance(currentCategory.clusters.get(i).masterSimilarityCentroid, currentCategory.clusters.get(j).masterSimilarityCentroid);
                currentIDVector.add(currentDistance);
                int[] pair= {i, j};
                if ((currentCategory.lump > currentDistance) && (mergeCount < currentCategory.maxpair) && (!ISODATAFactory.pairExists(minPairVector, pair))){
                    minPairVector.add(pair);
                    mergeCount++;
                }
            }
        }
        return minPairVector;
    }

    public static boolean baSpecialTreatment(Category currentCategory, int index){
        if(currentCategory.categoryIndex == 1) { // Alle Keywords der Kategorie Ba liegen in der 1. Ebene und haben keinerlei Querverbindungen miteinander => Kein Vektor kann erstellt werden.
            Cluster cluster = new Cluster();
            for(int j = 0; j < currentCategory.categoryMembers.size(); j++){
                cluster.memberVertices.add(currentCategory.categoryMembers.get(j));
            }
            currentCategory.clusters.add(cluster);

            flushEmptyClusters(currentCategory);
            if(currentCategory.clusters.size() == 0) return true;
            systemOutPrint(index);
            return true;
        }
        return false;
    }

    /**
     * This is the core function of our clustering algorithm implementation.
     * It assigns and repositions points and cluster centers until convergence.
     * @param maxIteration Number of iteration
     * @param maxRealloc Maximum permitted reallocation count
     * @param i index of the current category
     * @param rootKeywordVertices root keywords from data set
     */
    public static void performKMeans(int maxIteration,
                                     int maxRealloc,
                                     int i,
                                     ArrayList<RootKeywordVertex> rootKeywordVertices){
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;
        ArrayList<KeywordVertex> currentCategoryMembers = categories.get(i).categoryMembers;
        ArrayList<Cluster> currentCategoryClusters = categories.get(i).clusters;


        for(int j = 0; j < currentCategoryMembers.size(); j++){
            Cluster nearestCluster = null;
            nearestCluster = nearestCentroid(currentCategoryMembers.get(j).masterSimilarityVector, currentCategoryClusters);
            if(!nearestCluster.memberVertices.contains(currentCategoryMembers.get(j)) /*&& !currentCategoryMembers.get(j).originCluster.equals(nearestCluster)*/){
                nearestCluster.memberVertices.add(currentCategoryMembers.get(j));
                currentCategoryMembers.get(j).originCluster = nearestCluster;
            }
        }

        if(isoclusMode){
            clusterSuspended = ISODATAFactory.suspendSmallClusters(categories.get(i));
        }

        while(iteration < maxIteration && reallocCount > maxRealloc){
            reallocCount = 0;
            flushEmptyClusters(categories.get(i));
            recentralizeCentroids(vectorSpaceDimension, currentCategoryClusters);
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
        setGrandMaster(categories.get(i), rootKeywordVertices);

        /*
        double overallAverage = 0;
        for(int j = 0; j < currentCategoryClusters.size(); j++){
            currentCategoryClusters.get(j).averageEuclideanDistance = calculateAverageEuclideanDistance(currentCategoryClusters.get(j));
            overallAverage += currentCategoryClusters.get(j).averageEuclideanDistance;
            categories.get(i).clusters.get(j).isClosed = true;
        }
        overallAverage = overallAverage / currentCategoryClusters.size();*/
    }

    /**
     * finds for each cluster the closest common parent keyword, which is the smallest coordinate
     * of the cluster center vector that is reachable from every cluster member.
     * @param category current category
     * @param rootKeywordVertices root keywords from data set
     */
    public static void setGrandMaster(Category category,
                                      ArrayList<RootKeywordVertex> rootKeywordVertices){
        for(int j = 0; j < category.clusters.size(); j++){
            Cluster currentCluster = category.clusters.get(j);
            int masterIndex;
            if((masterIndex = Utility.findIndexOfMinEntry(currentCluster.masterSimilarityCentroid, currentCluster)) != -1){
                currentCluster.grandMaster = category.masterVertices.get(masterIndex);
            } else {
                currentCluster.grandMaster = rootKeywordVertices.get(category.categoryIndex);
            }
        }
    }

    /**
     * deletes outstanding points.
     * @param category current keyword category
     */
    public static void eliminateOutstanders(Category category){
        int clusterCount = category.clusters.size();
        //int categoryMemberCount = Utility.categoryMemberCounter(category);
        for(int i = 0; i < clusterCount; i++){
            Cluster currentCluster = category.clusters.get(i);
            if(currentCluster.memberVertices.size() < KMeansFactory.MIN_MEMBER_COUNT && Utility.clusterMemberCounter(currentCluster) < searchExampleCount / 4){
                category.clusters.remove(currentCluster);
                //searchExampleCount -= Utility.clusterMemberCounter(currentCluster);
                i--;
                clusterCount--;
                abandonedKeywords++;
            }
        }
    }

    /**
     * deletes empty clusters.
     * @param category current category
     */
    public static void flushEmptyClusters(Category category){
        for(int i = 0; i < category.clusters.size(); i++){
            if(category.clusters.get(i).memberVertices.size() == 0){
                category.clusters.remove(category.clusters.get(i));
                i--;
            }
        }
    }

    /**
     * merges clusters which are sharing the same grand master.
     * @param category current keyword category
     */
    public static void mergeSameClusters(Category category){
        for(int i = 0; i < category.clusters.size(); i++){
            for(int j = i+1; j < category.clusters.size(); j++){
                if (category.clusters.get(i).grandMaster.equals(category.clusters.get(j).grandMaster)){
                    category.clusters.get(i).memberVertices.addAll(category.clusters.get(j).memberVertices);
                    category.clusters.get(i).averageEuclideanDistance = calculateAverageEuclideanDistance(category.clusters.get(i));
                    category.clusters.remove(category.clusters.get(j));
                    j--;
                }
            }
        }
    }

    /**
     * System output
     * @param index index of the current cluster
     */
    public static void systemOutPrint(int index){
        System.out.println(GraphFactory.rootKeywordVertices.get(categories.get(index).categoryMembers.get(0).dominantCategory).name.toUpperCase() + " Clustering: ".toUpperCase());
        System.out.println();
        for(int k = 0; k < categories.get(index).clusters.size(); k++){
            if(categories.get(index).categoryIndex == 1){
                categories.get(index).clusters.get(k).averageEuclideanDistance = -1;
                categories.get(index).clusters.get(k).grandMaster = GraphFactory.rootKeywordVertices.get(1);
            }
            System.out.println(categories.get(index).clusters.get(k).grandMaster.name + ". cluster, AverageEuclideanDistance: " + Double.toString(categories.get(index).clusters.get(k).averageEuclideanDistance));
            for(int j = 0; j < categories.get(index).clusters.get(k).memberVertices.size(); j++){
                System.out.println(categories.get(index).clusters.get(k).memberVertices.get(j).name + " x " + Integer.toString(categories.get(index).clusters.get(k).memberVertices.get(j).duplicateCount));
            }
            System.out.println();
        }
    }

    public static Vertex[] convertClusterToVertexArray(Cluster currentCluster){
        Vertex[] list = new Vertex[currentCluster.memberVertices.size() + 1];
        list[0] = currentCluster.grandMaster;
        for(int i = 0; i < currentCluster.memberVertices.size(); i++){
            list[i + 1] = currentCluster.memberVertices.get(i);
        }

        // cluster size completion for wordle
        if(currentCluster.memberVertices.size() < 5){
            Vertex[] complement = Utility.findMinVerticesFromSVector(Utility.findVertexWithMostDublicates(currentCluster.memberVertices).pathLengthVector, 5 - currentCluster.memberVertices.size(), list);
            if(complement != null){
                list = ArrayUtils.addAll(list, complement);
            }
        }

        return list;
    }

    public static void performSquareErrorClusteringPP(ArrayList<KeywordVertex> inputKeywords,
                                                      ArrayList<RootKeywordVertex> rootKeywordVertices){

        clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters using K-Means++
        Initializer.kMeansPPInitializer(10, inputKeywords, clusters);
        vectorSpaceDimension = inputKeywords.get(0).pathLengthVector.size();

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).pathLengthVector, clusters);
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < KMeansFactory.MAX_ITERATION && reallocCount > KMeansFactory.MAX_REALLOC_COUNT){
            reallocCount = 0;
            recentralizeCentroids(vectorSpaceDimension, clusters);
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
            //clusters.get(i).averageEuclideanDistance = calculateAverageSquareDistance(vectorSpaceDimension, clusters.get(i));
            clusters.get(i).averageEuclideanDistance = calculateAverageEuclideanDistance(clusters.get(i));
        }
        squareError = calculateSquareErrorFORGYStyle(vectorSpaceDimension);


        // Print
        for(int k = 0; k < clusters.size(); k++){
            System.out.println(Integer.toString(k) + ". cluster, ASD " + Double.toString(clusters.get(k).averageEuclideanDistance));
            for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                System.out.println(clusters.get(k).memberVertices.get(i).name);
            }
            System.out.println();
        }

        System.out.println("FORGY Square Error: " + Double.toString(squareError));
        System.out.println("Iterations: " + Integer.toString(iteration));
    }

    /**
     * Calculates distances between the input keyword and all cluster centers, then returns the nearest cluster.
     * @param currentMasterSimilarityVector Vector of the input keyword
     * @param currentClusters cluster list of current category
     * @return the nearest cluster.
     */
    public static Cluster nearestCentroid(Vector<Double> currentMasterSimilarityVector,
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

    public static double calculateSquareErrorFORGYStyle(int dimension){
        double withinClusterVariation = 0;
        for(int k = 0; k < clusters.size(); k++){
            for(int j = 0; j < dimension; j++){
                withinClusterVariation += calculateWithinClusterVariation(j, clusters.get(k));
            }
        }
        return withinClusterVariation;
    }

    public static double calculateAverageSquareDistance(int dimension, Cluster currentCluster){
        int memberCount = currentCluster.memberVertices.size();
        double averageSquareDistance = 0;
        for(int j = 0; j < dimension; j++){
            averageSquareDistance += calculateWithinClusterVariation(j, currentCluster) / (double) memberCount;
        }
        return Math.sqrt(averageSquareDistance / dimension);
    }

    public static double calculateAverageEuclideanDistance(Cluster k){
        double averageEuclideanDistance = 0;
        for(int i = 0; i < k.memberVertices.size(); i++){
            averageEuclideanDistance += euclideanDistance(k.memberVertices.get(i).masterSimilarityVector, k.masterSimilarityCentroid);
        }
        return averageEuclideanDistance / k.memberVertices.size();
    }

    public static void recentralizeCentroids(int dimension, ArrayList<Cluster> clusters){
        int counter = 0;
        for(int j = 0; j < dimension; j++){
            for(int k = 0; k < clusters.size(); k++){
                double entry = 0;
                for(int i = 0; i < clusters.get(k).memberVertices.size(); i++){
                    for(int l = 0; l < clusters.get(k).memberVertices.get(i).duplicateCount; l++){
                        entry += clusters.get(k).memberVertices.get(i).masterSimilarityVector.get(j);
                        counter++;
                    }
                }
                entry = entry / counter;
                counter = 0;
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
        boolean infinityA;
        boolean infinityB;

        for(int i = 0; i < a.size(); i++){
            infinityA = (a.get(i) == Double.MAX_VALUE);
            infinityB = (b.get(i) == Double.MAX_VALUE);

            if(infinityA && !infinityB){
                distance += Math.pow((b.get(i)), 2);
            } else if(!infinityA && infinityB){
                distance += Math.pow(a.get(i), 2);
            } else if(infinityA && infinityB){
                distance += 0;
            } else {
                distance += Math.pow((a.get(i) - b.get(i)), 2);
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

    public static double calculateWithinClusterVariation(int currentDimension, Cluster currentCluster){
        double variance = 0;
        for(int i = 0; i < currentCluster.memberVertices.size(); i++){
            double xij = currentCluster.memberVertices.get(i).masterSimilarityVector.get(currentDimension);
            double mj = currentCluster.masterSimilarityCentroid.get(currentDimension);
            variance += Math.pow((xij - mj), 2);
        }
        return variance;
    }

}
