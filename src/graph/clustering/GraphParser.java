package graph.clustering;

import graph.clustering.vertex.EdgeFactory;
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
                        bfsQueue.offer(GraphFactory.keywordVertices.get(k));
                        GraphFactory.keywordVertices.get(k).setLayer(1);
                        GraphFactory.layerNum = 0;
                    }
                }
            }
        }

        System.out.println("Start layer computation...");
        while(!bfsQueue.isEmpty()){
            v = bfsQueue.poll();
            for(int i = 0; i < v.subordinateList.size(); i++){
                if(GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)).layerIsUnset()){
                    GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)).setLayer(v.layer + 1);
                    if(GraphFactory.layerNum < v.layer + 1){
                        GraphFactory.layerNum = v.layer + 1;
                    }
                    bfsQueue.offer(GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)));
                }
            }
        }
    }

    public static void calculateEdgesWeights(){
        String currentEndVertexName;
        EdgeFactory currentEdge;

        System.out.println("Start edges calculations...");
        for(int i = 0; i < GraphFactory.keywordVertices.size(); i++){
            for(int j = 0; j < GraphFactory.keywordVertices.get(i).edgeList.size(); j++){
                currentEndVertexName = GraphFactory.keywordVertices.get(i).edgeList.get(j).getTargetVertexName();
                currentEdge = GraphFactory.keywordVertices.get(i).edgeList.get(j);

                if(GraphFactory.isRootKeyword(currentEndVertexName)){
                    currentEdge.setEdgeLength(0);
                } else {
                    currentEdge.setEdgeLength(GraphFactory.findVertexForName(currentEndVertexName, GraphFactory.keywordVertices).layer);
                }
            }
        }
    }
}
