package com.anyone.smardy.motaj.badtrew.Utilites;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class sharedPreferencesUtil {
    public static final String USERNAME = "username";
    public static final String IS_USER_LOGGED = "userLogged";
    public static final String LOGIN_METHOD = "loginMethod";
    public static final String PHOTO_URL = "photo_url" ;
    public static final String USER_ID = "user_id" ;
    public static final String FACEBOOK_PHOTO_URL = "facebook_photo_url" ;
    public static final String FACEBOOK_USERNAME = "facebook_username" ;
    public static final String CURRENT_PHOTO = "current_photo" ;

    private static SharedPreferences sharedPreferences ;

    private sharedPreferencesUtil() {

    }

    public static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences==null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sharedPreferences ;
    }

    public static void updateLoginMethod (Context context , LoginMethod loginMethod) {
        getSharedPreferences(context).edit().putString(LOGIN_METHOD , loginMethod.name()).apply();
    }

    public static void updateLoginStatues (Context context , boolean isLogin) {
        getSharedPreferences(context).edit().putBoolean(IS_USER_LOGGED , isLogin).apply();
    }

    public static void updateUserID (Context context , int user_id) {
        getSharedPreferences(context).edit().putInt(USER_ID , user_id).apply();
    }

    public static void updateUsername (Context context , String username) {
        getSharedPreferences(context).edit().putString(USERNAME , username).apply();
    }

    public static void updatePhotoURl (Context context , String url) {
        getSharedPreferences(context).edit().putString(PHOTO_URL , url).apply();
    }

    public static void updateFacebookUsername (Context context , String username) {
        getSharedPreferences(context).edit().putString(FACEBOOK_USERNAME , username).apply();
    }

    public static void updateFacebookPhotoURl (Context context , String url) {
        getSharedPreferences(context).edit().putString(FACEBOOK_PHOTO_URL , url).apply();
    }

    public static void updateCurrentPhoto (Context context , String url) {
        getSharedPreferences(context).edit().putString(CURRENT_PHOTO , url).apply();
    }

}
