package com.anyone.smardy.motaj.badtrew.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.Database.SQLiteDatabaseManager;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.DownloadNeededAppDialog;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.Utilites.Utilities;
import com.anyone.smardy.motaj.badtrew.activities.ExoplayerActivity;
import com.anyone.smardy.motaj.badtrew.activities.SettingsActivity;
import com.anyone.smardy.motaj.badtrew.model.Admob;
import com.anyone.smardy.motaj.badtrew.model.Episode;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.EnqueueAction;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import java.io.File;
import java.util.List;
import java.util.UUID;

import io.michaelrocks.paranoid.Obfuscate;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

@Obfuscate
public class Config {

    public static String video_player_package_name = "" ;
    public static String video_download_package_name = "" ;
    public static String video_player_website = "" ;
    public static String video_download_website = "" ;
    private static FetchListener fetchListener;
    private static ProgressDialog progressDialog ;
    //    public static final String BASE_URL = "http://dxd-player.com/animelivev/API/";
    static BroadcastReceiver receiver ;
    public static final String BASE_URL = "https://apps-player.com/anime_for_all/API/";

    public static int numOfItemsBetweenAds = 30;

    public static boolean minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ;

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }


    public static boolean ifShouldDisableAds(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.RemoveAdKey) , false);
    }
    public static void updateTheme(AppCompatActivity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int theme_id = sharedPreferences.getInt(activity.getString(R.string.THEME_KEY) , activity.getResources().getInteger(R.integer.default_theme));
        Log.i("ab_do" , "Theme Id " + theme_id);

        if (theme_id == activity.getResources().getInteger(R.integer.default_theme)) {
            activity.setTheme(R.style.AppTheme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.black_theme)) {
            activity.setTheme(R.style.AppBlackTheme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Purple)) {
            activity.setTheme(R.style.theme_Purple);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Deep_Purple)) {
            activity.setTheme(R.style.DeepPurpleTheme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_green)) {
            activity.setTheme(R.style.GreenTheme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_red)) {
            activity.setTheme(R.style.redTheme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_blue)) {
            activity.setTheme(R.style.theme_blue);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Gray)) {
            activity.setTheme(R.style.themeGray);
        }
        if (theme_id != activity.getResources().getInteger(R.integer.default_theme) && activity.getSupportActionBar()!=null) {
            TypedValue value = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.toolbarColor, value, true);
            activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(value.data));
        }
    }


    public static void shareApp(Context context){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "حمل التطبيق من هنا" +
                "\n\n" +
                "https://apps-anime.com";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "مشاركة من خلال"));
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void ShowDialog(Context context) {
        if (progressDialog!=null && progressDialog.isShowing())  return;
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        try {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception exception) {

        }

    }

    public static void optionsDialog(Activity activity, String url, Episode episode,
                                     String playlistTitle, String cartoonTitle)
    {

        if (episode.isError()) {
            Toast.makeText(activity.getApplicationContext() , "السيرفر معطل يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        if (url!=null && (url.startsWith("https://vudeo.net/") || url.startsWith("https://vudeo.io/") || url.startsWith("https://m3.vudeo.io/"))) {
            Toast.makeText(activity.getApplicationContext() , "السيرفر معطل يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        if (episode.getVideo()!=null && (episode.getVideo().startsWith("https://vudeo.net/") || episode.getVideo().startsWith("https://vudeo.io/") || episode.getVideo().startsWith("https://m3.vudeo.io/"))) {
            Toast.makeText(activity.getApplicationContext() , "السيرفر معطل يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        String[] optionsArr = new String[]{"مشاهدة الحلقة", "تحميل الحلقة"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("");
        builder.setItems(optionsArr, (dialog, which) -> {
            if(which == 0){
                openExoPlayerApp(activity, url, episode, null);
            }
            else if(which == 1){
                if (!minSdk29 && ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                            , 1);

                }
                else {
                    startDownloadingEpisode(activity, url, episode, playlistTitle, cartoonTitle);
                }
            }
        });

        builder.show();

    }



    @SuppressLint("SuspiciousIndentation")
    public static void startDownloadingEpisode(Activity activity, String url, Episode episode,
                                               String playlistTitle, String cartoonTitle) {

        ShowDialog(activity);
        if (video_download_package_name.isEmpty()) {
            // load package first :
            getDownloadAppPackage(activity, url, episode, playlistTitle, cartoonTitle);
        }
        else
            handleDownloadProcess(activity, url, episode, playlistTitle, cartoonTitle);

    }

    private static void getDownloadAppPackage(Activity activity, String url, Episode episode, String playlistTitle, String cartoonTitle) {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(activity.getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .getDownloadAppPackageName()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String package_name) {
                                String[] lists = package_name.split("\\|");
                                Config.video_download_package_name = lists[0] ;
                                Config.video_download_website = lists[1] ;
                                handleDownloadProcess(activity, url, episode, playlistTitle, cartoonTitle);
                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissDialog(activity);
                                Toast.makeText(activity, "حدث خطأ ما يرجي إعادة المحاولة", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private static void handleDownloadProcess(Activity activity, String url, Episode episode, String playlistTitle, String cartoonTitle) {
        if(Config.video_download_package_name.equals(Constants.SKIP_INSTALL_APPS)) {
            dismissDialog(activity);
            startDownloadViaDownloadManager(activity, url, episode, playlistTitle, cartoonTitle);
            return;
        }
        if (!isPackageInstalled (video_download_package_name , activity.getPackageManager())) {
            dismissDialog(activity);
            showDownloadDownloaderAppDialog(activity);
            return;
        }
        // check if can download with fetch :
        Fetch fetch = Fetch.Impl.getInstance(new FetchConfiguration.Builder(activity).build());
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" +
                episode.getTitle() + UUID.randomUUID().toString() + ".mp4" ;
        fetchListener = new FetchListener() {
            @Override
            public void onAdded(@NonNull Download download) {

            }

            @Override
            public void onQueued(@NonNull Download download, boolean b) {

            }

            @Override
            public void onWaitingNetwork(@NonNull Download download) {

            }

            @Override
            public void onCompleted(@NonNull Download download) {

            }

            @Override
            public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
                if (error.getValue() == Error.REQUEST_NOT_SUCCESSFUL.getValue()) {
                    Log.i("ab_do" , "anime REQUEST_NOT_SUCCESSFUL");
                    endFetch(download, fetch);
                    //startDownloadViaDownloadManager(activity, url, episode, playlistTitle, cartoonTitle);
                    Toast.makeText(activity, "حدث خطأ .. يرجي تجربة سيرفر أخر", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {
                Log.i("ab_do" , "start with anime");
                // the download can be downloaded with fetch
                endFetch(download, fetch);
                openDownloaderApp(activity, playlistTitle, url, cartoonTitle, episode);
            }

            @Override
            public void onProgress(@NonNull Download download, long l, long l1) {

            }

            @Override
            public void onPaused(@NonNull Download download) {

            }

            @Override
            public void onResumed(@NonNull Download download) {

            }

            @Override
            public void onCancelled(@NonNull Download download) {

            }

            @Override
            public void onRemoved(@NonNull Download download) {

            }

            @Override
            public void onDeleted(@NonNull Download download) {

            }
        };
        fetch.addListener(fetchListener);
        Request request = new Request(url, path);
        request.setEnqueueAction(EnqueueAction.INCREMENT_FILE_NAME);
        fetch.enqueue(request,updatedRequest -> {
            //Request was successfully enqueued for download.
            Log.i("ab_do" , "enqueued ");

        }, error -> {
            Log.i("ab_do" , "error on enqueee " + error.toString());
            endFetch(null , fetch);
            try {
                File file = new File(request.getFile());
                file.delete();
            }
            catch (Exception exception) {}
            startDownloadViaDownloadManager(activity, url, episode, playlistTitle, cartoonTitle);
        });
//          String googleUserContentURl = "https://lh3.googleusercontent.com";
//        if (url.contains(googleUserContentURl)) {
//            // this url can`t be handled with downloader app
//            startDownloadViaDownloadManager(activity, url, episode, playlistTitle, cartoonTitle);
//        }
//        else
//            openDownloaderApp(activity, url, cartoonTitle, episode);
    }

    @SuppressLint("SuspiciousIndentation")
    private static void endFetch(Download download, Fetch fetch) {
        fetch.removeListener(fetchListener);
        if (download != null) {
            fetch.remove(download.getId());
            fetch.remove(download.getRequest().getId());
            File file = new File(download.getRequest().getFile());
            file.delete();
        }
        fetch.cancelAll();
        fetch.removeAll();
        if (progressDialog!=null)
            progressDialog.dismiss();
    }


    @SuppressLint("SuspiciousIndentation")
    private static void startDownloadViaDownloadManager(Activity activity, String url, Episode episode, String playlistTitle, String cartoonTitle) {
//        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
//        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", url);
//        clipboard.setPrimaryClip(clip);
        Uri episodeUri = Uri.parse(url);

        DownloadManager.Request req=new DownloadManager.Request(episodeUri);

        String full_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + "Downloads" + "/" + cartoonTitle + " " + episode.getTitle() + ".mp4";
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setAllowedOverMetered(true)
                .setTitle(cartoonTitle + " " + episode.getTitle())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , "Downloads" + "/" + cartoonTitle + " " + episode.getTitle() + ".mp4" )
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        ;

        /*.setDestinationInExternalPublicDir(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/AnimeCartoons/" + cartoonTitle + "/" + playlistTitle,
                episode.getTitle() + ".mp4")*/

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(req);
        SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(activity);
        String mainPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                + File.separator ;
        sqliteManager.insertDownload(cartoonTitle + " - " + playlistTitle + " - " + episode.getTitle()
                , full_path);
        Toast.makeText(activity, "يتم تحميل الحلقة الان...", Toast.LENGTH_SHORT).show();
        LoginUtil loginUtil = new LoginUtil(activity);
        if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null)
            Utilities.insertEpisodeDownload(activity , loginUtil.getCurrentUser().getId() , episode.getId() , full_path );
    }

    private static void openDownloaderApp(Activity activity, String playlistTitle , String url, String cartoonTitle, Episode episode) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(new ComponentName(video_download_package_name,video_download_package_name+".UI.ActiveDownloads.MainActivity"));
        intent.setAction("download.anime.action");
        intent.putExtra("anime_url", url);
        intent.putExtra("anime_file_name", activity.getString(R.string.format_mp4, cartoonTitle + " " + episode.getTitle()));
        intent.putExtra("animeName", cartoonTitle + " - " + playlistTitle + " - " + episode.getTitle());
        try {
            activity.startActivityForResult(intent, 101);
        } catch (ActivityNotFoundException e) {
            showDownloadDownloaderAppDialog(activity);
        }
        IntentFilter intentFilter = new IntentFilter("anime.saveData");
        receiver = new BroadcastReceiver() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("ab_do" ,"FromDownloader ");
                String path = intent.getStringExtra("animePath");
                String name = intent.getStringExtra("animeName");
                Log.i("ab_do" , "path " + path);
                LoginUtil loginUtil = new LoginUtil(activity);
                if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null)
                    Utilities.insertEpisodeDownload(activity , loginUtil.getCurrentUser().getId() , episode.getId() , path);
//                sqliteManager.insertDownload(name, path);
//                SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(context);
//                sqliteManager.insertDownload(name, path);
                try {
                    activity.unregisterReceiver(receiver);
                }
                catch (Exception ignore){}
            }
            // register the receiver
        };
        activity.registerReceiver(receiver, intentFilter);
    }

    private static void showDownloadDownloaderAppDialog(Activity activity) {
        DownloadNeededAppDialog dialog = new DownloadNeededAppDialog(activity ,false);
        dialog.showDialog();

    }

    public static void openDownloaderAppOnPlayStore(Activity activity) {
        // try {
        //   activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("samsungapps://ProductDetail/" + video_download_package_name )));
        // }
        //catch (android.content.ActivityNotFoundException anfe) {
        //   Log.i("new_abdo" , "cannot open samsung market");
        // try to open the huawei market
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("appmarket://details?id=" + video_download_package_name )));
        }
        catch (android.content.ActivityNotFoundException anfe2) {
            Log.i("new_abdo" , "cannot open huawei market");
            // try to open the play store market
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + video_download_package_name)));
        }
    }
    // }

    @SuppressLint("SuspiciousIndentation")
    public static void openExoPlayerApp(Activity activity, String url, Episode episode, FrameLayout progressBarLayout){
        if(Constants.SKIP_INSTALL_APPS.equals(Config.video_player_package_name)) {
            if(progressBarLayout!=null)
                progressBarLayout.setVisibility(View.GONE);
            else Config.dismissDialog(activity);
            activity.startActivity(new Intent(activity , ExoplayerActivity.class).setData(Uri.parse(url)));
            return;
        }
        Log.i("new_abdo" , "openExoPlayerApp " + url);
        if (progressBarLayout==null) {
            ShowDialog(activity);
        }
        openTheApp(activity, url, episode, progressBarLayout);
    }


    private static void openTheApp(Activity activity, String url, Episode episode, FrameLayout progressBarLayout) {
        PackageManager packageManager = activity.getPackageManager();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setComponent(new ComponentName(video_player_package_name,video_player_package_name+".activity.ExoplayerActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction("quick.launch.me");
        intent.putExtra("url", url);
        if (intent.resolveActivity(packageManager) != null) {
            // insert seen episode and increment watched cartoons
            LoginUtil loginUtil = new LoginUtil(activity.getApplicationContext());
            if (loginUtil.userIsLoggedIN() && !UserOptions.getUserOptions().getSeenEpisodesIds().contains(episode.getId())) {
                insertSeenEpisode(intent , activity, episode, loginUtil , progressBarLayout);
            }
            else {
                startExoPlayer(activity, intent , progressBarLayout);
            }
        }
        else {
            if (progressBarLayout ==null)
                dismissDialog(activity);
            else
                progressBarLayout.setVisibility(View.GONE);
            installExoPlayerDialog(activity);
        }
    }

    public static void installExoPlayerDialog(Activity activity) {
        DownloadNeededAppDialog dialog = new DownloadNeededAppDialog(activity ,true);
        dialog.showDialog();
    }

    private static void insertSeenEpisode(Intent intent, Activity activity, Episode episode, LoginUtil loginUtil, FrameLayout progressBarLayout) {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(activity.getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .insertSeenEpisode(loginUtil.getCurrentUser().getId(), episode.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    UserOptions.getUserOptions().getSeenEpisodesIds().add(episode.getId());
                                    incrementWatchedEpisodes(intent , activity, episode, loginUtil , progressBarLayout);
                                }
                                else {
                                    if (progressBarLayout==null)
                                        dismissDialog(activity);
                                    else
                                        progressBarLayout.setVisibility(View.GONE);
                                    startExoPlayer(activity, intent, progressBarLayout);
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (progressBarLayout==null)
                                    dismissDialog(activity);
                                else
                                    progressBarLayout.setVisibility(View.GONE);
                                startExoPlayer(activity, intent, progressBarLayout);
                            }
                        })
        );
    }

    private static void incrementWatchedEpisodes(Intent intent, Activity activity, Episode episode, LoginUtil loginUtil, FrameLayout progressBarLayout) {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(activity.getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .incrementWatchedEpisodes(loginUtil.getCurrentUser().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @SuppressLint("SuspiciousIndentation")
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (progressBarLayout==null)
                                    dismissDialog(activity);
                                else
                                    progressBarLayout.setVisibility(View.GONE);
                                startExoPlayer(activity , intent, progressBarLayout);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (progressBarLayout==null)
                                    dismissDialog(activity);
                                else
                                    progressBarLayout.setVisibility(View.GONE);
                                startExoPlayer(activity , intent, progressBarLayout);
                            }
                        })
        );
    }

    public static void dismissDialog(Activity activity) {
        try {
            progressDialog.dismiss();
            progressDialog = null ;
        }catch (Exception exception) {
            Log.i("ab_do" , "dismiss dialog error " + exception.getMessage());
        }
    }

    private static void startExoPlayer(Activity activity, Intent intent, FrameLayout progressBarLayout) {

        if (progressBarLayout==null)
            dismissDialog(activity);
        else
            progressBarLayout.setVisibility(View.GONE);
        try {
            activity.startActivity(intent);
        }

        catch (ActivityNotFoundException e) {
            installExoPlayerDialog(activity);
        }
    }


    public static void openExoPlayerOnPlayStore(Context activity){
        // try to open the samsung market
        //   try {
        //   activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("samsungapps://ProductDetail/" + video_player_package_name )));
        //  }
        //  catch (android.content.ActivityNotFoundException anfe) {
        //Log.i("new_abdo" , "cannot open samsung market");
        // try to open the huawei market
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("appmarket://details?id=" + video_player_package_name )));
        }
        catch (android.content.ActivityNotFoundException anfe2) {
            Log.i("new_abdo" , "cannot open huawei market");
            // try to open the play store market
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + video_player_package_name)));
        }
    }

    // }


    public static final int Nav_LatestEpisode = 0;
    public static final String CHANNEL_ID = "animeCartoonNotification";

    public static Admob admob;


    public static void updateSettingsTheme(SettingsActivity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int theme_id = sharedPreferences.getInt(activity.getString(R.string.THEME_KEY) , activity.getResources().getInteger(R.integer.default_theme));
        Log.i("ab_do" , "Theme Id " + theme_id);

        if (theme_id == activity.getResources().getInteger(R.integer.default_theme)) {
            activity.setTheme(R.style.settings_app_theme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.black_theme)) {
            activity.setTheme(R.style.settings_dark_theme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Purple)) {
            activity.setTheme(R.style.settings_purple_theme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Deep_Purple)) {
            activity.setTheme(R.style.settings_theme_Deep_Purple);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_green)) {
            activity.setTheme(R.style.settings_green_theme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_blue)) {
            activity.setTheme(R.style.settings_blue_theme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_red)) {
            activity.setTheme(R.style.settings_red_theme);
        }
        else if (theme_id == activity.getResources().getInteger(R.integer.theme_Gray)) {
            activity.setTheme(R.style.settings_Gray_theme);
        }



        if (theme_id != activity.getResources().getInteger(R.integer.default_theme) && activity.getSupportActionBar()!=null) {
            TypedValue value = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.toolbarColor, value, true);
            activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(value.data));
        }
    }
    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getApplicationInfo(packageName, 0);
            return true ;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
