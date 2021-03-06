package graph.clustering.algorithm;

import graph.clustering.GraphFactory;
import graph.clustering.algorithm.process.Category;
import graph.clustering.algorithm.process.Cluster;
import graph.clustering.algorithm.process.CoreFunctions;
import graph.clustering.algorithm.process.Initializer;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;
import graph.clustering.vertex.SearchKeyword;
import graph.clustering.vertex.Vertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by DWang on 2016/12/22.
 */

/**
 * ISODATA is a clustering algorithm based on the traditional K-Means clustering algorithm.
 * The major difference between ISODATA and K-Means is that during iteration, ISODATA adjusts k
 * by merging and splitting clusters considering several criteria.
 * This implementation of ISODATA contains some minor modification, for example, it uses K-Means++
 * initialization strategy to initialize clusters smartly.
 *
 * The steps of ISODATA algorithm:
 * 1. Initialize k clusters.
 * 2. Assign each point to it's closest cluster center.
 * 3. Remove cluster centers with fewer than the preset minimum points.
 * 4. Recentralize cluster centers. Go back to step 2 if any cluster was deleted.
 * 5. Calculate the average distance of the points to the associated cluster center.
 * and the overall average, for each cluster.
 * 6. If this is the last iteration, go to step 9.
 * 7. For each cluster Sj, compute a vector vj whose coordinates are the standard deviations of the
 * euclidean distances between the center of Sj and every point of Sj in each dimension.
 * 8. For each cluster, decide whether a split is needed by comparing the largest coordinate of vj
 * with the preset maximum permitted standard deviation (and by counting in the maximum permitted average
 * squared distance). If any splits occur, go to step 2.
 * 9. Compute the pairwise intercluster distances between all distinct pairs of cluster centers.
 * 10. Select pairs whose intercluster distance is smaller than the preset minimum permitted value.
 * Merge these pairs.
 * 11. If the number of iterations is less than the preset iteration number, go to step 2.
 */

public class ISODATAFactory {

    /**
     * Number of iteration
     */
    public static int MAX_ITERATION;

    /**
     * Minimum permitted number of points of a single cluster
     */
    public static int MIN_CLUSTER_SIZE;

    /**
     * Minimum permitted intercluster distance
     */
    public static double MIN_INTERCLUSTER_DISTANCE;

    /**
     * Maximum permitted standard deviation
     */
    public static double MAX_STANDARD_DEVIATION;

    /**
     * Maximum permitted number of pairs that can be merged at one iteration
     */
    public static int MAX_PAIR;

    /**
     * Maximum permitted average squared distance
     */
    public static double MAX_ASD;

    /**
     * performs the so called ISODATA clustering algorithm for the input list.
     * @param inputKeywords input keyword list
     * @param currentSearchKeyword current set of search results
     * @param keywordVertices all keywords from csv file
     * @param rootKeywordVertices all root keywords from csv file
     * @param maxIteration
     * @param minClusterSize
     * @param minInterclusterDistance
     * @param maxStandardDeviation
     * @param maxPair
     * @param maxASD
     * @param presetNumClus wished number of final clusters by user
     */
    public static void performISODATAClustering(ArrayList<KeywordVertex> inputKeywords,
                                                SearchKeyword currentSearchKeyword,
                                                ArrayList<KeywordVertex> keywordVertices,
                                                ArrayList<RootKeywordVertex> rootKeywordVertices,
                                                int maxIteration,
                                                int minClusterSize,
                                                double minInterclusterDistance,
                                                double maxStandardDeviation,
                                                int maxPair,
                                                double maxASD,
                                                int presetNumClus){

        int numClus = presetNumClus;
        setParameters(maxIteration, minClusterSize, minInterclusterDistance, maxStandardDeviation, maxPair, maxASD);
        GraphFactory.calculateSparseVector(inputKeywords);

        // step 1
        Initializer.categoriesBasedInitializer(inputKeywords, GraphFactory.keywordVertices, MAX_ITERATION, MIN_CLUSTER_SIZE, MAX_PAIR, MAX_STANDARD_DEVIATION, MIN_INTERCLUSTER_DISTANCE);
        if(CoreFunctions.categories.size() >= numClus){
            numClus = 1;
        }
        for(int i = 0; i < CoreFunctions.categories.size(); i++){
            Category currentCategory = CoreFunctions.categories.get(i);
            int iteration = 0;

            if(CoreFunctions.baSpecialTreatment(currentCategory, i)) continue;
            CoreFunctions.vectorSpaceDimension = currentCategory.categoryMembers.get(0).masterSimilarityVector.size();

            while(iteration < currentCategory.maxIter){
                for(int j = 0; j < currentCategory.clusters.size(); j++){
                    currentCategory.clusters.get(j).involvedInMerge = false;
                    currentCategory.clusters.get(j).interclusterDistance = new Vector<>();
                }

                do{
                    // step 2
                    CoreFunctions.performKMeans(currentCategory.maxIter, KMeansFactory.MAX_REALLOC_COUNT, i, rootKeywordVertices);

                    // step 3 + 4
                } while (CoreFunctions.clusterSuspended);

                // step 5
                assignDeltaDistance(currentCategory);
                iteration++;

                // step 6
                if(iteration == currentCategory.maxIter){
                    Vector<int[]> minClusterPair = CoreFunctions.calculateInterclusterDistances(currentCategory, numClus);
                    mergeClusterPairs(minClusterPair, currentCategory);
                    CoreFunctions.performKMeans(currentCategory.maxIter, KMeansFactory.MAX_REALLOC_COUNT, i, rootKeywordVertices);
                    continue;
                }

                // step 7
                calculateStandardDeviationVectors(currentCategory);

                // step 8
                if(splitCluster(currentCategory, numClus)) continue;

                // step 9
                Vector<int[]> minClusterPair = CoreFunctions.calculateInterclusterDistances(currentCategory, numClus);

                // step 10
                mergeClusterPairs(minClusterPair, currentCategory);
            }
            CoreFunctions.setGrandMaster(currentCategory, rootKeywordVertices);
            CoreFunctions.mergeSameClusters(currentCategory);
            CoreFunctions.systemOutPrint(i);

            // convert clusters to vertex array for later use (visualization).
            for(int z = 0; z < currentCategory.clusters.size(); z++){

                Vertex[] vertexArray = CoreFunctions.convertClusterToVertexArray(currentCategory.clusters.get(z));
                currentSearchKeyword.countOriginalMembers.add(currentCategory.clusters.get(z).memberVertices.size() + 1);
                currentSearchKeyword.clusters.add(vertexArray);

            }

            /*
            for(int z = 0; z < GraphFactory.keywordStrings.size(); z++){
                if(GraphFactory.keywordStrings.get(z).length > 2){
                    Sketch.renderWordle(GraphFactory.keywordStrings.get(z));

                    try {
                        System.in.read();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }*/
        }
    }

