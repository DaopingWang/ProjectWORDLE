package graph.clustering;

import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 07.12.2016.
 */
public class Utility {
    public static KeywordVertex findVertexForName(String inputName, ArrayList<KeywordVertex> inputList){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return inputList.get(i);
            }
        }
        //System.out.println("ERROR: VERTEX NOT FOUND FOR " + inputName);
        return null;
    }

    public static RootKeywordVertex findVertexForName(String inputName){
        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            if(GraphFactory.rootKeywordVertices.get(i).name.equals(inputName)){
                return GraphFactory.rootKeywordVertices.get(i);
            }
        }
        //System.out.println("ERROR: VERTEX NOT FOUND FOR " + inputName);
        return null;
    }

    public static boolean isRootKeyword(String inputName){
        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            if(inputName.equals(GraphFactory.rootKeywordVertices.get(i).name)){
                return true;
            }
        }
        return false;
    }

    public static int findIndexForName(String inputName, ArrayList<KeywordVertex> inputList){
        for(int i = 0; i < inputList.size(); i++){
            if(inputList.get(i).name.equals(inputName)){
                return i;
            }
        }
        //System.out.println("ERROR: INDEX NOT FOUND FOR " + inputName);
        return -1;
    }

    public static int findIndexForName(String inputName){
        for(int i = 0; i < GraphFactory.rootKeywordVertices.size(); i++){
            if(GraphFactory.rootKeywordVertices.get(i).name.equals(inputName)){
                return i;
            }
        }
        return -1;
    }

    static int processPercentage(int i, int count){
        int fivePercent = count / 20;
        if(i % fivePercent == 0){
            return 5 * (i / fivePercent);
        }
        return 0;
    }
}
