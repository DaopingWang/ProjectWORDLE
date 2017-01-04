package graph.visualization;

import graph.clustering.vertex.Vertex;
import processing.core.PApplet;
import processing.core.PConstants;
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
                try {
                    maxDuplicateCount = (maxDuplicateCount < list.get(i)[j].duplicateCount) ? list.get(i)[j].duplicateCount : maxDuplicateCount;
                } catch (NullPointerException e){
                    System.out.println("j=" + Integer.toString(j) + " i=" + Integer.toString(i));
                    System.exit(666);
                }
            }

            Word[] buffer = new Word[list.get(i).length];
            buffer[0] = new Word(list.get(i)[0].name, 1f);
            for(int j = 1; j < list.get(i).length; j++){
                buffer[j] = new Word(list.get(i)[j].name, (float) list.get(i)[j].duplicateCount / (float) maxDuplicateCount);
                if(list.get(i)[j].name.length() > 15)               // if a word is too long, it will not be rotated.
                {
                    buffer[j].setAngle(0);
                } else if(list.get(i)[j].name.length() < 9)         // if a word is short, it will certainly be rotated to create some newness.
                {
                    buffer[j].setAngle(PConstants.PI / 2f);
                }
            }
            buffer[0].setAngle(0);
                    //.setPlace(SKETCH_X / 4, SKETCH_Y / 3);

            words.add(buffer);
        }

        return words;
    }
}
