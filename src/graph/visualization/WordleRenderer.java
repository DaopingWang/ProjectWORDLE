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
    private PFont adobeSourceSansPro;

    public WordleRenderer(PApplet p){
        parent = p;
        adobeSourceSansPro = parent.createFont("SourceSansPro-Regular.ttf", 16);
    }

    public void renderWordCram(Word[] keywords){

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

    public void renderTitle(String searchKeyword,
                            String currentCategory,
                            int currentClusterIndex,
                            int totalNumberClusters){
        Word[] info = new Word[3];
        Word searchKeywordInfo = new Word("User query: " + searchKeyword, 1f);
        searchKeywordInfo
                .setAngle(0)
                .setSize(16)
                .setFont(adobeSourceSansPro)
                .setColor(parent.color(0))
                .setPlace(10f,(float) 10);

        Word categoryInfo = new Word("Current MKX category: " + currentCategory, 1f);
        categoryInfo
                .setAngle(0)
                .setSize(16)
                .setFont(adobeSourceSansPro)
                .setColor(parent.color(0))
                .setPlace(10f, (float) 30 + searchKeywordInfo.getRenderedHeight());

        Word clusterInfo = new Word("Cluster " + Integer.toString(currentClusterIndex) + "/" + Integer.toString(totalNumberClusters), 1f);
        clusterInfo
                .setAngle(0)
                .setSize(16)
                .setFont(adobeSourceSansPro)
                .setColor(parent.color(0))
                .setPlace(10f, (float) 50 + searchKeywordInfo.getRenderedHeight() + categoryInfo.getRenderedHeight());

        info[0] = searchKeywordInfo;
        info[1] = categoryInfo;
        info[2] = clusterInfo;

        new WordCram(parent)
                .fromWords(info)
                .drawAll();
    }
}
