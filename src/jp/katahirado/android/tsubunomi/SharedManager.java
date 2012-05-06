package jp.katahirado.android.tsubunomi;

import android.content.SharedPreferences;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.auth.AccessToken;

/**
 * Created with IntelliJ IDEA.
 * Author: yuichi_katahira
 */
public class SharedManager {
    private SharedPreferences sharedPreferences;


    public SharedManager(SharedPreferences preferences) {
        sharedPreferences=preferences;
    }


    public boolean isConnected() {
        return sharedPreferences.getString(Const.PREF_KEY_TOKEN,null) !=null;
    }

    public void saveOAuth(AccessToken accessToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Const.PREF_KEY_TOKEN,accessToken.getToken());
        editor.putString(Const.PREF_KEY_SECRET,accessToken.getTokenSecret());
        editor.commit();
    }

    public void removeOAuth() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Const.PREF_KEY_TOKEN);
        editor.remove(Const.PREF_KEY_SECRET);
        editor.commit();
    }

    public String getPrefString(String prefKey, String s) {
        return sharedPreferences.getString(prefKey,s);
    }

    public void setTwitterAPIConfiguration(TwitterAPIConfiguration configuration) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Const.CHARACTERS_RESERVED_PER_MEDIA, configuration.getCharactersReservedPerMedia());
        editor.putInt(Const.SHORT_URL_LENGTH, configuration.getShortURLLength());
        editor.putInt(Const.SHORT_URL_LENGTH_HTTPS, configuration.getShortURLLengthHttps());
        editor.putInt(Const.MAX_MEDIA_PER_UPLOAD, configuration.getMaxMediaPerUpload());
        editor.putInt(Const.PHOTO_SIZE_LIMIT,configuration.getPhotoSizeLimit());
        editor.commit();
    }

    public int getPrefInt(String prefKey,int i) {
        return sharedPreferences.getInt(prefKey,i);
    }
}
