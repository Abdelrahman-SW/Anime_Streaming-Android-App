package com.anyone.smardy.motaj.badtrew.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.LeaderboardDialog;
import com.anyone.smardy.motaj.badtrew.adapters.LeaderboardAdapter;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityLeaderboardBinding;
import com.anyone.smardy.motaj.badtrew.model.User;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LeaderboardActivity extends AppCompatActivity {
    ActivityLeaderboardBinding binding ;
    private LeaderboardAdapter leaderboardAdapter;
    private boolean isRefresh;
    CompositeDisposable disposable;
    ApiService apiService;
    LeaderboardDialog leaderboardDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadLeaderboardData();
        binding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(binding.includedToolbar.toolbar);
        if (getSupportActionBar()!= null) {
            getSupportActionBar().setTitle("أبرز المستخدمين");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void init() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        initToolbar();
        leaderboardAdapter = new LeaderboardAdapter(this);
        binding.recycleView.setAdapter(leaderboardAdapter);
        binding.recycleView.setItemAnimator(null);
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        initSwipeRefresh();
        leaderboardDialog = new LeaderboardDialog(this);
    }

    private void initSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefreshLayout.setRefreshing(true);
                isRefresh = true;
                loadLeaderboardData();
            }
        });
    }

    private void loadLeaderboardData() {
        disposable.add(
                apiService.
                        getLeaderboard()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {
                            @Override
                            public void onSuccess(List<User> users) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                if (isRefresh) {
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    isRefresh = false;
                                }
                                leaderboardAdapter.submitList(users);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do" , "onError " + e.getMessage());
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(LeaderboardActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.info) {
            leaderboardDialog.showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.leadrboard_menu , menu) ;
        return super.onCreateOptionsMenu(menu);
    }
}