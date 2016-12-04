package graph.clustering.vertex;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class EdgeFactory {
    private String targetVertexName;
    private double edgeWeight;

    public EdgeFactory(String inputName){
        this.setTargetVertexName(inputName);
    }

    public EdgeFactory(String inputName, double inputWeight){
        this.setTargetVertexName(inputName);
        this.setEdgeWeight(inputWeight);
    }

    public EdgeFactory(String inputName, int inputLayer){
        this.setTargetVertexName(inputName);
        this.setEdgeWeight(inputLayer);
    }

    public void setTargetVertexName(String inputName){
        this.targetVertexName = inputName;
    }

    public void setEdgeWeight(int inputLayer){
        this.edgeWeight = Math.pow(0.5, (double) inputLayer);
        if(inputLayer != 0){
            this.edgeWeight += 0.2;
        }
    }

    public void setEdgeWeight(double inputWeight){
        this.edgeWeight = inputWeight;
    }

    public String getTargetVertexName(){
        return targetVertexName;
    }

    public double getEdgeWeight(){
        return edgeWeight;
    }
}
