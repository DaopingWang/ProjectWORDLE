package DataBackend;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Wang.Daoping on 16.11.2016.
 */
public class DepthSearch {

    /**
     * is based on breadth first search. It discovers layer structure of the input vertex object.
     * @param vertex is the vertex object whose depth needs to be found out.
     */
    public static void findDepthFor(Vertex vertex){
        Queue<Vertex> queue = new LinkedList<>();
        int discoveredDepth;

        Vertex v = vertex;

        // For each iteration, pick one of vertex's parents, find it's depth with modified breadth first search.
        for(int i = 0; i < vertex.parentNum; i++){
            discoveredDepth = 0;

            // Find the Vertex object that matches vertex.parent[i], refer it with v.
            for(int j = 0; j < CSVFileIO.keywordEntries; j++){
                if(vertex.parent[i].equals(CSVFileIO.keywordArray[j].name)){    // This is a must!
                    v = CSVFileIO.keywordArray[j];
                    break;
                }
            }

            // Initialize/reset layer attribute of vertices.
            for(int z = 0; z < CSVFileIO.keywordEntries; z++){
                CSVFileIO.keywordArray[z].layer = 0;
            }

            // Enqueue v.
            queue.offer(v);

            // Dequeue the first element while queue not empty, refer it with v.
            while((v = queue.poll()) != null){

                // During each iteration, compare one parent of v with all nodes, find the appropriate object
                for(int k = 0; k < v.parentNum; k++){
                    for(int l = 0; l < CSVFileIO.keywordEntries; l++){

                        // Enqueue the right vertex , if it's layer <= layer of v, increase it by 1.
                        // If cannot find any vertex with .name == current parent of v -> End of a path reached
                        // with v.layer == possible longest path of vertex.parent[i], so compare it with discoveredDepth,
                        // if discoveredDepth smaller -> discoveredDepth = v.layer.
                        if(v.parent[k].equals(CSVFileIO.keywordArray[l].name)){
                            if(!DepthSearch.isLooping(v, CSVFileIO.keywordArray[l])){
                                queue.offer(CSVFileIO.keywordArray[l]);
                                if(CSVFileIO.keywordArray[l].layer <= v.layer){
                                    CSVFileIO.keywordArray[l].layer = v.layer + 1;
                                }
                            }
                            break;
                        }
                        if(discoveredDepth < v.layer){
                            discoveredDepth = v.layer;
                        }
                    }
                }
            }

            // Longest path found after all possible ways discovered.
            vertex.pathLength[i] = discoveredDepth + 1;
        }
    }

    /**
     * checks if an edge has already been traversed by comparing given child vertex with parent's child array
     * @param child given child vertex
     * @param parent has a child array which holds all children who have already visited him.
     * @return true if child already visited (parent won't be enqueued again, looping avoided); false and adds the child into child array if cannot be found.
     */
    private static boolean isLooping(Vertex child, KeywordVertex parent){
        for(int i = 0; i < parent.childNum; i++){
            if(parent.child[i].equals(child.name)){
                //System.out.println("Loop discovered: " + child.name);
                return true;
            }
        }
        parent.setChild(child.name);
        return false;
    }
}
