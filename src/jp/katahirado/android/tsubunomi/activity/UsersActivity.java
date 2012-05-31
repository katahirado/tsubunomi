package jp.katahirado.android.tsubunomi.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.LowerCaseComparator;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SharedManager;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UsersActivity extends ListActivity {
    private ArrayAdapter<String> adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        setTitle(getString(R.string.app_name)+" : 表示ユーザー一覧");

        SharedManager sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        ArrayList<String> screenNames = sharedManager.getScreenNames();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNames);
        adapter.sort(new LowerCaseComparator());
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this,UserTimelineActivity.class);
        intent.putExtra(Const.SCREEN_NAME,adapter.getItem(position));
        startActivity(intent);
    }
}