package graph.visualization;

import processing.core.PApplet;
import wordcram.*;

/**
 * Created by wang.daoping on 02.01.2017.
 */

public class WordleRenderer {
    private PApplet parent;

    public WordleRenderer(PApplet p){
        parent = p;
    }

    public void makeWordCram(Word[] keywords){
        new WordCram(parent)
                .fromWords(keywords)
                .withColors(parent.color(255), parent.color(255), parent.color(255))
                .withFont("Copse")
                .includeNumbers()
                .keepCase()
                .sizedByRank(10, 40)
                .withNudger(new PlottingWordNudger(parent, new RandomWordNudger()))
                .withPlacer(new PlottingWordPlacer(parent, new SwirlWordPlacer()))
                .drawAll();
    }
}
