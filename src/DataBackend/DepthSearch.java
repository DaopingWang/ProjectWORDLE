package DataBackend;

import java.util.Stack;

/**
 * Created by Wang.Daoping on 16.11.2016.
 */
public class DepthSearch {
    public static void findDepthFor(Vertex vertex){
        Stack<Vertex> queue = new Stack<>();
        String[] discovered = new String[CSVFileIO.keywordEntries];
        int discoveredDepth = 1;
        int oldLayer = 0;
        Vertex v = vertex;

        for(int i = 0; i < vertex.parentNum; i++){
            discoveredDepth = 1;
            for(int j = 0; j < CSVFileIO.keywordEntries; j++){
                if(vertex.parent[i].equals(CSVFileIO.keywordArray[j].name)){    // This is a must!
                    v = CSVFileIO.keywordArray[j];
                }
            }
            for(int z = 0; z < CSVFileIO.keywordEntries; z++){
                CSVFileIO.keywordArray[z].layer = 0;
                for(int y = 0; y < CSVFileIO.keywordArray[z].parentNum; y++){
                    CSVFileIO.keywordArray[z].pathLength[y] = 0;
                }
            }

            queue.push(v);
            while(!queue.empty()){
                v = queue.pop();
                for(int k = 0; k < v.parentNum; k++){
                    for(int l = 0; l < CSVFileIO.keywordEntries; l++){
                        if(v.parent[k].equals(CSVFileIO.keywordArray[l].name)){
                            queue.push(CSVFileIO.keywordArray[l]);
                            CSVFileIO.keywordArray[l].layer = oldLayer + 1;
                            break;
                        }
                        if(vertex.pathLength[i] < discoveredDepth){
                            vertex.pathLength[i] = discoveredDepth;
                        }
                    }
                }
            }
        }
        ////////////////////////////////////////////////
        for(int i = 0; i < vertex.parentNum; i++){
            Vertex v = vertex;
            queue.push(v);
            while(!queue.empty()){
                v = queue.pop();
                for(int j = 0; j < CSVFileIO.keywordEntries; j++){
                    for(int k = 0; k < CSVFileIO.keywordEntries; k++){

                    }
                    if(v.parent[i].equals(CSVFileIO.keywordArray[j].name)){
                        queue.push(CSVFileIO.keywordArray[j]);
                        discoveredDepth += 1;
                        break;
                    }
                    if(vertex.pathLength[i] < discoveredDepth){
                        vertex.pathLength[i] = discoveredDepth;
                    }
                }
            }
            discoveredDepth = 0;
        }
        /////////////////////////////////////////////

        queue.push(v);
        while(!queue.empty()){
            v = queue.pop();
            if(discoveredDepth == 0){
                for(int i = 0; i < v.parentNum; i++){
                    discoveredDepth += 1;
                    for(int j = 0; j < CSVFileIO.keywordEntries; j++){
                        if(v.parent[i].equals(CSVFileIO.keywordArray[j].name)){
                            queue.push(CSVFileIO.keywordArray[j]);
                            break;
                        }
                        if(vertex.pathLength[parentIndex] < discoveredDepth){
                            vertex.pathLength[parentIndex] = discoveredDepth;
                        }
                        parentIndex += 1;
                        discoveredDepth = 0;
                    }
                }
            }
        }
    }
}
