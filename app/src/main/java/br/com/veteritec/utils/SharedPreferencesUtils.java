package br.com.veteritec.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static final String IS_LOGGED = "logged_in";
    private static final String USER_NAME = "user_name";
    private static final String USER_TOKEN = "user_token";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("veteritec", Context.MODE_PRIVATE);
    }

    public void setLoggedIn (Context context, boolean isLogged, String name, String token) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(IS_LOGGED, isLogged);
        editor.putString(USER_NAME, name);
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public void setLogoff(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(IS_LOGGED, false);
        editor.putString(USER_NAME, "");
        editor.putString(USER_TOKEN, "");
        editor.apply();
    }

    public boolean isLogged(Context context) {
        return getPreferences(context).getBoolean(IS_LOGGED, false);
    }

    public String getUserName(Context context) {
        return getPreferences(context).getString(USER_NAME, "");
    }

    public String getUserToken(Context context) {
        return getPreferences(context).getString(USER_TOKEN, "");
    }
}
