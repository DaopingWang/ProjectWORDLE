package DataBackend;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by wang.daoping on 11.11.2016.
 */
public class CSVFileIO {
    private static String filename;
    public static KeywordVertex[] keywordArray;
    public static ProductVertex[] productArray;
    public static int keywordEntries = 0;
    public static int productEntries = 0;

    public static void createGraphFromCSV(String filename) throws IOException{
        CSVFileIO.keywordArray = new KeywordVertex[10000];
        String[] lineBuffer;
        CSVReader reader = new CSVReader(new FileReader(CSVFileIO.filename), ',', '\'', 1);
        while((lineBuffer = reader.readNext()) != null && CSVFileIO.keywordEntries < 1000){
            if(CSVFileIO.keywordEntries == 0){
                CSVFileIO.keywordArray[CSVFileIO.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                CSVFileIO.keywordEntries += 1;
                //System.out.println("Iteration " + CSVFileIO.keywordEntries);
            } else {
                for(int i = 0; i < CSVFileIO.keywordEntries; i++){
                    if(CSVFileIO.keywordArray[i].name.equals(lineBuffer[0])){
                        CSVFileIO.keywordArray[i].setParent(lineBuffer[1]);
                        break;
                    }
                    if(i == CSVFileIO.keywordEntries - 1){
                        CSVFileIO.keywordArray[CSVFileIO.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[2]);
                        CSVFileIO.keywordEntries += 1;
                        //System.out.println("Iteration " + CSVFileIO.keywordEntries);
                        break;
                    }
                }
            }
        }



    }
/*
    private static String[] getLineFromCSV(int lineNum) throws IOException{
        CSVReader reader = new CSVReader(new FileReader(CSVFileIO.filename), ',', '\'', lineNum);
        String[] nextLine;
        if ((nextLine = reader.readNext()) != null) {
            return nextLine;
        } else {
            return null;
        }
    }
*/
    public static void setFilename(String file) {
        CSVFileIO.filename = file;
    }

    public static String getFilename() {
        return CSVFileIO.filename;
    }

    public static void main(String[] args) {
        CSVFileIO.setFilename("C:/Users/wang.daoping/Documents/Keyword_Graph.csv");
        String[] content;
        System.out.println("Loading CSV...");
        try{
            CSVFileIO.createGraphFromCSV(CSVFileIO.filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("We have " + CSVFileIO.keywordEntries + " keywords.");
        for(int i = 0; i < 1000; i++){
            System.out.println(i + ". Keyword: " + CSVFileIO.keywordArray[i].name);
        }
        System.out.println(CSVFileIO.keywordArray[506].name + " has these parents: ");
        for(int i = 0; i < CSVFileIO.keywordArray[506].parentNum; i++){
            System.out.println(CSVFileIO.keywordArray[506].parent[i]);
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
