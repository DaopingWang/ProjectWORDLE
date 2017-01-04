package graph.visualization.placer;

/**
 * Created by Wang.Daoping on 04.01.2017.
 */

import processing.core.*;
import wordcram.Word;
import wordcram.WordPlacer;


public class CustomSwirlWordPlacer implements WordPlacer {

    public PVector place(Word word, int wordIndex, int wordsCount,
                         int wordImageWidth, int wordImageHeight, int fieldWidth,
                         int fieldHeight) {

        float normalizedIndex = (float) wordIndex / wordsCount / 10;

        float theta = normalizedIndex * 6 * PConstants.TWO_PI;
        float radius = normalizedIndex * fieldWidth / 2f;

        float centerX = fieldWidth * 0.5f;
        float centerY = fieldHeight * 0.5f;

        float x = PApplet.cos(theta) * radius;
        float y = PApplet.sin(theta) * radius;

        return new PVector(centerX + x - wordImageWidth/2, centerY + y - wordImageHeight/2);
    }
}
