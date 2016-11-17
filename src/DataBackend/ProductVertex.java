package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */
public class ProductVertex extends Vertex {
    /**
     * holds all parent vertices in a string array.
     */
    public String[] parent;

    /**
     * is the sum of it's parents.
     */
    public int parentNum;

    public ProductVertex(String inputName, String inputParent){
        super();
        this.parentNum = 0;
        this.parent = new String[20];
        this.setName(inputName);
        this.setParent(inputParent);
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
