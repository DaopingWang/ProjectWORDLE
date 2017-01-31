package graph.clustering;

import graph.clustering.GraphFactory;
import graph.clustering.Utility;
import graph.clustering.vertex.Edge;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.*;

/**
 * Created by Wang.Daoping on 02.12.2016.
 */
public class GraphParser {
    public static void calculateLayers(ArrayList<KeywordVertex> keywordVertices,
                                       ArrayList<RootKeywordVertex> rootKeywordVertices){
        KeywordVertex v;
        LinkedList<KeywordVertex> bfsQueue = new LinkedList<>();

        for(int i = 0; i < rootKeywordVertices.size(); i++){
            for(int j = 0; j < rootKeywordVertices.get(i).subordinateList.size(); j++){
                for(int k = 0; k < keywordVertices.size(); k++){
                    if(keywordVertices.get(k).name.equals(rootKeywordVertices.get(i).subordinateList.get(j))){
                        bfsQueue.offer(keywordVertices.get(k));
                        keywordVertices.get(k).setLayer(1);
                        GraphFactory.layerNum = 0;
                    }
                }
            }
        }

        System.out.println("Start layer computation... (no percentage shown)");
        while(!bfsQueue.isEmpty()){
            v = bfsQueue.pollFirst();
            for(int i = 0; i < v.subordinateList.size(); i++){
                if(keywordVertices.get(Utility.findIndexForName(v.subordinateList.get(i), keywordVertices)).layerIsUnset()){
                    keywordVertices.get(Utility.findIndexForName(v.subordinateList.get(i), keywordVertices)).setLayer(v.layer + 1);
                    if(GraphFactory.layerNum < v.layer + 1){
                        GraphFactory.layerNum = v.layer + 1;
                    }
                    bfsQueue.offerLast(keywordVertices.get(Utility.findIndexForName(v.subordinateList.get(i), keywordVertices)));
                }
            }
        }
    }

    public static void calculateEdgesWeights(ArrayList<KeywordVertex> keywordVertices,
                                             ArrayList<RootKeywordVertex> rootKeywordVertices){
        String currentEndVertexName;
        Edge currentEdge;

        System.out.println("Start edges computation... (no percentage shown)");
        for(int i = 0; i < keywordVertices.size(); i++){
            for(int j = 0; j < keywordVertices.get(i).edgeList.size(); j++){
                currentEndVertexName = keywordVertices.get(i).edgeList.get(j).getTargetVertexName();
                currentEdge = keywordVertices.get(i).edgeList.get(j);

                if(Utility.isRootKeyword(currentEndVertexName)){
                    currentEdge.setEdgeWeight(0);
                } else {
                    currentEdge.setEdgeWeight(Utility.findVertexForName(currentEndVertexName, GraphFactory.keywordVertices).layer);
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
            if((foundRootKeyVertexIndex = Utility.findIndexForName(currentEdge.getTargetVertexName())) != -1){
                double previousValue = inputStartVertex.probabilityList.get(foundRootKeyVertexIndex).getProbability();
                inputStartVertex.probabilityList.get(foundRootKeyVertexIndex).setProbability(previousValue + 1 / (kv.distance + currentEdge.getEdgeWeight()));

            } else if((foundKeywordVertex = Utility.findVertexForName(currentEdge.getTargetVertexName(), keywordVertices)) != null && !inputStack.contains(foundKeywordVertex)){
                inputStack.push(foundKeywordVertex);
                foundKeywordVertex.distance = currentEdge.getEdgeWeight() + kv.distance;
                performDFS(inputStack, inputStartVertex, keywordVertices);
            }
        }
        inputStack.pop();
    }

    public static void setProbability(KeywordVertex startVertex){
        double sum = 0;
        startVertex.categorySimilarityVector = new Vector<>();
        for(int i = 0; i < startVertex.probabilityList.size(); i++){
            sum += startVertex.probabilityList.get(i).getProbability();
        }
        for(int i = 0; i < startVertex.probabilityList.size(); i++){
            double probability = startVertex.probabilityList.get(i).getProbability();
            startVertex.probabilityList.get(i).setProbability(probability / sum);
            startVertex.categorySimilarityVector.add(probability / sum);
        }

    }
}
