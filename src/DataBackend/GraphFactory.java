package DataBackend;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by wang.daoping on 11.11.2016.
 */

/**
 * read data from .csv files and hold them in an array of Vertex objects.
 * If needed, .csv output should also be implemented in this class.
 */
public class GraphFactory {
    private static String rawCSVFilename;
    private static String parsedCSVFilepath;
    public static KeywordVertex[] keywordArray;
    public static ProductVertex[] productArray;
    public static int keywordEntries = 0;
    public static int productEntries = 0;
    public static int maxLayer = 0;

    /**
     * reads data from a raw .csv file, parses them by creating Vertex objects which hold the parsed information.
     * @param filename is the file path.
     * @throws IOException If file cannot be created.
     */
    public static void createGraphFromRawCSV(String filename) throws IOException{
        GraphFactory.keywordArray = new KeywordVertex[42000];
        GraphFactory.productArray = new ProductVertex[10000];
        String[] lineBuffer;
        int percentage;
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"), ',', '\"', 1);
        while((lineBuffer = reader.readNext()) != null && GraphFactory.keywordEntries < 42000){
            if(GraphFactory.keywordEntries == 0){
                GraphFactory.keywordArray[GraphFactory.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                GraphFactory.keywordEntries += 1;
            } else {
                for(int i = 0; i < GraphFactory.keywordEntries; i++){
                    if(GraphFactory.keywordArray[i].name.equals(lineBuffer[0])){
                        GraphFactory.keywordArray[i].setParent(lineBuffer[1]);
                        break;
                    }
                    if(i == GraphFactory.keywordEntries - 1){
                        GraphFactory.keywordArray[GraphFactory.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                        GraphFactory.keywordEntries += 1;
                        System.out.println(i + ". Keyword: " + GraphFactory.keywordArray[i].name);
                        break;
                    }
                }
            }
        }
        for(int q = 0; q < GraphFactory.keywordEntries; q++){
            GraphFactory.keywordArray[q].pathLength = new int[GraphFactory.keywordArray[q].parentNum];
        }

        System.out.println("Start graph parsing...");
        // For each iteration, find a vertex's depth and update it's weight.
        for(int i = 0; i < GraphFactory.keywordEntries; i++){
            //BreadthFirstSearcher.findDepthFor(GraphFactory.keywordArray[i]);                 //BFS
            DepthFirstSearcher.performDFS(GraphFactory.keywordArray[i]);                       //DFS
            GraphFactory.updateWeight(i);
            if((percentage = GraphFactory.processPercentage(i, GraphFactory.keywordEntries)) != 0){
                System.out.println("Calculating depth... " + percentage + "% done... Be patient");
            }
        }
        System.out.println("====================================================");
        System.out.println("Depth calculation done");
        System.out.println("====================================================");
    }

    public static void debugKeywordFromCSV(String filename, String vertexName, String debugMode) throws IOException{
        GraphFactory.keywordArray = new KeywordVertex[42000];
        GraphFactory.productArray = new ProductVertex[10000];
        String[] lineBuffer;
        CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filename), "Cp1252"), ',', '\"', 1);
        while((lineBuffer = reader.readNext()) != null && GraphFactory.keywordEntries < 42000){
            if(GraphFactory.keywordEntries == 0){
                GraphFactory.keywordArray[GraphFactory.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                GraphFactory.keywordEntries += 1;
            } else {
                for(int i = 0; i < GraphFactory.keywordEntries; i++){
                    if(GraphFactory.keywordArray[i].name.equals(lineBuffer[0])){
                        GraphFactory.keywordArray[i].setParent(lineBuffer[1]);
                        break;
                    }
                    if(i == GraphFactory.keywordEntries - 1){
                        GraphFactory.keywordArray[GraphFactory.keywordEntries] = new KeywordVertex(lineBuffer[0], lineBuffer[1]);
                        GraphFactory.keywordEntries += 1;
                        //System.out.println(i + ". Keyword: " + GraphFactory.keywordArray[i].name);
                        break;
                    }
                }
            }
        }
        for(int q = 0; q < GraphFactory.keywordEntries; q++){
            GraphFactory.keywordArray[q].pathLength = new int[GraphFactory.keywordArray[q].parentNum];
        }

        for(int i = 0; i < GraphFactory.keywordEntries; i++){
            if(vertexName.equals(GraphFactory.keywordArray[i].name)){
                switch (debugMode){
                    case "BFS":
                        BreadthFirstSearcher.findDepthFor(GraphFactory.keywordArray[i]);

                        break;
                    case "DFS":
                        DepthFirstSearcher.performDFS(GraphFactory.keywordArray[i]);
                        break;
                }
            }
        }
    }

