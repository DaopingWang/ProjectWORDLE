package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */
public abstract class Vertex {
    public String[] parent;
    public double[] weight;
    public short[] pathLength;
    public String name;



    public Vertex(String inputName, String inputParent){
        this.setName(inputName);
        if(!this.parentExist(inputParent)){
            this.setParent(inputParent, this.parent.length);
        }
    }

    public void setName(String inputName){
        this.name = inputName;
    }

    public void setParent(String inputParent, int i){
        this.parent[i] = inputParent;
    }

    private boolean parentExist(String inputParent){
        for(int i = 0; i < this.parent.length; i++){
            if(inputParent.equals(this.parent[i])){
                return true;
            }
        }
        return false;
    }
}
