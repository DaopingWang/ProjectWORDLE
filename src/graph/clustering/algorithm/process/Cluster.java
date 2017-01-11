package graph.clustering.algorithm.process;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.Vertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by wang.daoping on 07.12.2016.
 */

/**
 * The clusters created and adjusted by ISODATA / K-Means clustering algorithms.
 */
public class Cluster {

    /**
     * Member points of this cluster.
     */
    public ArrayList<KeywordVertex> memberVertices;

    /**
     * The vector whose ith coordinate contains the standard deviation in the ith dimension
     * within this cluster.
     */
    public Vector<Double> standardDeviationVector;

    /**
     * The maximum standard deviation among all coordinates of this.standardDeviationVector
     */
    public double maxStandardDeviation;

    /**
     * <b>Deprecated</b>
     */
    public Vector<Double> categoryBasedCentroid;

    /**
     * The vector representing this cluster's center. It's dimensions are the chosen master keywords.
     */
    public Vector<Double> masterSimilarityCentroid;

    /**
     * <b>Deprecated</b>
     */
    public SparseDoubleMatrix1D centroid;

    /**
     * The average euclidean distance between cluster members and center.
     */
    public double averageEuclideanDistance;

    /**
     * The "parent" keyword of this cluster's members, for example, "fruit" shall be a possible parent
     * keyword of "apple", "orange" and "watermelon". In fact, this is the master keyword whose representative
     * vector entry is the smallest.
     */
    public Vertex grandMaster;

    /**
     * <b>Deprecated</b>
     */
    public boolean isClosed;

    /**
     * The vector whose coordinates are the euclidean distances between this cluster and other clusters.
     */
    public Vector<Double> interclusterDistance;

    public boolean involvedInMerge;

    /**
     * The standard constructor
     */
    public Cluster(){
        this.categoryBasedCentroid = new Vector<>();
        this.masterSimilarityCentroid = new Vector<>();
        this.standardDeviationVector = new Vector<>();
        this.memberVertices = new ArrayList<>();
        this.averageEuclideanDistance = 0;
        this.isClosed = false;
        this.interclusterDistance = new Vector<>();
    }

    /**
     * <b>Deprecated</b>
     * @param similarityVector
     */
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
