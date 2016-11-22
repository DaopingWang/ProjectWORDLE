package DataBackend;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by wang.daoping on 11.11.2016.
 */

/**
 * read data from .csv files and hold them in an array of Vertex objects.
 * If needed, .csv output should also be implemented in this class.
 */
public class CSVParser {
    private static String filename;
    public static KeywordVertex[] keywordArray;
    public static ProductVertex[] productArray;
    public static int keywordEntries = 0;
    public static int productEntries = 0;
    public static int maxLayer = 0;

    public static void createGraphFromCSV(String filename) throws IOException{
        CSVParser.keywordArray = new KeywordVertex[42000];
        CSVParser.productArray = new ProductVertex[10000];
        String[] lineBuffer;
        CSVReader reader = new CSVReader(new FileReader(CSVParser.filename), ',', '\"', 1);
        while((lineBuffer = reader.readNext()) != null && CSVParser.keywordEntries < 42000){
            if(CSVParser.keywordEntries == 0){
                CSVParser.keywordArray[CSVParser.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                CSVParser.keywordEntries += 1;
            } else {
                for(int i = 0; i < CSVParser.keywordEntries; i++){
                    if(CSVParser.keywordArray[i].name.equals(lineBuffer[0])){
                        CSVParser.keywordArray[i].setParent(lineBuffer[1]);
                        break;
                    }
                    if(i == CSVParser.keywordEntries - 1){
                        CSVParser.keywordArray[CSVParser.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                        CSVParser.keywordEntries += 1;
                        System.out.println(i + ". Keyword: " + CSVParser.keywordArray[i].name);
                        break;
                    }
                }
            }
        }
        for(int q = 0; q < CSVParser.keywordEntries; q++){
            CSVParser.keywordArray[q].pathLength = new int[CSVParser.keywordArray[q].parentNum];
        }
    }

    public static void createCSVLayersFromGraph(String filepath) throws IOException{
        String filename;
        for(int i = 0; i < CSVParser.maxLayer; i++){
            filename = filepath + "layer" + i + ".csv";
            CSVWriter writer = new CSVWriter(new FileWriter(filename));
            for(int j = 0; j < CSVParser.keywordEntries; j++){
                if(CSVParser.keywordArray[j].layer == i){
                    String lineBuffer = CSVParser.keywordArray[j].name + ",";
                    for(int k = 0; k < CSVParser.keywordArray[j].dominantChildNum && i != CSVParser.maxLayer; k++){
                        lineBuffer += CSVParser.keywordArray[j].dominantChild[k] + ",";
                    }
                    lineBuffer += "EOL";
                    String[] record = lineBuffer.split(",");
                    writer.writeNext(record);
                }
            }
            writer.close();
        }
    }

    public static void createCSVAllFromGraph(String filepath) throws IOException{
        String filename = filepath + "CSVOutput.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(filename));
        String buffer = Integer.toString(CSVParser.keywordEntries) + "," + Integer.toString(CSVParser.maxLayer) + ",EOL";
        String[] rc = buffer.split(",");
        writer.writeNext(rc);
        for(int i = 0; i < CSVParser.keywordEntries;i++){
            String linebuffer = CSVParser.keywordArray[i] + "," + Integer.toString(CSVParser.keywordArray[i].layer) + "," + Integer.toString(CSVParser.keywordArray[i].parentNum) + ",";
            for(int j = 0; j < CSVParser.keywordArray[i].parentNum; j++){
                linebuffer += CSVParser.keywordArray[i].parent[j] + ",";
            }
            for(int j = 0; j < CSVParser.keywordArray[i].parentNum; j++){
                linebuffer += Integer.toString(CSVParser.keywordArray[i].pathLength[j]) + ",";
            }
            linebuffer += "EOL";
            String[] record = linebuffer.split(",");
            writer.writeNext(record);
        }
        writer.close();
    }

    public static void updateWeight(int vertex){
        for(int j = 0; j < CSVParser.keywordArray[vertex].parentNum; j++){
            CSVParser.keywordArray[vertex].weight[j] = Math.pow(0.5, (double) CSVParser.keywordArray[vertex].pathLength[j]);

            //for(int k = 0; k < CSVParser.keywordArray[vertex].pathLength[j]; k++){
            //    CSVParser.keywordArray[vertex].weight[j] += Math.pow(0.5, (double) k);
            //}
            CSVParser.keywordArray[vertex].weight[j] += 0.2 * (double) (CSVParser.keywordArray[vertex].pathLength[j] - 1);
        }
    }

    public static void assignChildren(int vertex){
        for(int i = 0; i < CSVParser.keywordArray[vertex].parentNum; i++){
            for(int j = 0; j < CSVParser.keywordEntries; j++){
                if(CSVParser.keywordArray[vertex].parent[i].equals("Mercateo")){
                    CSVParser.keywordArray[vertex].isRootKeyword = true;
                    break;
                }
                if(CSVParser.keywordArray[vertex].parent[i].equals(CSVParser.keywordArray[j].name) && !CSVParser.keywordArray[j].childExists(CSVParser.keywordArray[vertex].name)){
                    CSVParser.keywordArray[j].setChild(CSVParser.keywordArray[vertex].name);
                    break;
                }
            }
        }
    }

    public static void assignDominantChildren(int vertex){
        for(int i = 0; i < CSVParser.keywordArray[vertex].parentNum; i++){
            if(CSVParser.keywordArray[vertex].pathLength[i] == CSVParser.keywordArray[vertex].layer && CSVParser.findVertexForName(CSVParser.keywordArray[vertex].parent[i]) != null){
                CSVParser.findVertexForName(CSVParser.keywordArray[vertex].parent[i]).setDominantChild(CSVParser.keywordArray[vertex].name);
            }
        }
    }

    public static void setFilename(String file) {
        CSVParser.filename = file;
    }

    public static KeywordVertex findVertexForName(String inputName){
        for(int i = 0; i < CSVParser.keywordEntries; i++){
            if(CSVParser.keywordArray[i].name.equals(inputName)){
                return CSVParser.keywordArray[i];
            }
        }
        return null;
    }

    public static int processPercentage(int i, int count){
        int fivePercent = count / 20;
        if(i % fivePercent == 0){
            return 5 * (i / fivePercent);
        }
        return 0;
    }

    public static void main(String[] args) {
        int testKeyword = 12940;
        CSVParser.setFilename("C:/Users/wang.daoping/Documents/Keyword_Graph.csv");
        String[] content;
        System.out.println("Loading CSV...");

        // Pass the .csv file to createGraphFromCSV
        try{
            CSVParser.createGraphFromCSV(CSVParser.filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("====================================================");
        System.out.println("We have " + CSVParser.keywordEntries + " keywords.");


        System.out.println(testKeyword + ". keyword " + CSVParser.keywordArray[testKeyword].name + " has these parents: ");
        System.out.println();
        for(int i = 0; i < CSVParser.keywordArray[testKeyword].parentNum; i++){
            for(int j = 0; j < CSVParser.keywordEntries; j++){
                if(CSVParser.keywordArray[j].name.equals(CSVParser.keywordArray[testKeyword].parent[i])){
                    System.out.print(j + ". keyword ");
                }
            }
            System.out.print(CSVParser.keywordArray[testKeyword].parent[i]);
            System.out.println();
        }
        System.out.println("====================================================");
        System.out.println();
        System.out.println("Start graph parsing...");
        int percentage;

        // For each iteration, find a vertex's depth and update it's weight.
        for(int i = 0; i < CSVParser.keywordEntries; i++){
            DepthSearcher.findDepthFor(CSVParser.keywordArray[i]);
            CSVParser.updateWeight(i);
            if((percentage = CSVParser.processPercentage(i, CSVParser.keywordEntries)) != 0){
                System.out.println("Calculating depth... " + percentage + "% done... Be patient");
            }
        }
        System.out.println("====================================================");
        System.out.println("Depth calculation done");
        System.out.println("====================================================");
        System.out.println();

        // After CSV reading, depth calculation and weight updates, start finding out the root keywords and assign children.
        for(int i = 0; i < CSVParser.keywordEntries; i++){
            CSVParser.assignChildren(i);
            CSVParser.assignDominantChildren(i);
            if((percentage = CSVParser.processPercentage(i, CSVParser.keywordEntries)) != 0){
                System.out.println("Assigning children... " + percentage + "% done");
            }
        }

        System.out.println("And these lengths: ");
        for(int i = 0; i < CSVParser.keywordArray[testKeyword].parentNum; i++){
            System.out.print(CSVParser.keywordArray[testKeyword].pathLength[i] + " with weight == ");
            System.out.print(CSVParser.keywordArray[testKeyword].weight[i]);
            System.out.println();
        }

        System.out.println("Root keywords are :");
        for(int i= 0; i < CSVParser.keywordEntries; i++){
            if(CSVParser.keywordArray[i].isRootKeyword){
                System.out.print(CSVParser.keywordArray[i].name + " in layer " + CSVParser.keywordArray[i].layer + " with dominant children : ");
                for(int j = 0; j < CSVParser.keywordArray[i].dominantChildNum; j++){
                    System.out.print(CSVParser.keywordArray[i].dominantChild[j] );
                }
                System.out.println();
            }
        }

        try{
            CSVParser.createCSVAllFromGraph("C:/Users/wang.daoping/Documents/");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}
