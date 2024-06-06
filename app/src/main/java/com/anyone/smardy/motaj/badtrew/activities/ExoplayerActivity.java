package com.anyone.smardy.motaj.badtrew.activities;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.multidex.MultiDex;
import androidx.preference.PreferenceManager;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.onSwipeTouchListener;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityExoplayerBinding;
import com.anyone.smardy.motaj.badtrew.model.Admob;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ExoplayerActivity extends AppCompatActivity {
    public static final String URL = "url";
    private final int PERMISSIONS_REQUEST_STORAGE = 1;
    private final String TAG = ExoplayerActivity.class.getSimpleName();
    ActivityExoplayerBinding mBinding;
    String videoUrl;
    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;
    private InterstitialAd beforeInterstitialAd = null;
    private InterstitialAd afterInterstitialAd = null;
    ExoPlayer simpleExoPlayer;
    StyledPlayerView playerView;
    AudioManager audioManager;
    SeekBar seekBar;
    public static AlertDialog dialog = null;
    boolean isLocked = false;
    Handler handler;
    Runnable runnable;
    int current_audio = 0;
    boolean fromExternalApp = false;
    Uri uri = null;
    private ImaAdsLoader adsLoader;
    SharedPreferences sharedPreferences;
    boolean adsNotLoaded = false;
    boolean showAfterAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initWindow();
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        init();
        checkIFLaunchedFromExternalApp();
        initMediaPlayer();
        checkIFInitMediaPlayerAfterAd();
        initRetrofit();
        initAds();
        getAdmobData();
    }

    private void checkIFInitMediaPlayerAfterAd() {
        if (Config.ifShouldDisableAds(this)) {
            simpleExoPlayer.prepare();
            return;
        }
        if (sharedPreferences.getString(getString(R.string.InterstitialBeforeKey), "").trim().isEmpty()) {
            simpleExoPlayer.prepare();
        } else {
            MobileAds.initialize(this);
            createInterstitialAd();
        }
    }


    private void checkIFLaunchedFromExternalApp() {
        if (getIntent().getAction() != null && (getIntent().getAction().equals(Intent.ACTION_VIEW) || getIntent().getAction().equals(Intent.ACTION_SEND))) {
            // the activity is launched from external app
            fromExternalApp = true;
            Log.i("ab_do", "FromExternalApp");
        }
    }

    private void init() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_exoplayer);
        mBinding.space.setVisibility(View.VISIBLE);
        mBinding.hideAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.bannerTemplate.setVisibility(View.GONE);
                mBinding.hideAd.setVisibility(View.GONE);
            }
        });
        MultiDex.install(this);
        // Create an AdsLoader.
        adsLoader = new ImaAdsLoader.Builder(/* context= */ this).build();
    }

    private void releasePlayer() {
        adsLoader.setPlayer(null);
        playerView.setPlayer(null);
        simpleExoPlayer.release();
        simpleExoPlayer = null;
    }

    private void initWindow() {
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent));
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } catch (Exception exception) {
            Log.i("ab_do", exception.getMessage());
        }
    }

    private void initAds() {
        if (Objects.equals(sharedPreferences.getString(getString(R.string.bannerKey), " "), " ") && Objects.equals(sharedPreferences.getString(getString(R.string.InterstitialAfterKey), " "), " ")) {
            // ids is not loaded yet from server
            adsNotLoaded = true;
            return;
        }
        if (!Config.ifShouldDisableAds(this)) {
            createBannerAd();
            createInterstitialAd2();
        }
//        else {
//            simpleExoPlayer.play();
//        }
    }

    private int getSavedProgress() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt(getString(R.string.soundKey), 7);
    }


    private Uri getMediaUri() {
        uri = getIntent().getData();
        return uri;
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        // Create a HLS media source pointing to a playlist uri.
        return new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri));
    }


    private void initMediaPlayer() {
        // this is new code :
        ////////////////////////////////////////////////////////////////
        TrackSelector trackSelector = new DefaultTrackSelector(this);
        DefaultLoadControl loadControl = new DefaultLoadControl
                .Builder()
                .setBufferDurationsMs(32 * 1024, 1024 * 1024, 1024, 1024)
                .build();
        ////////////////////////////////////////////////////////////////
        Log.i("ab_dd", "initMediaPlayer");
        playerView = mBinding.videoView;
        // Set up the factory for media sources, passing the ads loader and ad view providers.
        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);

        MediaSource.Factory mediaSourceFactory =
                new DefaultMediaSourceFactory(dataSourceFactory)
                        .setLocalAdInsertionComponents(unusedAdTagUri -> adsLoader, playerView);
        // Create an ExoPlayer and set it as the player for content and ads.
        simpleExoPlayer = new ExoPlayer.Builder(this)
                .setTrackSelector(trackSelector) // this is new code
                .setUseLazyPreparation(false) // this is new code
                .setLoadControl(loadControl) // this is new code
                .setMediaSourceFactory(mediaSourceFactory)
                .build();

        playerView.setVisibility(View.INVISIBLE);
        mBinding.controller.setVisibility(View.INVISIBLE);
        playerView.setPlayer(simpleExoPlayer);
        adjustAudio();
        initRunnable();
        initVideoTouchScreenListener();
        initListeners();
        if (!Config.ifShouldDisableAds(this) || !sharedPreferences.getString(getString(R.string.ima_id_key), "").trim().isEmpty()) {
            adsLoader.setPlayer(simpleExoPlayer);
            Log.i("ab_dddd", "adsLoader set");
        }
        addMediaItem();
        try {
            //simpleExoPlayer.prepare();
            //simpleExoPlayer.play();
            simpleExoPlayer.addListener(new Player.Listener() {
                @SuppressLint("SwitchIntDef")
                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    if (isPlaying) {
                        // the video is currently playing ..
                        Log.i("ab_do", "the video is currently playing " + uri.toString());
                        mBinding.progressBar.setVisibility(View.GONE);
                        mBinding.bannerTemplate.setVisibility(View.GONE);
                        mBinding.hideAd.setVisibility(View.GONE);
                    } else {
                        switch (simpleExoPlayer.getPlaybackState()) {
                            case Player.STATE_READY:
                                // the video is paused
                                Log.i("ab_do", "the video is paused");
                                mBinding.progressBar.setVisibility(View.GONE);
                                mBinding.bannerTemplate.setVisibility(View.VISIBLE);
                                mBinding.hideAd.setVisibility(View.VISIBLE);
                                break;
                            case Player.STATE_ENDED:
                                // the video is ended
                                Log.i("ab_do", "the video is ended");
                                // TODO: 9/10/2021 take action when the video is ended
                                break;
                            default:
                                // there is an error : loading video
                                mBinding.progressBar.setVisibility(View.VISIBLE);
                                Log.i("ab_do", "there is an error in loading video");
                        }
                    }
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    Log.d("ab_do", "onPlayerError " + uri.toString());
                    Log.d("ab_do", "onPlayerError " + error.getMessage());
                    //open in the web view
//                    startActivity(new Intent(getBaseContext() , webViewActivity.class).setAction(uri.toString()).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
//                    finish();
                    Toast.makeText(getApplicationContext(), "لا يمكن تشغيل هذا الرابط", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    if (playbackState == Player.STATE_READY) {
                        playerView.setVisibility(View.VISIBLE);
                        mBinding.controller.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "initMediaPlayer: " + e.getMessage());
        }
    }

    private void addMediaItem() {
        Log.i("ab_dd", "addMediaItem");
        // Create the MediaItem to play, specifying the content URI and ad tag URI.
        Uri uri = getMediaUri();
        if (uri == null) return;
        if (uri.toString().endsWith("m3u8")) // here we need to add hls container to play m3u8 files
            simpleExoPlayer.addMediaSource(buildMediaSource(uri));
        else {
            if (Config.ifShouldDisableAds(this) || sharedPreferences.getString(getString(R.string.ima_id_key), "").trim().isEmpty()) {
                simpleExoPlayer.addMediaItem(new MediaItem.Builder()
                        .setUri(uri)
                        .build());
            } else {
                Uri adTagUri = Uri.parse(sharedPreferences.getString(getString(R.string.ima_id_key), " "));
                //Uri adTagUri = Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?iu=/121764058,21914104527/quick_player_app/quick_player_app_o3b_video_o3b_APP_VAST_1&url=$$REFERER$$&description_url=http%3A%2F%2Fneom.com%2F&tfcd=0&npa=0&sz=300x250%7C400x300%7C640x480%7C750x100%7C980x300&gdfp_req=1&output=xml_vast&unviewed_position_start=1&env=vp&nofb=1&vpa=auto&osd=2&frm=0&vis=1&sdr=1&is_amp=0&impl=s");
                simpleExoPlayer.addMediaItem(new MediaItem.Builder()
                        .setUri(uri)
                        .setAdsConfiguration(new MediaItem.AdsConfiguration.Builder(adTagUri)
                                .build())
                        .build());
            }
        }
    }


    private void initListeners() {
        mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocked) adjustLockButton();
            }
        });
        playerView.setControllerVisibilityListener(new StyledPlayerView.ControllerVisibilityListener() {
            @Override
            public void onVisibilityChanged(int visibility) {
                if (visibility == StyledPlayerView.VISIBLE) {
                    setControllerVisibility(View.VISIBLE);
                } else {
                    setControllerVisibility(View.GONE);
                }
            }
        });
    }

    private void initVideoTouchScreenListener() {
        mBinding.videoView.setOnTouchListener(new onSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                if (isLocked) return;
                Log.i("ab_do", "onSwipeTop");
                if (dialog == null || seekBar == null) {
                    createSoundDialog();
                }
                if (!dialog.isShowing()) {
                    try {
                        dialog.show();
                    } catch (Exception exception) {
                        Log.i("ab_do", exception.getMessage());
                    }
                }
                increaseVolume();
            }

            @Override
            public void onSwipeRight() {
                if (isLocked) return;
                Log.i("ab_do", "onSwipeRight");
                Long pos = simpleExoPlayer.getContentPosition() + 1000;
                simpleExoPlayer.seekTo(pos);
            }

            @Override
            public void onSwipeLeft() {
                if (isLocked) return;
                Log.i("ab_do", "onSwipeLeft");
                Long pos = simpleExoPlayer.getContentPosition() - 1000;
                if (pos < 0) pos = 0L;
                simpleExoPlayer.seekTo(pos);
            }

            @Override
            public void onSwipeBottom() {
                if (isLocked) return;
                Log.i("ab_do", "onSwipeBottom");
                if (dialog == null || seekBar == null) {
                    createSoundDialog();
                }
                if (!dialog.isShowing()) {
                    try {
                        dialog.show();
                    } catch (Exception exception) {
                        Log.i("ab_do", exception.getMessage());
                    }
                }
                decreaseVolume();
            }

        });
    }

    private void increaseVolume() {
        current_audio++;
        if (current_audio > 15) current_audio = 15;
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current_audio, 0);
        } catch (Exception exception) {
            Log.i("ab_do", exception.getMessage());
        }
        seekBar.setProgress(current_audio);
    }

    private void decreaseVolume() {
        current_audio--;
        if (current_audio < 0) current_audio = 0;
        try {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current_audio, 0);
        } catch (Exception exception) {
            Log.i("ab_do", exception.getMessage());
        }

        seekBar.setProgress(current_audio);
    }

    public void changeExoControllerVisibility() {
        if (isLocked) {
            adjustLockButton();
            return;
        }
        if (mBinding.videoView.isControllerFullyVisible()) {
            Log.i("ab_do", "hide");
            mBinding.videoView.hideController();
        } else {
            mBinding.videoView.showController();
        }
    }

    public void ShowView() {
        if (isLocked) {
            adjustLockButton();
            return;
        }
        if (!mBinding.videoView.isControllerFullyVisible())
            mBinding.videoView.showController();
    }

    private void initRunnable() {
        runnable = new Runnable() {
            @Override
            public void run() {
                mBinding.lockScreen.setVisibility(View.GONE);
            }
        };
    }

    private void setControllerVisibility(int visible) {
        mBinding.ivFullScreen.setVisibility(visible);
        if (!isLocked)
            mBinding.lockScreen.setVisibility(visible);
        else {
            // should show the lock button
            adjustLockButton();
        }
        mBinding.resize.setVisibility(visible);
//        mBinding.sound.setVisibility(visible);
//        mBinding.MuteSound.setVisibility(visible);
    }


    private void adjustLockButton() {
        if (handler != null && runnable != null) handler.removeCallbacks(runnable);
        mBinding.lockScreen.setVisibility(View.VISIBLE);
        handler = new Handler();
        handler.postDelayed(runnable, 5000);
    }

    private void adjustAudio() {
        simpleExoPlayer.setVolume(1);
        Log.i("ab_do", "Volume " + getSavedProgress());
        current_audio = getSavedProgress();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current_audio, 0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void getAdmobData() {
        disposable.add(
                apiService
                        .getAdmob()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Admob>() {
                            @Override
                            public void onSuccess(Admob admob) {
                                updateAdIds(admob);
                                if (Config.admob == null) {
                                    Config.admob = admob;
                                    try {
                                        ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                        Bundle bundle = ai.metaData;
                                        String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        Log.d(TAG, "Name Found: " + myApiKey);
                                        ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", admob.getApp_id());//you can replace your key APPLICATION_ID here
                                        String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        Log.d(TAG, "ReNamed Found: " + ApiKey);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                                    } catch (NullPointerException e) {
                                        Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "Error ad mob " + e.getMessage());
                                //initMediaPlayer();
                            }
                        })
        );
    }

    private void updateAdIds(Admob admob) {
        Log.i("ab_do", "updateAdIds");
        Log.i("ab_do", "banner = " + admob.getBanner());
        Log.i("ab_do", "InterstitialBeforeKey " + admob.getInterstitial());
        Log.i("ab_do", "InterstitialAfterKey " + admob.getInterstitial2());
        Log.i("ab_do", "ima_id_key " + admob.getImaAd());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.bannerKey), admob.getBanner()).apply();
        editor.putString(getString(R.string.InterstitialBeforeKey), admob.getInterstitial()).apply();
        editor.putString(getString(R.string.InterstitialAfterKey), admob.getInterstitial2()).apply();
        editor.putString(getString(R.string.ima_id_key), admob.getImaAd()).apply();
        if (adsNotLoaded) {
            initAds();
            adsNotLoaded = false;
        }
    }

    private void createBannerAd() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(sharedPreferences.getString(getString(R.string.bannerKey), ""));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.i("ab_do", "onAdLoadedExo");
                mBinding.bannerTemplate.addView(adView);
            }
        });
    }

    public void createInterstitialAd() {
        Log.i("ab_do_ad", "createInterstitialAd");
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, sharedPreferences.getString(getString(R.string.InterstitialBeforeKey), ""), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.i("ab_do", "onAdLoaded Before");
                        beforeInterstitialAd = interstitialAd;
                        beforeInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                simpleExoPlayer.prepare();
                            }
                        });
                        beforeInterstitialAd.show(ExoplayerActivity.this);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i("ab_do", "onAdFailedToLoad Before");
                        beforeInterstitialAd = null;
                        simpleExoPlayer.prepare();
                    }
                });
    }

    private void createInterstitialAd2() {
        Log.i("ab_do", "createInterstitialAd2");
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, sharedPreferences.getString(getString(R.string.InterstitialAfterKey), ""), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        afterInterstitialAd = interstitialAd;
                        afterInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.i("ab_do", "onAdDismissedFullScreenContent");
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        afterInterstitialAd = null;
                    }
                });
    }

    private void initRetrofit() {
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }


    private void performBackAction() {
        finish();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void fullScreen(View view) {
        if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            //Portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mBinding.space.setVisibility(View.VISIBLE);
        } else { //Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mBinding.space.setVisibility(View.GONE);
        }
    }


    private void showThanksDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExoplayerActivity.this);

        builder.setMessage(getString(R.string.thanks_msg));
        builder.setCancelable(true);
        builder.setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ExoplayerActivity.this.finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        try {
            alertDialog.show();
        } catch (Exception exception) {
            Log.i("ab_do", exception.getMessage());
        }
    }

    //--------------Override Methods-------------//

    @Override
    protected void onStart() {
        // resume the player
        super.onStart();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // pause the player
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onBackPressed() {
        backPressedActions();
    }

    private void backPressedActions() {
        if (isLocked) return;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
        if (mBinding != null) {
            mBinding.bannerTemplate.setVisibility(View.GONE);
            mBinding.hideAd.setVisibility(View.GONE);
        }
        if (afterInterstitialAd != null) {
            Log.i("ab_do", "show");
            afterInterstitialAd.show(ExoplayerActivity.this);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        if (adsLoader != null)
            adsLoader.release();
        if (disposable != null)
            disposable.dispose();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
        try {
            if (dialog != null && dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.i("ab_do", e.getMessage());
                }
            }
        } catch (Exception exception) {
            Log.i("ab_do", exception.getMessage());
        }
        dialog = null;
        super.onDestroy();
    }

    public void download(View view) {
        startDownloadingEpisode();
    }

    private void startDownloadingEpisode() {
        String url = getIntent().getStringExtra("url");
        if (url == null) return;
        Uri episodeUri = Uri.parse(url);

        DownloadManager.Request req = new DownloadManager.Request(episodeUri);

        String episodeUniqueName = UUID.randomUUID().toString();

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("video")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        episodeUniqueName + ".mp4")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        ;

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(req);

        Toast.makeText(this, "يتم تحميل الحلقة الان...", Toast.LENGTH_SHORT).show();
    }

    //Example Download Google Drive Video with ADM
    public void downloadWithADM() {
        boolean appInstalledOrNot = appInstalledOrNot("com.dv.adm");
        boolean appInstalledOrNot2 = appInstalledOrNot("com.dv.adm.pay");
        boolean appInstalledOrNot3 = appInstalledOrNot("com.dv.adm.old");
        String str3;
        if (appInstalledOrNot || appInstalledOrNot2 || appInstalledOrNot3) {
            if (appInstalledOrNot2) {
                str3 = "com.dv.adm.pay";
            } else if (appInstalledOrNot) {
                str3 = "com.dv.adm";
            } else {
                str3 = "com.dv.adm.old";
            }

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(videoUrl), "application/x-mpegURL");
                intent.setPackage(str3);
                /*if (xModel.getCookie()!=null) {
                    intent.putExtra("Cookie", xModel.getCookie());
                    intent.putExtra("Cookies", xModel.getCookie());
                    intent.putExtra("cookie", xModel.getCookie());
                    intent.putExtra("cookies", xModel.getCookie());
                }*/

                startActivity(intent);
                return;
            } catch (Exception e) {
                return;
            }
        }
        str3 = "com.dv.adm";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + str3)));
        } catch (ActivityNotFoundException e2) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + str3)));
        }
    }

    public boolean appInstalledOrNot(String str) {
        try {
            getPackageManager().getPackageInfo(str, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    //----------Connectivity Broadcast Receiver --------//
//    private BroadcastReceiver connectStateReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final ConnectivityManager connectivityManager = (ConnectivityManager) context
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
//                mBinding.noSignalIcon.setVisibility(View.GONE);
//                if(player == null){
//                    initVideoViewModel();
//                }else{
//                    videoViewModel.preparePlayer();
//                }
//
//            }else{ //Internet Connection Stopped
//                mBinding.noSignalIcon.setVisibility(View.VISIBLE);
//                Toast.makeText(context, "هناك مشكلة في الاتصال بالانترنت !", Toast.LENGTH_LONG).show();
//            }
//        }
//    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
            } else {
                startDownloadingEpisode();
            }
        }
    }


    public void changeSound(View view) {
        createSoundDialog();
    }

    private void createSoundDialog() {
        View dialog_view = LayoutInflater.from(this).inflate(R.layout.edit_sound_dialog, (ViewGroup) mBinding.getRoot(), false);
        seekBar = dialog_view.findViewById(R.id.seek_bar);
        ((TextView) dialog_view.findViewById(R.id.sound_txt)).setTextColor(getResources().getColor(R.color.white));
        ImageView close = dialog_view.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dialog.dismiss();
                } catch (Exception exception) {
                    Log.d(TAG, "onClick: " + exception.getMessage());
                }

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                current_audio = i;
                try {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                } catch (Exception exception) {
                    Log.i("ab_do", exception.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(dialog_view);
        dialog = builder.create();
        try {
            dialog.show();
        } catch (Exception exception) {
            Log.i("ab_do", exception.getMessage());
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_background);
    }

    public void resize(View view) {
        if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FILL) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }
    }

    public void lockScreen(View view) {
        if (handler != null && runnable != null) handler.removeCallbacks(runnable);
        if (isLocked) {
            // unlock
            isLocked = false;
            mBinding.lockScreen.setImageResource(R.drawable.ic_baseline_lock_open_24);
            mBinding.videoView.setControllerAutoShow(true);
            mBinding.videoView.setUseController(true);
            mBinding.videoView.showController();
        } else {
            // lock
            isLocked = true;
            mBinding.lockScreen.setImageResource(R.drawable.ic_baseline_lock_24);
            mBinding.videoView.hideController();
            mBinding.videoView.setControllerAutoShow(false);
            mBinding.videoView.setUseController(false);

        }
    }

//    public void muteSound(View view) {
//        if (simpleExoPlayer.getVolume() == 0) {
//            // is mute already
//            simpleExoPlayer.setVolume(1f);
//            mBinding.MuteSound.setBackground(ContextCompat.getDrawable(this, R.drawable.icons_background));
//        } else {
//            // mute it
//            simpleExoPlayer.setVolume(0f);
//            mBinding.MuteSound.setBackground(ContextCompat.getDrawable(this, R.drawable.sound_mute));
//        }
//    }
}

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
//            Log.i("ab_do" , "KEYCODE_VOLUME_DOWN");
//            return true ;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
//            Log.i("ab_do" , "KEYCODE_VOLUME_UP");
//    