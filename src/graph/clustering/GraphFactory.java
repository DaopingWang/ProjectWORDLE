package graph.clustering;

import com.opencsv.CSVReader;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class GraphFactory {
    public static ArrayList<KeywordVertex> keywordVertices;
    public static ArrayList<RootKeywordVertex> rootKeywordVertices;

    public static void parseGraphFromRawCSV(String filename) throws IOException{
        keywordVertices = new ArrayList<>();
        rootKeywordVertices = new ArrayList<>();
        String[] lineBuffer;
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"), ',', '\"', 1);
        while((lineBuffer = reader.readNext()) != null){
            switch (lineBuffer[1]){
                case "Mercateo":
                    if(!rootEntryExists(rootKeywordVertices, lineBuffer[0])){
                        RootKeywordVertex rkv = new RootKeywordVertex(lineBuffer[0], 0);
                        rootKeywordVertices.add(rkv);
                    }
                    break;

                default:
                    if(!entryExists(keywordVertices, lineBuffer[0], 0)){
                        KeywordVertex kv = new KeywordVertex(lineBuffer[0]);
                        keywordVertices.add(kv);
                        keywordVertices.get(keywordVertices.size() - 1).createNewEdge(lineBuffer[1]);
                    } else if(entryExists(keywordVertices, lineBuffer[0], 0) && !entryExists(keywordVertices, lineBuffer[1], 1)){
                        try {
                            keywordVertices.get(keywordVertices.indexOf(findVertexForName(lineBuffer[0], keywordVertices))).createNewEdge(lineBuffer[1]);
                        } catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    public static KeywordVertex findVertexForName(String inputName, ArrayList<KeywordVertex> inputList){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return inputList.get(i);
            }
        }
        System.out.println("ERROR: VERTEX NOT FOUND FOR " + inputName);
        return null;
    }

    private static boolean rootEntryExists(ArrayList<RootKeywordVertex> inputList, String inputName){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return true;
            }
        }
        return false;
    }

    private static boolean entryExists(ArrayList<KeywordVertex> inputList, String inputName, int mode){
        switch (mode){
            case 0:
                for(int i = 0; i < inputList.size(); i++){
                    if(inputList.get(i).name.equals(inputName)){
                        return true;
                    }
                }
                return false;

            case 1:
                for(int i = 0; i < inputList.size(); i++){
                    for(int j = 0; j < inputList.get(i).edgeList.size(); j++){
                        if(inputList.get(i).edgeList.get(j).getTargetVertexName().equals(inputName)){
                            return true;
                        }
                    }
                }
                return false;

            default:
                System.out.println("ERROR: UNKNOWN MODE");
                return false;
        }
    }
}
