package com.anyone.smardy.motaj.badtrew.activities;

import static com.anyone.smardy.motaj.badtrew.app.Config.isNetworkConnected;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginMethod;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginToMainCachedData;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.Utilites.Utilities;
import com.anyone.smardy.motaj.badtrew.Utilites.dialogUtilities;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.app.UserOptions;
import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;
import com.anyone.smardy.motaj.badtrew.model.EpisodeWithInfo;
import com.anyone.smardy.motaj.badtrew.model.User;
import com.anyone.smardy.motaj.badtrew.model.UserData;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    Button Login , Google , Facebook ;
    TextView Register , forgetPassword , privacyTxt;
    EditText Email , password ;
    private GoogleSignInClient mGoogleSignInClient;
    dialogUtilities dialogUtilities ;
    LoginUtil loginUtil ;
    CompositeDisposable disposable ;
    ApiService apiService ;
    private int user_id ;
    UserOptions userOptions ;
    View decor_View ;
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        decor_View = getWindow().getDecorView();
        Utilities.hideNavBar(decor_View);
        setContentView(R.layout.activity_login);
        init();
        setListeners();
    }

    private void setListeners() {
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext() , RegisterActivity.class));
                finish();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_Login(Email.getText().toString() , password.getText().toString());
            }
        });
        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signInWithGoogle();
            }
        });
        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(getBaseContext() , FacebookAuthActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }

    private void init() {
        userOptions = UserOptions.getUserOptions();
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        Login = findViewById(R.id.Login);
        Register = findViewById(R.id.Register);
        privacyTxt = findViewById(R.id.privacyTxt);
        Email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Google = findViewById(R.id.google);
        Facebook = findViewById(R.id.facebook);
        forgetPassword = findViewById(R.id.forgetPassword);
        dialogUtilities = new dialogUtilities();
        loginUtil = new LoginUtil(this);
        initPrivacyTxt();
        prepareSignInWithGoogle();
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext() , forgetPasswordActivity.class));
            }
        });
    }

    private void initPrivacyTxt() {
        ClickableSpan policySpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(Constants.POLICY_LINK)));
            }
        };
        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse(Constants.TERMS_OF_USE_LINK)));
            }
        };
        SpannableString spannableString = new SpannableString(Constants.privacyTxt);
        spannableString.setSpan(termsSpan , 32 , 46 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(policySpan , 49 , 63 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyTxt.setMovementMethod(LinkMovementMethod.getInstance());
        privacyTxt.setText(spannableString);
    }

    private void prepareSignInWithGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.ClientID))
                .requestServerAuthCode(getString(R.string.ClientID))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, options);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void User_Login(String email , String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Snackbar.make(Login , "يرجي ملأ كل الحقول أولا" , Snackbar.LENGTH_SHORT).show();
            return;
        }
        dialogUtilities.ShowDialog(this);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .loginWithEmail(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.isError()) {
                                    if (userResponse.getUser().getIs_blocked() == User.IS_BLOCKED) {
                                        loginUtil.signOut();
                                        dialogUtilities.dismissDialog();
                                        Snackbar.make(Login , "تم حظر هذا الحساب ! لا يمكنك تسجيل الدخول" , Snackbar.LENGTH_SHORT).show();
                                        return;
                                    }
                                    loginUtil.saveLoginInformation(LoginMethod.EMAIL , userResponse.getUser().getName() , userResponse.getUser().getPhoto_url() , userResponse.getUser().getId());
                                    user_id = userResponse.getUser().getId();
                                    loadUserData();
                                }
                                else {
                                    int code = userResponse.getCode();
                                    if (code == Constants.USER_NOT_FOUND) {
                                        dialogUtilities.dismissDialog();
                                        Snackbar.make(Login , "هذا الحساب غير موجود ( يرجي إدخال إيميل صحيح )!" , Snackbar.LENGTH_SHORT).show();
                                    }
                                    else if (code == Constants.INCORRECT_PASSWORD) {
                                        dialogUtilities.dismissDialog();
                                        Snackbar.make(Login , "كلمة السر خاطئة يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                                    }
                                    else {
                                        dialogUtilities.dismissDialog();
                                        Snackbar.make(Login , "حدث خطأ ما يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialogUtilities.dismissDialog();
                                Snackbar.make(Login , "حدث خطأ ما يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                            }
                        })
        );
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Load(user);
//                        }
//                        else {
//                            // If sign in fails, display a message to the user.
//                            Snackbar.make(Login , "المعلومات الذي أدخلتها غير صحيحه يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
//                            // ...
//                        }
//
//                        // ...
//                    }
//                });
    }

    private void checkIfUserBlocked(int is_blocked) {

    }

    private void loginCompleted() {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        dialogUtilities.dismissDialog();
        startActivity(intent);
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialogUtilities.ShowDialog(this);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("ab_do_google", "id Token: " + account.getIdToken());
                Log.d("ab_do_google", "email: " + account.getEmail());
                Log.d("ab_do_google", "username: " + account.getDisplayName());
                Log.d("ab_do_google", "photo: " + account.getPhotoUrl());
                Uri photo_Uri = account.getPhotoUrl() != null ?  account.getPhotoUrl() : Uri.EMPTY ;
                createNewUserWithGoogle(account.getIdToken() ,  account.getEmail() , account.getDisplayName()
                , photo_Uri);
                //firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                dialogUtilities.dismissDialog();
                Snackbar.make(Login , "فشل عملية تسجيل الدخول يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void createNewUserWithGoogle(String idToken, String email, String displayName, Uri photoUrl) {
        Utilities.hideNavBar(decor_View);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .createNewUserWithGoogleToken(idToken , email, displayName, photoUrl.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.isError()) {
                                    user_id = userResponse.getUser().getId();
                                    if (userResponse.getCode() == Constants.USER_ALREADY_EXISTS) {
                                        if (userResponse.getUser().getIs_blocked() == User.IS_BLOCKED) {
                                            loginUtil.signOut();
                                            dialogUtilities.dismissDialog();
                                            Snackbar.make(Login , "تم حظر هذا الحساب ! لا يمكنك تسجيل الدخول" , Snackbar.LENGTH_SHORT).show();
                                            return;
                                        }
                                        loginUtil.saveLoginInformation(LoginMethod.GOOGLE , userResponse.getUser().getName() , userResponse.getUser().getPhoto_url() , userResponse.getUser().getId());
                                        loadUserData();
                                    }
                                    else {
                                        loginUtil.saveLoginInformation(LoginMethod.GOOGLE , "" , photoUrl.toString() , userResponse.getUser().getId());
                                        loginCompleted();
                                    }
                                }
                                else {
                                    dialogUtilities.dismissDialog();
                                    Snackbar.make(Login , "حدث خطأ ما يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialogUtilities.dismissDialog();
                                Snackbar.make(Login , "حدث خطأ ما يرجي إعادة المحاولة" , Snackbar.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadWatchLaterCartoons() {
        disposable.add(
                apiService
                        .getAllWatchedLaterCartoons(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                UserOptions.getUserOptions().setWatchLaterCartoons(retrievedCartoonList);
                                loadSeenEpisodes();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //loadSeenEpisodes();
                                Toast.makeText(LoginActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadUserData() {
        disposable.add(
                apiService
                        .LoadUserData(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserData>() {
                            @Override
                            public void onSuccess(UserData userData) {
                                List<EpisodeWithInfo> episodeList = new ArrayList<>(userData.getLatestEpisodes());
                                Intent intent = new Intent(getBaseContext() , MainActivity.class);
                                LoginToMainCachedData.episodeList = episodeList ;
                                LoginToMainCachedData.userData = userData ;
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //Toast.makeText(splashActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                if (!isNetworkConnected(LoginActivity.this))
                                    openNoNetworkActivity();
                                else  Toast.makeText(LoginActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void openNoNetworkActivity() {
        startActivity(new Intent(getBaseContext() , NoNetworkActivity.class));
        finish();
    }

    private void loadSeenEpisodes() {
        disposable.add(
                apiService
                        .getAllSeenEpisodes(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> retrievedEpisodesIdsList) {
                                UserOptions.getUserOptions().setSeenEpisodesIds(retrievedEpisodesIdsList);
                                loginCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //loginCompleted();
                                Toast.makeText(LoginActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadWatchedCartoons() {
        disposable.add(
                apiService
                        .getAllWatchedCartoons(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                UserOptions.getUserOptions().setWatchedCartoons(retrievedCartoonList);
                                loadWatchLaterCartoons();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //loadWatchLaterCartoons();
                                Toast.makeText(LoginActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadFavouriteCartoons() {
        disposable.add(
                apiService
                        .getAllFavouriteCartoons(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<CartoonWithInfo>>() {
                            @Override
                            public void onSuccess(List<CartoonWithInfo> retrievedCartoonList) {
                                UserOptions.getUserOptions().setFavouriteCartoons(retrievedCartoonList);
                                loadWatchedCartoons();
                            }

                            @Override
                            public void onError(Throwable e) {
                                //loadWatchedCartoons();
                                Toast.makeText(LoginActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

}