package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
public class UsersActivity extends Activity implements AdapterView.OnItemClickListener {
    private ArrayAdapter<String> adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        setTitle(getString(R.string.app_name) + " : 表示ユーザー一覧");
        ListView listView = (ListView) findViewById(R.id.users_list);

        SharedManager sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        ArrayList<String> screenNames = sharedManager.getScreenNames();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNames);
        adapter.sort(new LowerCaseComparator());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, UserTimelineActivity.class);
        intent.putExtra(Const.SCREEN_NAME, adapter.getItem(position));
        startActivity(intent);
    }
}