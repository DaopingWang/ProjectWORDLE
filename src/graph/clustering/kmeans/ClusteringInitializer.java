package graph.clustering.kmeans;

import graph.clustering.GraphFactory;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusteringInitializer {
    public static void categoriesBasedInitializer(ArrayList<RootKeywordVertex> rootKeywordVertices){
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            Cluster k = new Cluster(rootKeywordVertices.get(i).similarityVector);
            ClusterFactory.clusters.add(k);
        }
    }
}
