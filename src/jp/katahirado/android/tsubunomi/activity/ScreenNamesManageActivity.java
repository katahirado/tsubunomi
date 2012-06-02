package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
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
public class ScreenNamesManageActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> screenNames;
    private SharedManager sharedManager;
    private EditText screenNameText;
    private ListView manageList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_names_manage);
        setTitle(getString(R.string.app_name) + " : Delete autocomplete word");

        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        screenNames = sharedManager.getScreenNames();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNames);

        manageList = (ListView) findViewById(R.id.screen_name_manage_list);
        Button searchButton = (Button) findViewById(R.id.screen_name_search_button);
        screenNameText = (EditText) findViewById(R.id.manage_screen_name_text);

        searchButton.setOnClickListener(this);
        adapter.sort(new LowerCaseComparator());
        manageList.setAdapter(adapter);
        manageList.setOnItemClickListener(this);
        manageList.requestFocus();
        manageList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideIME();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.screen_name_search_button:
                SpannableStringBuilder builder = (SpannableStringBuilder) screenNameText.getText();
                String query = builder.toString();
                adapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, screenNamesFilter(query));
                adapter.sort(new LowerCaseComparator());
                manageList.setAdapter(adapter);
                hideIME();
                screenNameText.setText("");
                break;
        }
    }

    private void hideIME() {
        InputMethodManager manager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(screenNameText.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private ArrayList<String> screenNamesFilter(String query) {
        if (query.length() == 0) {
            return sharedManager.getScreenNames();
        }
        ArrayList<String> list = new ArrayList<String>();
        for (String s : screenNames) {
            if (s.toLowerCase().startsWith(query.toLowerCase())) {
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        final String screenName = adapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(screenName + " を削除してもよろしいですか?").setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                adapter.remove(screenName);
                screenNames.remove(screenName);
                ArrayList<String> names = sharedManager.getScreenNames();
                names.remove(screenName);
                sharedManager.setScreenNames(names);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}