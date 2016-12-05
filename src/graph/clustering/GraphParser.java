package graph.clustering;

import graph.clustering.vertex.Edge;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.Probability;

import java.util.LinkedList;
import java.util.Stack;

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
            v = bfsQueue.pollFirst();
            for(int i = 0; i < v.subordinateList.size(); i++){
                if(GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)).layerIsUnset()){
                    GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)).setLayer(v.layer + 1);
                    if(GraphFactory.layerNum < v.layer + 1){
                        GraphFactory.layerNum = v.layer + 1;
                    }
                    bfsQueue.offerLast(GraphFactory.keywordVertices.get(GraphFactory.findIndexForName(v.subordinateList.get(i), GraphFactory.keywordVertices)));
                }
            }
        }
    }

    public static void calculateEdgesWeights(){
        String currentEndVertexName;
        Edge currentEdge;

        System.out.println("Start edges calculations...");
        for(int i = 0; i < GraphFactory.keywordVertices.size(); i++){
            for(int j = 0; j < GraphFactory.keywordVertices.get(i).edgeList.size(); j++){
                currentEndVertexName = GraphFactory.keywordVertices.get(i).edgeList.get(j).getTargetVertexName();
                currentEdge = GraphFactory.keywordVertices.get(i).edgeList.get(j);

                if(GraphFactory.isRootKeyword(currentEndVertexName)){
                    currentEdge.setEdgeWeight(0);
                } else {
                    currentEdge.setEdgeWeight(GraphFactory.findVertexForName(currentEndVertexName, GraphFactory.keywordVertices).layer);
                }
            }
        }
    }



    public static void calculateProbability(KeywordVertex inputStartKeyword, KeywordVertex inputTargetVertex){
        Stack<KeywordVertex> stack = new Stack<>();
        Probability p = new Probability(inputTargetVertex.name);
        inputStartKeyword.probabilityList.add(p);

        stack.push(inputStartKeyword);
    }

    private static void performDFS(Stack<KeywordVertex> inputStack, KeywordVertex inputTargetVertex){
        KeywordVertex kv;
        KeywordVertex foundVertex;
        double discoveredLength = 0;

        while(!inputStack.isEmpty()){
            kv = inputStack.peek();
            for(int i = 0; i < kv.edgeList.size(); i++){
                Edge currentEdge = kv.edgeList.get(i);

                if(currentEdge.getTargetVertexName().equals(inputTargetVertex.name)){
                    discoveredLength =  kv.edgeList.get(i).getEdgeWeight();
                } else if((foundVertex = GraphFactory.findVertexForName(currentEdge.getTargetVertexName(), GraphFactory.keywordVertices)) != null){
                    inputStack.push(foundVertex);
                    discoveredLength += currentEdge.getEdgeWeight();
                    performDFS(inputStack, inputTargetVertex);
                } else {
                    discoveredLength = 0;
                    inputStack.pop();
                }
            }
        }

    }
}
