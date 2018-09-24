package com.example.clarissajarem.newsappstage1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsArticle>>{

    public static final String LOG_TAG = NewsActivity.class.getName();
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWSARTICLE_LOADER_ID = 1;
    private TextView localEmptyStateTextView;

    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "Test: onCreateLoader Called");
        // Create a new loader for the given URL
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String minArticles = sharedPrefs.getString(
                getString(R.string.settings_min_numberofarticles_key),
                getString(R.string.settings_min_numberofarticles_default));
        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("q", "housing");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("page-size", minArticles);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        return new NewsLoader(this, GUARDIAN_REQUEST_URL);
    }
    @Override
    public void onLoadFinished(Loader<List<NewsArticle>> loader, List<NewsArticle> articles) {
        Log.i(LOG_TAG, "Test: onLoadFinished Called");
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Clear the adapter of previous article data
        // Set empty state text to display "No articles found."
        localEmptyStateTextView.setText(R.string.no_articles);
        localAdapter.clear();

        // If there is a valid list of {@link articles}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        Log.i(LOG_TAG, "here there shall be articles");
            if (articles != null && !articles.isEmpty()) {
                Log.i(LOG_TAG, "I'll add them");
            localAdapter.addAll(articles);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<NewsArticle>> loader) {
        // Loader reset, so we can clear out our existing data.
        localAdapter.clear();
    }
    /** URL for article data from the Guardian news api */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?&show-tags=contributor&api-key=88b7be55-1ef7-4d9d-81e5-a295fe565250&";
    private NewsArticleAdapter localAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Test: News Activity OnCreate Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView newsArticleListView = (ListView) findViewById(R.id.news_list);

        // Create a new adapter that takes a list of articles as an input
        localAdapter = new NewsArticleAdapter(this, new ArrayList<NewsArticle>());
        localEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsArticleListView.setEmptyView(localEmptyStateTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsArticleListView.setAdapter(localAdapter);
        newsArticleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                NewsArticle currentNewsArticle = localAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(currentNewsArticle.getWebUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        // Get a reference to the LoaderManager, in order to interact with loaders.
        //LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        //loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWSARTICLE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            localEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

