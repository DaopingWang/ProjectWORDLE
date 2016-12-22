package graph.clustering.kmeans;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.GraphFactory;
import graph.clustering.Utility;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusterFactory {
    public static ArrayList<Cluster> clusters;
    public static ArrayList<Category> categories;

    public static int vectorSpaceDimension;
    public static double squareError;
    public static int abandonedKeywords;
    public static int searchExampleCount;
    public static double dropRate;

    public static final int MAX_ITERATION = 10000;
    public static final double MAX_ERROR = 0.5;
    public static final int MAX_REALLOC_COUNT = 0;
    public static final int MAX_MEMBER_COUNT = 15;
    public static final int MIN_MEMBER_COUNT = 2;

    public static boolean isoclusMode;
    public static boolean clusterSuspended;

    public static void performISOCLUSClustering(ArrayList<KeywordVertex> inputKeywords){
        isoclusMode = true;
        categories = new ArrayList<>(GraphFactory.rootKeywordVertices.size());
        abandonedKeywords = 0;
        searchExampleCount = inputKeywords.size();
        dropRate = 0.0;

        ClusteringInitializer.categoriesBasedInitializer(inputKeywords, categories, GraphFactory.keywordVertices);

        for(int i = 0; i < categories.size(); i++){
            Category currentCategory = categories.get(i);
            int iteration = 0;

            if(baSpecialTreatment(currentCategory, i)) continue;
            vectorSpaceDimension = currentCategory.categoryMembers.get(0).masterSimilarityVector.size();

            while(iteration < currentCategory.maxIter){
                for(int j = 0; j < currentCategory.clusters.size(); j++){
                    currentCategory.clusters.get(j).involvedInMerge = false;
                    currentCategory.clusters.get(j).interclusterDistance = new Vector<>();
                }

                do{
                    performKMeans(currentCategory.maxIter, MAX_REALLOC_COUNT, i);
                } while (clusterSuspended);
                assignDeltaDistance(currentCategory);
                iteration++;

                if(iteration == currentCategory.maxIter){
                    Vector<int[]> minClusterPair = calculateInterclusterDistances(currentCategory);
                    mergeClusterPairs(minClusterPair, currentCategory);
                    continue;
                }

                calculateStandardDeviationVectors(currentCategory);
                if(splitClusterISOCLUS(currentCategory)) continue;

                // TODO step 9
                Vector<int[]> minClusterPair = calculateInterclusterDistances(currentCategory);
                mergeClusterPairs(minClusterPair, currentCategory);
            }
            setGrandMaster(currentCategory);
            mergeSameClusters(currentCategory);
            systemOutPrint(i);
        }

    }

    private static void mergeClusterPairs(Vector<int[]> clusterPairs, Category currentCategory){
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

    private static Vector<int[]> calculateInterclusterDistances(Category currentCategory){
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
                if ((currentCategory.lump > currentDistance) && (mergeCount < currentCategory.maxpair) && (!pairExists(minPairVector, pair))){
                    minPairVector.add(pair);
                    mergeCount++;
                }
            }
        }
        return minPairVector;
    }

    private static boolean pairExists(Vector<int[]> minPairVector, int[] minPair){
        for(int i = 0; i < minPairVector.size(); i++){
            int[] currentPair = minPairVector.get(i);
            if((currentPair[0]==minPair[0] && currentPair[1]==minPair[1]) || (currentPair[1]==minPair[0] && currentPair[0]==minPair[1])){
                return true;
            }
        }
        return false;
    }

    private static boolean splitClusterISOCLUS(Category currentCategory){
        for(int i = 0; i < currentCategory.clusters.size(); i++){
            Cluster currentCluster = currentCategory.clusters.get(i);
            if (currentCluster.maxStandardDeviation > currentCategory.stdv){
                if((currentCluster.averageEuclideanDistance > currentCategory.overallAverageEuclideanDistance && currentCluster.memberVertices.size() > 2 * (currentCategory.samprm + 1)) || (currentCategory.clusters.size() <= 1 + currentCategory.numClus / 2)){
                    currentCategory.clusters.remove(currentCluster);
                    ClusteringInitializer.kMeansPPInitializer(2, currentCluster.memberVertices, currentCategory.clusters);
                    return true;
                }
            }
        }
        return false;
    }

    private static void calculateStandardDeviationVectors(Category category){
        double maxStdv = 0;
        for(int i = 0; i < category.clusters.size(); i++){
            for(int j = 0; j < vectorSpaceDimension; j++){
                double stdv = calculateStandardDeviation(j, category.clusters.get(i));
                maxStdv = (maxStdv > stdv) ? maxStdv : stdv;
                category.clusters.get(i).standardDeviationVector.add(stdv);
            }
            category.clusters.get(i).maxStandardDeviation = maxStdv;
        }
    }

    private static boolean baSpecialTreatment(Category currentCategory, int index){
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

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords){
        isoclusMode = false;
        categories = new ArrayList<>(GraphFactory.rootKeywordVertices.size());
        abandonedKeywords = 0;
        searchExampleCount = inputKeywords.size();
        dropRate = 0.0;

        ClusteringInitializer.categoriesBasedInitializer(inputKeywords, categories, GraphFactory.keywordVertices);

        for(int i = 0; i < categories.size(); i++){

            if(categories.get(i).categoryIndex == 1) { // Alle Keywords der Kategorie Ba liegen in der 1. Ebene und haben keinerlei Querverbindungen miteinander => Kein Vektor kann erstellt werden.
                Cluster cluster = new Cluster();
                for(int j = 0; j < categories.get(i).categoryMembers.size(); j++){
                    cluster.memberVertices.add(categories.get(i).categoryMembers.get(j));
                }
                categories.get(i).clusters.add(cluster);
                //categories.get(i).clusters.get(0).averageEuclideanDistance = calculateAverageSquareDistance(vectorSpaceDimension, categories.get(i).clusters.get(0));

                flushEmptyClusters(categories.get(i));
                if(categories.get(i).clusters.size() == 0) continue;
                systemOutPrint(i);
                continue;
            }

            vectorSpaceDimension = categories.get(i).categoryMembers.get(0).masterSimilarityVector.size();
/*
            if(categories.get(i).categoryMembers.size() < 6) {
                Cluster cluster = new Cluster();
                for(int j = 0; j < categories.get(i).categoryMembers.size(); j++){
                    cluster.memberVertices.add(categories.get(i).categoryMembers.get(j));
                }
                categories.get(i).clusters.add(cluster);
                categories.get(i).clusters.get(0).averageEuclideanDistance = calculateAverageEuclideanDistance(categories.get(i).clusters.get(0));

                flushEmptyClusters(categories.get(i));
                setGrandMaster(categories.get(i));
                systemOutPrint(i);
                continue;
            }
*/
            do {
                performKMeans(MAX_ITERATION, MAX_REALLOC_COUNT, i);
                assignDeltaDistance(categories.get(i));
                } while (splitCluster(categories.get(i)));

            // Print
            flushEmptyClusters(categories.get(i));
            if(categories.get(i).clusters.size() == 0) continue;
            mergeSameClusters(categories.get(i));
            systemOutPrint(i);
        }

        dropRate = (double) abandonedKeywords / (double) searchExampleCount;
        DecimalFormat f = new DecimalFormat("#0.00");
        System.out.println("DropRate " + f.format(dropRate * 100) + "%");
        System.out.println();
    }

    private static void performKMeans(int maxIteration, int maxRealloc, int i){
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
            clusterSuspended = suspendSmallClusters(categories.get(i));
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
        setGrandMaster(categories.get(i));

        /*
        double overallAverage = 0;
        for(int j = 0; j < currentCategoryClusters.size(); j++){
            currentCategoryClusters.get(j).averageEuclideanDistance = calculateAverageEuclideanDistance(currentCategoryClusters.get(j));
            overallAverage += currentCategoryClusters.get(j).averageEuclideanDistance;
            categories.get(i).clusters.get(j).isClosed = true;
        }
        overallAverage = overallAverage / currentCategoryClusters.size();*/
    }

    private static void assignDeltaDistance(Category currentCategory){
        double overallAverage = 0;
        for(int j = 0; j < currentCategory.clusters.size(); j++){
            currentCategory.clusters.get(j).averageEuclideanDistance = calculateAverageEuclideanDistance(currentCategory.clusters.get(j));
            overallAverage += currentCategory.clusters.get(j).averageEuclideanDistance;
            currentCategory.clusters.get(j).isClosed = true;
        }
        currentCategory.overallAverageEuclideanDistance = overallAverage / currentCategory.clusters.size();
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
        //int categoryMemberCount = Utility.categoryMemberCounter(category);
        for(int i = 0; i < clusterCount; i++){
            Cluster currentCluster = category.clusters.get(i);
            if(currentCluster.memberVertices.size() < MIN_MEMBER_COUNT && Utility.clusterMemberCounter(currentCluster) < searchExampleCount / 4){
                category.clusters.remove(currentCluster);
                //searchExampleCount -= Utility.clusterMemberCounter(currentCluster);
                i--;
                clusterCount--;
                abandonedKeywords++;
            }
        }
    }

    private static boolean suspendSmallClusters(Category currentCategory){
        for(int i = 0; i < currentCategory.clusters.size(); i++){
            Cluster currentCluster = currentCategory.clusters.get(i);
            if(currentCluster.memberVertices.size() < currentCategory.samprm && currentCategory.categoryMemberCount > currentCategory.samprm){
                currentCategory.clusters.remove(currentCluster);
                return true;
            }
        }
        return false;
    }

    private static void flushEmptyClusters(Category category){
        for(int i = 0; i < category.clusters.size(); i++){
            if(category.clusters.get(i).memberVertices.size() == 0){
                category.clusters.remove(category.clusters.get(i));
                i--;
            }
        }
    }

    private static void mergeSameClusters(Category category){
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

    private static boolean splitCluster(Category category){
        eliminateOutstanders(category);
        for(int i = 0; i < category.clusters.size(); i++){
            Cluster currentCluster = category.clusters.get(i);
            if(currentCluster.memberVertices.size() > MAX_MEMBER_COUNT && currentCluster.averageEuclideanDistance > 0.0){   // Cluster too big
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                ClusteringInitializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            } else if(currentCluster.averageEuclideanDistance > MAX_ERROR){                                   // Members' divergence too high
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                ClusteringInitializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            } else if(Utility.findIndexForName(currentCluster.grandMaster.name) != -1 && currentCluster.averageEuclideanDistance > 0.0){  // Grandmaster should better not be a root keyword
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                ClusteringInitializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            }
        }
        return false;
    }

    private static void systemOutPrint(int index){
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

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords,
                                                    ArrayList<RootKeywordVertex> rootKeywordVertices){

        clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters, k currently equals the number of categories.
        ClusteringInitializer.categoriesBasedInitializer(rootKeywordVertices);
        vectorSpaceDimension = rootKeywordVertices.size();

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
            recentralizeCentroids(vectorSpaceDimension, clusters);
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

    public static void performSquareErrorClusteringPP(ArrayList<KeywordVertex> inputKeywords,
                                                      ArrayList<RootKeywordVertex> rootKeywordVertices){

        clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters using K-Means++
        ClusteringInitializer.kMeansPPInitializer(10, inputKeywords, clusters);
        vectorSpaceDimension = inputKeywords.get(0).pathLengthVector.size();

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = nearestCentroid(inputKeywords.get(i).pathLengthVector, clusters);
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < MAX_ITERATION && reallocCount > MAX_REALLOC_COUNT){
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

    private static double calculateAverageSquareDistance(int dimension, Cluster currentCluster){
        int memberCount = currentCluster.memberVertices.size();
        double averageSquareDistance = 0;
        for(int j = 0; j < dimension; j++){
            averageSquareDistance += calculateWithinClusterVariation(j, currentCluster) / (double) memberCount;
        }
        return Math.sqrt(averageSquareDistance / dimension);
    }

    private static double calculateAverageEuclideanDistance(Cluster k){
        double averageEuclideanDistance = 0;
        for(int i = 0; i < k.memberVertices.size(); i++){
            averageEuclideanDistance += euclideanDistance(k.memberVertices.get(i).masterSimilarityVector, k.masterSimilarityCentroid);
        }
        return averageEuclideanDistance / k.memberVertices.size();
    }

    private static void recentralizeCentroids(int dimension, ArrayList<Cluster> clusters){
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
