package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */

/**
 * holds basic attributes of a node in the graph.
 */

public abstract class Vertex {
    /**
     * contains the weights between this vertex and it's parents.
     */
    public double[] weight;

    /**
     * holds the maximum length from each of it's parents to the top vertices.
     */
    public int[] pathLength;

    /**
     * is the name of this node, either a keyword or a product.
     */
    public String name;

    /**
     * helps BreadthFirstSearcher to find out the depth.
     */
    public int tempLayer;

    /**
     * in which a vertex is located in the graph.
     */
    public int layer;

    public boolean alreadyCalculated;

    public double inputSimilarity;

    /**
     * The default constructor initiate attributes in order to avoid null pointer exceptions.
     */

    public Vertex(){
        this.weight = new double[20];
        this.tempLayer = 0;
        this.layer = -1;
        this.alreadyCalculated = false;
        this.inputSimilarity = 0;
    }

    /**
     * set the name attribute.
     * @param inputName a string containing a keyword or a product.
     */
    protected void setName(String inputName){
        this.name = inputName;
    }

    public void setLayer(int inputLayer){
        this.layer = inputLayer;
        if(inputLayer > GraphFactory.maxLayer){
            GraphFactory.maxLayer = inputLayer;
        }
    }

    public boolean noLayerSet(){
        if(this.layer == -1){
            return true;
        }
        return false;
    }
}
