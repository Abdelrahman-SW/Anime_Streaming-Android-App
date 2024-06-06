package com.anyone.smardy.motaj.badtrew.Utilites;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.anyone.smardy.motaj.badtrew.model.User;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LoginUtil {
    Context context ;

    public LoginUtil(Context context) {
        this.context = context;
    }


    public boolean userIsLoggedIN () {
       SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
       if (!sharedPreferences.getBoolean(sharedPreferencesUtil.IS_USER_LOGGED , false)) {
           return false;
       }
       String loginMethod = getLoginMethod();

       if (loginMethod.equals(LoginMethod.EMAIL.name())) {
           return true ;
       }

       else if (loginMethod.equals(LoginMethod.GOOGLE.name())) {
            return GoogleSignIn.getLastSignedInAccount(context) != null ;
       }

        else if (loginMethod.equals(LoginMethod.FACEBOOK.name())) {
            return (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired());
       }
        return false ;
    }


    public User getCurrentUser() {

        if (!userIsLoggedIN()) return null ;

        String loginMethod = getLoginMethod();
        if (loginMethod.equals(LoginMethod.EMAIL.name())) {
            User user = new User();
            user.setName(getUsername());
            user.setPhoto_url(getCurrentPhotoUrl());
            user.setId(getId());
            return user ;
        }

        else if (loginMethod.equals(LoginMethod.GOOGLE.name())) {
            User user = GoogleAuth.getCurrentUserFromGoogleAccount(context);
            if (user!=null) {
                user.setId(getId());
                user.setPhoto_url(getCurrentPhotoUrl());
            }
            return user ;
        }

        else if (loginMethod.equals(LoginMethod.FACEBOOK.name())) {
            User user = new User();
            user.setName(getUsernameFromFacebook());
            user.setPhoto_url(getCurrentPhotoUrl());
            user.setId(getId());
            return user ;
        }
        return null ;
    }

    private String getCurrentPhotoUrl() {
        SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
        return sharedPreferences.getString(sharedPreferencesUtil.CURRENT_PHOTO , "");
    }


    private String getPhotoUrlFromFacebook() {
        SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
        return sharedPreferences.getString(sharedPreferencesUtil.FACEBOOK_PHOTO_URL , "");
    }

    private String getUsernameFromFacebook() {
        SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
        return sharedPreferences.getString(sharedPreferencesUtil.FACEBOOK_USERNAME , "");

    }

    public String getLoginMethod () {
        SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
        return sharedPreferences.getString(sharedPreferencesUtil.LOGIN_METHOD , "");
    }


    private String getUsername () {
        SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
        return sharedPreferences.getString(sharedPreferencesUtil.USERNAME , "");
    }

    private String getPhotoUrl() {
        SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
        return sharedPreferences.getString(sharedPreferencesUtil.PHOTO_URL , "");
    }

    private int getId() {
        SharedPreferences sharedPreferences = sharedPreferencesUtil.getSharedPreferences(context);
        return sharedPreferences.getInt(sharedPreferencesUtil.USER_ID , -1);
    }

    public void saveLoginInformation(LoginMethod loginMethod , String username , String photoUrl , int userId) {
        Log.i("ab_do" , " save information of account type : " + loginMethod.name() + " "
        + "username " + username + " " + "photo url : " + photoUrl +" "
        + "user id " + userId );
        sharedPreferencesUtil.updateLoginStatues(context , true);
        sharedPreferencesUtil.updateLoginMethod(context , loginMethod);
        if (loginMethod.equals(LoginMethod.EMAIL)) {
            sharedPreferencesUtil.updateUsername(context, username);
            sharedPreferencesUtil.updatePhotoURl(context, photoUrl);
        }
        else if (loginMethod.equals(LoginMethod.FACEBOOK)) {
            sharedPreferencesUtil.updateFacebookUsername(context, username);
            sharedPreferencesUtil.updateFacebookPhotoURl(context, photoUrl);
        }
        sharedPreferencesUtil.updateUserID(context , userId);
        sharedPreferencesUtil.updateCurrentPhoto(context , photoUrl);
    }

    public void signOut() {
        //
        String loginMethod = getLoginMethod();

        if (loginMethod.equals(LoginMethod.GOOGLE.name())) {
            GoogleSignInClient client = GoogleAuth.getGoogleSignInClient(context);
            client.signOut();
        }

        else if (loginMethod.equals(LoginMethod.FACEBOOK.name())) {
            LoginManager.getInstance().logOut();
        }
        sharedPreferencesUtil.updateLoginStatues(context , false);
        sharedPreferencesUtil.updateLoginMethod(context , LoginMethod.NONE);
        sharedPreferencesUtil.updateUserID(context,-1);
    }

}


