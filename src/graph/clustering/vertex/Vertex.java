package graph.clustering.vertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public abstract class Vertex {
    /**
     * is the name of this node, either a keyword or a product.
     */
    public String name;

    /**
     * in which a vertex is located in the graph.
     */
    public int layer;

    public ArrayList<String> subordinateList;

    public double distance;
    public String previous;

    public Vector<Double> similarityVector;
    public int dominantCategory;

    public Vertex(String inputName){
        this.setLayer(-1);
        this.setName(inputName);
        this.subordinateList = new ArrayList<>();
        this.similarityVector = new Vector<>();
    }

    public Vertex(String inputName, int inputLayer){
        this.setName(inputName);
        this.setLayer(inputLayer);
        this.subordinateList = new ArrayList<>();
        this.similarityVector = new Vector<>();
    }

    /**
     * set the name attribute.
     * @param inputName a string containing a keyword or a product.
     */
    private void setName(String inputName){
        this.name = inputName;
    }

    public void setLayer(int inputLayer){
        this.layer = inputLayer;
    }

    public void setDominantCategory(){
        double maxProbability = 0;
        int index = 0;
        for(int i = 0; i < this.similarityVector.size(); i++){
            if(this.similarityVector.get(i)> maxProbability){
                maxProbability = this.similarityVector.get(i);
                index = i;
            }
        }
        this.dominantCategory = index;
    }

    public boolean layerIsUnset(){
        if(this.layer == -1){
            return true;
        }
        return false;
    }
}
