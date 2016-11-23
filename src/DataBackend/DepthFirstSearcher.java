package DataBackend;

import java.util.Stack;

/**
 * Created by Wang.Daoping on 23.11.2016.
 */
public class DepthFirstSearcher {
    public static Stack<KeywordVertex> stack = new Stack<>();
    public static int discoveredLength;

    public static void performDFS(KeywordVertex vertex){
        int maxLength = 0;
        DepthFirstSearcher.stack.push(vertex);
        DepthFirstSearcher.discoveredLength = 0;
        vertex.tempLayer = 0;
        for(int i = 0; i < CSVParser.keywordEntries; i++){
            CSVParser.keywordArray[i].tempLayer = 0;
        }

        for(int i = 0; i < vertex.parentNum; i++){
            for(int j = 0; j < CSVParser.keywordEntries; j++){
                if(vertex.parent[i].equals(CSVParser.keywordArray[j].name) && !vertex.parent[i].equals("Mercateo")){
                    DepthFirstSearcher.dfs(CSVParser.keywordArray[j]);
                    vertex.pathLength[i] = DepthFirstSearcher.discoveredLength + 1;
                    if(maxLength < DepthFirstSearcher.discoveredLength + 1){
                        maxLength = DepthFirstSearcher.discoveredLength + 1;
                    }
                    DepthFirstSearcher.stack.removeAllElements();
                    DepthFirstSearcher.discoveredLength = 0;
                    break;
                }
            }
        }

        if(vertex.noLayerSet()){
            vertex.setLayer(maxLength);
        }
    }

    private static void dfs(KeywordVertex v){
        for(int i = 0; i < v.parentNum; i++){
            for(int j = 0; j < CSVParser.keywordEntries; j++){
                if(v.parent[i].equals(CSVParser.keywordArray[j].name) && !v.parent[i].equals("Mercateo")){
                    if(!isOnStack(CSVParser.keywordArray[j], DepthFirstSearcher.stack)){
                        DepthFirstSearcher.stack.push(CSVParser.keywordArray[j]);
                        CSVParser.keywordArray[j].tempLayer = v.tempLayer + 1;
                        if(DepthFirstSearcher.discoveredLength < CSVParser.keywordArray[j].tempLayer){
                            DepthFirstSearcher.discoveredLength = CSVParser.keywordArray[j].tempLayer;
                        }
                        DepthFirstSearcher.dfs(CSVParser.keywordArray[j]);
                    }
                    break;
                } else if(v.parent[i].equals(CSVParser.keywordArray[j].name) && v.parent[i].equals("Mercateo")){
                    break;
                }
            }
        }

    }

    private static boolean isOnStack(KeywordVertex vertex, Stack stack){
        if(stack.search(vertex) != -1){
            return true;
        }
        return false;
    }
}
