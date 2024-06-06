package com.anyone.smardy.motaj.badtrew.activities;

import static com.anyone.smardy.motaj.badtrew.Constants.Constants.LATEST_EPISODES;
import static com.anyone.smardy.motaj.badtrew.app.Config.admob;
import static com.anyone.smardy.motaj.badtrew.app.Config.isNetworkConnected;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.Database.SQLiteDatabaseManager;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.ImgUtilities;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginDialog;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginToMainCachedData;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.Utilites.MessageDialog;
import com.anyone.smardy.motaj.badtrew.Utilites.sharedPreferencesUtil;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.app.UserOptions;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityMainBinding;
import com.anyone.smardy.motaj.badtrew.fragments.CartoonFragment;
import com.anyone.smardy.motaj.badtrew.fragments.LatestEpisodesFragment;
import com.anyone.smardy.motaj.badtrew.model.Admob;
import com.anyone.smardy.motaj.badtrew.model.Cartoon;
import com.anyone.smardy.motaj.badtrew.model.EpisodeWithInfo;
import com.anyone.smardy.motaj.badtrew.model.Playlist;
import com.anyone.smardy.motaj.badtrew.model.Redirect;
import com.anyone.smardy.motaj.badtrew.model.User;
import com.anyone.smardy.motaj.badtrew.model.UserData;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MessageDialog.onMsgBtnClickListener {

    private final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding mBinding;

    MainActivity activity;
    LatestEpisodesFragment LatestEpisodesFragmentFragment;

    private android.widget.SearchView searchView;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    public static boolean searchCase = false;

    public static int selectedType = LATEST_EPISODES;

    public static MenuItem search_item;
    SQLiteDatabaseManager sqLiteDatabaseManager;
    CartoonFragment cartoonFragment;
    boolean grid;
    private Menu menu;
    private LoginUtil loginUtil;
    ActivityResultLauncher<String> activityResultRegistry;
    LoginDialog loginDialog;

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activity = this;
        // check Notification perm first
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.POST_NOTIFICATIONS} , 1001);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create channel notification if the device is greater the or equal 26 (oreo) as it`s required
            // and make it priority high so the notification is popping up to the user
            createChannel();
        }
        initRetrofit();
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        init();
        Log.i("ab_do", " time = " + System.currentTimeMillis());
        initToolbar();
        initNavDrawer();
        ImportUserData();
        checkIfServerIsUnderMaintenance();
        getRedirect(savedInstanceState);
        if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser() != null)
            checkIfUserIsNotBlocked(loginUtil.getCurrentUser().getId());
        //test();
        //Print onesignal token
        /*OneSignal.idsAvailable((userId, registrationId) -> {
            Log.d("debug", "User:" + userId);
            if (registrationId != null)
                Log.d("debug", "registrationId:" + registrationId);

        });*/
        if (mBinding.progressBarLayout != null) {
            mBinding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.notification_perm_denied , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ImportUserData() {
        if (LoginToMainCachedData.userData != null) {
            UserData userData = LoginToMainCachedData.userData;
            UserOptions.getUserOptions().setFavouriteCartoons(userData.getFavouriteCartoons());
            UserOptions.getUserOptions().setWatchLaterCartoons(userData.getWatchLaterCartoons());
            UserOptions.getUserOptions().setWatchedCartoons(userData.getWatchCartoons());
            UserOptions.getUserOptions().setSeenEpisodesIds(userData.getSeenEpisodesIds());
        }
    }

    private void checkIfServerIsUnderMaintenance() {
        disposable.add(
                apiService
                        .checkIfServerIsUnderMaintains()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Integer>() {
                            @Override
                            public void onSuccess(Integer statue) {
                                if (statue == 1) {
                                    // server is under maintenance
                                    startActivity(new Intent(getBaseContext(), ServerIsUnderMaintenanceActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "error " + e.getMessage());
                            }
                        })
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.notification_channel_id), "Anime updates", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void init() {
        Log.i("ab_do", sharedPreferencesUtil.getSharedPreferences(this).getString(sharedPreferencesUtil.CURRENT_PHOTO, "no photo"));
        Log.i("ab_do", "User Logged is is = " + sharedPreferencesUtil.getSharedPreferences(this).getInt(sharedPreferencesUtil.USER_ID, -1));
        checkIfTheUserLodged();
        sqLiteDatabaseManager = new SQLiteDatabaseManager(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String viewVal = sharedPreferences.getString(getString(R.string.view_key), getString(R.string.grid));
        grid = viewVal.equals(getString(R.string.grid));
        loginDialog = new LoginDialog(this);
    }

    private void checkIfTheUserLodged() {
        View view = mBinding.navView.getHeaderView(0);
        TextView username, login;
        ImageView user_profile = view.findViewById(R.id.user_profile);
        initUploadPhotoRegister(user_profile);
        user_profile.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View v) {
                Log.i("ab_do", "ClickedProfile");
                if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser() != null)
                    activityResultRegistry.launch("image/jpeg");
            }
        });
        username = view.findViewById(R.id.Username);
        login = view.findViewById(R.id.Login);
        //username.setTextColor(getResources().getColor(R.color.black));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        });
        loginUtil = new LoginUtil(this);
        if (loginUtil.userIsLoggedIN()) {
            User user = loginUtil.getCurrentUser();

            if (user != null) {
                String name = user.getName();
                if (name == null || name.isEmpty())
                    username.setText("لا يوجد اسم");
                else
                    username.setText(name);
                login.setVisibility(View.GONE);
                Uri uri = Uri.parse(loginUtil.getCurrentUser().getPhoto_url());
                if (uri != null && !uri.toString().isEmpty()) {
                    Glide.with(MainActivity.this)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.user_profile)
                            .error(R.drawable.user_profile)
                            .into(user_profile);
                }
            }
        }
    }

    private void initUploadPhotoRegister(ImageView img) {
        activityResultRegistry = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Log.i("ab_do", "onActivityResult " + result);
                if (result != null) {
                    try {
                        if (mBinding != null && mBinding.progressBarLayout != null)
                            mBinding.progressBarLayout.setVisibility(View.VISIBLE);
                        Bitmap uploadedPhotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                        // add api call to save user img :) and get the url of the saved img !
                        String base64Img = ImgUtilities.getBase64Image(uploadedPhotoBitmap);
                        disposable.add(
                                apiService
                                        .changeUserImg(base64Img, loginUtil.getCurrentUser().getId(), getUserSavedImgName())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<String>() {
                                            @Override
                                            public void onSuccess(String imgUrl) {
                                                // update photo url in shared Preference
                                                if (mBinding != null && mBinding.progressBarLayout != null)
                                                    mBinding.progressBarLayout.setVisibility(View.GONE);
                                                if (imgUrl == null || imgUrl.contains("null") || imgUrl.isEmpty()) {
                                                    Log.i("ab_do", "null");
                                                    Toast.makeText(getApplicationContext(), "حدث خطأ ما أثناء حفظ الصورة", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    sharedPreferencesUtil.updateCurrentPhoto(getBaseContext(), imgUrl);
                                                    Uri uri = Uri.parse(imgUrl);
                                                    if (uri != null && !uri.toString().isEmpty()) {
                                                        Glide.with(MainActivity.this)
                                                                .load(uri)
                                                                .centerCrop()
                                                                .placeholder(R.drawable.user_profile)
                                                                .error(R.drawable.user_profile)
                                                                .into(img);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                if (mBinding != null && mBinding.progressBarLayout != null)
                                                    mBinding.progressBarLayout.setVisibility(View.GONE);
                                                Log.i("ab_do", "onError " + e.getMessage());
                                                Toast.makeText(getApplicationContext(), "حدث خطأ ما أثناء حفظ الصورة", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String getUserSavedImgName() {
        String img_url = loginUtil.getCurrentUser().getPhoto_url();
        String[] strings = img_url.split("/");
        return strings[strings.length - 1];
    }

    private void updateFavouriteCartoon() {
        sqLiteDatabaseManager.deleteAllFavouriteCartoons();
//        if (auth.getCurrentUser()!=null){
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            FavouriteRef = database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("FavouriteCartoon");
//            FavouriteListener = new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.d("ab_do" , "onDataChange " + dataSnapshot);
//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                        // TODO: handle the post
//                        Cartoon cartoon = postSnapshot.getValue(Cartoon.class);
//                        if (!sqLiteDatabaseManager.isCartoonFavorite(cartoon.getId()))
//                        sqLiteDatabaseManager.insertFavoriteCartoon(cartoon);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    // Getting Post failed, log a message
//                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                    // ...
//                }
//            };
//            FavouriteRef.addValueEventListener(FavouriteListener);
//        }
    }


    private void updateSeenEpisodes() {
        sqLiteDatabaseManager.deleteAllSeenEpisode();
//        if (auth.getCurrentUser()!=null){
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            //database.getReference().removeValue();
//            SeenEpisodesRef = database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("SeenEpisodes");
//            SeenEpisodeslistener = new ValueEventListener(){
//                @SuppressLint("NotifyDataSetChanged")
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.d("ab_do" , "onDataChange " + dataSnapshot);
//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                        // TODO: handle the post
//                        int val = postSnapshot.getValue(int.class);
//                        Log.d("ab_do" , "Val = " + val);
//                        checkTheEpisodesInDatabase(val);
//                    }
////                    if (LatestEpisodesFragmentFragment!=null &&LatestEpisodesFragmentFragment.getAdapter()!=null)
////                        LatestEpisodesFragmentFragment.getAdapter().notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    // Getting Post failed, log a message
//                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                    //auth.getCurrentUser().updateProfile();
//                    // ...
//                }
//            };
//            SeenEpisodesRef.addValueEventListener(SeenEpisodeslistener);
//        }
    }


    @SuppressLint("SuspiciousIndentation")
    private void checkTheEpisodesInDatabase(int id) {
        if (!sqLiteDatabaseManager.isEpisodeSeen(id))
            sqLiteDatabaseManager.insertSeenEpisode(id);
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
                                if (Config.admob == null) {
                                    Config.admob = admob;
                                    updateAdIds(admob);
//                                    Config.showFacebookBannerAd(MainActivity.this, mBinding.addContainer);

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

                                    MobileAds.initialize(MainActivity.this, initializationStatus -> {

                                    });
                                    createBannerAd();
//                                    loadRewardedAd();

                                    //Test Mediation
//                                    MediationTestSuite.launch(MainActivity.this);

                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

    private void initRetrofit() {
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int theme_id = sharedPreferences.getInt(activity.getString(R.string.THEME_KEY), activity.getResources().getInteger(R.integer.default_theme));
        if (theme_id != activity.getResources().getInteger(R.integer.default_theme) && getSupportActionBar() != null) {
            TypedValue value = new TypedValue();
            getTheme().resolveAttribute(R.attr.toolbarColor, value, true);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(value.data));
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initNavDrawer() {
        mBinding.navView.setNavigationItemSelectedListener(this);
        mBinding.navView.setItemIconTintList(null);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mBinding.drawerLayout,
                mBinding.includedToolbar.toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                /*mBinding.mainContent.setTranslationX(slideOffset * drawerView.getWidth() * -1);
                mBinding.drawerLayout.bringChildToFront(drawerView);
                mBinding.drawerLayout.requestLayout();*/
            }
        };

        //        mDrawerLayout.setScrimColor(getResources().getColor(R.color.drawerBackgroundTint));
        mBinding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Menu nav_Menu = mBinding.navView.getMenu();
        nav_Menu.findItem(R.id.log_out).setVisible(loginUtil.userIsLoggedIN());
        mBinding.navView.setCheckedItem(R.id.latest_episodes);
    }

    public void getRedirect(Bundle savedInstanceState) {
        disposable.add(
                apiService
                        .getRedirect()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Redirect>() {
                            @Override
                            public void onSuccess(Redirect redirect) {
                                if (redirect.getIs_active().equals("yes")) {
                                    Intent intent = new Intent(MainActivity.this, RedirectActivity.class);
                                    intent.putExtra("redirect", redirect);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    getMessage();
                                    initNavDrawer();

                                    if (savedInstanceState == null) {
//                                        inflateCartoonsFragment();
                                        inflateLatestEpisodesFragment();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getMessage();
                                initNavDrawer();

                                if (savedInstanceState == null) {
                                    inflateLatestEpisodesFragment();
//                                    inflateCartoonsFragment();
                                }
                            }
                        })
        );
    }

    private void createBannerAd() {
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);

        if (admob != null && admob.getBanner() != null) {
            mAdView.setAdUnitId(admob.getBanner());

            if (mBinding.addContainer != null) {
                ((LinearLayout) mBinding.addContainer).addView(mAdView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void showDrawer() {
        mBinding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    @SuppressLint("SuspiciousIndentation")
    private void inflateLatestEpisodesFragment() {
        if (menu != null) {
            menu.findItem(R.id.notification).setVisible(true);
        }
        showSearchMenu(false);
        Objects.requireNonNull(getSupportActionBar()).setTitle("اخر الحلقات المضافة");
        if (LoginToMainCachedData.episodeList != null) {
            Log.i("ab_do2", "list is ready");
            List<EpisodeWithInfo> episodes = LoginToMainCachedData.episodeList;
            Log.i("ab_do2", "size " + episodes.size());
            LatestEpisodesFragmentFragment = new LatestEpisodesFragment(episodes);
        } else
            LatestEpisodesFragmentFragment = new LatestEpisodesFragment();
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_fragment, LatestEpisodesFragmentFragment, getString(R.string.latest_episodes_fragment));
            transaction.commit();
        } catch (Exception exception) {
            Log.i("ab_do", "exp " + exception.getMessage());
        }

    }

    private void replaceLatestEpisodesFragment() {
        if (menu != null) {
            menu.findItem(R.id.notification).setVisible(true);
        }
        showSearchMenu(false);
        selectedType = LATEST_EPISODES;
        Objects.requireNonNull(getSupportActionBar()).setTitle("أحدث الحلقات");
        LatestEpisodesFragmentFragment = new LatestEpisodesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment, LatestEpisodesFragmentFragment, getString(R.string.latest_episodes_fragment));
        transaction.commit();
    }

    private void replaceCartoonsFragment() {
        cartoonFragment = new CartoonFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment, cartoonFragment, getString(R.string.cartoon_fragment));
        transaction.commit();
    }

    private void inflateCartoonsFragment() {
        getSupportActionBar().setTitle(getString(R.string.app_name));
        CartoonFragment fragment = new CartoonFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_fragment, fragment, getString(R.string.cartoon_fragment));
        transaction.commit();
    }


    @SuppressLint("SuspiciousIndentation")
    public void getNewCartoons() {
        cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.cartoon_fragment));
        if (cartoonFragment != null)
            cartoonFragment.getCartoonsByType(selectedType);
    }


    public void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();

        if (focusedView != null) {
            try {
                assert inputManager != null;
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (AssertionError e) {
                e.printStackTrace();
            }
        }
    }


    public void getMessage() {
        disposable.add(
                apiService
                        .getMessage()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Redirect>() {
                            @Override
                            public void onSuccess(Redirect redirect) {
                                if (redirect.getIs_active().equals("yes")) {
                                    MessageDialog messageDialog = new MessageDialog(activity, redirect);
                                    messageDialog.showDialog();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        })
        );
    }

    //-----------Override Methods----------------//


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String viewVal = sharedPreferences.getString(getString(R.string.view_key), getString(R.string.grid));
        boolean newGrid = viewVal.equals(getString(R.string.grid));
        if(newGrid!=grid) recreate();
        getAdmobData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        updateGridIcon(menu.findItem(R.id.grid_or_list));
        updateNotificationIcon(menu.findItem(R.id.notification));
        search_item = menu.findItem(R.id.menusearch);
        searchView = (android.widget.SearchView) search_item.getActionView();
        searchView.setQueryHint("   أبحث هنا ");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @SuppressLint("SuspiciousIndentation")
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s)) {
                    if (cartoonFragment != null)
                        cartoonFragment.filterData(s);
                }
                hideSoftKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.i("ab_do", "onQueryTextChange " + s);
                if (cartoonFragment != null) {
                    if (!TextUtils.isEmpty(s)) {
                        searchCase = true;
                        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        if (cartoonFragment != null)
                            cartoonFragment.filterData(s);
                    } else {
                        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        searchCase = false;
                        if (cartoonFragment != null)
                            cartoonFragment.resetDataAfterSearch();
                    }
                }
                return true;
            }
        });


        return true;
    }

    private void updateNotificationIcon(MenuItem notification_item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean active_notification = sharedPreferences.getBoolean("notify", true);
        if (active_notification) {
            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.notification_topic));
            notification_item.setIcon(R.drawable.ic_baseline_notifications_active_24);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(getString(R.string.notification_topic));
            notification_item.setIcon(R.drawable.ic_baseline_notifications_none_24);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Config.shareApp(MainActivity.this);
        } else if (item.getItemId() == R.id.grid_or_list) {
            grid = !grid;
            updateGridIcon(item);
            if (cartoonFragment != null) {
                cartoonFragment.initRecyclerview(grid, false);
            } else if (LatestEpisodesFragmentFragment != null) {
                LatestEpisodesFragmentFragment.initRecyclerview(grid);
            }
        } else if (item.getItemId() == R.id.notification) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean active_notification = sharedPreferences.getBoolean("notify", true);
            active_notification = !active_notification;
            sharedPreferences.edit().putBoolean("notify", active_notification).commit();
            updateNotificationIcon(item);
            if (active_notification) {
                showSnackMsg("سيتم إعلامك بالحلقات الجديده");
            } else {
                showSnackMsg("لن يتم إعلامك بالحلقات الجديده");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackMsg(String s) {
        Snackbar snack = Snackbar.make(mBinding.getRoot(), s, Snackbar.LENGTH_SHORT);
        showSnack(snack);
    }

    private void showSnack(Snackbar snack) {
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        snack.show();
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


    private void updateAdIds(Admob admob) {
        Log.i("ab_do", "updateAdIds");
        Log.i("ab_do", "banner = " + admob.getBanner());
        Log.i("ab_do", "InterstitialBeforeKey " + admob.getInterstitial());
        Log.i("ab_do", "InterstitialAfterKey " + admob.getInterstitial2());
        Log.i("ab_do", "ima_id_key " + admob.getImaAd());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.bannerKey), admob.getBanner()).apply();
        editor.putString(getString(R.string.InterstitialBeforeKey), admob.getInterstitial()).apply();
        editor.putString(getString(R.string.InterstitialAfterKey), admob.getInterstitial2()).apply();
        editor.putString(getString(R.string.ima_id_key), admob.getImaAd()).apply();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            hideDrawer();
        } else if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
            hideSoftKeyboard();
        } else if (selectedType != LATEST_EPISODES) {
            selectedType = LATEST_EPISODES;
            mBinding.navView.getMenu().getItem(0).setChecked(true);
            cartoonFragment = null;
            updateGridIcon(menu.findItem(R.id.grid_or_list));
            replaceLatestEpisodesFragment();
        } else {
            /*Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/

            //Check If rated
            /*SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE);
            boolean isRated = prefs.getBoolean("isRated", false);
            if (isRated) {
                MainActivity.this.finish();
            }else{
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

                builder.setMessage("قيمنا بخمس نجوم");
                builder.setCancelable(true);
                builder.setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE).edit();
                        editor.putBoolean("isRated", true);
                        editor.apply();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    }
                });

                builder.setNegativeButton("في المرة القادمة", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        MainActivity.this.finish();
                    }
                });

                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }*/

            MainActivity.this.finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        int itemId = item.getItemId();//No Action
        if (itemId == R.id.latest_episodes) {
            showSearchMenu(false);
            mBinding.navView.setCheckedItem(itemId);
            cartoonFragment = null;
            updateGridIcon(menu.findItem(R.id.grid_or_list));
            replaceLatestEpisodesFragment();
        } else if (itemId == R.id.anime) {
            openCartoonFragment(Constants.DUBBED_ANIME, "المدبلج", itemId);
            showSearchMenu(true);
        } else if (itemId == R.id.films_t) {
            openCartoonFragment(Constants.TRANSLATED_FILMS, "الافلام المترجمه", itemId);
            showSearchMenu(true);
        } else if (itemId == R.id.films_d) {
            openCartoonFragment(Constants.DUBBED_FILMS, "الافلام المدبلجة", itemId);
            showSearchMenu(true);
        } else if (itemId == R.id.most_viewed) {
            mBinding.navView.setCheckedItem(itemId);
            openCartoonFragment(Constants.MOST_VIEWED, "الأكثر المشاهدة", itemId);
            showSearchMenu(false);
        } else if (itemId == R.id.see_later) {
            if (loginUtil.userIsLoggedIN()) {
                openCartoonFragment(Constants.WATCH_LATER, "قائمتي", itemId);
                showSearchMenu(false);
            } else {
                loginDialog.showDialog();
                hideDrawer();
                return false;
            }
        } else if (itemId == R.id.animeSeen) {
            if (loginUtil.userIsLoggedIN()) {
                openCartoonFragment(Constants.WATCHED, "تمت مشاهدته", itemId);
                showSearchMenu(false);
            } else {
                loginDialog.showDialog();
                hideDrawer();
                return false;
            }
        } else if (itemId == R.id.favourite) {
            if (loginUtil.userIsLoggedIN()) {
                openCartoonFragment(Constants.FAVOURITE, "المفضلة", itemId);
                showSearchMenu(false);
            } else {
                loginDialog.showDialog();
                hideDrawer();
                return false;
            }
        } else if (itemId == R.id.anime_time) {
            startActivity(new Intent(getBaseContext(), EpisodeDatesActivity.class));
            return false;
        } else if (itemId == R.id.translation_anime) {
            openCartoonFragment(Constants.TRANSLATED_ANIME, "المترجم", itemId);
            showSearchMenu(true);
        } else if (itemId == R.id.new_cartoon) {
            openCartoonFragment(Constants.NEW_ANIME, "المستمر", itemId);
            showSearchMenu(false);
        } else if (itemId == R.id.leaderboard) {
            startActivity(new Intent(getBaseContext(), LeaderboardActivity.class));
            return false;
        } else if (itemId == R.id.anime_downloads) {
            if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser() != null) {
                startActivity(new Intent(getBaseContext(), EpisodeDownloadsActivity.class));
            } else {
                loginDialog.showDialog();
                hideDrawer();
            }
            return false;
        } else if (itemId == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return false;
        }
//        else if (itemId == R.id.support) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/m_c_w_a")));
//        }
//        else if (itemId == R.id.rate) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://apps-anime.com")));
//        }
//        else if (itemId == R.id.contact_us) {
//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                    "mailto", "support@apps-anime.com", null));
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
//            startActivity(Intent.createChooser(emailIntent, "Send email"));
//        }

        else if (itemId == R.id.log_out) {
            try {
                loginUtil.signOut();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
            } catch (Exception exception) {
                Log.d("ab_do", exception.getMessage());
            }
        } else {
            Toast.makeText(this, "القسم تحت الصيانة أخي الغالي :)", Toast.LENGTH_LONG).show();
        }
        getAdmobData();
        hideDrawer();
        return true;
    }

    private void showSearchMenu(boolean b) {
        if (search_item != null)
            search_item.setVisible(b);
    }

    private void openCartoonFragment(int type, String s, int item_id) {
        if (menu != null) {
            menu.findItem(R.id.notification).setVisible(false);
        }
        mBinding.navView.setCheckedItem(item_id);
        selectedType = type;
        updateGridIcon(menu.findItem(R.id.grid_or_list));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(s);
        replaceCartoonsFragment();
    }

    public void getFilms(String action) {
        Log.i("ab_do", "getFilms " + action);
        int pageNumber = 1;
        disposable.add(
                apiService
                        .getCartoons(pageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Cartoon>>() {
                            @Override
                            public void onSuccess(List<Cartoon> retrievedCartoonList) {
                                for (int i = 0; i < retrievedCartoonList.size(); i++) {
//
                                    if (retrievedCartoonList.get(i).getTitle() != null && retrievedCartoonList.get(i).getTitle().equals("الافلام")) {
                                        Log.i("Ab_do", "catch Film");
                                        Cartoon cartoon = retrievedCartoonList.get(i);
                                        getPlaylists(cartoon, action);
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
//                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void getPlaylists(Cartoon cartoon, String action) {
        Log.i("ab_bo", "getPlaylists " + action);
        disposable.add(
                apiService
                        .getPlaylists(cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Playlist>>() {
                            @Override
                            public void onSuccess(List<Playlist> playlistList) {
                                for (int i = 0; i < playlistList.size(); i++) {
                                    if (playlistList.get(i).getTitle() != null && playlistList.get(i).getTitle().trim().equals(action.trim())) {
                                        // catch playlist
                                        //dialogUtilities.dismissDialog();
                                        mBinding.progressBarLayout.setVisibility(View.GONE);
                                        Intent intent = new Intent(getBaseContext(), EpisodesActivity.class);
                                        intent.putExtra("playlist", playlistList.get(i));
                                        intent.putExtra("cartoon", cartoon);
                                        intent.putExtra("title", action);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                //mBinding.progressBarLayout.setVisibility(View.GONE);
                            }
                        })
        );

    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onReportClicked(Redirect redirect) {
        if (redirect.getRedirect_type().equals("package_name")) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + redirect.getPackage_name())));

        } else if (redirect.getRedirect_type().equals("url")) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect.getUrl()));
            startActivity(browserIntent);
        }
    }

    private void checkIfUserIsNotBlocked(int user_id) {
        disposable.add(
                apiService
                        .getUserBlockedStatue(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Integer>() {
                            @Override
                            public void onSuccess(Integer statue) {
                                Log.i("ab_do", "Statue = " + statue);
                                if (statue == User.IS_BLOCKED) {
                                    // sign out the user
                                    Log.i("ab_do", "user is blocked");
                                    loginUtil.signOut();
                                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "error get block statue " + e.getMessage());
                                //Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(MainActivity.this))
                                    openNoNetworkActivity();
                            }
                        })
        );
    }

    private void openNoNetworkActivity() {
        startActivity(new Intent(getBaseContext(), NoNetworkActivity.class));
        finish();
    }
}
