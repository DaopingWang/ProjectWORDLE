package old.graph.clustering;

/**
 * Created by Wang.Daoping on 17.11.2016.
 */
public class RootKeywordVertex extends Vertex {
    public String[] child;
    public int childNum;

    public RootKeywordVertex(String inputName, String inputChild){
        super();
        this.child = new String[1000];
        this.childNum = 0;
        this.setName(inputName);
        this.setChild(inputChild);
    }

    public boolean childExists(String inputChild){
        for(int i = 0; i < this.childNum; i++){
            if (this.child[i].equals(inputChild)){
                return true;
            }
        }
        return false;
    }

    public void setChild(String inputChild){
        this.child[childNum] = inputChild;
        this.childNum += 1;
    }
}
