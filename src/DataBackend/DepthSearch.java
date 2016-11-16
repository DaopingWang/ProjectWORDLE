package DataBackend;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Wang.Daoping on 16.11.2016.
 */
public class DepthSearch {
    public static void findDepthFor(Vertex vertex){
        Queue<Vertex> queue = new LinkedList<>();
        int discoveredDepth;

        Vertex v = vertex;

        for(int i = 0; i < vertex.parentNum; i++){
            discoveredDepth = 0;
            for(int j = 0; j < CSVFileIO.keywordEntries; j++){
                if(vertex.parent[i].equals(CSVFileIO.keywordArray[j].name)){    // This is a must!
                    v = CSVFileIO.keywordArray[j];
                    break;
                }
            }
            for(int z = 0; z < CSVFileIO.keywordEntries; z++){
                CSVFileIO.keywordArray[z].layer = 0;
            }
            queue.offer(v);
            while((v = queue.poll()) != null){
                for(int k = 0; k < v.parentNum; k++){
                    for(int l = 0; l < CSVFileIO.keywordEntries; l++){
                        if(v.parent[k].equals(CSVFileIO.keywordArray[l].name)){
                            queue.offer(CSVFileIO.keywordArray[l]);
                            if(CSVFileIO.keywordArray[l].layer <= v.layer){
                                CSVFileIO.keywordArray[l].layer = v.layer + 1;
                            }
                            break;
                        }
                        if(discoveredDepth < v.layer){
                            discoveredDepth = v.layer;
                        }
                    }
                }
            }
            vertex.pathLength[i] = discoveredDepth + 1;
        }
    }
}
