package com.anyone.smardy.motaj.badtrew.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.Database.SQLiteDatabaseManager;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.EpisodeToServerCachedData;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.Utilites.Utilities;
import com.anyone.smardy.motaj.badtrew.adapters.EpisodesAdapter;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityEpisodesBinding;
import com.anyone.smardy.motaj.badtrew.model.Cartoon;
import com.anyone.smardy.motaj.badtrew.model.Episode;
import com.anyone.smardy.motaj.badtrew.model.Playlist;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.inside4ndroid.jresolver.Jresolver;
import com.inside4ndroid.jresolver.Model.Jmodel;
//import com.htetznaing.lowcostvideo.LowCostVideo;
//import com.htetznaing.lowcostvideo.Model.XModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.anyone.smardy.motaj.badtrew.app.Config.admob;

public class EpisodesActivity extends AppCompatActivity {

    MutableLiveData<List<Playlist>> playlistLists ;
    ActivityEpisodesBinding mBinding;

    List<Episode> episodeList = new ArrayList<>();

    Cartoon cartoon;
    Playlist playlist;
    private final int VIDEO_REQUEST_CODE = 1;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    int pageNumber = 1;

    SQLiteDatabaseManager sqLiteDatabaseManager;
    private android.widget.SearchView searchView;

    public boolean searchCase = false;

    private final int PERMISSIONS_REQUEST_STORAGE = 1;

    private EpisodesAdapter adapter;

    private int lastAdPosition = 0;

//    private InterstitialAd beforeInterstitialAd = null;
//    private InterstitialAd afterInterstitialAd = null;
    private boolean grid ;
    static int ASC = 1 ;
    static int DESC = 2 ;
    static int order ;
    Episode episode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_episodes);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String viewVal = sharedPreferences.getString(getString(R.string.view_key), getString(R.string.grid));
        grid = viewVal.equals(getString(R.string.grid));
        order = ASC ;
        initDatabase();
        getIntentData();
