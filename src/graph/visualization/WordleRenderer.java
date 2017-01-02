package graph.visualization;

import processing.core.PApplet;
import wordcram.WordCram;

/**
 * Created by wang.daoping on 02.01.2017.
 */

public class WordleRenderer {
    private PApplet parent;

    public WordleRenderer(PApplet p){
        parent = p;
    }

    public void makeWordCram(String[] keywords){
        new WordCram(parent)
                .fromTextString(keywords)
                .withColors(parent.color(30), parent.color(110), parent.color(parent.random(255), 240, 200))
                .withFont("Copse")
                .drawAll();
    }
}
