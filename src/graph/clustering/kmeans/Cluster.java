package graph.clustering.kmeans;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.Vertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class Cluster {
    public ArrayList<KeywordVertex> memberVertices;
    public Vector<Double> withinClusterVariation;
    public Vector<Double> categoryBasedCentroid;
    public Vector<Double> masterSimilarityCentroid;
    public SparseDoubleMatrix1D centroid;
    public double averageEuclideanDistance;
    public Vertex grandMaster;
    public boolean isClosed;

    public Cluster(){
        this.categoryBasedCentroid = new Vector<>();
        this.masterSimilarityCentroid = new Vector<>();
        this.withinClusterVariation = new Vector<>();
        this.memberVertices = new ArrayList<>();
        this.averageEuclideanDistance = 0;
        this.isClosed = false;
    }

    public Cluster(Vector<Double> similarityVector){
        this.categoryBasedCentroid = new Vector<>();
        this.masterSimilarityCentroid = new Vector<>();
        this.categoryBasedCentroid.addAll(similarityVector);
        this.withinClusterVariation = new Vector<>();
        this.memberVertices = new ArrayList<>();
        this.averageEuclideanDistance = 0;
        this.isClosed = false;
    }

}
