package graph.clustering.vertex;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class Probability {
    private String targetVertexName;
    private double probability;

    public Probability(String inputName){
        this.setTargetVertexName(inputName);
    }

    public Probability(String inputName, double probability){
        this.setTargetVertexName(inputName);
        this.setProbability(probability);
    }

    public void setTargetVertexName(String targetVertexName) {
        this.targetVertexName = targetVertexName;
    }

    public void setProbability(double probability) {
        if(probability < 0.00001){
            this.probability = 0;
            return;
        }
        this.probability = probability;
    }

    public String getTargetVertexName() {
        return targetVertexName;
    }

    public double getProbability() {
        return probability;
    }
}
