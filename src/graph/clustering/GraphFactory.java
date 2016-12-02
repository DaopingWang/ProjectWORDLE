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
        int index;

        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"), ',', '\"', 1);
        while((lineBuffer = reader.readNext()) != null){
            System.out.println(lineBuffer[0] + " " + lineBuffer[1]);
            switch (lineBuffer[1]){
                case "Mercateo":
                    if(!rootEntryExists(rootKeywordVertices, lineBuffer[0])){
                        RootKeywordVertex rkv = new RootKeywordVertex(lineBuffer[0], 0);
                        rootKeywordVertices.add(rkv);
                    }
                    break;

                default:
                    if(entryExists(keywordVertices, lineBuffer[0]) == -1){
                        KeywordVertex kv = new KeywordVertex(lineBuffer[0]);
                        keywordVertices.add(kv);
                        keywordVertices.get(keywordVertices.size() - 1).createNewEdge(lineBuffer[1]);
                    } else if((index = entryExists(keywordVertices, lineBuffer[0])) != -1 && !keywordVertices.get(index).edgeExist(lineBuffer[1])){
                        try {
                            keywordVertices.get(findIndexForName(lineBuffer[0], keywordVertices)).createNewEdge(lineBuffer[1]);
                        } catch (NullPointerException e){
                            System.out.println("ERROR: INDEX OUT OF BOUND");
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

    public static int findIndexForName(String inputName, ArrayList<KeywordVertex> inputList){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return i;
            }
        }
        System.out.println("ERROR: INDEX NOT FOUND FOR " + inputName);
        return -1;
    }

    private static boolean rootEntryExists(ArrayList<RootKeywordVertex> inputList, String inputName){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return true;
            }
        }
        return false;
    }

    private static int entryExists(ArrayList<KeywordVertex> inputList, String inputName){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return i;
            }
        }
        return -1;
    }

    private static int processPercentage(int i, int count){
        int fivePercent = count / 20;
        if(i % fivePercent == 0){
            return 5 * (i / fivePercent);
        }
        return 0;
    }
}
