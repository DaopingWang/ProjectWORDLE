package DataBackend;

import java.util.Stack;

/**
 * Created by Wang.Daoping on 23.11.2016.
 */

/**
 * is implemented after finding out BreadthFirstSearcher's fatal problems with cycle detection and
 * computation inaccuracy. DepthFirstSearcher is basically based on recursive DFS (hence it's name).
 * It traverses through the graph prioritising the latest discovered nodes and avoids running in circle
 * by comparing the next node with the stack which holds all the lately visited nodes. After all parents
 * of a node have been visited, it will be removed from the stack.
 *
 * The performance of this algorithm is horrible. It takes around 45 minutes to complete the computation.
 */
public class DepthFirstSearcher {
    public static Stack<KeywordVertex> stack = new Stack<>();
    public static int discoveredLength;

    /**
     * computes all path lengths between the given node and it's parents by recursively calling dfs() method.
     * After the computation, it updates pathLength[] of the given vertex.
     * @param vertex which is given.
     */
    public static void performDFS(KeywordVertex vertex){
        int maxLength = 0;
        DepthFirstSearcher.stack.push(vertex);
        DepthFirstSearcher.discoveredLength = 0;
        vertex.tempLayer = 0;
        for(int i = 0; i < GraphFactory.keywordEntries; i++){
            GraphFactory.keywordArray[i].tempLayer = 0;
        }

        for(int i = 0; i < vertex.parentNum && !vertex.alreadyCalculated; i++){
            for(int j = 0; j < GraphFactory.keywordEntries; j++){
                if(vertex.parent[i].equals(GraphFactory.keywordArray[j].name) && !vertex.parent[i].equals("Mercateo")){
                    DepthFirstSearcher.stack.push(GraphFactory.keywordArray[j]);
                    DepthFirstSearcher.dfs(DepthFirstSearcher.stack);
                    vertex.pathLength[i] = DepthFirstSearcher.discoveredLength + 1;
                    if(maxLength < DepthFirstSearcher.discoveredLength + 1){
                        maxLength = DepthFirstSearcher.discoveredLength + 1;
                    }
                    DepthFirstSearcher.stack.removeAllElements();
                    DepthFirstSearcher.discoveredLength = 0;
                    break;
                }
            }
            for(int q = 0; q < GraphFactory.keywordEntries; q++){
                GraphFactory.keywordArray[q].tempLayer = 0;
            }
        }
        if(vertex.noLayerSet() && !vertex.alreadyCalculated){
            vertex.setLayer(maxLength);
        }
    }

    /**
     * peeks the first vertex from the stack, pushes one of it's parents if it doesn't exist on the stack, then
     * calls itself recursively. When an end is reached, it goes one stage back, checks if there is any parent left,
     * if not, remove that node from the stack.
     * @param stack
     */
    private static void dfs(Stack<KeywordVertex> stack){
        KeywordVertex v = stack.peek();
        for(int i = 0; i < v.parentNum && !v.alreadyCalculated; i++){
            for(int j = 0; j < GraphFactory.keywordEntries; j++){
                if(v.parent[i].equals(GraphFactory.keywordArray[j].name) && !v.parent[i].equals("Mercateo")){
                    if(!isOnStack(GraphFactory.keywordArray[j], DepthFirstSearcher.stack)){
                        DepthFirstSearcher.stack.push(GraphFactory.keywordArray[j]);
                        GraphFactory.keywordArray[j].tempLayer = v.tempLayer + 1;
                        if(DepthFirstSearcher.discoveredLength < GraphFactory.keywordArray[j].tempLayer){
                            DepthFirstSearcher.discoveredLength = GraphFactory.keywordArray[j].tempLayer;
                        }
                        DepthFirstSearcher.dfs(DepthFirstSearcher.stack);
                    }
                    break;
                }
            }
        }
        DepthFirstSearcher.stack.pop();
    }

    private static boolean isOnStack(KeywordVertex vertex, Stack stack){
        if(stack.search(vertex) != -1){
            return true;
        }
        return false;
    }
}
