package DataBackend;

/**
 * Created by Wang.Daoping on 14.11.2016.
 */
public class KeywordVertex extends Vertex {
    /**
     * holds all parent vertices in a string array.
     */
    public String[] parent;

    /**
     * is the sum of it's parents.
     */
    public int parentNum;

    public String[] loopCheckArray;
    public String[] child;
    public String[] dominantChild;
    public int childNum;
    public int dominantChildNum;
    public int loopCheckArrayNum;
    public String keywordType;

    public KeywordVertex(){
        super();
        this.parentNum = 0;
        this.parent = new String[20];
        //this.loopCheckArray = new String[20];
        this.child = new String[3500];
        this.dominantChild = new String[3000];
        //this.loopCheckArrayNum = 0;
        this.childNum = 0;
        this.dominantChildNum = 0;
        this.keywordType = null;
    }

    public KeywordVertex(String inputName, String inputParent){
        super();
        this.parentNum = 0;
        this.parent = new String[20];
        //this.loopCheckArray = new String[20];
        this.child = new String[3500];
        this.dominantChild = new String[3000];
        //this.loopCheckArrayNum = 0;
        this.childNum = 0;
        this.dominantChildNum = 0;
        this.keywordType = null;
        this.setName(inputName);
        this.setParent(inputParent);
    }

    public void setLoopCheckArray(String inputChild){
        this.loopCheckArray[this.loopCheckArrayNum] = inputChild;
        this.loopCheckArrayNum += 1;
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

    public void setDominantChild(String inputDominantChild){
        this.dominantChild[dominantChildNum] = inputDominantChild;
        this.dominantChildNum += 1;
    }

    public void setKeywordType(){
        if(this.keywordType == null){
            if(this.dominantChildNum == 0){
                this.keywordType = "LEAF";
            } else if(this.dominantChildNum < 6){
                this.keywordType = "LOW";
            } else if(this.dominantChildNum >= 6 && dominantChildNum < 11){
                this.keywordType = "MIDDLE";
            } else if(this.dominantChildNum >= 11){
                this.keywordType = "HIGH";
            }
        }
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
