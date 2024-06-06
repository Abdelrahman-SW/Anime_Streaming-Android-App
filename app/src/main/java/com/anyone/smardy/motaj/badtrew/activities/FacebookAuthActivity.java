package com.anyone.smardy.motaj.badtrew.activities;

import static com.anyone.smardy.motaj.badtrew.app.Config.isNetworkConnected;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginMethod;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.app.UserOptions;
import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;
import com.anyone.smardy.motaj.badtrew.model.EpisodeWithInfo;
import com.anyone.smardy.motaj.badtrew.model.User;
import com.anyone.smardy.motaj.badtrew.model.UserData;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FacebookAuthActivity extends AppCompatActivity {

    private static final String TAG = "ab_do";
    CallbackManager callbackManager ;
    LoginUtil loginUtil ;
    CompositeDisposable disposable ;
    ApiService apiService ;
    private int user_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ab_do" , "create");
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        loginUtil = new LoginUtil(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends" , "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("ab_do" , "onSuccess");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("ab_do" , "onCancel");
                        facebookFailure(null);
                    }

                    @Override
                    public void onError(@NonNull FacebookException exception) {
                        // App code
                        Log.d("ab_do" , "onError");
                        facebookFailure(exception);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                            String photo_url = "";
                        try {
                            String name = object.getString("name");
//                            String photo_url = object.getJSONObject("picture").getJSONObject("data")
//                                    .getString("url");
                            if (Profile.getCurrentProfile()!=null && Profile.getCurrentProfile().getId() !=null) {
                                photo_url = "http://graph.facebook.com/" + Profile.getCurrentProfile().getId() + "/picture?type=large";
                            }
                            createUserWithFacebook(token.getUserId(), name , photo_url);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            createUserWithFacebook(token.getUserId() , "None" , photo_url);
                        }
                        // Application code
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Load(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            facebookFailure(task.getException());
//                        }
//                    }
//                });
    }

    private void createUserWithFacebook(String token, String name, String photo_url) {
        Log.i("ab_do_facebook" , "token " + token);
        Log.i("ab_do_facebook" , "name " + name);
        Log.i("ab_do_facebook" , "photo_url " + photo_url);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .createNewUserWithToken(token , "", name, photo_url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.isError()) {
                                    user_id = userResponse.getUser().getId();
                                    if (userResponse.getCode() == Constants.USER_ALREADY_EXISTS) {
                                        if (userResponse.getUser().getIs_blocked() == User.IS_BLOCKED) {
                                            loginUtil.signOut();
                                            Toast.makeText(FacebookAuthActivity.this, "تم حظر هذا الحساب ! لا يمكنك تسجيل الدخول", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        loginUtil.saveLoginInformation(LoginMethod.FACEBOOK , userResponse.getUser().getName() , userResponse.getUser().getPhoto_url() , userResponse.getUser().getId());
                                        loadUserData();
                                    }
                                    else {
                                        loginUtil.saveLoginInformation(LoginMethod.FACEBOOK , name , photo_url , userResponse.getUser().getId());
                                        Load();
                                    }
                                }
                                else {
                                    facebookFailure(null);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                facebookFailure(null);
                            }
                        })
        );
    }


    private void facebookFailure(Exception exception) {
        if (exception != null)
        Log.d("ab_do" , "facebookFailure " +  exception.getMessage());
        finish();
        Toast.makeText(getApplicationContext(), "حدث خطأ ما",
                Toast.LENGTH_SHORT).show();
    }

    private void Load() {
        Intent intent = new Intent(getBaseContext() , MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadWatchLaterCartoons() {
        disposable.add(
                apiService
                        .getAllWatchedLaterCartoons(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                UserOptions.getUserOptions().setWatchLaterCartoons(retrievedCartoonList);
                                loadSeenEpisodes();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //loadSeenEpisodes();
                                Toast.makeText(FacebookAuthActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }
    private void loadUserData() {
        disposable.add(
                apiService
                        .LoadUserData(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserData>() {
                            @Override
                            public void onSuccess(UserData userData) {
                                List<EpisodeWithInfo> episodeList = new ArrayList<>(userData.getLatestEpisodes());
                                Intent intent = new Intent(getBaseContext() , MainActivity.class);
                                intent.putExtra("list" , (Serializable) episodeList);
                                intent.putExtra("data" , userData);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(FacebookAuthActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(FacebookAuthActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void openNoNetworkActivity() {
        startActivity(new Intent(getBaseContext() , NoNetworkActivity.class));
        finish();
    }

    private void loadSeenEpisodes() {
        disposable.add(
                apiService
                        .getAllSeenEpisodes(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> retrievedEpisodesIdsList) {
                                UserOptions.getUserOptions().setSeenEpisodesIds(retrievedEpisodesIdsList);
                                Load();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Load();
                                Toast.makeText(FacebookAuthActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadWatchedCartoons() {
        disposable.add(
                apiService
                        .getAllWatchedCartoons(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                UserOptions.getUserOptions().setWatchedCartoons(retrievedCartoonList);
                                loadWatchLaterCartoons();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //loadWatchLaterCartoons();
                                Toast.makeText(FacebookAuthActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadFavouriteCartoons() {
        disposable.add(
                apiService
                        .getAllFavouriteCartoons(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                UserOptions.getUserOptions().setFavouriteCartoons(retrievedCartoonList);
                                loadWatchedCartoons();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //loadWatchedCartoons();
                                Toast.makeText(FacebookAuthActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

}