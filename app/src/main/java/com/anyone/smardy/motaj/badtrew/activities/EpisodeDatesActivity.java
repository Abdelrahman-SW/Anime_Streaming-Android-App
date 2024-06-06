package com.anyone.smardy.motaj.badtrew.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.adapters.CartoonsAdapter;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityEpisodeDatesBinding;
import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;
import com.anyone.smardy.motaj.badtrew.model.EpisodeDate;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class EpisodeDatesActivity extends AppCompatActivity {
    ActivityEpisodeDatesBinding binding;
    // cartoon lists from saturday to friday
    private final List<CartoonWithInfo> list1 = new ArrayList<>();
    private final List<CartoonWithInfo> list2 = new ArrayList<>();
    private final List<CartoonWithInfo> list3 = new ArrayList<>();
    private final List<CartoonWithInfo> list4 = new ArrayList<>();
    private final List<CartoonWithInfo> list5 = new ArrayList<>();
    private final List<CartoonWithInfo> list6 = new ArrayList<>();
    private final List<CartoonWithInfo> list7 = new ArrayList<>();
    private CartoonsAdapter adapter1, adapter2, adapter3, adapter4, adapter5, adapter6, adapter7;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;
    boolean isGrid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        binding = ActivityEpisodeDatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        init();
        initRetrofit();
        getEpisodeDates();
        binding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getEpisodeDates() {
        disposable.add(
                apiService
                        .getEpisodeDates()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<EpisodeDate>>() {
                            @Override
                            public void onSuccess(List<EpisodeDate> episodeDates) {
                                for (int i = 0; i < episodeDates.size(); i++) {
                                    switch (episodeDates.get(i).getDay()) {
                                        case 1:
                                            list1.add(getCartoonObjectFromEpisodeDate(episodeDates.get(i)));
                                            break;

                                        case 2:
                                            list2.add(getCartoonObjectFromEpisodeDate(episodeDates.get(i)));
                                            break;

                                        case 3:
                                            list3.add(getCartoonObjectFromEpisodeDate(episodeDates.get(i)));
                                            break;

                                        case 4:
                                            list4.add(getCartoonObjectFromEpisodeDate(episodeDates.get(i)));
                                            break;

                                        case 5:
                                            list5.add(getCartoonObjectFromEpisodeDate(episodeDates.get(i)));
                                            break;

                                        case 6:
                                            list6.add(getCartoonObjectFromEpisodeDate(episodeDates.get(i)));
                                            break;

                                        case 7:
                                            list7.add(getCartoonObjectFromEpisodeDate(episodeDates.get(i)));
                                            break;
                                    }
                                }
                                binding.swipeRefreshLayout.setRefreshing(false);
                                binding.progressBarLayout.setVisibility(View.GONE);
                                notifyAllAdapters();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.swipeRefreshLayout.setRefreshing(false);
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void notifyAllAdapters() {
        adapter1.updateList(list1);
        adapter2.updateList(list2);
        adapter3.updateList(list3);
        adapter4.updateList(list4);
        adapter5.updateList(list5);
        adapter6.updateList(list6);
        adapter7.updateList(list7);
    }

    private CartoonWithInfo getCartoonObjectFromEpisodeDate(EpisodeDate episodeDate) {
        CartoonWithInfo cartoon = new CartoonWithInfo();
        cartoon.setEpisodeDateTitle(episodeDate.getName());
        cartoon.setId(episodeDate.getCartoon_id());
        cartoon.setType(episodeDate.getType());
        cartoon.setView_date(episodeDate.getView_date());
        cartoon.setStatus(episodeDate.getStatus());
        cartoon.setWorld_rate(episodeDate.getWorld_rate());
        cartoon.setTitle(episodeDate.getTitle());
        cartoon.setThumb(episodeDate.getThumb());
        cartoon.setClassification(episodeDate.getClassification());
        cartoon.setCategory(episodeDate.getCategory());
        cartoon.setAge_rate(episodeDate.getAge_rate());
        return cartoon;
    }


    private void initRetrofit() {
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void init() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String viewVal = sharedPreferences.getString(getString(R.string.view_key), getString(R.string.grid));
        isGrid = viewVal.equals(getString(R.string.grid));
        initToolbar();
        initSwipeRefreshLayout();
        prepareRecycleViews();
    }

    private void initSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefreshLayout.setRefreshing(true);
                clearALLData();
                getEpisodeDates();
            }
        });
    }

    private void clearALLData() {
        list1.clear();
        list2.clear();
        list3.clear();
        list4.clear();
        list5.clear();
        list6.clear();
        list7.clear();
    }

    private void initToolbar() {
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setTitle("مواعيد الحلقات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void assignRecycleViewsAdapter() {
        binding.recyclerview1.setAdapter(adapter1);
        binding.recyclerview2.setAdapter(adapter2);
        binding.recyclerview3.setAdapter(adapter3);
        binding.recyclerview4.setAdapter(adapter4);
        binding.recyclerview5.setAdapter(adapter5);
        binding.recyclerview6.setAdapter(adapter6);
        binding.recyclerview7.setAdapter(adapter7);
    }

    private void initRecycleView(RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        if (isGrid) {
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
        recyclerView.setHasFixedSize(true);
    }

    private void prepareRecycleViews() {
        initRecycleView(binding.recyclerview1);
        initRecycleView(binding.recyclerview2);
        initRecycleView(binding.recyclerview3);
        initRecycleView(binding.recyclerview4);
        initRecycleView(binding.recyclerview5);
        initRecycleView(binding.recyclerview6);
        initRecycleView(binding.recyclerview7);
        initAdapters();
        assignRecycleViewsAdapter();
    }

    private void initAdapters() {
        adapter1 = new CartoonsAdapter(this, isGrid, true);
        adapter2 = new CartoonsAdapter(this, isGrid, true);
        adapter3 = new CartoonsAdapter(this, isGrid, true);
        adapter4 = new CartoonsAdapter(this, isGrid, true);
        adapter5 = new CartoonsAdapter(this, isGrid, true);
        adapter6 = new CartoonsAdapter(this, isGrid, true);
        adapter7 = new CartoonsAdapter(this, isGrid, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.grid_or_list) {
            isGrid = !isGrid;
            updateGridIcon(item);
            prepareRecycleViews();
            notifyAllAdapters();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateGridIcon(MenuItem item) {
        if (!isGrid) {
            item.setIcon(R.drawable.ic_baseline_grid_on_24);
            item.setTitle("شبكة");
        } else {
            item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
            item.setTitle("قائمة");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.episode_date_menu, menu);
        updateGridIcon(menu.findItem(R.id.grid_or_list));
        return super.onCreateOptionsMenu(menu);
    }

}