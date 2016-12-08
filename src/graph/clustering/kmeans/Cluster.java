package graph.clustering.kmeans;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.vertex.KeywordVertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class Cluster {
    public ArrayList<KeywordVertex> memberVertices;
    public Vector<Double> withinClusterVariation;
    public Vector<Double> categoryBasedCentroid;
    public SparseDoubleMatrix1D centroid;
    public double averageSquaredDistance;

    public Cluster(){
        this.categoryBasedCentroid = new Vector<>();
        this.withinClusterVariation = new Vector<>();
        this.memberVertices = new ArrayList<>();
        this.averageSquaredDistance = 0;
    }

    public Cluster(Vector<Double> similarityVector){
        this.categoryBasedCentroid = new Vector<>();
        this.categoryBasedCentroid.addAll(similarityVector);
        this.withinClusterVariation = new Vector<>();
        this.memberVertices = new ArrayList<>();
        this.averageSquaredDistance = 0;
    }

}
