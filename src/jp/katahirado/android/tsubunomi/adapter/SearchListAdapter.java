package jp.katahirado.android.tsubunomi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import jp.katahirado.android.tsubunomi.R;
import twitter4j.Tweet;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SearchListAdapter extends ArrayAdapter<Tweet> {
    private LayoutInflater layoutInflater;
    private TextView screenName;
    private TextView createdAt;
    private TextView tweetText;
    private String formatDateText;

    public SearchListAdapter(Context context, List<Tweet> objects) {
        super(context, 0, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.search_row, null);
        }

        Tweet tweet = this.getItem(position);
        if (tweet != null) {
            screenName = (TextView) view.findViewById(R.id.row_screen_name);
            screenName.setText(tweet.getFromUser());
            createdAt = (TextView) view.findViewById(R.id.row_created_at);
            formatDateText = new SimpleDateFormat("yyyy年MM月dd日HH時mm分").format(tweet.getCreatedAt());
            createdAt.setText(" " + formatDateText);
            tweetText = (TextView) view.findViewById(R.id.row_tweet_text);
            tweetText.setText(tweet.getText());
        }
        return view;
    }
}
