package com.anyone.smardy.motaj.badtrew.Utilites;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Utilities {
    public static void hideNavBar(View decor_View) {
        int ui_Options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decor_View.setSystemUiVisibility(ui_Options);
    }

    public static void insertEpisodeDownload (Context context , int user_id , int episode_id , String video_url) {
        ApiService apiService = ApiClient.getClient(context.getApplicationContext()).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .insertDownloadEpisode(user_id , episode_id , video_url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    Log.i("ab_do" , "onSuccess save download");
                                }
                                else {
                                    Log.i("ab_do" , "error happen when save episode download ");

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do" , "error happen when save episode download " + e.getMessage());
                            }
                        })
        );
    }
}
