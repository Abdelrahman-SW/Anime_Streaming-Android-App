package com.anyone.smardy.motaj.badtrew.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.DownloadNeededAppDialog;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.adapters.DownloadsAdapter;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityEpisodeDownloadsBinding;
import com.anyone.smardy.motaj.badtrew.model.Episode;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class EpisodeDownloadsActivity extends AppCompatActivity implements DownloadsAdapter.OnRemoveDownloadedEpisode , DownloadNeededAppDialog.ClickListener {
    ActivityEpisodeDownloadsBinding binding;
    DownloadsAdapter adapter;
    List<Episode> downloadList = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;
    int user_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        binding = ActivityEpisodeDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        binding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void init () {
        apiService = ApiClient.getClient(this).create(ApiService.class);
        LoginUtil loginUtil = new LoginUtil(this);
        user_id = loginUtil.getCurrentUser().getId() ;
        initToolbar();
        initRecyclerview();
        getEpisodeDownloads();
    }

    private void getEpisodeDownloads() {
        disposable.add(
                apiService
                        .getEpisodeDownloads(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Episode>>() {
                            @Override
                            public void onSuccess(List<Episode> episodeList) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                downloadList.clear();
                                downloadList.addAll(episodeList);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(EpisodeDownloadsActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void initRecyclerview(){
        adapter = new DownloadsAdapter(this, downloadList);
        binding.downloadedEpisodesRecyclerview.setLayoutManager(new LinearLayoutManager(EpisodeDownloadsActivity.this));
        binding.downloadedEpisodesRecyclerview.setAdapter(adapter);
    }

    private void initToolbar(){
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("التحميلات");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRemove(int pos) {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        disposable.add(
                apiService
                        .deleteDownloadEpisode(user_id , downloadList.get(pos).getId() , downloadList.get(pos).getVideo_url())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                downloadList.remove(pos);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(EpisodeDownloadsActivity.this, "حدث خطا ما !", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    @Override
    public void onGoToStoreClicked(boolean isWatch) {
        if (isWatch)
            Config.openExoPlayerOnPlayStore(this);
        else
            Config.openDownloaderAppOnPlayStore(this);

    }

    @Override
    public void onGoToWebsiteClicked(boolean isWatch) {
        if (isWatch) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.video_player_website)));
        }
        else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.video_download_website)));
        }
    }
}