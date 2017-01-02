package graph.clustering.algorithm.process;

import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.Vertex;

import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 11.12.2016.
 */
public class Category {
    public ArrayList<KeywordVertex> categoryMembers;
    public int categoryMemberCount;
    public ArrayList<Cluster> clusters;
    public ArrayList<Vertex> masterVertices;
    public int categoryIndex;
    public int maxLayer;

    // ISODATAFactory
    /**
     * Initial number of clusters.
     */
    public int numClus;

    /**
     * Minimum number of points required to form a cluster.
     */
    public int samprm;

    /**
     * Maximum number of iterations.
     */
    public int maxIter;

    /**
     * Maximum standard deviation of points from their cluster center along each axis.
     */
    public double stdv;

    /**
     * Minimum required distance between two cluster centers.
     */
    public double lump;

    /**
     * Maximum number of cluster pairs that can be merged per iteration.
     */
    public int maxpair;

    /**
     * Average of averageEuDistances of all clusters.
     */
    public double overallAverageEuclideanDistance;

    public Category(int inputIndex){
        this.categoryIndex = inputIndex;
        this.clusters = new ArrayList<>();
        this.categoryMembers = new ArrayList<>();
        this.maxLayer = Integer.MAX_VALUE;
        this.masterVertices = new ArrayList<>();
    }

    public void updateLayer(int inputLayer){
        if(this.maxLayer > inputLayer){
            this.maxLayer = inputLayer;
        }
    }

    public void addMember(KeywordVertex inputVertex){
        this.categoryMembers.add(inputVertex);
        this.updateLayer(inputVertex.layer);
    }

}
