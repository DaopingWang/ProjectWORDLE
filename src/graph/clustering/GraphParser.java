package graph.clustering;

import graph.clustering.vertex.KeywordVertex;

import java.util.LinkedList;

/**
 * Created by Wang.Daoping on 02.12.2016.
 */
public class GraphParser {

    public static void calculateLayers(){
        KeywordVertex v;
        LinkedList<KeywordVertex> bfsQueue = new LinkedList<>();

        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            for(int j = 0; j < GraphFactory.rootKeywordVertices.get(i).subordinateList.size(); j++){
                for(int k = 0; k < GraphFactory.keywordVertices.size(); k++){
                    if(GraphFactory.keywordVertices.get(k).name.equals(GraphFactory.rootKeywordVertices.get(i).subordinateList.get(j))){
                        bfsQueue.push(GraphFactory.keywordVertices.get(k));
                        GraphFactory.keywordVertices.get(k).setLayer(1);
                    }
                }
            }
        }

        System.out.println("Start layer computation...");
        while(!bfsQueue.isEmpty()){
            v = bfsQueue.pop();
            for(int i = 0; i < v.subordinateList.size(); i++){
                if(GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)).layerIsUnset()){
                    GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)).setLayer(v.layer + 1);
                    bfsQueue.push(GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)));
                }
            }
        }
    }
}
