package graph.visualization;
/**
 * Created by wang.daoping on 02.01.2017.
 */

import graph.clustering.vertex.Vertex;
import processing.core.PApplet;
import wordcram.*;

import java.util.ArrayList;

public class Sketch extends PApplet{
    private ArrayList<Word[]> clusters;
    private int counter;
    private WordleRenderer wordleRenderer;
    private int x;
    private int y;

    public Sketch(ArrayList<Word[]> words, float X, float Y){
        super();
        this.clusters = words;
        this.counter = 0;
        this.x = (int) X;
        this.y = (int) Y;
    }

    public void settings(){
        size(x, y);
    }

    public void setup(){
        noLoop();
        this.wordleRenderer = new WordleRenderer(this);
    }

    public void draw(){
        background(0);
        //WordleRenderer wordleRenderer = new WordleRenderer(this);
        this.wordleRenderer.makeWordCram(clusters.get(counter));
        counter++;
    }

    public void mousePressed(){
        if(counter < clusters.size()){
            redraw();
        } else{
            counter = 0;
            redraw();
        }
    }

    public void keyPressed(){

        //surface.setVisible(false);
    }

}