package com.example.clarissajarem.newsappstage1;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by clarissajarem on 9/11/18.
 */

public class NewsLoader extends AsyncTaskLoader<List<NewsArticle>> {
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();
    private String localUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        localUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsArticle> loadInBackground() {
        if (localUrl == null) {
            return null;
        }
        List<NewsArticle> newsArticles = QueryUtils.fetchNewsArticleData(localUrl);
        return newsArticles;
    }
}
