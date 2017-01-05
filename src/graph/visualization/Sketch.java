package graph.visualization;
/**
 * Created by wang.daoping on 02.01.2017.
 */

import processing.core.PApplet;
import processing.core.PSurface;
import wordcram.*;

import java.util.ArrayList;
import java.util.StringJoiner;

public class Sketch extends PApplet{
    private ArrayList<Word[]> clusters;
    private String searchW;
    private int counter;
    private WordleRenderer wordleRenderer;
    private int x;
    private int y;

    public Sketch(float X, float Y){
        super();
        this.counter = 0;
        this.x = (int) X;
        this.y = (int) Y;
    }

    public void setClusters(ArrayList<Word[]> words){
        this.clusters = words;
    }

    public void setSearchW(String searchWord){
        this.searchW = searchWord;
    }

    public void settings(){
        size(x, y);
    }

    public void setup(){
        removeExitEvent(getSurface());
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

    static final void removeExitEvent(final PSurface surf) {
        final java.awt.Window win
                = ((processing.awt.PSurfaceAWT.SmoothCanvas) surf.getNative()).getFrame();

        for (final java.awt.event.WindowListener evt : win.getWindowListeners())
            win.removeWindowListener(evt);
    }
}