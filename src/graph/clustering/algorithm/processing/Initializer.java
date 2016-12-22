package graph.clustering.algorithm.processing;

import graph.clustering.GraphFactory;
import graph.clustering.Utility;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

import static graph.clustering.algorithm.processing.CoreFunctions.categories;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class Initializer {

    public static void categoriesBasedInitializer(ArrayList<RootKeywordVertex> rootKeywordVertices){
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            Cluster k = new Cluster(rootKeywordVertices.get(i).categorySimilarityVector);
            CoreFunctions.clusters.add(k);
        }
    }

    public static void categoriesBasedInitializer(ArrayList<KeywordVertex> inputKeywords,
                                                  ArrayList<KeywordVertex> keywordVertices,
                                                  int maxIter,
                                                  int samprm,
                                                  int maxpair,
                                                  double stdv,
                                                  double lump){

        CoreFunctions.isoclusMode = true;
        CoreFunctions.categories = new ArrayList<>(GraphFactory.rootKeywordVertices.size());
        CoreFunctions.abandonedKeywords = 0;
        CoreFunctions.searchExampleCount = inputKeywords.size();
        CoreFunctions.dropRate = 0.0;

        checkDuplicates(inputKeywords);

        ArrayList createdCategories = new ArrayList();
        for(int i = 0; i < inputKeywords.size(); i++){
            int dominantCategory = inputKeywords.get(i).dominantCategory;
            if(!createdCategories.contains(dominantCategory)){
                Category category = new Category(dominantCategory);
                category.addMember(inputKeywords.get(i));
                category.maxIter = maxIter;
                category.samprm = samprm;
                category.lump = lump;
                category.stdv = stdv;
                category.maxpair = maxpair;
                categories.add(category);
                createdCategories.add(dominantCategory);
            } else {
                categories.get(Utility.findIndexForCategoryIndex(categories, dominantCategory)).addMember(inputKeywords.get(i));
            }
        }

        for(int i = 0; i < categories.size(); i++){
            for(int j = 0; j < keywordVertices.size(); j++){
                //if((keywordVertices.get(j).dominantCategory == inputCategories.get(i).categoryIndex) && (keywordVertices.get(j).layer <= inputCategories.get(i).maxLayer)){
                if(checkMasterQualification(keywordVertices.get(j), categories.get(i).categoryMembers, categories.get(i))){
                    categories.get(i).masterVertices.add(keywordVertices.get(j)); // Add vertex to master vector of category.
                    for(int k = 0; k < categories.get(i).categoryMembers.size(); k++){
                        categories.get(i).categoryMembers.get(k).masterSimilarityVector.add(categories.get(i).categoryMembers.get(k).pathLengthVector.get(j));
                    }
                }
            }

            // Add root dependency to the last entry to ensure everyone has a mastersimivector
            categories.get(i).masterVertices.add(GraphFactory.rootKeywordVertices.get(categories.get(i).categoryIndex));
            for(int j = 0; j < categories.get(i).categoryMembers.size(); j++){
                categories.get(i).categoryMembers.get(j).masterSimilarityVector.add((double) 1);
            }

            categories.get(i).numClus = Math.max(categories.get(i).categoryMembers.size() / 10, 1);
            kMeansPPInitializer(categories.get(i).numClus, categories.get(i).categoryMembers, categories.get(i).clusters);

        }
    }

    public static void categoriesBasedInitializer(ArrayList<KeywordVertex> inputKeywords,
                                                  ArrayList<KeywordVertex> keywordVertices){

        CoreFunctions.isoclusMode = false;
        categories = new ArrayList<>(GraphFactory.rootKeywordVertices.size());
        CoreFunctions.abandonedKeywords = 0;
        CoreFunctions.searchExampleCount = inputKeywords.size();
        CoreFunctions.dropRate = 0.0;

        checkDuplicates(inputKeywords);

        ArrayList createdCategories = new ArrayList();
        for(int i = 0; i < inputKeywords.size(); i++){
            int dominantCategory = inputKeywords.get(i).dominantCategory;
            if(!createdCategories.contains(dominantCategory)){
                Category category = new Category(dominantCategory);
                category.addMember(inputKeywords.get(i));
                categories.add(category);
                createdCategories.add(dominantCategory);
            } else {
                categories.get(Utility.findIndexForCategoryIndex(categories, dominantCategory)).addMember(inputKeywords.get(i));
            }
        }

        for(int i = 0; i < categories.size(); i++){
            for(int j = 0; j < keywordVertices.size(); j++){
                //if((keywordVertices.get(j).dominantCategory == categories.get(i).categoryIndex) && (keywordVertices.get(j).layer <= categories.get(i).maxLayer)){
                if(checkMasterQualification(keywordVertices.get(j), categories.get(i).categoryMembers, categories.get(i))){
                    categories.get(i).masterVertices.add(keywordVertices.get(j)); // Add vertex to master vector of category.
                    for(int k = 0; k < categories.get(i).categoryMembers.size(); k++){
                        categories.get(i).categoryMembers.get(k).masterSimilarityVector.add(categories.get(i).categoryMembers.get(k).pathLengthVector.get(j));
                    }
                }
            }

            // Add root dependency to the last entry to ensure everyone has a mastersimivector
            categories.get(i).masterVertices.add(GraphFactory.rootKeywordVertices.get(categories.get(i).categoryIndex));
            for(int j = 0; j < categories.get(i).categoryMembers.size(); j++){
                categories.get(i).categoryMembers.get(j).masterSimilarityVector.add((double) 1);
            }

            categories.get(i).numClus = Math.max(categories.get(i).categoryMembers.size() / 10, 1);
            kMeansPPInitializer(categories.get(i).numClus, categories.get(i).categoryMembers, categories.get(i).clusters);

        }
    }

    private static boolean checkMasterQualification(KeywordVertex potentialMaster,
                                                    ArrayList<KeywordVertex> memberList,
                                                    Category category){

        for(int i = 0; i < potentialMaster.subordinateList.size(); i++){
            for(int j = 0; j < memberList.size(); j++){
                if(potentialMaster.subordinateList.get(i).equals(memberList.get(j).name) && potentialMaster.dominantCategory == category.categoryIndex){
                    //if(potentialMaster.layer <= category.maxLayer){
                        return true;
                    //}
                }
            }
        }
        return false;
    }

    private static void checkDuplicates(ArrayList<KeywordVertex> inputKeywords){
        for(int i = 0; i < inputKeywords.size(); i++){
            for(int j = i+1; j < inputKeywords.size(); j++){
                if(inputKeywords.get(i).name.equals(inputKeywords.get(j).name)){
                    inputKeywords.get(i).duplicateCount++;
                    inputKeywords.remove(inputKeywords.get(j));
                    j--;
                }
            }
        }
    }

    /**
     * using K-Means++ algorithm.
     * @param k Wished number of clusters.
     */
    public static void kMeansPPInitializer(int k,
                                           ArrayList<KeywordVertex> inputVertices,
                                           ArrayList<Cluster> clusters){

        int inputVerticesCount = inputVertices.size();
        int createdCentroid = 1;

        // Firstly initialize the first categoryBasedCentroid randomly.
        Cluster first = new Cluster();
        first.masterSimilarityCentroid = (Vector<Double>) inputVertices.get(0).masterSimilarityVector.clone();
        clusters.add(first);

        while(createdCentroid < k){
            double maxProbability = -1;
            Vector<Double> farestVector = null;
            KeywordVertex farestKeyword = null;
            double distanceSum = 0;

            for(int i = 0; i < inputVerticesCount; i++){
                inputVertices.get(i).shortestDistance = Math.pow(shortestDistanceToClosestCentroid(inputVertices.get(i).masterSimilarityVector, clusters), 2);
                distanceSum += inputVertices.get(i).shortestDistance;
            }
            for(int i = 0; i < inputVerticesCount; i++){
                double probability = inputVertices.get(i).shortestDistance / distanceSum;
                if(maxProbability < probability){
                    maxProbability = probability;
                    farestVector = (Vector<Double>) inputVertices.get(i).masterSimilarityVector.clone();
                    farestKeyword = inputVertices.get(i);
                }
            }
            Cluster next = new Cluster();
            if(farestVector == null){ // bad way to prevent null mastersimicentroid
                next.masterSimilarityCentroid = clusters.get(clusters.size() - 1).masterSimilarityCentroid;
            } else {
                next.masterSimilarityCentroid = farestVector;
                //next.memberVertices.add(farestKeyword);
            }
            clusters.add(next);
            createdCentroid++;
        }
    }

    private static double shortestDistanceToClosestCentroid(Vector<Double> inputVertex,
                                                            ArrayList<Cluster> clusters){

        double shortestDistance = Double.MAX_VALUE;
        for(int i = 0; i < clusters.size(); i++){
            if(clusters.get(i).isClosed) continue;
            double distance = CoreFunctions.euclideanDistance(inputVertex, clusters.get(i).masterSimilarityCentroid);
            if(shortestDistance > distance){
                shortestDistance = distance;
            }
        }
        return shortestDistance;
    }

}
