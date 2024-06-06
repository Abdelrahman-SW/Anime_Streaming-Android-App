package com.anyone.smardy.motaj.badtrew.activities;

import static com.anyone.smardy.motaj.badtrew.app.Config.isNetworkConnected;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginToMainCachedData;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.Utilites.Utilities;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.app.UserOptions;
import com.anyone.smardy.motaj.badtrew.databinding.ActivitySplashBinding;
import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;
import com.anyone.smardy.motaj.badtrew.model.EpisodeWithInfo;
import com.anyone.smardy.motaj.badtrew.model.UserData;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class splashActivity extends AppCompatActivity {
    UserOptions userOptions ;
    ActivitySplashBinding binding ;
    CompositeDisposable disposable ;
    ApiService apiService ;
    private int user_id ;
    LoginUtil loginUtil ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        View decor_View = getWindow().getDecorView();
        Utilities.hideNavBar(decor_View);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        userOptions = UserOptions.getUserOptions();
        setContentView(binding.getRoot());
        Glide.with(this)
                .asGif()
                .load(R.raw.wave_2)
                .centerCrop()
                .into(binding.splashImg);
        init();
    }
    private void  init () {
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        loginUtil = new LoginUtil(this);
        // check first if server is underMaintenance or not
        checkNetwork();
    }

    private void checkNetwork() {
        if (isNetworkConnected(this)) {
            load();
        }
        else {
            openNoNetworkActivity();
        }
    }

    private void openNoNetworkActivity() {
        startActivity(new Intent(getBaseContext() , NoNetworkActivity.class));
        finish();
    }


    private void load() {
        if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!= null) {
            // display loading text , load (favourite , later , watched )
            binding.msg.setVisibility(View.VISIBLE);
            binding.msg.setAnimation(AnimationUtils.loadAnimation(this , R.anim.fade_text));
            user_id = loginUtil.getCurrentUser().getId();
            loadUserData();
        }
        else {
            binding.msg.setVisibility(View.GONE);
            loadLatestEpisodes();
        }
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
                                LoginToMainCachedData.episodeList = episodeList ;
                                LoginToMainCachedData.userData = userData ;
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Toast.makeText(splashActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(splashActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
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
                                //Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(splashActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
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
                                loadLatestEpisodes();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(splashActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();

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
                                //Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(splashActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();

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
                                //Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(splashActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();

                            }
                        })
        );
    }

    private void loadLatestEpisodes() {
        disposable.add(
                apiService
                        .latestEpisodesWithInfo()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<EpisodeWithInfo>>() {
                            @Override
                            public void onSuccess(List<EpisodeWithInfo> retrivedEpisodeList) {
                                Log.i("splash_abdo" , "onSuccess ");
                                List<EpisodeWithInfo> episodeList = new ArrayList<>(retrivedEpisodeList);
                                Intent intent = new Intent(getBaseContext() , MainActivity.class);
                                LoginToMainCachedData.episodeList = episodeList ;
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                               Log.i("splash_abdo" , "error " + e.getMessage());
                                if (!isNetworkConnected(splashActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();

                            }
                        })
        );
    }
}