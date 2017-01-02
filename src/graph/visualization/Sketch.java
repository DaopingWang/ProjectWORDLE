package graph.visualization;
/**
 * Created by wang.daoping on 02.01.2017.
 */

import processing.core.PApplet;
import wordcram.*;

public class Sketch extends PApplet{

    public void settings(){
        size(1000, 600);
    }

    public void draw(){
        background(0);

        WordleRenderer wordleRenderer = new WordleRenderer(this);
        //wordleRenderer.makeWordCram();
    }

    public static void main(String[] args){
        PApplet.main("graph.visualization.Sketch");
    }
}