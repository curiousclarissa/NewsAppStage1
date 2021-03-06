package com.example.clarissajarem.newsappstage1;

/**
 * Created by clarissajarem on 9/11/18.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static java.util.Locale.US;

/**
 * Helper methods related to requesting and receiving article data from Guardian.
 */
public final class QueryUtils {
    /**
     * Sample JSON response for a USGS query
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    /** URL news articles from the guardian */
    /**
     * Tag for the log messages
     */


    private QueryUtils() {
    }

    /**
     * Return a list of {@link NewsArticle} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<NewsArticle> fetchNewsArticleData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        //perform HTTP request to URL to get json response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // extract relevant article info from json and make a list of it
        List<NewsArticle> newsArticles = extractFeatureFromJson(jsonResponse);

        //Return the list of {@link Earthquake}s
        return newsArticles;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //if URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        }catch(IOException e){
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsArticle> extractFeatureFromJson(String newsArticleJSON) {
        if (TextUtils.isEmpty(newsArticleJSON)) {
            return null;
        }
        List<NewsArticle> newsArticles = new ArrayList<>();
        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(newsArticleJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of features (or articles).
            JSONArray newsArticleArray = response.getJSONArray("results");
            // For each article in the newsArticle, create an {@link News Article} object
            for (int i = 0; i < newsArticleArray.length(); i++) {

                // Get a single article at position i within the list of articles
                JSONObject currentNewsArticle = newsArticleArray.getJSONObject(i);

                // For a given article, extract the JSONObject associated with the
                // key called "results", which represents a list of all properties
                // for that article.

                // Extract the value for the key called "sectionName"
                String section = currentNewsArticle.getString("sectionName");


                // Extract the value for the key called "webPublicationDate"
                String dateToFormat = currentNewsArticle.getString("webPublicationDate");
                String webPublicationDate = formatDate(dateToFormat);

                // Extract the value for the key called "webTitle"
                String webTitle = currentNewsArticle.getString("webTitle");
                // Extract the value for the key called "webUrl"
                String webUrl = currentNewsArticle.getString("webUrl");

                String contributor = "No author available";

                // Create a new {@link NewsArticle} object with the section, contributor, title, publication date and url,
                // and url from the JSON response.
                JSONArray tagsArray = currentNewsArticle.getJSONArray("tags");
                // Name the first JSONObject currentTags so we can get the string of webTitle key
                if (tagsArray.length() > 0) {
                    JSONObject currentTags = tagsArray.getJSONObject(0);
                    //Assign the value of the key called "webTitle" to articleAuthor
                    contributor = currentTags.getString("webTitle");
                }


                NewsArticle newsArticle = new NewsArticle(section, contributor, webTitle, webPublicationDate, webUrl);

                // Add the new {@link NewsArticle} to the list of articles.
                newsArticles.add(newsArticle);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // return a list of articles
        return newsArticles;
    }

    //make date a format that can be displayed on screen

    private static String formatDate(String dateToFormat) throws ParseException {
        if (dateToFormat==null){
            dateToFormat = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        Date date1 = dateFormatter.parse(dateToFormat);
        SimpleDateFormat webPublishedDateFormatter = new SimpleDateFormat("yyyy.MM.DD");
        final String webPublishedDate = webPublishedDateFormatter.format(date1);
        return webPublishedDate;
    }

        }

