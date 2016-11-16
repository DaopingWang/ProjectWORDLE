package DataBackend;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by wang.daoping on 11.11.2016.
 */

/**
 * read data from .csv files and hold them in an array of Vertex objects.
 * If needed, .csv output should also be implemented in this class.
 */
public class CSVFileIO {
    private static String filename;
    public static KeywordVertex[] keywordArray;
    public static ProductVertex[] productArray;
    public static int keywordEntries = 0;
    public static int productEntries = 0;

    public static void createGraphFromCSV(String filename) throws IOException{
        CSVFileIO.keywordArray = new KeywordVertex[20000];
        String[] lineBuffer;
        CSVReader reader = new CSVReader(new FileReader(CSVFileIO.filename), ',', '\"', 1);
        while((lineBuffer = reader.readNext()) != null && CSVFileIO.keywordEntries < 20000){
            if(CSVFileIO.keywordEntries == 0){
                CSVFileIO.keywordArray[CSVFileIO.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                CSVFileIO.keywordEntries += 1;
            } else {
                for(int i = 0; i < CSVFileIO.keywordEntries; i++){
                    if(CSVFileIO.keywordArray[i].name.equals(lineBuffer[0])){
                        CSVFileIO.keywordArray[i].setParent(lineBuffer[1]);
                        break;
                    }
                    if(i == CSVFileIO.keywordEntries - 1){
                        CSVFileIO.keywordArray[CSVFileIO.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                        CSVFileIO.keywordEntries += 1;
                        //System.out.println(i + ". Keyword: " + CSVFileIO.keywordArray[i].name);
                        break;
                    }
                }
            }
        }
        for(int q = 0; q < CSVFileIO.keywordEntries; q++){
            CSVFileIO.keywordArray[q].pathLength = new int[CSVFileIO.keywordArray[q].parentNum];
        }
    }

    public static void setFilename(String file) {
        CSVFileIO.filename = file;
    }

    public static String getFilename() {
        return CSVFileIO.filename;
    }

    public static void main(String[] args) {
        int testKeyword = 10000;
        CSVFileIO.setFilename("C:/Users/wang.daoping/Documents/Keyword_Graph.csv");
        String[] content;
        System.out.println("Loading CSV...");
        try{
            CSVFileIO.createGraphFromCSV(CSVFileIO.filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("We have " + CSVFileIO.keywordEntries + " keywords.");


        System.out.println(CSVFileIO.keywordArray[testKeyword].name + " has these parents: ");
        for(int i = 0; i < CSVFileIO.keywordArray[testKeyword].parentNum; i++){
            System.out.println(CSVFileIO.keywordArray[testKeyword].parent[i]);
        }
        System.out.println("And these lengths:");
        DepthSearch.findDepthFor(CSVFileIO.keywordArray[testKeyword]);
        for(int i = 0; i < CSVFileIO.keywordArray[testKeyword].parentNum; i++){
            System.out.println(CSVFileIO.keywordArray[testKeyword].pathLength[i]);
        }
        /*
        for(int i = 0; i<100 ; i++) {
            try {
                content = CSVFileIO.getLineFromCSV(i);
                if(content == null) {
                    break;
                } else {
                    for(int j = 0; j < content.length; j++){
                        System.out.print(content[j] + ",");
                    }
                    System.out.println();
                }
            } catch (IOException e){
                e.getStackTrace();
            }
        }
        */

    }

}
