package graph.clustering.algorithm;

import graph.clustering.GraphFactory;
import graph.clustering.Utility;
import graph.clustering.algorithm.processing.Category;
import graph.clustering.algorithm.processing.Cluster;
import graph.clustering.algorithm.processing.CoreFunctions;
import graph.clustering.algorithm.processing.Initializer;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by DWang on 2016/12/22.
 */
public class KMeansFactory {
    public static final int MAX_ITERATION = 10000;
    public static final double MAX_ERROR = 0.5;
    public static final int MAX_REALLOC_COUNT = 0;
    public static final int MAX_MEMBER_COUNT = 15;
    public static final int MIN_MEMBER_COUNT = 2;

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords){

        Initializer.categoriesBasedInitializer(inputKeywords, GraphFactory.keywordVertices);

        for(int i = 0; i < CoreFunctions.categories.size(); i++){

            if(CoreFunctions.categories.get(i).categoryIndex == 1) { // Alle Keywords der Kategorie Ba liegen in der 1. Ebene und haben keinerlei Querverbindungen miteinander => Kein Vektor kann erstellt werden.
                Cluster cluster = new Cluster();
                for(int j = 0; j < CoreFunctions.categories.get(i).categoryMembers.size(); j++){
                    cluster.memberVertices.add(CoreFunctions.categories.get(i).categoryMembers.get(j));
                }
                CoreFunctions.categories.get(i).clusters.add(cluster);
                //categories.get(i).clusters.get(0).averageEuclideanDistance = calculateAverageSquareDistance(vectorSpaceDimension, categories.get(i).clusters.get(0));

                CoreFunctions.flushEmptyClusters(CoreFunctions.categories.get(i));
                if(CoreFunctions.categories.get(i).clusters.size() == 0) continue;
                CoreFunctions.systemOutPrint(i);
                continue;
            }

            CoreFunctions.vectorSpaceDimension = CoreFunctions.categories.get(i).categoryMembers.get(0).masterSimilarityVector.size();
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
                CoreFunctions.performKMeans(MAX_ITERATION, MAX_REALLOC_COUNT, i);
                ISODATAFactory.assignDeltaDistance(CoreFunctions.categories.get(i));
                } while (splitCluster(CoreFunctions.categories.get(i)));

            // Print
            CoreFunctions.flushEmptyClusters(CoreFunctions.categories.get(i));
            if(CoreFunctions.categories.get(i).clusters.size() == 0) continue;
            CoreFunctions.mergeSameClusters(CoreFunctions.categories.get(i));
            CoreFunctions.systemOutPrint(i);
        }

        CoreFunctions.dropRate = (double) CoreFunctions.abandonedKeywords / (double) CoreFunctions.searchExampleCount;
        DecimalFormat f = new DecimalFormat("#0.00");
        System.out.println("DropRate " + f.format(CoreFunctions.dropRate * 100) + "%");
        System.out.println();
    }

    public static boolean splitCluster(Category category){
        CoreFunctions.eliminateOutstanders(category);
        for(int i = 0; i < category.clusters.size(); i++){
            Cluster currentCluster = category.clusters.get(i);
            if(currentCluster.memberVertices.size() > MAX_MEMBER_COUNT && currentCluster.averageEuclideanDistance > 0.0){   // Cluster too big
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                Initializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            } else if(currentCluster.averageEuclideanDistance > MAX_ERROR){                                   // Members' divergence too high
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                Initializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            } else if(Utility.findIndexForName(currentCluster.grandMaster.name) != -1 && currentCluster.averageEuclideanDistance > 0.0){  // Grandmaster should better not be a root keyword
                category.clusters.remove(currentCluster);
                category.categoryMembers = currentCluster.memberVertices;
                Initializer.kMeansPPInitializer(2, currentCluster.memberVertices, category.clusters);
                return true;
            }
        }
        return false;
    }

    public static void performSquareErrorClustering(ArrayList<KeywordVertex> inputKeywords,
                                                    ArrayList<RootKeywordVertex> rootKeywordVertices){

        CoreFunctions.clusters = new ArrayList<>();
        int iteration = 0;
        int reallocCount = Integer.MAX_VALUE;

        // Initialize k clusters, k currently equals the number of categories.
        Initializer.categoriesBasedInitializer(rootKeywordVertices);
        CoreFunctions.vectorSpaceDimension = rootKeywordVertices.size();

        //Initializer.kMeansPPInitializer(10, inputKeywords, clusters);

        // Assign vertices to nearest cluster the first time.
        for(int i = 0; i < inputKeywords.size(); i++){
            Cluster nearestCluster = CoreFunctions.nearestCentroid(inputKeywords.get(i).categorySimilarityVector, CoreFunctions.clusters);
            nearestCluster.memberVertices.add(inputKeywords.get(i));
            inputKeywords.get(i).originCluster = nearestCluster;
        }

        // Calculate new position of the centroids of the existing clusters and reallocate vertices iteratively.
        while(iteration < MAX_ITERATION && reallocCount > MAX_REALLOC_COUNT){
            reallocCount = 0;
            CoreFunctions.recentralizeCentroids(CoreFunctions.vectorSpaceDimension, CoreFunctions.clusters);
            for(int i = 0; i < inputKeywords.size(); i++){
                Cluster nearestCluster = CoreFunctions.nearestCentroid(inputKeywords.get(i).categorySimilarityVector, CoreFunctions.clusters);
                if(!nearestCluster.memberVertices.contains(inputKeywords.get(i))){
                    nearestCluster.memberVertices.add(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster.memberVertices.remove(inputKeywords.get(i));
                    inputKeywords.get(i).originCluster = nearestCluster;
                    reallocCount++;
                }
            }
            iteration++;
        }

        for(int i = 0; i < CoreFunctions.clusters.size(); i++){
            //clusters.get(i).averageEuclideanDistance = calculateAverageSquareDistance(vectorSpaceDimension, clusters.get(i));
            CoreFunctions.clusters.get(i).averageEuclideanDistance = CoreFunctions.calculateAverageEuclideanDistance(CoreFunctions.clusters.get(i));
        }
        CoreFunctions.squareError = CoreFunctions.calculateSquareErrorFORGYStyle(CoreFunctions.vectorSpaceDimension);


        // Print
        for(int k = 0; k < CoreFunctions.clusters.size(); k++){
            System.out.println(Integer.toString(k) + ". cluster, ASD " + Double.toString(CoreFunctions.clusters.get(k).averageEuclideanDistance));
            for(int i = 0; i < CoreFunctions.clusters.get(k).memberVertices.size(); i++){
                System.out.println(CoreFunctions.clusters.get(k).memberVertices.get(i).name);
            }
            System.out.println();
        }

        System.out.println("FORGY Square Error: " + Double.toString(CoreFunctions.squareError));
        System.out.println("Iterations: " + Integer.toString(iteration));
    }
}