    /**
     * reads data from a .csv file parsed by CreateCSVAllFromGraph method and creates objects.
     * @param filepath is the file path.
     * @throws IOException if file not found.
     */
    public static void createGraphFromParsedCSV(String filepath) throws IOException{
        String[] lineBuffer;
        String parentFile = filepath + "_parents.csv";
        String childrenFile = filepath + "_children.csv";
        GraphFactory.keywordArray = new KeywordVertex[42000];
        CSVReader parentReader = new CSVReader(new InputStreamReader(new FileInputStream(parentFile), "Cp1252"), ';', '\"', 0);
        CSVReader childrenReader = new CSVReader(new InputStreamReader(new FileInputStream(childrenFile), "Cp1252"), ';', '\"', 0);
        lineBuffer = parentReader.readNext();
        GraphFactory.keywordEntries = Integer.parseInt(lineBuffer[0]);
        GraphFactory.maxLayer = Integer.parseInt(lineBuffer[1]);
        GraphFactory.keywordArray = new KeywordVertex[GraphFactory.keywordEntries];
        int i = 0;

        System.out.println("Start reading from CSVAll...");
        while((lineBuffer = parentReader.readNext()) != null){
            GraphFactory.keywordArray[i] = new KeywordVertex();
            GraphFactory.keywordArray[i].setName(lineBuffer[0]);
            GraphFactory.keywordArray[i].setLayer(Integer.parseInt(lineBuffer[1]));
            GraphFactory.keywordArray[i].parentNum = Integer.parseInt(lineBuffer[2]);
            GraphFactory.keywordArray[i].pathLength = new int[GraphFactory.keywordArray[i].parentNum];
            for(int j = 3; j < GraphFactory.keywordArray[i].parentNum + 3; j++){
                GraphFactory.keywordArray[i].parent[j - 3] = lineBuffer[j];
            }
            for(int j = 3 + GraphFactory.keywordArray[i].parentNum; j < 3 + (GraphFactory.keywordArray[i].parentNum * 2); j++){
                GraphFactory.keywordArray[i].pathLength[j - 3 - GraphFactory.keywordArray[i].parentNum] = Integer.parseInt(lineBuffer[j]);
            }
            i += 1;
        }

        while((lineBuffer = childrenReader.readNext()) != null){
            KeywordVertex currentVertex = findVertexForName(lineBuffer[0]);
            currentVertex.dominantChildNum = Integer.parseInt(lineBuffer[1]);
            for(int j = 0; j < currentVertex.dominantChildNum; j++){
                currentVertex.dominantChild[j] = lineBuffer[j + 2];
            }
            currentVertex.childNum = Integer.parseInt(lineBuffer[2 + currentVertex.dominantChildNum]);
            for(int j = 0; j < currentVertex.childNum; j++){
                currentVertex.child[j] = lineBuffer[j + 2 + currentVertex.dominantChildNum + 1];
            }
        }



    }

    /**
     * creates .csv files which equal the number of total layers, then writes in the corresponding keywords with their direct children.
     * @param filepath Where the files should be created.
     * @throws IOException
     */
    public static void createCSVLayersFromGraph(String filepath) throws IOException{
        String filename;
        for(int i = 0; i < GraphFactory.maxLayer; i++){
            filename = filepath + "layer" + i + ".csv";
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1252"),';');
            for(int j = 0; j < GraphFactory.keywordEntries; j++){
                if(GraphFactory.keywordArray[j].layer == i){
                    String lineBuffer = GraphFactory.keywordArray[j].name + ";";
                    for(int k = 0; k < GraphFactory.keywordArray[j].dominantChildNum && i != GraphFactory.maxLayer; k++){
                        lineBuffer += GraphFactory.keywordArray[j].dominantChild[k] + ";";
                    }
                    lineBuffer += "EOL";
                    String[] record = lineBuffer.split(";");
                    writer.writeNext(record);
                }
            }
            writer.close();
        }
    }

