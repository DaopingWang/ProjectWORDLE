package graph.clustering.algorithm.process;

import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.Vertex;

import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 11.12.2016.
 */

/**
 * After input data are read, which are in our case results from the fuzzy search engine,
 * they will be preclustered by the 15 MKX categories and stored as objects of class "Category".
 */
public class Category {

    /**
     * The search results assigned to this MKX category.
     */
    public ArrayList<KeywordVertex> categoryMembers;

    /**
     * Total number of search results assigned to this category, including duplicate entries.
     */
    public int categoryMemberCount;

    /**
     * The clusters within this category.
     */
    public ArrayList<Cluster> clusters;

    /**
     * Keywords which chosen as the coordinates of the vector space representing this category.
     */
    public ArrayList<Vertex> masterVertices;

    /**
     * This category keyword's index in GraphFactory.rootKeywordVertices
     */
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

    /**
     * The standard Constructor
     * @param inputIndex this category keyword's index in GraphFactory.rootKeywordVertices
     */
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

    /**
     * adds the given keyword into this.categoryMembers
     * @param inputVertex The given keyword
     */
    public void addMember(KeywordVertex inputVertex){
        this.categoryMembers.add(inputVertex);
        this.updateLayer(inputVertex.layer);
    }

}
