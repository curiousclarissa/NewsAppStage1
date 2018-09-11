package com.example.clarissajarem.newsappstage1;

/**
 * Created by clarissajarem on 9/11/18.
 * An {@link NewsArticle} object contains information related to a single article.
 */

public class NewsArticle {
    /** title on website **/
    private String localWebTitle;
    /** author of article **/
    private String localContributor;
    /** publication date **/
    private String localWebPublicationDate;
    /** section the article is in **/
    private String localSectionName;

    /**
     * Constructs a new {@link NewsArticle} object.
     *
     * @param webTitle website location
     * @param contributor author
     * @param webPublicationDate published on
     * @param section category of news
     */

    public NewsArticle(String section, String contributor, String webTitle, String webPublicationDate){
        localSectionName = section;
        localContributor = contributor;
        localWebTitle = webTitle;
        localWebPublicationDate = webPublicationDate;
    }
    /** return associated values **//
    public String getSectionName(){return localSectionName;}
    public String getContributor(){return localContributor; }
    public String getWebTitle(){return  localWebTitle;}
    public String getWebPublicationDate(){return localWebPublicationDate;}

}
