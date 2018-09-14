package com.example.clarissajarem.newsappstage1;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by clarissajarem on 9/11/18.
 */

public class NewsArticleAdapter extends ArrayAdapter<NewsArticle> {

    /**
     * The part of the location string from the Guardian service that we use to determine
     */
    private static final String LOCATION_SEPARATOR = " of ";

    /**
     * Constructs a new {@link NewsArticleAdapter}.
     *
     * @param context of the app
     * @param newsArticles is the list of articles, which is the data source of the adapter
     */
    public NewsArticleAdapter(Context context, List<NewsArticle> newsArticles) {
        super(context, 0, newsArticles);
    }

    /**
     * Returns a list item view that displays information about the earthquake at the given position
     * in the list of articles.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_articles, parent, false);
        }

        // Find the earthquake at the given position in the list of articles
        NewsArticle currentArticle = getItem(position);

        // Find the TextView with view ID titleView
        TextView titleView = (TextView) listItemView.findViewById(R.id.articletitle);
        String title = currentArticle.getWebTitle();

        // Find the TextView with view ID contributor
        TextView contributorView = (TextView) listItemView.findViewById(R.id.contributor);


        // Get the original author string from the article object,
        String contributor = currentArticle.getContributor();

        // Find the TextView with view ID sectionView
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        String sectionName = currentArticle.getSectionName();

        // Find the TextView with view ID titleView
        TextView urlView = (TextView) listItemView.findViewById(R.id.weburl);
        String weburl = currentArticle.getWebUrl();

        // Create a new Date object from the time in milliseconds of the earthquake
        Date dateObject = new Date(currentArticle.getWebPublicationDate());

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.publicationdate);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = formatDate(dateObject);
        // Display the date of the current earthquake in that TextView
        dateView.setText(formattedDate);



        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }



    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

}
