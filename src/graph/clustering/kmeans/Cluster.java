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
    public Vector<Double> standardDeviationVector;
    public double maxStandardDeviation;
    public Vector<Double> categoryBasedCentroid;
    public Vector<Double> masterSimilarityCentroid;
    public SparseDoubleMatrix1D centroid;
    public double averageEuclideanDistance;
    public Vertex grandMaster;
    public boolean isClosed;
    public Vector<Double> interclusterDistance;
    public boolean involvedInMerge;

    public Cluster(){
        this.categoryBasedCentroid = new Vector<>();
        this.masterSimilarityCentroid = new Vector<>();
        this.standardDeviationVector = new Vector<>();
        this.memberVertices = new ArrayList<>();
        this.averageEuclideanDistance = 0;
        this.isClosed = false;
        this.interclusterDistance = new Vector<>();
    }

    public Cluster(Vector<Double> similarityVector){
        this.categoryBasedCentroid = new Vector<>();
        this.masterSimilarityCentroid = new Vector<>();
        this.categoryBasedCentroid.addAll(similarityVector);
        this.standardDeviationVector = new Vector<>();
        this.memberVertices = new ArrayList<>();
        this.averageEuclideanDistance = 0;
        this.isClosed = false;
        this.interclusterDistance = new Vector<>();
    }

}
