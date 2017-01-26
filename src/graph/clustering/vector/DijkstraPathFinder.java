package graph.clustering.vector;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import graph.clustering.Utility;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;

/**
 * Created by Wang.Daoping on 05.12.2016.
 */

/**
 * In order to apply the k-means clustering algorithm on our keywords, we need
 * to firstly define a specific Euclidean space for each set of keywords, individually.
 * To find out how keywords are chosen as the dimensions / coordinates of
 * these vector spaces, see Initializer.checkMasterQualification() method.
 *
 * After defining the vector space, we have to compute the distance between each keyword-cluster center
 * and between keyword-keyword. The coordinates of each keyword within a cluster are calculated in this class.
 */
public class DijkstraPathFinder {

    /**
     * calculates the shortest paths from the starting keyword to every other keyword using the
     * dijkstra algorithm.
     * The distance between the starting keyword and a unreachable keyword is undefined, which is zero
     * due to the way SparseDoubleMatrix1D of colt is implemented.
     * The distance between the starting keyword and itself is zero.
     * @param startVertex the keyword we select as the start vertex
     * @param keywordVertices all keywords from the csv file
     * @param rootKeywordVertices all root keywords from the csv file
     */
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
            //if(keywordVertices.get(i).distance == Double.MAX_VALUE/* && (startVertex.pathLengthVector.get(i) == 0)*/){ // second argument for undirected
              //  startVertex.pathLengthVector.set(i, 0);
            //} else
                if(keywordVertices.get(i).distance != Double.MAX_VALUE){
                startVertex.pathLengthVector.set(i, keywordVertices.get(i).distance); // sparse
                //startVertex.densePathLengthVector.set(i, keywordVertices.get(i).distance); // dense
                keywordVertices.get(i).pathLengthVector.set(Utility.findIndexForName(startVertex.name, keywordVertices), keywordVertices.get(i).distance); // Undirected
                } else if(keywordVertices.get(i).name.equals(startVertex.name)){
                    startVertex.pathLengthVector.set(i, 0);
                    keywordVertices.get(i).pathLengthVector.set(Utility.findIndexForName(startVertex.name, keywordVertices), 0); // Undirected
                    //startVertex.densePathLengthVector.set(i, 0);
                }
        }

        for(int i = 0; i < rootKeywordVertices.size(); i++){
            if(rootKeywordVertices.get(i).distance != Double.MAX_VALUE){
                startVertex.pathLengthVector.set(i + keywordVertices.size(), rootKeywordVertices.get(i).distance); // sparse
//                startVertex.densePathLengthVector.set(i + keywordVertices.size(), rootKeywordVertices.get(i).distance); // dense
            }
        }
        /*
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            if(rootKeywordVertices.get(i).distance == Double.MAX_VALUE){
                startVertex.pathLengthVector.set(i + keywordVertices.size(), 0);
            } else {
                startVertex.pathLengthVector.set(i + keywordVertices.size(), rootKeywordVertices.get(i).distance);
            }
        }*/
    }

    public static void initSparseVectors(ArrayList<KeywordVertex> keywordVertices,
                                          ArrayList<RootKeywordVertex> rootKeywordVertices){

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).pathLengthVector = new SparseDoubleMatrix1D(keywordVertices.size() + rootKeywordVertices.size());
            //keywordVertices.get(i).densePathLengthVector = new DenseDoubleMatrix1D(keywordVertices.size() + rootKeywordVertices.size());
            //keywordVertices.get(i).densePathLengthVector.assign(Double.MAX_VALUE);
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
