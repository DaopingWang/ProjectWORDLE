package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */
public class KeywordVertex extends Vertex {
    public String[] children;
    public int level;

    public KeywordVertex(String inputName, String inputParent){
        super(inputName,inputParent);
    }

    public void setChildren(String inputChildren, int i){
        this.children[i] = inputChildren;
    }

    public void setLevel(int inputLevel){
        this.level = inputLevel;
    }
}
