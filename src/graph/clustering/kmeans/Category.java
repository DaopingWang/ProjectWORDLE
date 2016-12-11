package graph.clustering.kmeans;

import graph.clustering.vertex.KeywordVertex;

import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 11.12.2016.
 */
public class Category {
    public ArrayList<KeywordVertex> categoryMembers;
    public ArrayList<KeywordVertex> categoryMasters;
    public ArrayList<Cluster> clusters;
    public int categoryIndex;
    public int maxLayer;

    public Category(int inputIndex){
        this.categoryIndex = inputIndex;
        this.clusters = new ArrayList<>();
        this.categoryMasters = new ArrayList<>();
        this.categoryMembers = new ArrayList<>();
        this.maxLayer = Integer.MAX_VALUE;
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

    public void addMaster(KeywordVertex inputVertex){
        this.categoryMasters.add(inputVertex);
    }
}
