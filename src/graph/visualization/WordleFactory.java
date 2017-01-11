package graph.visualization;

import graph.clustering.Utility;
import graph.clustering.vertex.RootKeywordVertex;
import graph.clustering.vertex.Vertex;
import processing.core.PApplet;
import processing.core.PConstants;
import wordcram.Word;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 03.01.2017.
 */
public class WordleFactory {
    public static float SKETCH_X = 1300;
    public static float SKETCH_Y = 700;

    public static void renderWordle(ArrayList<Vertex[]> list,
                                    ArrayList originalMembers,
                                    String title,
                                    float width,
                                    float height,
                                    int renderLimit,
                                    ArrayList<RootKeywordVertex> rootKeywordVertices){

        SKETCH_X = width;
        SKETCH_Y = height;
        ArrayList<String> categoryList = new ArrayList<>();
        ArrayList<String> newCategoryList = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            categoryList.add(rootKeywordVertices.get(list.get(i)[0].dominantCategory).name);
        }

        Sketch sketch = new Sketch(SKETCH_X, SKETCH_Y);
        ArrayList<Word[]> words = convertKeywordListToWords(list, categoryList, newCategoryList, originalMembers, sketch, renderLimit);
        sketch.setClusters(words);
        sketch.setSearchKeyword(title);
        sketch.setCategoryList(newCategoryList);
        sketch.setTotalNumberClusters(list.size());

        PApplet.runSketch(new String[]{"graph.visualization.Sketch"}, sketch);
    }

    private static ArrayList<Vertex[]> bestKClusters(ArrayList<Vertex[]> list,
                                                     ArrayList<String> categoryList,
                                                     ArrayList<String> newCategoryList,
                                                     int renderLimit,
                                                     ArrayList originalMembers,
                                                     ArrayList newOriginalMembers){

        if((list.size() <= renderLimit) || (renderLimit == -1)) {
            newOriginalMembers.addAll(originalMembers);
            newCategoryList.addAll(categoryList);
            return list;
        }

        ArrayList<Vertex[]> bestKClusters = new ArrayList<>();
        int addedCount = 0;

        do{
            int maxDuplicatesCount = 0;
            Vertex[] bestCluster = null;
            int index = -1;
            for(int i = 0; i < list.size(); i++){
                int duplicateCount = 0;
                for(int j = 1; j < (int) originalMembers.get(i); j++){
                    duplicateCount += list.get(i)[j].duplicateCount;
                }
                if(maxDuplicatesCount < duplicateCount && !bestKClusters.contains(list.get(i))){
                    maxDuplicatesCount = duplicateCount;
                    bestCluster = list.get(i);
                    index = i;
                }
            }
            if(bestCluster == null){
                System.out.println("Error: No best cluster found.");
                System.exit(6);
            }
            bestKClusters.add(bestCluster);
            newOriginalMembers.add(originalMembers.get(index));
            newCategoryList.add(categoryList.get(index));
            addedCount++;
        } while (addedCount < renderLimit);
        return bestKClusters;
    }

    public static ArrayList<Word[]> convertKeywordListToWords(ArrayList<Vertex[]> list,
                                                              ArrayList<String> categoryList,
                                                              ArrayList<String> newCategoryList,
                                                              ArrayList originalMembers,
                                                              Sketch sketch,
                                                              int k){

        ArrayList<Word[]> words = new ArrayList<>();
        ArrayList newOriginalMembers = new ArrayList();
        ArrayList<Vertex[]> bestKClusters = bestKClusters(list, categoryList, newCategoryList, k, originalMembers, newOriginalMembers);

        for(int i = 0; i < bestKClusters.size(); i++){
            int maxDuplicateCount = 0;
            for(int j = 0; j < bestKClusters.get(i).length; j++){
                maxDuplicateCount = (maxDuplicateCount < bestKClusters.get(i)[j].duplicateCount) ? bestKClusters.get(i)[j].duplicateCount : maxDuplicateCount;
            }

            Word[] buffer = new Word[bestKClusters.get(i).length];
            buffer[0] = new Word(bestKClusters.get(i)[0].name, 1f);
            for(int j = 1; j < bestKClusters.get(i).length; j++){
                buffer[j] = new Word(bestKClusters.get(i)[j].name, (float) bestKClusters.get(i)[j].duplicateCount / (float) maxDuplicateCount);
                if(bestKClusters.get(i)[j].name.length() > 15)               // if a word is too long, it will not be rotated.
                {
                    buffer[j].setAngle(0);
                } else if(bestKClusters.get(i)[j].name.length() < 9)         // if a word is short, it will certainly be rotated to create some newness.
                {
                    buffer[j].setAngle(PConstants.PI / 2f);
                }
            }

            for(int j = (int) newOriginalMembers.get(i); j < bestKClusters.get(i).length; j++){
                buffer[j].setColor(sketch.color(66,66,66));          // set complementary words to a different color.
                buffer[j].setSize(20);
            }
            buffer[0].setAngle(0);
                    //.setPlace(SKETCH_X / 4, SKETCH_Y / 3);

            words.add(buffer);
        }

        return words;
    }
}
