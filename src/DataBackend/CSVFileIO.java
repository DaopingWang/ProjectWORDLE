package DataBackend;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by wang.daoping on 11.11.2016.
 */
public class CSVFileIO {
    /*
    public static List<String[]> getListFromCSV(String filename) throws IOException{
        CSVReader reader = new CSVReader(new FileReader(filename), ',', '\'',1);

        return reader.readAll();
    }
    */

    public static String[] getLineFromCSV(String filename, int lineNum) throws IOException{
        CSVReader reader = new CSVReader(new FileReader(filename), ',', '\'', lineNum);
        String[] nextLine;
        if ((nextLine = reader.readNext()) != null) {
            return nextLine;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        String CSVFile = "H:/test.csv";
        String[] content;
        System.out.println("Loading CSV...");
        for(int i = 0; ; i++) {
            try {
                content = CSVFileIO.getLineFromCSV(CSVFile, i);
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
