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
                .sizedByRank(3, 45)
                .withNudger(new PlottingWordNudger(parent, new SpiralWordNudger()))
                .withPlacer(new PlottingWordPlacer(parent, new SwirlWordPlacer()))
                .drawAll();
    }
}
