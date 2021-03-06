package graph.visualization.nudger;

/**
 * Created by Wang.Daoping on 04.01.2017.
 */

import processing.core.PApplet;
import processing.core.PVector;
import wordcram.Word;
import wordcram.WordNudger;

public class CustomSpiralWordNudger implements WordNudger {

    // Who knows? this seems to be good, but it seems to depend on the font --
    // bigger fonts need a bigger thetaIncrement.
    private float thetaIncrement = (float) (Math.PI * 0.01);

    public PVector nudgeFor(Word w, int attempt) {
        float rad = powerMap(0.6f, attempt, 0, 600, 1, 100);

        thetaIncrement = powerMap(1, attempt, 0, 600, 0.5f, 0.3f);
        float theta = thetaIncrement * attempt;
        float x = PApplet.cos(theta) * rad;
        float y = PApplet.sin(theta) * rad;
        return new PVector(x, y);
    }

    /**
     *
     * @param power power factor
     * @param v current count of attempts -> the more we've attempted, the greater
     *          the outcoming value will be.
     * @param min1 minimum value of the current range
     * @param max1 maximum value of the current range
     * @param min2
     * @param max2
     * @return
     */
    private float powerMap(float power, float v, float min1, float max1,
                           float min2, float max2) {

        float val = PApplet.norm(v, min1, max1);
        val = PApplet.pow(val, power);
        return PApplet.lerp(min2, max2, val);
    }

}