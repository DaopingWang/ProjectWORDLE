package graph.visualization;

import graph.visualization.nudger.CustomSpiralWordNudger;
import graph.visualization.placer.CustomSwirlWordPlacer;
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
                //.withColors(parent.color(255), parent.color(255), parent.color(255))
                .withColor(parent.color(255))
                .withFont("Copse")
                .includeNumbers()
                //.angledAt(0f)
                .maxAttemptsToPlaceWord(Integer.MAX_VALUE)
                .keepCase()
                .sizedByRank(3, 50)
                .withNudger(new PlottingWordNudger(parent, new CustomSpiralWordNudger()))
                .withPlacer(new PlottingWordPlacer(parent, new CustomSwirlWordPlacer()))
                .drawAll();
    }
}