    /**
     * sets all parameter to input values
     * @param maxIteration
     * @param minClusterSize
     * @param minInterclusterDistance
     * @param maxStandardDeviation
     * @param maxPair
     * @param maxASD
     */
    private static void setParameters(int maxIteration,
                                      int minClusterSize,
                                      double minInterclusterDistance,
                                      double maxStandardDeviation,
                                      int maxPair,
                                      double maxASD){

        MAX_ITERATION = maxIteration;
        MIN_CLUSTER_SIZE = minClusterSize;
        MIN_INTERCLUSTER_DISTANCE = minInterclusterDistance;
        MAX_STANDARD_DEVIATION = maxStandardDeviation;
        MAX_PAIR = maxPair;
        MAX_ASD = maxASD;

    }

    /**
     * compares the given pair tuple with the pair tuple array. Returns true if the pair tuple exists.
     * @param minPairVector pair tuple array
     * @param minPair given pair tuple
     * @return true if pair tuple exists.
     */
    public static boolean pairExists(Vector<int[]> minPairVector, int[] minPair){
        for(int i = 0; i < minPairVector.size(); i++){
            int[] currentPair = minPairVector.get(i);
            if((((currentPair[0]==minPair[0]) && (currentPair[1]==minPair[1]))) || (((currentPair[1]==minPair[0]) && (currentPair[0]==minPair[1])))){
                return true;
            }
        }
        return false;
    }

    /**
     * performs the 8. step of ISODATA: For each cluster, it checks whether the standard deviation and the
     * average squared distance of the current cluster are exceeding the limit. If true, it splits that cluster
     * by passing it's member points to the K-Means++ method and deleting the original cluster.
     * @param currentCategory current keyword category
     * @param clusNum wished number of resulting clusters by user
     * @return true if any splits occur
     */
    private static boolean splitCluster(Category currentCategory,
                                        int clusNum){
        Cluster largestCluster = null;
        int maxClusterSize = 0;

        for(int i = 0; i < currentCategory.clusters.size(); i++){
            Cluster currentCluster = currentCategory.clusters.get(i);
            if (currentCluster.maxStandardDeviation > currentCategory.stdv){
                //if((currentCluster.averageEuclideanDistance > currentCategory.overallAverageEuclideanDistance && currentCluster.memberVertices.size() > 2 * (currentCategory.samprm + 1)) || (currentCategory.clusters.size() <= 1 + currentCategory.numClus / 2)){
                    currentCategory.clusters.remove(currentCluster);
                    Initializer.kMeansPPInitializer(2, currentCluster.memberVertices, currentCategory.clusters);
                    return true;
                /*} else
                    if(currentCluster.averageEuclideanDistance > MAX_ASD){
                    currentCategory.clusters.remove(currentCluster);
                    Initializer.kMeansPPInitializer(2, currentCluster.memberVertices, currentCategory.clusters);
                    return true;
                }*/
            } else if(currentCluster.averageEuclideanDistance > MAX_ASD){
                currentCategory.clusters.remove(currentCluster);
                Initializer.kMeansPPInitializer(2, currentCluster.memberVertices, currentCategory.clusters);
                return true;
            }
            int numDuplicates = CoreFunctions.totalNumberOfDuplicates(currentCluster);
            if(maxClusterSize < numDuplicates){
                maxClusterSize = numDuplicates;
                largestCluster = currentCluster;
            }
        }
        if(currentCategory.categoryMembers.size() >= clusNum && currentCategory.clusters.size() < clusNum){
            if(largestCluster == null){
                System.out.println("Error: Not enough clusters created, but no largest cluster found.");
                return false;
            }
            currentCategory.clusters.remove(largestCluster);
            Initializer.kMeansPPInitializer(2, largestCluster.memberVertices, currentCategory.clusters);
            return true;
        }
        return false;
    }