//        Config.showFacebookBannerAd(this, mBinding.addContainer);
        createBannerAd();
        initToolbar();
        initProgressBar();
        initRecyclerview(grid);
        initRetrofit();
        getEpisodes();
        mBinding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onResume() {
        if(adapter!=null)
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    private void initDatabase(){
        sqLiteDatabaseManager = new SQLiteDatabaseManager(EpisodesActivity.this);
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getStringExtra("title")!=null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        }
        else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
//        else
//        getSupportActionBar().setTitle("الحلقات");
//        Glide.with(this)
//                .load(playlist.getThumb())
//                .into(mBinding.toolbarImage);
//        mBinding.collapsingToolbar.setTitle(playlist.getTitle());
    }

    private void initProgressBar(){
        Circle circle = new Circle();
        mBinding.progress.setIndeterminateDrawable(circle);
    }

    private void initRetrofit(){
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void getIntentData(){
        cartoon = (Cartoon) getIntent().getSerializableExtra("cartoon");
        playlist = (Playlist) getIntent().getSerializableExtra("playlist");
    }

    private void initRecyclerview(boolean grid){
        adapter = new EpisodesAdapter(this, episodeList , grid);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        if (grid)
        mBinding.episodessRecyclerview.setLayoutManager(gridLayoutManager);
        else mBinding.episodessRecyclerview.setLayoutManager(new LinearLayoutManager(this ));
        //mBinding.episodessRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mBinding.episodessRecyclerview.setHasFixedSize(true);
        mBinding.episodessRecyclerview.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        //if(adapter!=null) adapter.notifyDataSetChanged();
        super.onStart();
    }

    public void getEpisodes(){
        disposable.add(
                apiService
                        .getEpisodes(playlist.getId(), pageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Episode>>() {
                            @Override
                            public void onSuccess(List<Episode> retrievedEpisodeList) {

                                Log.d("ab_do" , "To waatch size " + retrievedEpisodeList.size());
                                Log.d("ab_do" , "wwto " + playlist.getId());
                                if (pageNumber == 1)
                                episodeList.addAll(retrievedEpisodeList);
                                else {
                                    if (order == ASC) {
                                        episodeList.addAll(retrievedEpisodeList);
                                    }
                                    else {
                                        int index = 0 ;
                                        for (int i = retrievedEpisodeList.size() - 1 ; i >= 0 ; i--) {
                                            episodeList.add(index , retrievedEpisodeList.get(i));
                                            index++ ;
                                        }
                                    }
                                }

                                //--------------------//
                                if(episodeList.isEmpty()){
                                    mBinding.episodessRecyclerview.getAdapter().notifyDataSetChanged();
                                }
                                else{
                                    mBinding.episodessRecyclerview.getAdapter().notifyItemInserted(episodeList.size());
                                }

                                pageNumber++;
                                //mBinding.progressBarLayout.setVisibility(View.GONE);
                                getEpisodes();

                                /*int oldSize = episodeList.size();
                                episodeList.addAll(retrievedEpisodeList);
                                Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyItemRangeInserted(oldSize, episodeList.size());
                                mBinding.progressBarLayout.setVisibility(View.GONE);

                                pageNumber++;*/
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

    public String getThumb(){
        return playlist.getThumb();
    }
    public String getPlaylistTitle(){
        return playlist.getTitle();
    }
    public String getCartoonTitle(){
        return cartoon.getTitle();
    }

    public void startVideoActivity(int position, Episode episode, String episodeTitle, String thumb,
                                   String playlistTitle, String cartoonTitle){

        this.episode = episode ;
        mBinding.progressBarLayout.setVisibility(View.GONE);
//        checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
//        Config.showFacebookInterstitialAd(this, admob.getInterstitial());
        checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);

        //createInterstitialAd1(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
    }

//    private void createInterstitialAd1(final int position, final Episode episode, final String episodeTitle, final String thumb,
//                                       final String playlistTitle, final String cartoonTitle){
//
//        if(admob != null && admob.getInterstitial() != null){
//            mBinding.progressBarLayout.setVisibility(View.VISIBLE);
//
//            AdRequest adRequest = new AdRequest.Builder().build();
//
//            InterstitialAd.load(this,admob.getInterstitial(), adRequest,
//                    new InterstitialAdLoadCallback() {
//                        @Override
//                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                            mBinding.progressBarLayout.setVisibility(View.GONE);
//                            beforeInterstitialAd = interstitialAd;
//                            setBeforeAdListeners(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
//                            beforeInterstitialAd.show(EpisodesActivity.this);
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            mBinding.progressBarLayout.setVisibility(View.GONE);
//                            checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
//                        }
//                    });
//
//        }else{
//            checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
//        }
//    }

//    private void setBeforeAdListeners(final int position, final Episode episode, final String episodeTitle, final String thumb,
//                                      final String playlistTitle, final String cartoonTitle){
//        beforeInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
//            @Override
//            public void onAdDismissedFullScreenContent() {
//                // Called when fullscreen content is dismissed.
//                Log.d("TAG", "The ad was dismissed.");
//                mBinding.progressBarLayout.setVisibility(View.GONE);
//                checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
//            }
//
//            @Override
//            public void onAdFailedToShowFullScreenContent(AdError adError) {
//                // Called when fullscreen content failed to show.
//                Log.d("TAG", "The ad failed to show.");
//                mBinding.progressBarLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAdShowedFullScreenContent() {
//                // Called when fullscreen content is shown.
//                // Make sure to set your reference to null so you don't
//                // show it a second time.
//                beforeInterstitialAd = null;
//                mBinding.progressBarLayout.setVisibility(View.GONE);
//            }
//        });
//    }

    private void checkServers(final int position, final Episode episode, final String episodeTitle, final String thumb,
                              final String playlistTitle, final String cartoonTitle){
        //Log.i("ab_do" , "checkServers"+ episode.getVideo());
//        if(episode.getVideo1().isEmpty() &&
//                episode.getVideo2().isEmpty() &&
//                episode.getVideo3().isEmpty() &&
//                episode.getVideo4().isEmpty() &&
//                episode.getVideo5().isEmpty()
//        )
//        {
//
////            startVideoPlayer(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
////            Config.optionsDialog(this, episode.getVideo(), episode, playlistTitle, cartoonTitle);
//            checkjResolver(episode.getVideo(), episode, playlistTitle, cartoonTitle);
//        }
//        else  {
            openServersActivity(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
//        }
    }

    private void checkjResolver(String url, Episode episode,
                              String playlistTitle, String cartoonTitle) {
        episode.setError(false);
        //Check if needs jResolver
        if(episode.getjResolver() == 1){ //Needs extractions
            Log.i("ab_do" , "Needs extractions "+episode.getVideo());
            Jresolver jresolver = new Jresolver(this);
            jresolver.onFinish(new Jresolver.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<Jmodel> vidURL, boolean multiple_quality) {
                    if (multiple_quality){
                        //This video you can choose qualities
                        CharSequence[] qualities = new CharSequence[vidURL.size()];
                        CharSequence[] urls = new CharSequence[vidURL.size()];

                        for (int i=0; i<vidURL.size(); i++){
//                            String url = model.getUrl();
                            qualities[i] = vidURL.get(i).getQuality();
                            urls[i] = vidURL.get(i).getUrl();
                        }

                        mBinding.progressBarLayout.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodesActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("اختار جودة الحلقة");
                        builder.setItems(qualities, (dialog, which) -> {
                            Config.optionsDialog(EpisodesActivity.this, urls[which].toString(), episode, playlistTitle, cartoonTitle);
                        });

                        AlertDialog dialog = builder.create();

                        dialog.setOnShowListener(dlg -> {

                            Objects.requireNonNull(dialog.getWindow()).getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // set title and message direction to RTL
                        });

                        dialog.show();

                    }
                    else {
                        //If single
                        String url = vidURL.get(0).getUrl();

                        Config.optionsDialog(EpisodesActivity.this, url, episode, playlistTitle, cartoonTitle);
                    }
                    episode.setError(false);
                }

                @Override
                public void onError() {
                    //Error
                    episode.setError(true);
                    Toast.makeText(getApplicationContext() , "السيرفر غير متاح يرجي تجربة سيرفر آخر" , Toast.LENGTH_LONG).show();
                }
            });
            jresolver.find(episode.getVideo());

        }
        else
            {
                Log.i("ab_do" , "no Needs extractions "+episode.getVideo());
            Config.optionsDialog(this, episode.getVideo(), episode, playlistTitle, cartoonTitle);
        }
    }



    private void startVideoPlayerActivity(int position, Episode episode, Intent intent) {
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
        mBinding.progressBarLayout.setVisibility(View.GONE);
        // insert in the firebase that the user is watch this episode
        mBinding.episodessRecyclerview.getAdapter().notifyItemChanged(position);
    }

//    private void checkIfFullWatchedCartoon() {
//        pageNumber = 1 ;
//        Log.d("ab_do" , "checkIfFullWatchedCartoon");
//        disposable.add(
//                apiService
//                        .getPlaylists(cartoon.getId())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeWith(new DisposableSingleObserver<List<Playlist>>() {
//                            @Override
//                            public void onSuccess(List<Playlist> playlistList) {
//                                playlistLists.setValue(playlistList);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                            }
//                        })
//        );
//    }


    private void openServersActivity(int position, Episode episode, String episodeTitle, String thumb, String playlistTitle, String cartoonTitle) {
        EpisodeToServerCachedData.cachedEpisodeList.clear();
        EpisodeToServerCachedData.cachedEpisodeList = new ArrayList<>(episodeList) ;
        Intent intent = new Intent(EpisodesActivity.this, ServersActivity.class);
        intent.putExtra("episode", episode);
        intent.putExtra("title", episodeTitle);
        intent.putExtra("thumb", thumb);
        intent.putExtra("playlistTitle", playlistTitle);
        intent.putExtra("cartoonTitle", cartoonTitle);
        //intent.putExtra("episodeList" , (Serializable) episodeList);
        intent.putExtra("current_pos" , position);
        intent.putExtra("is_reversed" , order == DESC);
        startVideoPlayerActivity(position, episode, intent);
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

    private void searchEpisode(String query){

        disposable.add(
                apiService
                        .searchEpisodes(query, playlist.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Episode>>() {
                            @Override
                            public void onSuccess(List<Episode> retrievedEpisodeList) {

                                pageNumber = 0;
                                episodeList.clear();
                                episodeList.addAll(retrievedEpisodeList);
                                Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyDataSetChanged();

                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
//                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
                            }
                        })
        );

    }

    private void updateGridIcon(MenuItem item) {
        if (!grid) {
            item.setIcon(R.drawable.ic_baseline_grid_on_24);
            item.setTitle("شبكة");
        } else {
            item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
            item.setTitle("قائمة");
        }
    }

    //-----------Override Methods---------------//

    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        this.menu = menu;
        updateGridIcon(menu.findItem(R.id.grid_or_list));
        menu.findItem(R.id.menusearch).setVisible(false);
        menu.findItem(R.id.menu_empty_star).setVisible(false);
        menu.findItem(R.id.menu_filled_star).setVisible(false);
        if (getIntent().getStringExtra("title")!=null) {
            // films so remove favourite :)
            menu.findItem(R.id.change_order).setVisible(false);
            menu.findItem(R.id.grid_or_list).setVisible(false);
            menu.findItem(R.id.share).setVisible(false);
        }
        //Search Function
//        MenuItem search_item = menu.findItem(R.id.menusearch);
//        searchView = (android.widget.SearchView) search_item.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                if(!TextUtils.isEmpty(s)){
//                    searchCase = true;
//                    searchEpisode(s);
//                }else{
//                    searchCase = false;
//                    episodeList.clear();
//                    pageNumber = 1;
//                    Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyDataSetChanged();
//                    getEpisodes();
//                }
//                hideSoftKeyboard();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                if(!TextUtils.isEmpty(s)){
//                    searchCase = true;
//                    searchEpisode(s);
//                }
//                else{
//                    searchCase = false;
//                    episodeList.clear();
//                    pageNumber = 1;
//                    Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyDataSetChanged();
//                    getEpisodes();
//                }
//                return true;
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();//No Action
        if (itemId == android.R.id.home) {
            finish();
            if (getIntent().getStringExtra("title") != null) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        } else if (itemId == R.id.share) {
            Config.shareApp(EpisodesActivity.this);
        } else if (itemId == R.id.menu_empty_star) {
            menu.findItem(R.id.menu_empty_star).setVisible(false);
            menu.findItem(R.id.menu_filled_star).setVisible(true);
            sqLiteDatabaseManager.insertFavoriteCartoon(cartoon);
            setResult(RESULT_OK);
        } else if (itemId == R.id.menu_filled_star) {
            menu.findItem(R.id.menu_filled_star).setVisible(false);
            menu.findItem(R.id.menu_empty_star).setVisible(true);
            sqLiteDatabaseManager.deleteFavoriteCartoon(cartoon.getId());
            setResult(RESULT_OK);
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
            if (order == ASC) order = DESC ;
            else  order = ASC ;
            Collections.reverse(episodeList);
            adapter.updateList(episodeList);
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;
                String path = data.getStringExtra("animePath");
                String name = data.getStringExtra("animeName");
                Log.i("ab_do" ,"FromDownloader " + path);
                Log.i("ab_do" , "path " + path);
//                SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(this);
//                sqliteManager.insertDownload(name, path);
                LoginUtil loginUtil = new LoginUtil(getBaseContext());
                if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null)
                Utilities.insertEpisodeDownload(getBaseContext() , loginUtil.getCurrentUser().getId() , episode.getId() , path);
            }
        }

        /*if (requestCode == VIDEO_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                //show ad

            }
        }*/

       /* if (requestCode == VIDEO_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                //show ad
                createInterstitialAd2();
//                Config.showFacebookInterstitialAd(EpisodesActivity.this, admob.getInterstitial2());
            }
        }*/
//        createInterstitialAd2();ol
        super.onActivityResult(requestCode, resultCode, data);
       // createInterstitialAd2();
    }

//    private void createInterstitialAd2(){
//
//        if(admob != null && admob.getInterstitial2() != null){
//
//            AdRequest adRequest = new AdRequest.Builder().build();
//
//            InterstitialAd.load(this,admob.getInterstitial2(), adRequest,
//                    new InterstitialAdLoadCallback() {
//                        @Override
//                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                            afterInterstitialAd = interstitialAd;
//                            afterInterstitialAd.show(EpisodesActivity.this);
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        }
//                    });
//
//        }
//
//    }

    @Override
    public void onBackPressed() {
        if(searchView != null && !searchView.isIconified()){
            searchView.setIconified(true);
            hideSoftKeyboard();
        }
        else{
            EpisodesActivity.this.finish();
        }
        if (getIntent().getStringExtra("title") != null) {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case PERMISSIONS_REQUEST_STORAGE:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
                } else {

                }
                break;
        }
    }
}
