package graph.clustering.algorithm;

import graph.clustering.GraphFactory;
import graph.clustering.algorithm.processing.Category;
import graph.clustering.algorithm.processing.Cluster;
import graph.clustering.algorithm.processing.CoreFunctions;
import graph.clustering.algorithm.processing.Initializer;
import graph.clustering.vertex.KeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by DWang on 2016/12/22.
 */
public class ISOCLUSFactory {
    public static final int MAX_ITERATION = 1000;
    public static final int MIN_CLUSTER_SIZE = 1;
    public static final double MIN_INTERCLUSTER_DISTANCE = 0.35;
    public static final double MAX_STANDARD_DEVIATION = 0.2;
    public static final int MAX_PAIR = 3;

    public static void performISOCLUSClustering(ArrayList<KeywordVertex> inputKeywords){

        Initializer.categoriesBasedInitializer(inputKeywords, GraphFactory.keywordVertices, MAX_ITERATION, MIN_CLUSTER_SIZE, MAX_PAIR, MAX_STANDARD_DEVIATION, MIN_INTERCLUSTER_DISTANCE);

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
                    CoreFunctions.performKMeans(currentCategory.maxIter, KMeansFactory.MAX_REALLOC_COUNT, i);
                } while (CoreFunctions.clusterSuspended);
                assignDeltaDistance(currentCategory);
                iteration++;

                if(iteration == currentCategory.maxIter){
                    Vector<int[]> minClusterPair = CoreFunctions.calculateInterclusterDistances(currentCategory);
                    mergeClusterPairs(minClusterPair, currentCategory);
                    continue;
                }

                calculateStandardDeviationVectors(currentCategory);
                if(splitClusterISOCLUS(currentCategory)) continue;

                // TODO step 9
                Vector<int[]> minClusterPair = CoreFunctions.calculateInterclusterDistances(currentCategory);
                mergeClusterPairs(minClusterPair, currentCategory);
            }
            CoreFunctions.setGrandMaster(currentCategory);
            CoreFunctions.mergeSameClusters(currentCategory);
            CoreFunctions.systemOutPrint(i);
        }

    }

    public static boolean pairExists(Vector<int[]> minPairVector, int[] minPair){
        for(int i = 0; i < minPairVector.size(); i++){
            int[] currentPair = minPairVector.get(i);
            if((currentPair[0]==minPair[0] && currentPair[1]==minPair[1]) || (currentPair[1]==minPair[0] && currentPair[0]==minPair[1])){
                return true;
            }
        }
        return false;
    }

    public static boolean splitClusterISOCLUS(Category currentCategory){
        for(int i = 0; i < currentCategory.clusters.size(); i++){
            Cluster currentCluster = currentCategory.clusters.get(i);
            if (currentCluster.maxStandardDeviation > currentCategory.stdv){
                if((currentCluster.averageEuclideanDistance > currentCategory.overallAverageEuclideanDistance && currentCluster.memberVertices.size() > 2 * (currentCategory.samprm + 1)) || (currentCategory.clusters.size() <= 1 + currentCategory.numClus / 2)){
                    currentCategory.clusters.remove(currentCluster);
                    Initializer.kMeansPPInitializer(2, currentCluster.memberVertices, currentCategory.clusters);
                    return true;
                }
            }
        }
        return false;
    }

    public static void assignDeltaDistance(Category currentCategory){
        double overallAverage = 0;
        for(int j = 0; j < currentCategory.clusters.size(); j++){
            currentCategory.clusters.get(j).averageEuclideanDistance = CoreFunctions.calculateAverageEuclideanDistance(currentCategory.clusters.get(j));
            overallAverage += currentCategory.clusters.get(j).averageEuclideanDistance;
            currentCategory.clusters.get(j).isClosed = true;
        }
        currentCategory.overallAverageEuclideanDistance = overallAverage / currentCategory.clusters.size();
    }

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

    public static void mergeClusterPairs(Vector<int[]> clusterPairs, Category currentCategory){
        for(int i = 0; i < clusterPairs.size(); i++){
            Cluster old1 = currentCategory.clusters.get(clusterPairs.get(i)[0]);
            Cluster old2 = currentCategory.clusters.get(clusterPairs.get(i)[1]);
            if(!old1.involvedInMerge && !old2.involvedInMerge){
                Cluster mergedCluster = new Cluster();
                mergedCluster.memberVertices.addAll(old1.memberVertices);
                mergedCluster.memberVertices.addAll(old2.memberVertices);
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
        int size = currentCategory.clusters.size();
        for(int i = 0; i < size; i++){
            if(currentCategory.clusters.get(i).involvedInMerge){
                currentCategory.clusters.remove(currentCategory.clusters.get(i));
                i--;
                size--;
            }
        }
    }

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
            stdv += Math.pow((xij - mj), 2);
        }
        return Math.sqrt(stdv / currentCluster.memberVertices.size());
    }
}