    /**
     * computes the average squared distance for each cluster.
     * @param currentCategory current keyword category
     */
    public static void assignDeltaDistance(Category currentCategory){
        double overallAverage = 0;
        for(int j = 0; j < currentCategory.clusters.size(); j++){
            currentCategory.clusters.get(j).averageEuclideanDistance = CoreFunctions.calculateAverageEuclideanDistance(currentCategory.clusters.get(j));
            overallAverage += currentCategory.clusters.get(j).averageEuclideanDistance;
            currentCategory.clusters.get(j).isClosed = true;
        }
        currentCategory.overallAverageEuclideanDistance = overallAverage / currentCategory.clusters.size();
    }

    /**
     * suspends clusters whose member points are less than the preset limit.
     * @param currentCategory current keyword category
     * @return true if any clusters are suspended
     */
    public static boolean suspendSmallClusters(Category currentCategory){
        for(int i = 0; i < currentCategory.clusters.size(); i++){
            Cluster currentCluster = currentCategory.clusters.get(i);
            if(currentCluster.memberVertices.size() < currentCategory.samprm && currentCategory.categoryMemberCount > currentCategory.samprm){
                currentCategory.clusters.remove(currentCluster);
                return true;
            }
        }
        return false;
    }

    /**
     * merges the given pairs. Each cluster can only engage once in each iteration.
     * @param clusterPairs input pairs
     * @param currentCategory current keyword category
     */
    public static void mergeClusterPairs(Vector<int[]> clusterPairs, Category currentCategory){
        for(int i = 0; i < clusterPairs.size(); i++){
            Cluster old1 = currentCategory.clusters.get(clusterPairs.get(i)[0]);
            Cluster old2 = currentCategory.clusters.get(clusterPairs.get(i)[1]);
            if(!old1.involvedInMerge && !old2.involvedInMerge){
                Cluster mergedCluster = new Cluster();
                //mergedCluster.memberVertices.addAll(old1.memberVertices);
                //mergedCluster.memberVertices.addAll(old2.memberVertices);
                int memberCount1 = 0;
                int memberCount2 = 0;
                for(int k = 0; k < old1.memberVertices.size(); k++){
                    memberCount1 += old1.memberVertices.get(k).duplicateCount;
                }
                for(int k = 0; k < old2.memberVertices.size(); k++){
                    memberCount2 += old2.memberVertices.get(k).duplicateCount;
                }

                for(int j = 0; j < old1.masterSimilarityCentroid.size(); j++){
                    double mergedCenter = (memberCount1 * old1.masterSimilarityCentroid.get(j) + memberCount2 * old2.masterSimilarityCentroid.get(j)) * (1 / (memberCount1 + memberCount2));
                    mergedCluster.masterSimilarityCentroid.add(mergedCenter);
                }

                currentCategory.clusters.add(mergedCluster);
                old1.involvedInMerge = true;
                old2.involvedInMerge = true;
            }
        }
        for(int i = 0; i < currentCategory.clusters.size(); i++){
            if(currentCategory.clusters.get(i).involvedInMerge){
                currentCategory.clusters.remove(currentCategory.clusters.get(i));
                i--;
            }
        }
    }

    /**
     * computes the standard deviation vector of each cluster.
     * @param category current keyword category
     */
    public static void calculateStandardDeviationVectors(Category category){
        double maxStdv = 0;
        for(int i = 0; i < category.clusters.size(); i++){
            for(int j = 0; j < CoreFunctions.vectorSpaceDimension; j++){
                double stdv = calculateStandardDeviation(j, category.clusters.get(i));
                maxStdv = (maxStdv > stdv) ? maxStdv : stdv;
                category.clusters.get(i).standardDeviationVector.add(stdv);
            }
            category.clusters.get(i).maxStandardDeviation = maxStdv;
        }
    }


    public static double calculateStandardDeviation(int currentDimension, Cluster currentCluster){
        double stdv = 0;
        for(int i = 0; i < currentCluster.memberVertices.size(); i++){
            double xij = currentCluster.memberVertices.get(i).masterSimilarityVector.get(currentDimension);
            double mj = currentCluster.masterSimilarityCentroid.get(currentDimension);
            if(xij == Double.MAX_VALUE){
                stdv += Math.pow((0.0 - mj), 2);
            } else{
                stdv += Math.pow((xij - mj), 2);
            }
        }
        return Math.sqrt(stdv / currentCluster.memberVertices.size());
    }
}
