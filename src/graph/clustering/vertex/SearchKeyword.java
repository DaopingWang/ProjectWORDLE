package graph.clustering.vertex;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 14.12.2016.
 */
public class SearchKeyword {
    public ArrayList<KeywordVertex> searchResults;
    public ArrayList<String[]> clusters;
    public String name;

    public SearchKeyword(String inputSearchKeyword){
        this.name = inputSearchKeyword;
        this.searchResults = new ArrayList<>();
        this.clusters = new ArrayList<>();
    }
}
