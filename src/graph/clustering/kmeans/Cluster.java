package graph.clustering.kmeans;

import graph.clustering.vertex.KeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class Cluster {
    public ArrayList<KeywordVertex> memberVertices;
    public Vector<Double> withinClusterVariation;
    public Vector<Double> centroid;


    public Cluster(){
        this.centroid = new Vector<>();
        this.withinClusterVariation = new Vector<>();
        this.memberVertices = new ArrayList<>();
    }

}
