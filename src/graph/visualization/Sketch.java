package graph.visualization;
/**
 * Created by wang.daoping on 02.01.2017.
 */

import processing.core.PApplet;
import processing.core.PSurface;
import wordcram.*;

import java.util.ArrayList;

public class Sketch extends PApplet{
    private ArrayList<Word[]> clusters;
    private String searchKeyword;
    private ArrayList<String> categoryList;
    private int totalNumberClusters;
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

    public void setCategoryList(ArrayList<String> categoryList) {
        this.categoryList = categoryList;
    }

    public void setTotalNumberClusters(int totalNumberClusters) {
        this.totalNumberClusters = totalNumberClusters;
    }

    public void setClusters(ArrayList<Word[]> words){
        this.clusters = words;
    }

    public void setSearchKeyword(String searchWord){
        this.searchKeyword = searchWord;
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
        background(255);
        this.wordleRenderer.renderTitle(this.searchKeyword, categoryList.get(counter), counter + 1, totalNumberClusters);
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