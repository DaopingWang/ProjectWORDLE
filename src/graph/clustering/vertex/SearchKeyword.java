package graph.clustering.vertex;

import java.util.ArrayList;

/**
 * Created by wang.daoping on 14.12.2016.
 */
public class SearchKeyword {
    public ArrayList<KeywordVertex> searchResults;
    public String name;

    public SearchKeyword(String inputSearchKeyword){
        this.name = inputSearchKeyword;
        this.searchResults = new ArrayList<>();
    }
}