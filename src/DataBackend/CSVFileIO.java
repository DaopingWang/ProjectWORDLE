package DataBackend;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created by wang.daoping on 11.11.2016.
 */
public class CSVFileIO {
    private static String filename = "H:/test.csv";
    public static KeywordVertex[] keywordArray;
    public static ProductVertex[] productArray;
    public static int keywordEntries = 0;
    public static int productEntries = 0;

    public static void createGraphFromCSV(String filename) throws IOException{
        int index = 0;
        String[] lineBuffer;
        CSVReader reader = new CSVReader(new FileReader(CSVFileIO.filename), ',', '\'', 1);
        while((lineBuffer = reader.readNext()) != null){
            if(CSVFileIO.keywordEntries == 0){
                keywordArray[CSVFileIO.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[2]);
            } else {
                for( )
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
        CSVFileIO.setFilename("H:/Keyword_Graph.csv");
        String[] content;
        System.out.println("Loading CSV...");
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

    }

}
