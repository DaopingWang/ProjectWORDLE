package graph.clustering.vertex;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class EdgeFactory {
    private String targetVertexName;
    private double edgeLength;

    public EdgeFactory(String inputName){
        this.setTargetVertexName(inputName);
    }

    public void setTargetVertexName(String inputName){
        this.targetVertexName = inputName;
    }

    public void setEdgeLength(int inputLayer){
        this.edgeLength = Math.pow(0.5, (double) inputLayer);
        if(inputLayer != 0){
            this.edgeLength += 0.2;
        }
    }

    public String getTargetVertexName(){
        return targetVertexName;
    }

    public double getEdgeLength(){
        return edgeLength;
    }
}
