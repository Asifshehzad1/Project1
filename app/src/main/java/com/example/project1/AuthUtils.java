package com.example.project1;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthUtils {
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String TOKEN_KEY = "auth_token";

    public static String getToken(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_KEY,null);
    }
    public static void saveToken(Context context,String token){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(TOKEN_KEY,token);
        editor.apply();
    }

    public static void clearToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
    }
}
