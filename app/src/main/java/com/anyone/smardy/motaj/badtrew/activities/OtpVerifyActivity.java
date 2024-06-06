package com.anyone.smardy.motaj.badtrew.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.ImgUtilities;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginMethod;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityOtpVerifyBinding;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class OtpVerifyActivity extends AppCompatActivity {
    ActivityOtpVerifyBinding binding ;
    int code ;
    String email , password , username ;
    LoginUtil loginUtil ;
    Bitmap uploadedPhotoBitmap;
    String uploadedPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        binding = ActivityOtpVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        loginUtil = new LoginUtil(this);
        email = getIntent().getStringExtra("email");
        code = getIntent().getIntExtra("code" , -1);
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");
        if (getIntent().getStringExtra("photo") !=null) {
            uploadedPhoto = getIntent().getStringExtra("photo");
            Uri uri = Uri.parse(uploadedPhoto);
            try {
                uploadedPhotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                uploadedPhotoBitmap = null;
                e.printStackTrace();
            }
        } else  uploadedPhotoBitmap = null;
        binding.code1.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.code2.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.code3.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.code4.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.code1.requestFocus();
        binding.retrySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = (int)(Math.random()*9000)+1000;
                sendVerifyCode();
            }
        });
        binding.changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
                finish();
            }
        });
        binding.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;
                if (binding.code1.getText().toString().trim().isEmpty() || binding.code2.getText().toString().trim().isEmpty() || binding.code3.getText().toString().trim().isEmpty() || binding.code4.getText().toString().trim().isEmpty()) {
                    showSnackMsg("يرجي إدخال رمز التحقق المكون من 4 أرقام");
                    return;
                }
                String entered_code_Str = binding.code1.getText().toString() + binding.code2.getText().toString() + binding.code3.getText().toString() + binding.code4.getText().toString() ;
                try {
                    int entered_code = Integer.parseInt(entered_code_Str);
                    if (entered_code == code) {
                        // go
                        createNewUser();
                    }
                    else {
                        showSnackMsg("رمز التحقق الذي أدخلته غير صحيح");
                    }
                } catch (Exception exception) {
                    showSnackMsg("يرجي إدخال رمز صحيح");
                }
            }
        });
        binding.code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                  binding.code2.requestFocus();
            }
        });
        binding.code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                  binding.code3.requestFocus();
            }
        });
        binding.code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty())
                  binding.code4.requestFocus();
            }
        });
    }

    private void sendVerifyCode() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        binding.retrySend.setEnabled(false);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .sendOtpToEmail(email, code)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                binding.retrySend.setEnabled(true);
                                if (!response.isError()) {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إرسال رمز التحقق إلي الإيميل الخاص بك");
                                    binding.code1.requestFocus();
                                } else {
                                    Toast.makeText(getApplicationContext(), "حدث خطأ ما يرجي إعادة المحاولة", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.retrySend.setEnabled(true);
                                Log.i("ab_do", "error when make report");
                                Toast.makeText(getApplicationContext(), "حدث خطأ ما يرجي إعادة المحاولة", Toast.LENGTH_SHORT).show();
                            }
                        })
        );


    }

        private void showSnackMsg (String s) {
            Snackbar snack = Snackbar.make(binding.getRoot(), s, Snackbar.LENGTH_SHORT);
            showSnack(snack);
        }

        private void showSnack(Snackbar snack) {
            View view = snack.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.CENTER;
            view.setLayoutParams(params);
            snack.show();
        }

    private void createNewUser() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .createNewUserWithEmail(email, password, username , "")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse userResponse) {
                                if (!userResponse.isError()) {
                                    saveUserImg(userResponse.getUser().getId(), username , LoginMethod.EMAIL);
                                }
                                else {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("هناك خطأ ما يرجي إعادة المحاولة");
                                    }
                                }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                showSnackMsg("هناك خطأ ما يرجي إعادة المحاولة");
                            }
                        })
        );
    }

    private void saveUserImg(int user_id, String username , LoginMethod loginMethod) {
        // add api call to save user img :) and get the url of the saved img !
        if (uploadedPhotoBitmap==null) {
            load("", loginMethod, username, user_id);
            return;
        }
        String base64Img = ImgUtilities.getBase64Image(uploadedPhotoBitmap);
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CompositeDisposable disposable = new CompositeDisposable();
        disposable.add(
                apiService
                        .saveUserImg(base64Img , user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String imgUrl) {
                                if (imgUrl.contains("null")) {
                                    Toast.makeText(getApplicationContext(), "حدث خطأ ما أثناء حفظ الصورة", Toast.LENGTH_SHORT).show();
                                }
                                load(imgUrl, loginMethod, username, user_id);
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                showSnackMsg("هناك خطأ ما يرجي إعادة المحاولة");
                                load("", loginMethod, username, user_id);
                            }
                        })
        );
    }

    private void load(String imgUrl, LoginMethod loginMethod, String username, int user_id) {
        loginUtil.saveLoginInformation(loginMethod, username, imgUrl, user_id);
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        binding.progressBarLayout.setVisibility(View.GONE);
        startActivity(intent);
        finish();
    }
}