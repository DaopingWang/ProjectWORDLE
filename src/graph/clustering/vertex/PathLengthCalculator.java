package graph.clustering.vertex;

/**
 * Created by Wang.Daoping on 01.12.2016.
 */
public class PathLengthCalculator {
    private String targetVertexName;
    private double edgeLength;

    public PathLengthCalculator(String inputName, int targetLayer){
        this.setTargetVertexName(inputName);
        this.setEdgeLength(Math.pow(0.5, (double) targetLayer) + 0.2);
    }
    private void setTargetVertexName(String inputName){
        this.targetVertexName = inputName;
    }

    private void setEdgeLength(double inputEdgeLength){
        this.edgeLength = inputEdgeLength;
    }
}
