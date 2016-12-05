package graph.clustering;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import graph.clustering.vertex.Edge;
import graph.clustering.vertex.KeywordVertex;
import graph.clustering.vertex.RootKeywordVertex;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class GraphFactory {
    public static ArrayList<KeywordVertex> keywordVertices;
    public static ArrayList<RootKeywordVertex> rootKeywordVertices;
    public static int layerNum = 0;

    public static void readGraphFromParsedCSV(String filename) throws IOException{
        keywordVertices = new ArrayList<>();
        rootKeywordVertices = new ArrayList<>();
        String[] lineBuffer = null;
        int index;

        System.out.println("Loading parsed .csv...");
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"), ';', '\"', 0);
        try {
            while ((lineBuffer = reader.readNext()) != null) {
                switch (Integer.parseInt(lineBuffer[1])) {
                    case 0:
                        RootKeywordVertex rk = new RootKeywordVertex(lineBuffer[0], 0);
                        rootKeywordVertices.add(rk);
                        break;

                    default:
                        KeywordVertex kv = new KeywordVertex(lineBuffer[0], Integer.parseInt(lineBuffer[1]));
                        keywordVertices.add(kv);
                        index = keywordVertices.size() - 1;
                        for (int i = 3; i < Integer.parseInt(lineBuffer[2]) + 3; i++) {
                            Edge edge = new Edge(lineBuffer[i], Double.parseDouble(lineBuffer[i + Integer.parseInt(lineBuffer[2])]));
                            keywordVertices.get(index).edgeList.add(edge);
                        }
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("ERROR " + lineBuffer[0]);
        }

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).pathLengthVector = new SparseDoubleMatrix1D(keywordVertices.size() + rootKeywordVertices.size());
        }
        //setDirectSubordinates();

        DijkstraPathFinder.initSparseVectors(keywordVertices, rootKeywordVertices);
        int percentage;
        System.out.println("Start dijkstra...");
        for(int i = 0; i < keywordVertices.size(); i++){
            DijkstraPathFinder.findSingleSourceShortestPath(keywordVertices.get(i), keywordVertices, rootKeywordVertices);

            percentage = processPercentage(i, keywordVertices.size());
            if(percentage != 0){
                System.out.println("Dididi dijkstraing... " + Integer.toString(percentage) + "% done.");
            }
        }
    }

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

        for(int i = 0; i < keywordVertices.size(); i++){
            keywordVertices.get(i).pathLengthVector = new SparseDoubleMatrix1D(keywordVertices.size() + rootKeywordVertices.size());
        }
        setDirectSubordinates();
        GraphParser.calculateLayers();
        GraphParser.calculateEdgesWeights();

        DijkstraPathFinder.initSparseVectors(keywordVertices, rootKeywordVertices);
        int percentage;
        System.out.println("Start dijkstra...");
        for(int i = 0; i < keywordVertices.size(); i++){
            DijkstraPathFinder.findSingleSourceShortestPath(keywordVertices.get(i), keywordVertices, rootKeywordVertices);

            percentage = processPercentage(i, keywordVertices.size());
            if(percentage != 0){
                System.out.println("Dididi dijkstraing... " + Integer.toString(percentage) + "% done.");
            }
        }
    }

    public static void createParsedCSVFromGraph(String filepath) throws IOException{
        String filename = filepath + "ParsedCSV.csv";
        String lineBuffer;
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1252"),';');

        for(int i = 0; i < rootKeywordVertices.size(); i++){
            lineBuffer = rootKeywordVertices.get(i).name + ";" + rootKeywordVertices.get(i).layer;
            String[] record = lineBuffer.split(";");
            writer.writeNext(record);
        }

        for(int i = 0; i < keywordVertices.size(); i++){
            lineBuffer = keywordVertices.get(i).name + ";" + Integer.toString(keywordVertices.get(i).layer) + ";" + Integer.toString(keywordVertices.get(i).edgeList.size());
            for(int j = 0; j < keywordVertices.get(i).edgeList.size(); j++){
                lineBuffer += ";" + keywordVertices.get(i).edgeList.get(j).getTargetVertexName();
            }
            for(int j = 0; j < keywordVertices.get(i).edgeList.size(); j++){
                lineBuffer += ";" + Double.toString(keywordVertices.get(i).edgeList.get(j).getEdgeWeight());
            }

            String[] record = lineBuffer.split(";");
            writer.writeNext(record);
        }
        writer.close();
    }

    public static void createCSVLayersFromGraph(String filepath) throws IOException{
        String filename;
        String lineBuffer;

        for(int i = 0; i < layerNum; i++){
            filename = filepath + "layer_" + i + ".csv";
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1252"),';');
            switch (i){
                case 0:
                    for(int j = 0; j < rootKeywordVertices.size(); j++){
                        lineBuffer = rootKeywordVertices.get(j).name;
                        String[] record = lineBuffer.split(";");
                        writer.writeNext(record);
                    }
                    writer.close();
                    break;

                default:
                    for(int j = 0; j < keywordVertices.size(); j++){
                        if(keywordVertices.get(j).layer == i){
                            lineBuffer = keywordVertices.get(j).name + ";";
                            for(int k = 0; k < keywordVertices.get(j).edgeList.size(); k++){
                                lineBuffer += keywordVertices.get(j).edgeList.get(k).getTargetVertexName() + ";";
                            }
                            lineBuffer += "EOL;";
                            String[] record = lineBuffer.split(";");
                            writer.writeNext(record);
                        }
                    }
                    writer.close();
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
        //System.out.println("ERROR: VERTEX NOT FOUND FOR " + inputName);
        return null;
    }

    public static boolean isRootKeyword(String inputName){
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            if(inputName.equals(rootKeywordVertices.get(i).name)){
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
        for(int i = 0; i < rootKeywordVertices.size(); i++){
            if(rootKeywordVertices.get(i).name.equals(inputName)){
                return i;
            }
        }
        return -1;
    }

    public static void setDirectSubordinates(){
        int index;
        int percentage;

        for(int i = 0; i < keywordVertices.size(); i++){
            for(int j = 0; j < keywordVertices.get(i).edgeList.size(); j++){
                if((index = findIndexForName(keywordVertices.get(i).edgeList.get(j).getTargetVertexName(), keywordVertices)) != -1  && !keywordVertices.get(index).subordinateList.contains(keywordVertices.get(i).name)){
                    keywordVertices.get(findIndexForName(keywordVertices.get(i).edgeList.get(j).getTargetVertexName(), keywordVertices)).subordinateList.add(keywordVertices.get(i).name);
                } else if((index = findIndexForName(keywordVertices.get(i).edgeList.get(j).getTargetVertexName())) != -1 && !rootKeywordVertices.get(index).subordinateList.contains(keywordVertices.get(i).name)){
                    rootKeywordVertices.get(index).subordinateList.add(keywordVertices.get(i).name);
                }
            }
            percentage = processPercentage(i, keywordVertices.size());
            if(percentage != 0){
                System.out.println("Assigning direct subordinates. " + Integer.toString(percentage) + "% done.");
            }
        }
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
