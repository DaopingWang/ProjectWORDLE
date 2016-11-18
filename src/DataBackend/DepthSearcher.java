package DataBackend;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Wang.Daoping on 16.11.2016.
 */
public class DepthSearcher {

    /**
     * is based on breadth first search. It discovers the tempLayer structure of the input vertex object.
     * @param vertex is the vertex object whose depth needs to be found out.
     */
    public static void findDepthFor(KeywordVertex vertex){
        Queue<KeywordVertex> queue = new LinkedList<>();
        int discoveredDepth;
        int currentKeyword;
        boolean skipIteration;

        KeywordVertex v = vertex;

        // For each iteration, pick one of vertex's parents, find it's depth with modified breadth first search.
        for(int i = 0; i < vertex.parentNum; i++){
            discoveredDepth = 0;
            currentKeyword = 0;
            skipIteration = false;

            // Find the Vertex object that matches vertex.parent[i], refer it with v.
            // If no match found (meaning current parent is a root keyword), tag discoveredDepth and currentKeyword with -1.
            for(int j = 0; j < CSVParser.keywordEntries; j++){
                if(vertex.parent[i].equals(CSVParser.keywordArray[j].name)){    // This is a must!
                    v = CSVParser.keywordArray[j];
                    currentKeyword = j;                                         // This is a must too!
                    break;
                }
                if(j == CSVParser.keywordEntries - 1){
                    skipIteration = true;
                }
            }

            // If tagged, skip this iteration.
            if(skipIteration){
                continue;
            }

            // Initialize/reset tempLayer attribute of vertices.
            for(int z = 0; z < CSVParser.keywordEntries; z++){
                CSVParser.keywordArray[z].tempLayer = 0;
                for(int x = 0; x < CSVParser.keywordArray[z].loopCheckArrayNum; x++){
                    CSVParser.keywordArray[z].loopCheckArray[x] = null;
                }
                CSVParser.keywordArray[z].loopCheckArrayNum = 0;
            }

            // Enqueue v.
            queue.offer(v);

            // Dequeue the first element while queue not empty, refer it with v.
            while((v = queue.poll()) != null){

                // During each iteration, compare one parent of v with all nodes, find the appropriate object
                for(int k = 0; k < v.parentNum; k++){
                    for(int l = 0; l < CSVParser.keywordEntries; l++){

                        // Enqueue the right vertex , if it's tempLayer <= tempLayer of v, increase it by 1.
                        // If cannot find any vertex with .name == current parent of v -> End of a path reached
                        // with v.tempLayer == possible longest path of vertex.parent[i], so compare it with discoveredDepth,
                        // if discoveredDepth smaller -> discoveredDepth = v.tempLayer.
                        if(v.parent[k].equals(CSVParser.keywordArray[l].name)){
                            if(!DepthSearcher.isLooping(v, CSVParser.keywordArray[l])){
                                queue.offer(CSVParser.keywordArray[l]);
                                if(CSVParser.keywordArray[l].tempLayer <= v.tempLayer){
                                    CSVParser.keywordArray[l].tempLayer = v.tempLayer + 1;
                                }
                            }
                            break;
                        }
                        if(discoveredDepth < v.tempLayer){
                            discoveredDepth = v.tempLayer;
                        }
                    }
                }
            }

            // Longest path found after all possible ways discovered.
            vertex.pathLength[i] = discoveredDepth + 1;

            // Update layer of current node's parent.
            if(CSVParser.keywordArray[currentKeyword].noLayerSet()){
                CSVParser.keywordArray[currentKeyword].setLayer(vertex.pathLength[i] - 1);
            }
        }
    }

    /**
     * checks if an edge has already been traversed by comparing given child vertex with parent's loopCheckArray.
     * @param child given child vertex
     * @param parent has a loopCheckArray which holds all children who have already visited him.
     * @return true if child already visited (parent won't be enqueued again, looping avoided); returns false and adds the child into loopCheckArray if no duplicate found.
     */
    private static boolean isLooping(Vertex child, KeywordVertex parent){
        for(int i = 0; i < parent.loopCheckArrayNum; i++){
            if(parent.loopCheckArray[i].equals(child.name)){
                //System.out.println("Loop discovered: " + loopCheckArray.name);
                return true;
            }
        }
        parent.setLoopCheckArray(child.name);
        return false;
    }
}
