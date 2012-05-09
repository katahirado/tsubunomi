package jp.katahirado.android.tsubunomi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import twitter4j.Status;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class TweetListAdapter extends ArrayAdapter<Status> {

    private LayoutInflater layoutInflater;
    private TextView screenName;
    private TextView createdAt;
    private TextView tweetText;

    public TweetListAdapter(Context context, List<Status> objects) {
        super(context, 0, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.tweet_row, null);
        }

        Status status = this.getItem(position);
        if (status != null) {
            screenName = (TextView) view.findViewById(R.id.row_screen_name);
            screenName.setText(status.getUser().getScreenName());
            createdAt = (TextView) view.findViewById(R.id.row_created_at);
            createdAt.setText(" " + status.getCreatedAt().toString());
            tweetText = (TextView) view.findViewById(R.id.row_tweet_text);
            tweetText.setText(status.getText());
        }
        return view;
    }
}
