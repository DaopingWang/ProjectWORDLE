package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */

/**
 * holds basic attributes of a node in the graph.
 */

public abstract class Vertex {
    /**
     * holds all parent vertices in a string array.
     */
    public String[] parent;

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
     * is the sum of it's parents.
     */
    public int parentNum;

    /**
     * helps DepthSearch to find out the depth.
     */
    public int layer;

    /**
     * The class Vertex has no default constructor. To instantiate an Vertex object,
     * pick KeywordVertex or ProductVertex, then pass a name and it's parent. The
     * constructor will call setName() and setParent() to complete the object.
     * @param inputName is the name of this vertex.
     * @param inputParent is one parent of this vertex.
     */
    public Vertex(String inputName, String inputParent){
        this.parentNum = 0;
        this.parent = new String[20];
        this.layer = 0;
        this.setName(inputName);
        this.setParent(inputParent);
    }

    /**
     * set the name attribute.
     * @param inputName a string containing a keyword or a product.
     */
    private void setName(String inputName){
        this.name = inputName;
    }

    /**
     * add a parent into the parent array of this node.
     * @param inputParent a string.
     */
    public void setParent(String inputParent){
        if (!this.parentExist(inputParent)) {
            this.parent[this.parentNum] = inputParent;
            this.parentNum += 1;
        }
    }

    /**
     * returns true if the input parent already exists in the array.
     * @param inputParent name of a parent which this method should check.
     * @return boolean.
     */
    private boolean parentExist(String inputParent){
        for(int i = 0; i < this.parentNum; i++){
            if(inputParent.equals(this.parent[i])){
                return true;
            }
        }
        return false;
    }
}
