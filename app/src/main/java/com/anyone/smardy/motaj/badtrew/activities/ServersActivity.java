package com.anyone.smardy.motaj.badtrew.activities;

import static com.anyone.smardy.motaj.badtrew.app.Config.isNetworkConnected;
import static com.anyone.smardy.motaj.badtrew.app.Config.minSdk29;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.DownloadNeededAppDialog;
import com.anyone.smardy.motaj.badtrew.Utilites.EpisodeToServerCachedData;
import com.anyone.smardy.motaj.badtrew.Utilites.ServerReportDialog;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityServersBinding;
import com.anyone.smardy.motaj.badtrew.model.Episode;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.material.snackbar.Snackbar;
import com.inside4ndroid.jresolver.Jresolver;
import com.inside4ndroid.jresolver.Model.Jmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ServersActivity extends AppCompatActivity implements DownloadNeededAppDialog.ClickListener {

    ActivityServersBinding mBinding;
    Episode episode;

    boolean server1 = true;
    boolean server2 = true;
    boolean server3 = true;
    boolean server4 = true;
    boolean server5 = true;
    boolean server6 = true;
    int WATCH_ACTION = 100;
    int DOWNLOAD_ACTION = 101;
    int Action = -1;
    int current_pos = -1;
    List<Episode> episodeList;
    ServerReportDialog reportDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_servers);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int theme_id = sharedPreferences.getInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.default_theme));
        if (theme_id == getResources().getInteger(R.integer.black_theme)) {
            changeIconsToWhite();
        }
        reportDialog = new ServerReportDialog(this);
        getIntentData();
        initToolbar();
        checkSeversAvailability();
        setListeners();
        mBinding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void changeIconsToWhite() {
        mBinding.play1.setImageResource(R.drawable.play_white);
        mBinding.play2.setImageResource(R.drawable.play_white);
        mBinding.play3.setImageResource(R.drawable.play_white);
        mBinding.play4.setImageResource(R.drawable.play_white);
        mBinding.play5.setImageResource(R.drawable.play_white);
        mBinding.play6.setImageResource(R.drawable.play_white);
        mBinding.download1.setImageResource(R.drawable.download_white);
        mBinding.download2.setImageResource(R.drawable.download_white);
        mBinding.download3.setImageResource(R.drawable.download_white);
        mBinding.download4.setImageResource(R.drawable.download_white);
        mBinding.download5.setImageResource(R.drawable.download_white);
        mBinding.download6.setImageResource(R.drawable.download_white);
    }

    private void setListeners() {
        mBinding.play1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION;
                openServer1();
            }
        });
        mBinding.play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION;
                openServer2();
            }
        });
        mBinding.play3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION;
                openServer3();
            }
        });
        mBinding.play4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION;
                openServer4();
            }
        });
        mBinding.play5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION;
                openServer5();
            }
        });
        mBinding.play6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION;
                openServer6();
            }
        });
        //------------------------------------------------------------------//
        mBinding.download1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION;
                openServer1();
            }
        });
        mBinding.download2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION;
                openServer2();
            }
        });
        mBinding.download3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION;
                openServer3();
            }
        });
        mBinding.download4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION;
                openServer4();
            }
        });
        mBinding.download5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION;
                openServer5();
            }
        });
        mBinding.download6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION;
                openServer6();
            }
        });

        if (getIntent().getAction()!=null && getIntent().getAction().equals("Film")) return;
        if (getIntent().getAction()!=null && getIntent().getAction().equals("downloads")) return;
        if (episodeList.size()<=1) {
            return;
        }
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_pos == 0) return;
                current_pos -= 1;
                checkServers();
            }
        });
        mBinding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_pos >= episodeList.size() - 1) return;
                current_pos += 1;
                checkServers();
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle(episode.getTitle());
        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        if (getSupportActionBar()!=null)
            mBinding.title.setText(episode.getTitle());
    }

    private void getIntentData() {
        episode = (Episode) getIntent().getSerializableExtra("episode");
        if (getIntent().getAction()!=null && (getIntent().getAction().equals("Film") || getIntent().getAction().equals("downloads"))) {
            mBinding.back.setVisibility(View.GONE);
            mBinding.next.setVisibility(View.GONE);
            return;
        }
        current_pos = getIntent().getIntExtra("current_pos", -1);
        episodeList = EpisodeToServerCachedData.cachedEpisodeList;
        if (episodeList.size()<=1) {
            mBinding.back.setVisibility(View.GONE);
            mBinding.next.setVisibility(View.GONE);
            return;
        }
        updateBackNextBtn();
    }

    private void updateBackNextBtn() {
        mBinding.back.setEnabled(current_pos > 0);
        mBinding.next.setEnabled(current_pos < episodeList.size() - 1);
    }

    private void checkSeversAvailability() {
        Log.i("ab_do" , "checkSeversAvailability " + episode.getTitle());
        mBinding.progressBarLayout.setVisibility(View.GONE);
        if (episode.getVideo() == null || episode.getVideo().isEmpty()) {
            //mBinding.llServer1.setVisibility(View.GONE);
            mBinding.llServer1.setEnabled(false);
            mBinding.play1.setEnabled(false);
            mBinding.download1.setEnabled(false);
            mBinding.active1.setImageResource(R.drawable.not_active);
            server1 = false;
        }

        else {
            mBinding.llServer1.setEnabled(true);
            mBinding.active1.setImageResource(R.drawable.active);
            mBinding.play1.setEnabled(true);
            mBinding.download1.setEnabled(true);
            server1 = true;
        }

        if (episode.getVideo1() == null || episode.getVideo1().isEmpty()) {
            mBinding.llServer2.setEnabled(false);
            mBinding.active2.setImageResource(R.drawable.not_active);
            mBinding.play2.setEnabled(false);
            mBinding.download2.setEnabled(false);
            server2 = false;
        }

        else {
            mBinding.llServer2.setEnabled(true);
            mBinding.active2.setImageResource(R.drawable.active);
            mBinding.play2.setEnabled(true);
            mBinding.download2.setEnabled(true);
            server2 = true;
        }

        if (episode.getVideo2() == null || episode.getVideo2().isEmpty()) {
            mBinding.llServer3.setEnabled(false);
            mBinding.active3.setImageResource(R.drawable.not_active);
            mBinding.play3.setEnabled(false);
            mBinding.download3.setEnabled(false);
            server3 = false;
        }

        else {
            mBinding.llServer3.setEnabled(true);
            mBinding.active3.setImageResource(R.drawable.active);
            server3 = true;
            mBinding.play3.setEnabled(true);
            mBinding.download3.setEnabled(true);
        }

        if (episode.getVideo3() == null || episode.getVideo3().isEmpty()) {
            mBinding.llServer4.setEnabled(false);
            mBinding.active4.setImageResource(R.drawable.not_active);
            mBinding.play4.setEnabled(false);
            mBinding.download4.setEnabled(false);
            server4 = false;
        }

        else {
            mBinding.llServer4.setEnabled(true);
            mBinding.active4.setImageResource(R.drawable.active);
            server4 = true;
            mBinding.play4.setEnabled(true);
            mBinding.download4.setEnabled(true);
        }


        if (episode.getVideo4() == null || episode.getVideo4().isEmpty()) {
            mBinding.llServer5.setEnabled(false);
            mBinding.active5.setImageResource(R.drawable.not_active);
            mBinding.play5.setEnabled(false);
            mBinding.download5.setEnabled(false);
            server5 = false;
        }

        else {
            mBinding.llServer5.setEnabled(true);
            mBinding.active5.setImageResource(R.drawable.active);
            server5 = true;
            mBinding.play5.setEnabled(true);
            mBinding.download5.setEnabled(true);
        }


        if (episode.getVideo5() == null || episode.getVideo5().isEmpty()) {
            mBinding.llServer6.setEnabled(false);
            mBinding.active6.setImageResource(R.drawable.not_active);
            mBinding.play6.setEnabled(false);
            mBinding.download6.setEnabled(false);
            server6 = false;
        }

        else {
            mBinding.llServer6.setEnabled(true);
            mBinding.active6.setImageResource(R.drawable.active);
            mBinding.play6.setEnabled(true);
            mBinding.download6.setEnabled(true);
            server6 = true;
        }
        checkIfAllServerIsDisabled();
    }

    private void checkIfAllServerIsDisabled() {
        // display dialog to report :
        if (!server1&&!server2&&!server3&&!server4&&!server5&&!server6) {
            reportDialog.setEpisode_id(episode.getId());
            reportDialog.setEpisode_name(episode.getTitle());
            reportDialog.setPlaylist_name(getIntent().getStringExtra("playlistTitle"));
            reportDialog.setCartoon_name(getIntent().getStringExtra("cartoonTitle"));
            reportDialog.showDialog();
        }
    }

    public void openServer1() {
        serverClicked(1, episode.getVideo(), episode.getjResolver());
    }

    public void openServer2() {
        if (episode.getVideo1().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(2, episode.getVideo1(), episode.getjResolver1());
    }

    public void openServer3() {
        if (episode.getVideo2().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(3, episode.getVideo2(), episode.getjResolver2());
    }

    public void openServer4() {
        if (episode.getVideo3().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(4, episode.getVideo3(), episode.getjResolver3());

    }

    public void openServer5() {

        if (episode.getVideo4().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(5, episode.getVideo4(), episode.getjResolver4());
    }

    public void openServer6() {
        if (episode.getVideo4().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(6, episode.getVideo5(), episode.getjResolver5());

    }

    private void serverClicked(int serverNumber, String videoUrl, int needsXGetter) {
        Log.i("ab_do", "openVideoPlayer");
        episode.setError(false);
        if (!isNetworkConnected(ServersActivity.this)) {
            Toast.makeText(ServersActivity.this, "من فضلك تأكد من اتصالك بالانترنت", Toast.LENGTH_SHORT).show();
        }
        else {
            mBinding.progressBarLayout.setVisibility(View.VISIBLE);
            //Check if needs xgetter
            if (needsXGetter == 1) { //Needs extractions
                if (Action == DOWNLOAD_ACTION)
                    if (IsBlockedUrl(videoUrl)) {
                        mBinding.progressBarLayout.setVisibility(View.GONE);
                        return;
                    }
                Log.i("ab_do", "needsXGetter");
                Jresolver jresolver = new Jresolver(this);
                jresolver.onFinish(new Jresolver.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<Jmodel> vidURL, boolean multiple_quality) {
                        if (multiple_quality) {
                            //This video you can choose qualities
                            CharSequence[] qualities = new CharSequence[vidURL.size()];
                            CharSequence[] urls = new CharSequence[vidURL.size()];

                            for (int i = 0; i < vidURL.size(); i++) {
//                            String url = model.getUrl();
                                qualities[i] = vidURL.get(i).getQuality();
                                urls[i] = vidURL.get(i).getUrl();
                            }
                            mBinding.progressBarLayout.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(ServersActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("اختار جودة الحلقة");
                            builder.setItems(qualities, (dialog, which) -> {
                                handleAction(serverNumber, urls[which].toString(), Action , true);
                            });

                            AlertDialog dialog = builder.create();

                            dialog.setOnShowListener(dlg -> {

                                Objects.requireNonNull(dialog.getWindow()).getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // set title and message direction to RTL
                            });
                            try {
                                dialog.show();
                            }
                            catch (Exception ignore){}

                        }
                        else {
                            //If single
                            String url = vidURL.get(0).getUrl();
                            handleAction(serverNumber, url, Action , true);
                        }
                    }

                    @Override
                    public void onError() {
                        //Error
                        mBinding.progressBarLayout.setVisibility(View.GONE);
                        episode.setError(true);
                        turnOffServer(serverNumber);
                        if(!server1&&!server2&&!server3&&!server4&&!server5&&!server6) return;
                        Toast.makeText(ServersActivity.this, "السيرفر غير متاح يرجى تجربة سيرفر آخر", Toast.LENGTH_SHORT).show();
                    }
                });

                jresolver.find(videoUrl);

            }
            else {
                if (Action == DOWNLOAD_ACTION) {
                    mBinding.progressBarLayout.setVisibility(View.GONE);
                    // block any download to the url that don`t need to extraction
                    Toast.makeText(this, "هذا السيرفر غير متاح للتحميل", Toast.LENGTH_SHORT).show();
                    return;
                }
                handleAction(serverNumber, videoUrl, Action , false);
            }
//        mBinding.progressBarLayout.setVisibility(View.GONE);
        }
    }

    private void turnOffServer(int serverNumber) {
        mBinding.progressBarLayout.setVisibility(View.GONE);
        switch (serverNumber) {
            case 1:
                mBinding.active1.setImageResource(R.drawable.not_active);
                server1 = false;
                mBinding.play1.setEnabled(false);
                mBinding.download1.setEnabled(false);
                break;
            case 2:
                mBinding.active2.setImageResource(R.drawable.not_active);
                server2 = false;
                mBinding.play2.setEnabled(false);
                mBinding.download2.setEnabled(false);
                break;
            case 3:
                mBinding.active3.setImageResource(R.drawable.not_active);
                server3 = false;
                mBinding.play3.setEnabled(false);
                mBinding.download3.setEnabled(false);
                break;
            case 4:
                mBinding.active4.setImageResource(R.drawable.not_active);
                server4 = false;
                mBinding.play4.setEnabled(false);
                mBinding.download4.setEnabled(false);
                break;
            case 5:
                mBinding.active5.setImageResource(R.drawable.not_active);
                server5 = false;
                mBinding.play5.setEnabled(false);
                mBinding.download5.setEnabled(false);
                break;
            case 6:
                mBinding.active6.setImageResource(R.drawable.not_active);
                server6 = false;
                mBinding.play6.setEnabled(false);
                mBinding.download6.setEnabled(false);
                break;
        }
        checkIfAllServerIsDisabled();
    }

    private void handleAction(int serverNumber, String url, int Action , boolean isExtractedUrl) {
        mBinding.progressBarLayout.setVisibility(View.VISIBLE);
        if (episode.isError()) {
            turnOffServer(serverNumber);
            if(!server1&&!server2&&!server3&&!server4&&!server5&&!server6) return;
            Toast.makeText(getApplicationContext(), "السيرفر غير متاح يرجى تجربة سيرفر آخر", Toast.LENGTH_LONG).show();
            return;
        }

        if (url.startsWith("https://vudeo.net/") || url.startsWith("https://vudeo.io/") || url.startsWith("https://m3.vudeo.io/")) {
            turnOffServer(serverNumber);
            if(!server1&&!server2&&!server3&&!server4&&!server5&&!server6) return;
            Toast.makeText(getApplicationContext(), "السيرفر غير متاح يرجى تجربة سيرفر آخر", Toast.LENGTH_LONG).show();
            return;
        }

        if (episode.getVideo().startsWith("https://vudeo.net/") || episode.getVideo().startsWith("https://vudeo.io/") || episode.getVideo().startsWith("https://m3.vudeo.io/")) {
            turnOffServer(serverNumber);
            if(!server1&&!server2&&!server3&&!server4&&!server5&&!server6) return;
            Toast.makeText(getApplicationContext(), "السيرفر غير متاح يرجى تجربة سيرفر آخر", Toast.LENGTH_LONG).show();
            return;
        }


        if (Action == WATCH_ACTION) {
            if (Config.video_player_package_name.isEmpty()) {
                // load package name of video App First
                getVideoAppPackage(url, serverNumber, isExtractedUrl);
            }
            else {
                handleWatchAction(serverNumber, url, isExtractedUrl);
            }
        }

        else {
            mBinding.progressBarLayout.setVisibility(View.GONE);
            if (!minSdk29 && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 1);
            }
            else {
                if (IsBlockedUrl(url)) return;
                Config.startDownloadingEpisode(this, url, episode, getIntent().getStringExtra("playlistTitle"), getIntent().getStringExtra("cartoonTitle"));
            }
        }
    }

    private void handleWatchAction(int serverNumber, String url, boolean isExtractedUrl) {
        if(Constants.SKIP_INSTALL_APPS.equals(Config.video_player_package_name)) {
            checkIfCanPlayVideo(url, serverNumber , true);
            return;
        }
        if (Config.isPackageInstalled(Config.video_player_package_name, getPackageManager())) {
            if (isExtractedUrl) {
                // direct start play the video
                Config.openExoPlayerApp(ServersActivity.this, url, episode, mBinding.progressBarLayout);
            } else
                checkIfCanPlayVideo(url, serverNumber , false);
        }
        else {
            mBinding.progressBarLayout.setVisibility(View.GONE);
            Config.installExoPlayerDialog(ServersActivity.this);
        }
    }

    private void checkIfCanPlayVideo(String url , int server_number  , boolean openInternalExo) {
        ExoPlayer simpleExoPlayer = new ExoPlayer.Builder(this)
                .setUseLazyPreparation(false) // this is new code
                .build();
        simpleExoPlayer.addMediaItem(new MediaItem.Builder()
                .setUri(Uri.parse(url))
                .build());
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                mBinding.progressBarLayout.setVisibility(View.GONE);
                //simpleExoPlayer.release();
                turnOffServer(server_number);
                if(!server1&&!server2&&!server3&&!server4&&!server5&&!server6) return;
                Toast.makeText(ServersActivity.this, "السيرفر غير متاح يرجى تجربة سيرفر آخر", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    if(!openInternalExo) {
                        Config.openExoPlayerApp(ServersActivity.this, url, episode, mBinding.progressBarLayout);
                    }
                    else {
                        mBinding.progressBarLayout.setVisibility(View.GONE);
                        startActivity(new Intent(ServersActivity.this , ExoplayerActivity.class).setData(Uri.parse(url)));
                    }
                }
            }
        });
        simpleExoPlayer.prepare();
    }

    private void getVideoAppPackage(String url, int server_number, boolean isExtractedUrl) {
        Log.i("new_abdo" , "getVideoAppPackage");
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .getVideoAppPackageName()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String package_name) {
                                String[] lists = package_name.split("\\|");
                                Config.video_player_package_name = lists[0] ;
                                Config.video_player_website = lists[1] ;
                                handleWatchAction(server_number , url , isExtractedUrl);
                            }



                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(ServersActivity.this, "حدث خطأ ما يرجي إعادة المحاولة", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private boolean IsBlockedUrl(String url) {
        if (url.endsWith(".m3u8") || url.startsWith("https://ok.ru/") || url.startsWith("https://mixdrop.co/") || url.startsWith("https://www.ok.ru/") || url.startsWith("https://www.mixdrop.co/") ) {
            Toast.makeText(this, "هذا السيرفر غير متاح للتحميل", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void checkServers() {
        updateBackNextBtn();
        mBinding.progressBarLayout.setVisibility(View.VISIBLE);
        episode = episodeList.get(current_pos);
        updateToolbarTitle();
        if (episode == null) return;
        Log.i("ab_do", "checkServers : " + current_pos);
//        if (
//                episode.getVideo1() == null &&
//                        episode.getVideo2()== null &&
//                        episode.getVideo3()== null &&
//                        episode.getVideo4()== null ||
//                        episode.getVideo1().isEmpty() && (
//                                episode.getVideo2().isEmpty() &&
//                                episode.getVideo3().isEmpty() &&
//                                episode.getVideo4().isEmpty() )
//        ) {

//            startVideoPlayer(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
//            Config.optionsDialog(this, episode.getVideo(), episode, playlistTitle, cartoonTitle);
        //checkjResolver(episode.getVideo(), episode, getIntent().getStringExtra("playlistTitle"), getIntent().getStringExtra("cartoonTitle"));

        checkSeversAvailability();

    }

    private void checkjResolver(String url, Episode episode,
                                String playlistTitle, String cartoonTitle) {
        episode.setError(false);
        //Check if needs jResolver
        if (episode.getjResolver() == 1) { //Needs extractions
            Log.i("ab_do", "Needs extractions " + episode.getVideo());
            Jresolver jresolver = new Jresolver(this);
            jresolver.onFinish(new Jresolver.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<Jmodel> vidURL, boolean multiple_quality) {
                    if (multiple_quality) {
                        //This video you can choose qualities
                        CharSequence[] qualities = new CharSequence[vidURL.size()];
                        CharSequence[] urls = new CharSequence[vidURL.size()];

                        for (int i = 0; i < vidURL.size(); i++) {
//                            String url = model.getUrl();
                            qualities[i] = vidURL.get(i).getQuality();
                            urls[i] = vidURL.get(i).getUrl();
                        }

                        mBinding.progressBarLayout.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ServersActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("اختار جودة الحلقة");
                        builder.setItems(qualities, (dialog, which) -> {
                            Config.optionsDialog(ServersActivity.this, urls[which].toString(), episode, playlistTitle, cartoonTitle);
                        });

                        AlertDialog dialog = builder.create();

                        dialog.setOnShowListener(dlg -> {

                            Objects.requireNonNull(dialog.getWindow()).getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // set title and message direction to RTL
                        });

                        dialog.show();

                    } else {
                        //If single
                        String url = vidURL.get(0).getUrl();

                        Config.optionsDialog(ServersActivity.this, url, episode, playlistTitle, cartoonTitle);
                    }
                    episode.setError(false);
                }

                @Override
                public void onError() {
                    //Error
                    episode.setError(true);
                    mBinding.progressBarLayout.setVisibility(View.GONE);
                    if(!server1&&!server2&&!server3&&!server4&&!server5&&!server6) return;
                    Toast.makeText(getApplicationContext(), "السيرفر غير متاح يرجى تجربة سيرفر آخر", Toast.LENGTH_LONG).show();
                }
            });
            jresolver.find(episode.getVideo());

        }
        else {
            Log.i("ab_do", "no Needs extractions " + episode.getVideo());
            mBinding.progressBarLayout.setVisibility(View.GONE);
            Config.optionsDialog(this, episode.getVideo(), episode, playlistTitle, cartoonTitle);
        }
    }


    //=======================Override Methods=================//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 101) {
//            if (resultCode == RESULT_OK) {
//                Log.i("ab_do", "Hello FromDownloader ");
//                if (data == null)  {
//                    Log.i("ab_do" , "data is null");
//                    return;
//                }
//                String path = data.getStringExtra("animePath");
//                String name = data.getStringExtra("animeName");
////                SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(this);
////                sqliteManager.insertDownload(name, path);
//                Log.i("ab_do" , "path " + path);
//                LoginUtil loginUtil = new LoginUtil(getBaseContext());
//                if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null)
//                Utilities.insertEpisodeDownload(getBaseContext() , loginUtil.getCurrentUser().getId() , episode.getId() , path);
//            }
//        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true ;
        }if (item.getItemId() == R.id.add_comment) {
            goToEpisodeCommentsActivity();
            return true ;
        }


        return super.onOptionsItemSelected(item);
    }

    private void showSnackMsg (String msg) {
        Snackbar snack = Snackbar.make(mBinding.getRoot(), msg , Snackbar.LENGTH_SHORT);
        showSnack(snack);
    }

    private void showSnack(Snackbar snack) {
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        snack.show();
    }

    private void goToEpisodeCommentsActivity() {
        Intent intent = new Intent(this , EpisodeCommentsActivity.class);
        intent.putExtra(Constants.EPISODE_ID , episode.getId());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.server_menu , menu) ;
        return super.onCreateOptionsMenu(menu);
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
