package com.anyone.smardy.motaj.badtrew.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityForgetPasswordBinding;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class forgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.email.requestFocus();
        binding.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.email.getText().toString().trim().isEmpty()) {
                    Toast.makeText(forgetPasswordActivity.this, "يرجي إدخال البريد الإلكتروني أولا", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches() || TextUtils.isEmpty(binding.email.getText().toString()) || !validEmail(binding.email.getText().toString())) {
                    Toast.makeText(forgetPasswordActivity.this, "يرجي إدخال بريد إالكتروني صحيح", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkIfEmailExits();
            }
        });
        binding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfEmailExits() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .checkIfEmailExits(binding.email.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (response.getCode() == Constants.USER_ALREADY_EXISTS) {
                                   // send password to the email
                                    getPasswordAndSendITToEmail();
                                }
                                else {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("هذا الحساب غير موجود !");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Log.i("ab_do", "error when make report");
                                Toast.makeText(getApplicationContext(), "حدث خطأ ما يرجي إعادة المحاولة" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void getPasswordAndSendITToEmail() {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .sendPasswordToEmail(binding.email.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    // send password to the email
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    startActivity(new Intent(getBaseContext() , LoginActivity.class));
                                    finish();
                                    Toast.makeText(getApplicationContext(), "تم إرسال كلمة السر إلي البريد الخاص بك", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "لا يمكن إرسالة كلمة السر إلي هذا الحساب", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Log.i("ab_do", "error when make report");
                                Toast.makeText(getApplicationContext(),"حدث خطأ ما يرجي إعادة المحاولة" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private boolean validEmail(String email) {
        return (email.contains(".com") || email.contains(".Com") && (email.contains("yahoo") || (email.contains("hotmail") || ((email.contains("gmail")) || email.contains("Gmail"))))) ;
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
}