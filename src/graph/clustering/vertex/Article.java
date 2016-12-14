package graph.clustering.vertex;

/**
 * Created by wang.daoping on 14.12.2016.
 */
public class Article {
    public String articleNumber;
    public KeywordVertex correspondingKeyword;

    public Article(String articleNumber, KeywordVertex correspondingKeyword){
        this.articleNumber = articleNumber;
        this.correspondingKeyword = correspondingKeyword;
    }
}
