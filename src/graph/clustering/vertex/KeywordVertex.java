package graph.clustering.vertex;

import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class KeywordVertex extends Vertex{
    public ArrayList<EdgeFactory> edgeList;
    public ArrayList<ProbabilityCalculator> probabilityList;

    public KeywordVertex(String inputName){
        super(inputName);
        this.edgeList = new ArrayList<>();
        this.probabilityList = new ArrayList<>();
    }

    public void createNewEdge(String inputName){
        EdgeFactory edge = new EdgeFactory(inputName);
        this.edgeList.add(edge);
    }
}
