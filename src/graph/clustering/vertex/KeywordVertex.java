package graph.clustering.vertex;

import cern.colt.matrix.impl.SparseDoubleMatrix1D;

import java.util.ArrayList;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class KeywordVertex extends Vertex{
    public ArrayList<Edge> edgeList;
    public ArrayList<Probability> probabilityList;
    public SparseDoubleMatrix1D pathLengthVector;
    public double averageDistance;

    public KeywordVertex(String inputName){
        super(inputName);
        this.edgeList = new ArrayList<>();
        this.probabilityList = new ArrayList<>();
    }

    public KeywordVertex(String inputName, int inputLayer){
        super(inputName, inputLayer);
        this.edgeList = new ArrayList<>();
        this.probabilityList = new ArrayList<>();
    }

    public void createNewEdge(String inputName){
        Edge edge = new Edge(inputName);
        this.edgeList.add(edge);
    }

    public boolean edgeExist(String inputName){
        for(int i = 0; i < this.edgeList.size(); i++){
            if(this.edgeList.get(i).getTargetVertexName().equals(inputName)){
                return true;
            }
        }
        return false;
    }
}
