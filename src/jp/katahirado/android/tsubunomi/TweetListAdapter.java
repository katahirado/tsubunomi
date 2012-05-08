package jp.katahirado.android.tsubunomi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import twitter4j.Status;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class TweetListAdapter extends ArrayAdapter<Status>{

    private LayoutInflater layoutInflater;

    public TweetListAdapter(Context context,  List<Status> objects) {
        super(context, 0, objects);
        layoutInflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
