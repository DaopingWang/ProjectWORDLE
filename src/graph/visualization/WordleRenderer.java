package graph.visualization;

import graph.visualization.nudger.CustomSpiralWordNudger;
import graph.visualization.placer.CustomSwirlWordPlacer;
import processing.core.PApplet;
import processing.core.PFont;
import wordcram.*;

/**
 * Created by wang.daoping on 02.01.2017.
 */

public class WordleRenderer {
    private PApplet parent;

    public WordleRenderer(PApplet p){
        parent = p;
    }

    public void renderWordCram(Word[] keywords){
        PFont adobeSourceSansPro = parent.createFont("SourceSansPro-Regular.ttf", 16);

        new WordCram(parent)
                .fromWords(keywords)
                //.withColors(parent.color(255), parent.color(255), parent.color(255))
                .withColor(parent.color(242,28,10))
                .withFont(adobeSourceSansPro)
                .includeNumbers()
                //.angledAt(0f)
                .maxAttemptsToPlaceWord(Integer.MAX_VALUE)
                .keepCase()
                .sizedByRank(3, 50)
                //.withNudger(new PlottingWordNudger(parent, new CustomSpiralWordNudger()))
                .withNudger(new CustomSpiralWordNudger())
                //.withPlacer(new PlottingWordPlacer(parent, new CustomSwirlWordPlacer()))
                .withPlacer(new CustomSwirlWordPlacer())
                .drawAll();
    }

    public void renderTitle(String searchWord){
        Word[] title = new Word[1];
        Word searchW = new Word("Keyword: " + searchWord, 1f);
        searchW.setAngle(0)
                .setSize(20)
                .setColor(parent.color(0))
                .setPlace(10, 10);

        title[0] = searchW;

        new WordCram(parent)
                .fromWords(title)
                .drawAll();
    }
}
