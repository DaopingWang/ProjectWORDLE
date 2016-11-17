package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */
public class KeywordVertex extends Vertex {
    public String[] child;
    public int childNum;
    public int level;

    public KeywordVertex(String inputName, String inputParent){
        super(inputName,inputParent);
        this.child = new String[20];
        this.childNum = 0;
    }

    public void setChild(String inputChild){
        this.child[this.childNum] = inputChild;
        this.childNum += 1;
    }

    public boolean childExists(String inputChild){
        for(int i = 0; i < this.childNum; i++){
            if (this.child[i].equals(inputChild)){
                return true;
            }
        }
        return false;
    }

    public void setLevel(int inputLevel){
        this.level = inputLevel;
    }
}
