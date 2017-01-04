package graph.visualization;
/**
 * Created by wang.daoping on 02.01.2017.
 */

import processing.core.PApplet;
import wordcram.*;

import java.util.ArrayList;

public class Sketch extends PApplet{
    private ArrayList<Word[]> clusters;
    private String searchW;
    private int counter;
    private WordleRenderer wordleRenderer;
    private int x;
    private int y;

    public Sketch(ArrayList<Word[]> words, String searchWord, float X, float Y){
        super();
        this.clusters = words;
        this.searchW = searchWord;
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
        this.wordleRenderer.renderTitle(this.searchW);
        this.wordleRenderer.renderWordCram(clusters.get(counter));
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