package graph.visualization;

import graph.clustering.vertex.Vertex;
import processing.core.PApplet;
import wordcram.Word;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 03.01.2017.
 */
public class WordleFactory {
    public static float SKETCH_X = 1000;
    public static float SKETCH_Y = 1000;

    public static void renderWordle(ArrayList<Word[]> inputClusters){
        Sketch sketch = new Sketch(inputClusters, SKETCH_X, SKETCH_Y);
        PApplet.runSketch(new String[]{"graph.visualization.Sketch"}, sketch);
    }

    public static ArrayList<Word[]> convertKeywordListToWords(ArrayList<Vertex[]> list){
        ArrayList<Word[]> words = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            int maxDuplicateCount = 0;
            for(int j = 0; j < list.get(i).length; j++){
                maxDuplicateCount = (maxDuplicateCount < list.get(i)[j].duplicateCount) ? list.get(i)[j].duplicateCount : maxDuplicateCount;
            }

            Word[] buffer = new Word[list.get(i).length];
            buffer[0] = new Word(list.get(i)[0].name, 1f);
            for(int j = 1; j < list.get(i).length; j++){
                buffer[j] = new Word(list.get(i)[j].name, (float) list.get(i)[j].duplicateCount / (float) maxDuplicateCount);
            }
            buffer[0].setAngle(0);
                    //.setPlace(SKETCH_X / 4, SKETCH_Y / 3);

            words.add(buffer);
        }

        return words;
    }
}
