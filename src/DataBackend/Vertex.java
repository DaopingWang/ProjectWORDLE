package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */
public abstract class Vertex {
    public String[] parent;
    public double[] weight;
    public short[] pathLength;
    public String name;
    public int parentNum;

    public Vertex(String inputName, String inputParent){
        this.parentNum = 0;
        this.parent = new String[20];
        this.setName(inputName);
        this.setParent(inputParent);
    }

    public void setName(String inputName){
        this.name = inputName;
    }

    public void setParent(String inputParent){
        if (!this.parentExist(inputParent)) {
            this.parent[this.parentNum] = inputParent;
            this.parentNum += 1;
        }
    }

    private boolean parentExist(String inputParent){
        for(int i = 0; i < this.parentNum; i++){
            if(inputParent.equals(this.parent[i])){
                return true;
            }
        }
        return false;
    }
}
