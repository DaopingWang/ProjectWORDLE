package graph.clustering.vertex;

import java.util.ArrayList;

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

    public Vertex(String inputName){
        this.setLayer(-1);
        this.setName(inputName);
        this.subordinateList = new ArrayList<>();
    }

    public Vertex(String inputName, int inputLayer){
        this.setName(inputName);
        this.setLayer(inputLayer);
        this.subordinateList = new ArrayList<>();
    }

    /**
     * set the name attribute.
     * @param inputName a string containing a keyword or a product.
     */
    private void setName(String inputName){
        this.name = inputName;
    }

    public void setLayer(int inputLayer){
        if(this.layerIsUnset()){
            this.layer = inputLayer;
        }
    }

    public boolean layerIsUnset(){
        if(this.layer == -1){
            return true;
        }
        return false;
    }
}
