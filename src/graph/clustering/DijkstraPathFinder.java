package graph.clustering;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.Probability;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Wang.Daoping on 05.12.2016.
 */
public class DijkstraPathFinder {
    public static void findSingleSourceShortestPath(KeywordVertex startVertex,
                                                    ArrayList<KeywordVertex> keywordVertices,
                                                    ArrayList<RootKeywordVertex> rootKeywordVertices){

        LinkedList<KeywordVertex> unsettledVertices = new LinkedList<>();
        ArrayList<KeywordVertex> settledVertices = new ArrayList<>();

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).distance = Double.MAX_VALUE;
            keywordVertices.get(i).previous = null;
        }
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            rootKeywordVertices.get(i).distance = Double.MAX_VALUE;
            rootKeywordVertices.get(i).previous = null;
        }
        startVertex.distance = 0;

        unsettledVertices.offer(startVertex);
        dijkstra(unsettledVertices, settledVertices, keywordVertices, rootKeywordVertices);

        for(int i = 0; i < keywordVertices.size(); i++){
            if(keywordVertices.get(i).distance == Double.MAX_VALUE){
                startVertex.pathLengthVector.set(i, 0);
            } else {
                startVertex.pathLengthVector.set(i, keywordVertices.get(i).distance);
            }
        }

        for(int i = 0; i < rootKeywordVertices.size(); i++){
            startVertex.pathLengthVector.set(i + keywordVertices.size(), rootKeywordVertices.get(i).distance);
            Probability p = new Probability(rootKeywordVertices.get(i).name, rootKeywordVertices.get(i).distance);
            startVertex.probabilityList.add(p);
        }

        setProbability(startVertex);
    }

    public static void initSparseVectors(ArrayList<KeywordVertex> keywordVertices,
                                          ArrayList<RootKeywordVertex> rootKeywordVertices){

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).pathLengthVector = new SparseDoubleMatrix1D(keywordVertices.size() + rootKeywordVertices.size());
        }
    }

    private static void setProbability(KeywordVertex startVertex){
        double sum = 0;
        for(int i = 0; i < startVertex.probabilityList.size(); i++){
            sum += 1 / startVertex.probabilityList.get(i).getProbability();
        }
        for(int i = 0; i < startVertex.probabilityList.size(); i++){
            double probability = 1 / startVertex.probabilityList.get(i).getProbability();
            startVertex.probabilityList.get(i).setProbability(probability / sum);
        }
    }

    private static void dijkstra(LinkedList<KeywordVertex> unsettledVertices,
                                 ArrayList<KeywordVertex> settledVertices,
                                 ArrayList<KeywordVertex> keywordVertices,
                                 ArrayList<RootKeywordVertex> rootKeywordVertices){

        KeywordVertex evaluateVertex;

        while(!unsettledVertices.isEmpty()){
            evaluateVertex = getNearestVertex(unsettledVertices);
            settledVertices.add(evaluateVertex);
            evaluateNeighbours(evaluateVertex, settledVertices, unsettledVertices, keywordVertices, rootKeywordVertices);
        }
    }

    private static KeywordVertex getNearestVertex(LinkedList<KeywordVertex> unsettledVertices){
        double minDistance = Double.MAX_VALUE;
        int index = -1;
        KeywordVertex kv;
        for(int i = 0; i < unsettledVertices.size(); i++){
            if(minDistance > unsettledVertices.get(i).distance){
                minDistance = unsettledVertices.get(i).distance;
                index = i;
            }
        }
        kv = unsettledVertices.remove(index);
        return kv;
    }

    private static void evaluateNeighbours(KeywordVertex evaluateVertex,
                                           ArrayList<KeywordVertex> settledVertices,
                                           LinkedList<KeywordVertex> unsettledVertices,
                                           ArrayList<KeywordVertex> keywordVertices,
                                           ArrayList<RootKeywordVertex> rootKeywordVertices){

        for(int i = 0; i < evaluateVertex.edgeList.size(); i++){
            KeywordVertex destinationVertex = GraphFactory.findVertexForName(evaluateVertex.edgeList.get(i).getTargetVertexName(), keywordVertices);
            if(destinationVertex != null && !settledVertices.contains(destinationVertex)){
                if(destinationVertex.distance > evaluateVertex.edgeList.get(i).getEdgeWeight() + evaluateVertex.distance){
                    destinationVertex.distance = evaluateVertex.edgeList.get(i).getEdgeWeight() + evaluateVertex.distance;
                    unsettledVertices.offer(destinationVertex);
                }
            } else if(destinationVertex == null){
                int rootKeyIndex = GraphFactory.findIndexForName(evaluateVertex.edgeList.get(i).getTargetVertexName());
                rootKeywordVertices.get(rootKeyIndex).distance = evaluateVertex.distance + evaluateVertex.edgeList.get(i).getEdgeWeight();
                rootKeywordVertices.get(rootKeyIndex).previous = evaluateVertex.name;
            }
        }

    }

    private static String getNearestVertex(KeywordVertex inputVertex){
        double minWeight = Double.MAX_VALUE;
        String nearestVertexName = null;
        for(int i = 0; i < inputVertex.edgeList.size(); i++){
            if(minWeight > inputVertex.edgeList.get(i).getEdgeWeight()){
                minWeight = inputVertex.edgeList.get(i).getEdgeWeight();
                nearestVertexName = inputVertex.edgeList.get(i).getTargetVertexName();
            }
        }
        return nearestVertexName;

    }
}
