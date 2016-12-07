package old.graph.clustering;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Wang.Daoping on 16.11.2016.
 */
public class BreadthFirstSearcher {

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
            for(int j = 0; j < GraphFactory.keywordEntries; j++){
                if(vertex.parent[i].equals(GraphFactory.keywordArray[j].name)){    // This is a must!
                    v = GraphFactory.keywordArray[j];
                    currentKeyword = j;                                         // This is a must too!
                    break;
                }
                if(j == GraphFactory.keywordEntries - 1){
                    skipIteration = true;
                }
            }

            // If tagged, skip this iteration.
            if(skipIteration){
                continue;
            }

            // Initialize/reset tempLayer attribute of vertices.
            for(int z = 0; z < GraphFactory.keywordEntries; z++){
                GraphFactory.keywordArray[z].tempLayer = 0;
                for(int x = 0; x < GraphFactory.keywordArray[z].loopCheckArrayNum; x++){
                    GraphFactory.keywordArray[z].loopCheckArray[x] = null;
                }
                GraphFactory.keywordArray[z].loopCheckArrayNum = 0;
            }

            // Enqueue v.
            queue.offer(v);

            // Dequeue the first element while queue not empty, refer it with v.
            while((v = queue.poll()) != null){

                // During each iteration, compare one parent of v with all nodes, find the appropriate object
                for(int k = 0; k < v.parentNum; k++){
                    for(int l = 0; l < GraphFactory.keywordEntries; l++){

                        // Enqueue the right vertex , if it's tempLayer <= tempLayer of v, increase it by 1.
                        // If cannot find any vertex with .name == current parent of v -> End of a path reached
                        // with v.tempLayer == possible longest path of vertex.parent[i], so compare it with discoveredDepth,
                        // if discoveredDepth smaller -> discoveredDepth = v.tempLayer.
                        if(v.parent[k].equals(GraphFactory.keywordArray[l].name)){
                            if(!BreadthFirstSearcher.isLooping(v, GraphFactory.keywordArray[l])){
                                queue.offer(GraphFactory.keywordArray[l]);
                                if(GraphFactory.keywordArray[l].tempLayer <= v.tempLayer){
                                    GraphFactory.keywordArray[l].tempLayer = v.tempLayer + 1;
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
            vertex.setLayer(vertex.pathLength[i]);
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