    /**
     * creates a .csv file that saves data of the Vertex objects (name, layer, parentNum, parents, pathLength).
     * @param filepath is the saving path.
     * @throws IOException if path cannot be found.
     */
    public static void createParsedCSVFromGraph(String filepath) throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give the .csv file a name: ");
        String userInput = scanner.next();
        String filename = filepath + userInput + "_parents.csv";

        CSVWriter parentWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1252"), ';');
        String buffer = Integer.toString(GraphFactory.keywordEntries) + ";" + Integer.toString(GraphFactory.maxLayer) + ";EOL";
        String[] rc = buffer.split(";");
        parentWriter.writeNext(rc);
        for(int i = 0; i < GraphFactory.keywordEntries; i++){
            String lineBuffer = GraphFactory.keywordArray[i].name + ";" + Integer.toString(GraphFactory.keywordArray[i].layer) + ";" + Integer.toString(GraphFactory.keywordArray[i].parentNum) + ";";
            for(int j = 0; j < GraphFactory.keywordArray[i].parentNum; j++){
                lineBuffer += GraphFactory.keywordArray[i].parent[j] + ";";
            }
            for(int j = 0; j < GraphFactory.keywordArray[i].parentNum; j++){
                lineBuffer += Integer.toString(GraphFactory.keywordArray[i].pathLength[j]) + ";";
            }
            lineBuffer += "EOL";
            String[] record = lineBuffer.split(";");
            parentWriter.writeNext(record);
        }
        parentWriter.close();

