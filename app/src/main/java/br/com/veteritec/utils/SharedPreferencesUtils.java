package br.com.veteritec.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static final String IS_LOGGED = "logged_in";
    private static final String USER_TOKEN = "user_token";
    private static final String USER_CLINIC_ID = "user_clinic_id";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("veteritec", Context.MODE_PRIVATE);
    }

    public void setLoggedIn(Context context, boolean isLogged, String token, String clinicId) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(IS_LOGGED, isLogged);
        editor.putString(USER_TOKEN, token);
        editor.putString(USER_CLINIC_ID, clinicId);
        editor.apply();
    }

    public void setLogoff(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(IS_LOGGED, false);
        editor.putString(USER_TOKEN, "");
        editor.putString(USER_CLINIC_ID, "");
        editor.apply();
    }

    public boolean isLogged(Context context) {
        return getPreferences(context).getBoolean(IS_LOGGED, false);
    }

    public String getUserToken(Context context) {
        return getPreferences(context).getString(USER_TOKEN, "");
    }

    public String getUserClinicId(Context context) {
        return getPreferences(context).getString(USER_CLINIC_ID, "");
    }
}
