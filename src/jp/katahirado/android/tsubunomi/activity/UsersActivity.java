package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.LowerCaseComparator;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SharedManager;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class UsersActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ArrayAdapter<String> adapter;
    private EditText userText;
    private ArrayList<String> screenNames;
    private ListView listView;
    private SharedManager sharedManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        setTitle(getString(R.string.app_name) + " : 表示ユーザー一覧");
        listView = (ListView) findViewById(R.id.users_list);
        Button button = (Button) findViewById(R.id.users_screen_name_search_button);
        userText = (EditText) findViewById(R.id.users_screen_name_text);

        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        setListViewAdapter();
        listView.setOnItemClickListener(this);
        button.setOnClickListener(this);
        listView.requestFocus();
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideIME();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListViewAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.users_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_screen_name_manage:
                startActivity(new Intent(this, ScreenNamesManageActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, UserTimelineActivity.class);
        intent.putExtra(Const.SCREEN_NAME, adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.users_screen_name_search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) userText.getText();
                String query = builder.toString();
                adapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNamesFilter(query));
                adapter.sort(new LowerCaseComparator());
                listView.setAdapter(adapter);
                hideIME();
                userText.setText("");
                break;
        }
    }

    private void setListViewAdapter() {
        screenNames = sharedManager.getScreenNames();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNames);
        adapter.sort(new LowerCaseComparator());
        listView.setAdapter(adapter);
    }

    private ArrayList<String> screenNamesFilter(String query) {
        if (query.length() == 0) {
            return screenNames;
        }
        ArrayList<String> list = new ArrayList<String>();
        for (String s : screenNames) {
            if (s.toLowerCase().startsWith(query.toLowerCase())) {
                list.add(s);
            }
        }
        return list;
    }

    private void hideIME() {
        InputMethodManager manager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(userText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}