        filename = filepath + userInput + "_children.csv";
        CSVWriter childrenWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(filename), "Cp1252"), ';');
        for(int i = 0; i < keywordEntries; i++){
            String lineBuffer = keywordArray[i].name + ";" + Integer.toString(keywordArray[i].dominantChildNum) + ";";
            for(int j = 0; j < keywordArray[i].dominantChildNum; j++){
                lineBuffer += keywordArray[i].dominantChild[j] + ";";
            }
            lineBuffer += Integer.toString(keywordArray[i].childNum) + ";";
            for(int j = 0; j < keywordArray[i].childNum; j++){
                lineBuffer += keywordArray[i].child[j] + ";";
            }
            lineBuffer += "EOL";
            String[] record = lineBuffer.split(";");
            childrenWriter.writeNext(record);
        }
        childrenWriter.close();
    }

    /**
     * calculates weights of the edges by considering their lengths.
     * @param vertex
     */
    private static void updateWeight(int vertex){
        for(int j = 0; j < GraphFactory.keywordArray[vertex].parentNum; j++){

            // The longer the path, the more little it's weight scales.
            GraphFactory.keywordArray[vertex].weight[j] = Math.pow(0.5, (double) GraphFactory.keywordArray[vertex].pathLength[j]);

            //for(int k = 0; k < GraphFactory.keywordArray[vertex].pathLength[j]; k++){
            //    GraphFactory.keywordArray[vertex].weight[j] += Math.pow(0.5, (double) k);
            //}

            // But it will receive a constant bonus(0.2 * pathLength - 1)
            GraphFactory.keywordArray[vertex].weight[j] += 0.2 * (double) (GraphFactory.keywordArray[vertex].pathLength[j] - 1);
        }
    }

    /**
     * Just like the parent array of all Vertex objects, we also want them to know their children.
     * This method searches one's parents and add him into their children array.
     * @param vertex is the given node.
     */
    private static void assignChildren(int vertex){
        for(int i = 0; i < GraphFactory.keywordArray[vertex].parentNum; i++){
            for(int j = 0; j < GraphFactory.keywordEntries; j++){
                if(GraphFactory.keywordArray[vertex].parent[i].equals("Mercateo")){
                    GraphFactory.keywordArray[vertex].keywordType = "ROOT";
                    break;
                }
                if(GraphFactory.keywordArray[vertex].parent[i].equals(GraphFactory.keywordArray[j].name) && !GraphFactory.keywordArray[j].childExists(GraphFactory.keywordArray[vertex].name)){
                    GraphFactory.keywordArray[j].setChild(GraphFactory.keywordArray[vertex].name);
                    break;
                }
            }
        }
    }

    /**
     * finds out a vertex's parents with the longest paths toward the top of the graph by
     * comparing it's pathLengths with it's layer.
     * @param vertex a child node.
     */
    private static void assignDominantChildren(int vertex){
        for(int i = 0; i < GraphFactory.keywordArray[vertex].parentNum; i++){
            if(GraphFactory.keywordArray[vertex].pathLength[i] == GraphFactory.keywordArray[vertex].layer && GraphFactory.findVertexForName(GraphFactory.keywordArray[vertex].parent[i]) != null){
                GraphFactory.findVertexForName(GraphFactory.keywordArray[vertex].parent[i]).setDominantChild(GraphFactory.keywordArray[vertex].name);
            }
        }
    }

    public static ArrayList<KeywordVertex> findMostRelevantSubordinates(KeywordVertex inputKeyword){
        ArrayList<KeywordVertex> subordinates = new ArrayList<>();
        ArrayList<KeywordVertex> parsedSubordinates;

        if(!inputKeyword.keywordType.equals("LEAF")){
            for(int i = 0; i < inputKeyword.dominantChildNum; i++){
                insertChild(subordinates, findVertexForName(inputKeyword.dominantChild[i]));
            }
        }

        switch (inputKeyword.keywordType){
            case "ROOT":
                parsedSubordinates = new ArrayList<>(subordinates.subList(0, 10));
                return parsedSubordinates;
            case "HIGH":
                parsedSubordinates = new ArrayList<>(subordinates.subList(0, 10));
                return parsedSubordinates;
            case "MIDDLE":
                parsedSubordinates = new ArrayList<>(subordinates.subList(0, inputKeyword.dominantChildNum));
                return parsedSubordinates;
            case "LOW":
                parsedSubordinates = new ArrayList<>(subordinates.subList(0, inputKeyword.dominantChildNum));
                return parsedSubordinates;
            case "LEAF":
                System.out.println("THIS IS A LEAF");
                return null;
            default:
                System.out.println("WARNING: UNKNOWN KEYWORD TYPE");
                return null;
        }
    }

    public static void insertChild(ArrayList<KeywordVertex> array, KeywordVertex child){
        for(int i = 0; i < array.size() - 1; i++){
            if(array.get(i).childNum >= child.childNum && array.get(i + 1).childNum <= child.childNum){
                array.add(i + 1, child);
                return;
            }
        }
        if(array.size() == 0){
            array.add(child);
        } else if(array.get(0).childNum >= child.childNum){
            array.add(child);
        } else {
            array.add(0, child);
        }
    }

    public static KeywordVertex findMostSimilarKeywordOf(String inputKeyword){
        double maxSimilarity = 0;
        KeywordVertex mostSimilarKeyword = null;
        for(int i = 0; i < GraphFactory.keywordEntries; i++){
            keywordArray[i].inputSimilarity = InputKeywordComparator.compareStrings(GraphFactory.keywordArray[i].name, inputKeyword);
            if(maxSimilarity < keywordArray[i].inputSimilarity){
                maxSimilarity = keywordArray[i].inputSimilarity;
                mostSimilarKeyword = keywordArray[i];
            }
        }
        return mostSimilarKeyword;
    }

    public static void setRawCSVFilename(String file) {
        GraphFactory.rawCSVFilename = file;
    }

    public static void setParsedCSVFilepath(String file) {
        GraphFactory.parsedCSVFilepath = file;
    }

    public static KeywordVertex findVertexForName(String inputName){
        for(int i = 0; i < GraphFactory.keywordEntries; i++){
            if(GraphFactory.keywordArray[i].name.equals(inputName)){
                return GraphFactory.keywordArray[i];
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
        //int testKeyword = 2265;
        boolean readFLAG = true;
        boolean parseFLAG = false;
        boolean writeLayersFLAG = false;
        boolean writeParsedCSVFLAG = false;
        boolean inputKeywordFLAG = true;


        GraphFactory.setRawCSVFilename("C:/Users/wang.daoping/Documents/Keyword_Graph.csv");
        GraphFactory.setParsedCSVFilepath("C:/Users/wang.daoping/Documents/");
        System.out.println("Loading CSV...");

        // Pass the .csv file to createGraphFromRawCSV

        if(readFLAG){
            try{
                GraphFactory.createGraphFromParsedCSV("C:/Users/wang.daoping/Documents/DFSParsedGraph");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(parseFLAG){
            try{
                GraphFactory.createGraphFromRawCSV(GraphFactory.rawCSVFilename);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

/*
        System.out.println(testKeyword + ". keyword " + GraphFactory.keywordArray[testKeyword].name + " has these parents: ");
        System.out.println();
        for(int i = 0; i < GraphFactory.keywordArray[testKeyword].parentNum; i++){
            for(int j = 0; j < GraphFactory.keywordEntries; j++){
                if(GraphFactory.keywordArray[j].name.equals(GraphFactory.keywordArray[testKeyword].parent[i])){
                    System.out.print(j + ". keyword ");
                }
            }
            System.out.print(GraphFactory.keywordArray[testKeyword].parent[i]);
            System.out.println();
        }

        System.out.println("And these lengths: ");
        for(int i = 0; i < GraphFactory.keywordArray[testKeyword].parentNum; i++){
            System.out.print(GraphFactory.keywordArray[testKeyword].pathLength[i]);
            System.out.println();
        }*/

        if(inputKeywordFLAG){
            for(int i = 0; i < GraphFactory.keywordEntries; i++){
                GraphFactory.keywordArray[i].setKeywordType();
            }

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter keyword: ");
            String userInput = scanner.nextLine();
            KeywordVertex buffer = findMostSimilarKeywordOf(userInput);
            System.out.println("Most similar keyword found: " + buffer.name + " with similarity " + Double.toString(buffer.inputSimilarity));

            ArrayList<KeywordVertex> relevantSubordinates = findMostRelevantSubordinates(buffer);
            System.out.println("Most relevant subordinates are: ");
            if(relevantSubordinates != null){
                for(int i = 0; i < relevantSubordinates.size(); i++){
                    System.out.println(relevantSubordinates.get(i).name + " with " + Integer.toString(findVertexForName(relevantSubordinates.get(i).name).childNum) + " children.");
                }
            } else {
                System.out.println("This is a leaf.");
            }

        }

        if(writeParsedCSVFLAG){
            int percentage;
            for(int i = 0; i < GraphFactory.keywordEntries; i++){
                GraphFactory.assignChildren(i);
                GraphFactory.assignDominantChildren(i);
                if((percentage = GraphFactory.processPercentage(i, GraphFactory.keywordEntries)) != 0){
                    System.out.println("Assigning children... " + percentage + "% done");
                }
            }

            try{
                GraphFactory.createParsedCSVFromGraph("C:/Users/wang.daoping/Documents/");
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        if(writeLayersFLAG){
            // After CSV reading, depth calculation and weight updates, start finding out the root keywords and assign children.
            for(int i = 0; i < GraphFactory.keywordEntries; i++){
                GraphFactory.keywordArray[i].setKeywordType();
            }
/*
            System.out.println("Root keywords are :");
            for(int i= 0; i < GraphFactory.keywordEntries; i++){
                if(GraphFactory.keywordArray[i].isRootKeyword){
                    System.out.print(GraphFactory.keywordArray[i].name + " in layer " + GraphFactory.keywordArray[i].layer + " with dominant children : ");
                    System.out.println();
                    for(int j = 0; j < GraphFactory.keywordArray[i].dominantChildNum; j++){
                        System.out.print(GraphFactory.keywordArray[i].dominantChild[j] + "; ");
                    }
                    System.out.println();
                }
            }*/
            try{
                GraphFactory.createCSVLayersFromGraph("C:/Users/wang.daoping/Documents/DFS_Keywords_layers_2411/");
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}