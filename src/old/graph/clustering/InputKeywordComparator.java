package old.graph.clustering;

import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 24.11.2016.
 */
public class InputKeywordComparator {
    public static double compareStrings(String str1, String str2){
        ArrayList<String> pairs1 = wordLetterPairs(str1.toUpperCase());
        ArrayList<String> pairs2 = wordLetterPairs(str2.toUpperCase());
        int intersection = 0;
        int union = pairs1.size() + pairs2.size();
        for(int i = 0; i < pairs1.size(); i++){
            Object pair1 = pairs1.get(i);
            for(int j = 0; j < pairs2.size(); j++){
                Object pair2 = pairs2.get(j);
                if(pair1.equals(pair2)){
                    intersection++;
                    pairs2.remove(j);
                    break;
                }
            }
        }
        return (2.0 * intersection) / union;
    }

    private static ArrayList<String> wordLetterPairs(String str){
        ArrayList<String> allPairs = new ArrayList<>();
        String[] words = str.split("\\s");
        for(int i = 0; i < words.length; i++){
            String[] pairsInWord = letterPairs(words[i], str);
            if(pairsInWord == null) continue;
            for(int j = 0; j < pairsInWord.length; j++){
                allPairs.add(pairsInWord[j]);
            }
        }
        return allPairs;
    }

    private static String[] letterPairs(String str, String debug){
        try{
            int numPairs = str.length() - 1;
            if(numPairs >= 0){
                String[] pairs = new String[numPairs];
                for(int i = 0; i < numPairs; i++){
                    pairs[i] = str.substring(i, i+2);
                }
                return pairs;
            }
            return null;
        } catch (NegativeArraySizeException e){
            System.out.println("BOOM " + debug);
            e.printStackTrace();
            return null;
        }
    }
}
