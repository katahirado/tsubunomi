package jp.katahirado.android.tsubunomi.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import jp.katahirado.android.tsubunomi.Const;
import jp.katahirado.android.tsubunomi.R;
import jp.katahirado.android.tsubunomi.SharedManager;
import jp.katahirado.android.tsubunomi.TweetManager;
import jp.katahirado.android.tsubunomi.task.ProfileAndHelpConfigurationTask;
import jp.katahirado.android.tsubunomi.task.TweetPostTask;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TsubunomiActivity extends Activity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private Intent intent;
    private EditText tweetText;
    private Button tweetButton;
    private TextView tweetCount;
    private TextView replyText;

    private Twitter twitter;
    private long inReplyToStatusId = 0;
    private File mediaFile;
    private ImageView thumbnail;
    private SharedManager sharedManager;
    private boolean isAttachment = false;
    private TweetManager tweetManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tweetButton = (Button) findViewById(R.id.tweetButton);
        Button capButton = (Button) findViewById(R.id.capButton);
        Button picButton = (Button) findViewById(R.id.picButton);
        tweetText = (EditText) findViewById(R.id.tweetText);
        replyText = (TextView) findViewById(R.id.replyText);
        tweetCount = (TextView) findViewById(R.id.tweetCount);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        Button userTimelineButton = (Button) findViewById(R.id.uTimeButton);
        Button searchButton = (Button) findViewById(R.id.sButton);
        Button goToOAuthButton = (Button) findViewById(R.id.goToOAuthButton);

        tweetButton.setText("つぶやく");

        sharedManager = new SharedManager(getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE));
        tweetManager = new TweetManager(sharedManager);

        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweet();
            }
        });

        capButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mediaFile = getOutputMediaFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select a picture"), GALLERY_REQUEST_CODE);
            }
        });

        tweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int tCount;
                int diffCount = tweetManager.calculateShortURLsLength(editable.toString());
                if (isAttachment) {
                    tCount = Const.TWEET_COUNT_DEFAULT + diffCount -
                            sharedManager.getPrefInt(Const.CHARACTERS_RESERVED_PER_MEDIA, 0) -
                            editable.length();
                } else {
                    tCount = Const.TWEET_COUNT_DEFAULT + diffCount - editable.length();
                }

                tweetCount.setText(String.valueOf(tCount));
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(tweetText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        userTimelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUserTimelineActivity();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSearchActivity();
            }
        });

        goToOAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOAuthActivity();
            }
        });

        checkPreferences();

        intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                String screenName = uri.getQueryParameter(Const.SCREEN_NAME);
                inReplyToStatusId = Long.parseLong(uri.getQueryParameter(Const.IN_REPLY_TO_STATUS_ID));
                setReplyMessage(screenName);
            }
        } else if (Intent.ACTION_SEND.equals(action)) {
            CharSequence text = intent.getExtras().getCharSequence(Intent.EXTRA_TEXT);
            if (text != null) {
                tweetText.setText(text);
            }
            Uri stream = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (stream != null) {
                attachmentAndCalculateTweetCount(stream);
            }
        } else {
            String screenName = intent.getStringExtra(Const.SCREEN_NAME);
            if (screenName != null) {
                int type = intent.getIntExtra(Const.REPLY_TYPE, 0);
                inReplyToStatusId = intent.getLongExtra(Const.IN_REPLY_TO_STATUS_ID, 0);
                switch (type) {
                    case Const.REPLY:
                        setReplyMessage(screenName);
                        break;
                    case Const.QT:
                        setQTMessage(screenName);
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkPreferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                //Galaxy S2しか持っていないので他の機種では別処理をいれる必要があると思われる
                Bitmap workBitmap;
                isAttachment = true;
                calculateTweetCount();
                int orientation = 0;
                if (data == null) {
                    workBitmap = loadBitmap(mediaFile);
                    try {
                        ExifInterface exifInterface = new ExifInterface(mediaFile.getAbsolutePath());
                        orientation = convertAngle(exifInterface);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (orientation != 0) {
                        workBitmap = rotateBitmap(orientation, workBitmap);
                    }
                    thumbnail.setImageBitmap(workBitmap);
                    convertBitmapToFile(workBitmap);
                }
                break;
            case GALLERY_REQUEST_CODE:
                attachmentAndCalculateTweetCount(data.getData());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_user_timeline:
                goToUserTimelineActivity();
                break;
            case R.id.menu_oauth:
                goToOAuthActivity();
                break;
            case R.id.menu_search:
                goToSearchActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPreferences() {
        if (sharedManager.isConnected()) {
            twitter = tweetManager.connectTwitter();
            onceDayProfileAndConfigurationTask();
            tweetButton.setEnabled(true);
        } else {
            tweetButton.setEnabled(false);
            //Twitter連携設定が保持されていなかったら連携設定に飛ばす
            goToOAuthActivity();
        }
    }

    private void onceDayProfileAndConfigurationTask() {
        if (sharedManager.isCheckConfigTime()) {
            ProfileAndHelpConfigurationTask task = new ProfileAndHelpConfigurationTask(sharedManager);
            task.execute(twitter);
        }
    }

    private void goToUserTimelineActivity() {
        startActivity(new Intent(this, UserTimelineActivity.class));
    }

    private void goToOAuthActivity() {
        startActivity(new Intent(this, OAuthActivity.class));
    }

    private void goToSearchActivity() {
        startActivity(new Intent(this, SearchTimelineActivity.class));
    }

    private void attachmentAndCalculateTweetCount(Uri uri) {
        searchMediaFileData(uri);
        isAttachment = true;
        calculateTweetCount();
    }

    private void calculateTweetCount() {
        SpannableStringBuilder stringBuilder = (SpannableStringBuilder) tweetText.getText();
        String tText = stringBuilder.toString();
        int diffCount = tweetManager.calculateShortURLsLength(tText);
        int tweetLength = Const.TWEET_COUNT_DEFAULT + diffCount - tText.length() -
                sharedManager.getPrefInt(Const.CHARACTERS_RESERVED_PER_MEDIA, 0);
        tweetCount.setText(String.valueOf(tweetLength));
    }

    private int convertAngle(ExifInterface exifInterface) {
        int result = 0;
        int orientation =
                exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (orientation) {
            case ExifInterface.ORIENTATION_UNDEFINED:
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                result = 180;
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                result = 90;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                result = 270;
                break;
        }
        return result;
    }

    private void searchMediaFileData(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        String[] columns = {MediaStore.Images.Media.DATA, "_id", MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor c = contentResolver.query(uri, columns, null, null, null);
        c.moveToFirst();
        setThumbnail(contentResolver, c);
    }

    private void setThumbnail(ContentResolver resolver, Cursor cursor) {
        Bitmap workBitmap;
        long id = cursor.getLong(1);
        int orientation = cursor.getInt(2);
        Bitmap orgBitmap =
                MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
        //元画像のorientationを元にサムネイルを回転
        if (orientation == 0) {
            thumbnail.setImageBitmap(orgBitmap);
            workBitmap = orgBitmap;
        } else {
            workBitmap = rotateBitmap(orientation, orgBitmap);
        }
        thumbnail.setVisibility(View.VISIBLE);
        mediaFile = getOutputMediaFile();
        convertBitmapToFile(workBitmap);
    }

    private Bitmap rotateBitmap(int orientation, Bitmap orgBitmap) {
        int width = orgBitmap.getWidth();
        int height = orgBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        Bitmap bitmap = Bitmap.createBitmap(orgBitmap, 0, 0, width, height, matrix, true);
        thumbnail.setImageBitmap(bitmap);
        return bitmap;
    }

    private void convertBitmapToFile(Bitmap workBitmap) {
        try {
            FileOutputStream outputStream = new FileOutputStream(mediaFile);
            workBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadBitmap(File file) {
        Bitmap bitmap = null;
        if (file == null) {
            return bitmap;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        int sample_size = 1;
        //実際に読み込まないで情報だけ取得し、スケールを決める
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if ((options.outWidth * options.outHeight) > 1048576) {
            //1M超え
            int scaleW = options.outWidth / 380 + 1;
            int scaleH = options.outHeight / 420 + 1;
            sample_size = Math.max(scaleW, scaleH);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sample_size;
        //実際に画像を読み込む
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    private File getOutputMediaFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //Galaxy s2 /mnt/sdcard/Android/data/jp.karahido.android.tsubunomi/data/yyyyMMdd_HHmmss.jpg
        return new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), timeStamp + ".jpg");
    }

    private void setReplyMessage(String screenName) {
        String replyName = screenName.split(" ")[0];
        setTitle(getString(R.string.app_name) + " : " + replyName + "に返信");
        String message = intent.getStringExtra(Const.MESSAGE);
        if (message == null) {
            try {
                Status status = twitter.showStatus(inReplyToStatusId);
                screenName = tweetManager.buildReplyMention(status);
                message = status.getText();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
        replyText.setText(replyName + " : " + message);
        replyText.setVisibility(View.VISIBLE);
        CharSequence mentionString = "@" + screenName + " ";
        tweetText.setText(mentionString);
        tweetButton.setText("返信する");
        //位置を調整する
        tweetText.setSelection(mentionString.length());
    }

    private void setQTMessage(String screenName) {
        setTitle(getString(R.string.app_name) + " : " + screenName + "に返信");
        String message = intent.getStringExtra(Const.MESSAGE);
        CharSequence qtMessage = " QT: @" + screenName + " " + message;
        tweetText.setText(qtMessage);
        tweetButton.setText("返信する");
    }

    private void tweet() {
        SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) tweetText.getText();
        String tweetString = spannableStringBuilder.toString();
        StatusUpdate statusUpdate = new StatusUpdate(tweetString);
        if (inReplyToStatusId > 0) {
            statusUpdate.inReplyToStatusId(inReplyToStatusId);
        }
        if (mediaFile != null) {
            statusUpdate.media(mediaFile);
        }
        TweetPostTask tweetPostTask = new TweetPostTask(this, twitter);
        tweetPostTask.execute(statusUpdate);
    }

    public void afterPostResetView() {
        inReplyToStatusId = 0;
        isAttachment = false;
        mediaFile = null;
        tweetText.setText("");
        tweetButton.setText("つぶやく");
        tweetCount.setText(String.valueOf(Const.TWEET_COUNT_DEFAULT));
        replyText.setVisibility(View.GONE);
        thumbnail.setVisibility(View.INVISIBLE);
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(tweetButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        setTitle(getString(R.string.app_name));
    }

}