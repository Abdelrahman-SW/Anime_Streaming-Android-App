package com.anyone.smardy.motaj.badtrew.activities;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.Database.SQLiteDatabaseManager;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.adapters.PlaylistsAdapter;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityPlayListsBinding;
import com.anyone.smardy.motaj.badtrew.model.Cartoon;
import com.anyone.smardy.motaj.badtrew.model.Playlist;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.anyone.smardy.motaj.badtrew.app.Config.admob;

public class PlayListsActivity extends AppCompatActivity {

    private final String TAG = PlayListsActivity.class.getSimpleName();

    ActivityPlayListsBinding mBinding;

    List<Playlist> playlistList = new ArrayList<>();
    List<Playlist> playlistListBackup = new ArrayList<>();

    Cartoon cartoon;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;
    SQLiteDatabaseManager sqliteManager;
    private android.widget.SearchView searchView;
    private final int openEpisodesRequestCode = 1;

    boolean grid = true ;
    private PlaylistsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_lists);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String viewVal = sharedPreferences.getString(getString(R.string.view_key), getString(R.string.grid));
        grid = viewVal.equals(getString(R.string.grid));
        createBannerAd();
//        Config.showFacebookBannerAd(this, mBinding.addContainer);
        getIntentData();
        initToolbar();
        initSqliteDatabase();
        initProgressBar();
        initRecyclerview(grid);
        initRetrofit();
        getPlaylists();
        mBinding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSqliteDatabase(){
        sqliteManager = new SQLiteDatabaseManager(PlayListsActivity.this);
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle("الأجزاء");
//        Glide.with(this)
//                .load(cartoon.getThumb())
//                .into(mBinding.toolbarImage);
//        mBinding.collapsingToolbar.setTitle(cartoon.getTitle());
    }

    private void initProgressBar(){
        Circle circle = new Circle();
        mBinding.progress.setIndeterminateDrawable(circle);
    }

    private void getIntentData(){
        cartoon = (Cartoon) getIntent().getSerializableExtra("cartoon");
    }

    private void initRetrofit(){
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void initRecyclerview(boolean grid){
        adapter = new PlaylistsAdapter(this, playlistList, grid);
        if (grid) {
            //anim = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.recycle_anim);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mBinding.playlistsRecyclerview.setLayoutManager(gridLayoutManager);
        }
        else {
            mBinding.playlistsRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            //anim = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.recycle_anim);
        }
        mBinding.playlistsRecyclerview.setAdapter(adapter);
        //mBinding.episodesRecyclerview.setLayoutAnimation(anim)
    }

    private void createBannerAd(){
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);

        if(admob != null && admob.getBanner() != null){
            mAdView.setAdUnitId(admob.getBanner());

            if(mBinding.addContainer != null){
                ((LinearLayout)mBinding.addContainer).addView(mAdView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    private void getPlaylists(){
        disposable.add(
                apiService
                        .getPlaylists(cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Playlist>>() {
                            @Override
                            public void onSuccess(List<Playlist> playlistList) {
                                PlayListsActivity.this.playlistList.addAll(playlistList);
                                PlayListsActivity.this.playlistListBackup.addAll(playlistList);
                                if(playlistList.size() == 1){
                                    Intent intent = new Intent(PlayListsActivity.this, EpisodesActivity.class);
                                    intent.putExtra("playlist", playlistList.get(0));
                                    intent.putExtra("cartoon", cartoon);
                                    PlayListsActivity.this.startActivity(intent);
                                    PlayListsActivity.this.finish();
                                }
                                else{
                                    mBinding.playlistsRecyclerview.getAdapter().notifyDataSetChanged();
                                    mBinding.progressBarLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                            }
                        })
        );

    }

    public void progressBar(View view) {
        Toast.makeText(this, "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
    }

    public String getCartoonTitle(){
        return cartoon.getTitle();
    }

    public void openEpisodesActivity(Playlist playlist) {
        hideSoftKeyboard();
        Intent intent = new Intent(this, EpisodesActivity.class);
        intent.putExtra("cartoon", cartoon);
        intent.putExtra("playlist", playlist);
        startActivityForResult(intent, openEpisodesRequestCode);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();

        if (focusedView != null) {
            try{
                assert inputManager != null;
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }catch(AssertionError e){
                e.printStackTrace();
            }
        }
    }

    private void searchPlaylist(String query){

        playlistList.clear();
        for(Playlist playlist : playlistListBackup){
            if(playlist.getTitle().toLowerCase().contains(query.toLowerCase()))
                playlistList.add(playlist);
        }

        Objects.requireNonNull(mBinding.playlistsRecyclerview.getAdapter()).notifyDataSetChanged();
    }

    //-----------Override Methods---------------//


    private void updateGridIcon(MenuItem item) {
        if (!grid) {
            item.setIcon(R.drawable.ic_baseline_grid_on_24);
            item.setTitle("شبكة");
        } else {
            item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
            item.setTitle("قائمة");
        }
    }

    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        this.menu = menu;
        updateGridIcon(menu.findItem(R.id.grid_or_list));
        menu.findItem(R.id.menusearch).setVisible(false);
        menu.findItem(R.id.menu_empty_star).setVisible(false);
        menu.findItem(R.id.menu_filled_star).setVisible(false);
        //Search Function
        MenuItem search_item = menu.findItem(R.id.menusearch);
        searchView = (android.widget.SearchView) search_item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchPlaylist(s);
                hideSoftKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchPlaylist(s);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            if (getIntent().getAction() != null && getIntent().getAction().equals("Films"))
                startActivity(new Intent(getBaseContext(), MainActivity.class));
        } else if (itemId == R.id.share) {
            Config.shareApp(PlayListsActivity.this);
        } else if (itemId == R.id.menu_empty_star) {
            menu.findItem(R.id.menu_empty_star).setVisible(false);
            menu.findItem(R.id.menu_filled_star).setVisible(true);
            sqliteManager.insertFavoriteCartoon(cartoon);
        } else if (itemId == R.id.menu_filled_star) {
            menu.findItem(R.id.menu_filled_star).setVisible(false);
            menu.findItem(R.id.menu_empty_star).setVisible(true);
            sqliteManager.deleteFavoriteCartoon(cartoon.getId());
        }
        else if (item.getItemId() == R.id.grid_or_list) {
            if (grid) {
                item.setIcon(R.drawable.ic_baseline_grid_on_24);
                item.setTitle("شبكة");
            }
            else {
                item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
                item.setTitle("قائمة");
            }
            grid = !grid ;
            initRecyclerview(grid);
        }
        else if (item.getItemId() == R.id.change_order) {
            Collections.reverse(playlistList);
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == openEpisodesRequestCode){
                if(sqliteManager.isCartoonFavorite(cartoon.getId())){
                    menu.findItem(R.id.menu_empty_star).setVisible(false);
                    menu.findItem(R.id.menu_filled_star).setVisible(true);
                }else{
                    menu.findItem(R.id.menu_filled_star).setVisible(false);
                    menu.findItem(R.id.menu_empty_star).setVisible(true);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
