package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */
public class ProductVertex extends Vertex {
    /**
     * holds all parent vertices in a string array.
     */
    public String parent;

    public ProductVertex(String inputName, String inputParent){
        super();
        this.setName(inputName);
        this.setParent(inputParent);
    }

    /**
     * add a parent into the parent array of this node.
     * @param inputParent a string.
     */
    public void setParent(String inputParent){
            this.parent= inputParent;
    }
}
