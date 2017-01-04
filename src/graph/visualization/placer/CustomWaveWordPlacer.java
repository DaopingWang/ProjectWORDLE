package graph.visualization.placer;

/**
 * Created by Wang.Daoping on 04.01.2017.
 */
import processing.core.*;
import wordcram.Word;
import wordcram.WordPlacer;

public class CustomWaveWordPlacer implements WordPlacer {

    public PVector place(Word word, int wordIndex, int wordsCount,
                         int wordImageWidth, int wordImageHeight, int fieldWidth,
                         int fieldHeight) {

        float normalizedIndex = (float) wordIndex / wordsCount;
        float x = normalizedIndex * fieldWidth;
        float y = normalizedIndex * fieldHeight;

        float yOffset = getYOffset(wordIndex, wordsCount, fieldHeight);
        return new PVector(x, y + yOffset);
    }

    private float getYOffset(int wordIndex, int wordsCount, int fieldHeight) {
        float theta = PApplet.map(wordIndex, 0, wordsCount, PConstants.PI, -PConstants.PI);

        return (float) Math.sin(theta) * (fieldHeight / 3f);
    }
}