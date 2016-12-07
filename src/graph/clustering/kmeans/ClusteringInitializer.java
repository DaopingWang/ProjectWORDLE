package graph.clustering.kmeans;

import graph.clustering.GraphFactory;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class ClusteringInitializer {
    public static void categoriesBasedInitializer(){
        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            Cluster k = new Cluster(GraphFactory.rootKeywordVertices.get(i).similarityVector);
        }
    }
}
