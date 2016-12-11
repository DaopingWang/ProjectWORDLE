package graph.clustering.vector.generation;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.Utility;
import graph.clustering.vertex.KeywordVertex;
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
            if(keywordVertices.get(i).distance == Double.MAX_VALUE && (startVertex.pathLengthVector.get(i) == 0)){ // second argument for undirected
                startVertex.pathLengthVector.set(i, 0);
            } else if(keywordVertices.get(i).distance != Double.MAX_VALUE){
                startVertex.pathLengthVector.set(i, keywordVertices.get(i).distance);
                keywordVertices.get(i).pathLengthVector.set(Utility.findIndexForName(startVertex.name, keywordVertices), keywordVertices.get(i).distance); // Undirected
            }
        }

        for(int i = 0; i < rootKeywordVertices.size(); i++){
            if(rootKeywordVertices.get(i).distance == Double.MAX_VALUE){
                startVertex.pathLengthVector.set(i + keywordVertices.size(), 0);
            } else {
                startVertex.pathLengthVector.set(i + keywordVertices.size(), rootKeywordVertices.get(i).distance);
            }
        }
    }

    public static void initSparseVectors(ArrayList<KeywordVertex> keywordVertices,
                                          ArrayList<RootKeywordVertex> rootKeywordVertices){

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).pathLengthVector = new SparseDoubleMatrix1D(keywordVertices.size() + rootKeywordVertices.size());
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
            KeywordVertex destinationVertex = Utility.findVertexForName(evaluateVertex.edgeList.get(i).getTargetVertexName(), keywordVertices);
            if(destinationVertex != null && !settledVertices.contains(destinationVertex)){
                if(destinationVertex.distance > evaluateVertex.edgeList.get(i).getEdgeWeight() + evaluateVertex.distance){
                    destinationVertex.distance = evaluateVertex.edgeList.get(i).getEdgeWeight() + evaluateVertex.distance;
                    unsettledVertices.offer(destinationVertex);
                }
            } else if(destinationVertex == null){
                int rootKeyIndex = Utility.findIndexForName(evaluateVertex.edgeList.get(i).getTargetVertexName());
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
