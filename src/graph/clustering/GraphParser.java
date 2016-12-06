package graph.clustering;

import graph.clustering.vertex.Edge;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.Probability;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
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



    public static void calculateProbability(KeywordVertex inputStartVertex,
                                            ArrayList<KeywordVertex> keywordVertices){

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).distance = 0;
        }

        Stack<KeywordVertex> stack = new Stack<>();
        stack.push(inputStartVertex);
        performDFS(stack, inputStartVertex, keywordVertices);
    }

    private static void performDFS(Stack<KeywordVertex> inputStack,
                                   KeywordVertex inputStartVertex,
                                   ArrayList<KeywordVertex> keywordVertices){

        KeywordVertex kv;
        KeywordVertex foundKeywordVertex;
        int foundRootKeyVertexIndex;

        kv = inputStack.peek();
        for(int i = 0; i < kv.edgeList.size(); i++){
            Edge currentEdge = kv.edgeList.get(i);
            if((foundRootKeyVertexIndex = GraphFactory.findIndexForName(currentEdge.getTargetVertexName())) != -1){
                double previousValue = inputStartVertex.probabilityList.get(foundRootKeyVertexIndex).getProbability();
                inputStartVertex.probabilityList.get(foundRootKeyVertexIndex).setProbability(previousValue + 1 / (kv.distance + currentEdge.getEdgeWeight()));

            } else if((foundKeywordVertex = GraphFactory.findVertexForName(currentEdge.getTargetVertexName(), keywordVertices)) != null && !inputStack.contains(foundKeywordVertex)){
                inputStack.push(foundKeywordVertex);
                foundKeywordVertex.distance = currentEdge.getEdgeWeight() + kv.distance;
                performDFS(inputStack, inputStartVertex, keywordVertices);
            }
        }
        inputStack.pop();
    }

    public static void setProbability(KeywordVertex startVertex){
        double sum = 0;
        for(int i = 0; i < startVertex.probabilityList.size(); i++){
            sum += startVertex.probabilityList.get(i).getProbability();
        }
        for(int i = 0; i < startVertex.probabilityList.size(); i++){
            double probability = startVertex.probabilityList.get(i).getProbability();
            startVertex.probabilityList.get(i).setProbability(probability / sum);
        }
    }
}
