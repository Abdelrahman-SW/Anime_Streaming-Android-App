package com.anyone.smardy.motaj.badtrew.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.Utilites.dialogUtilities;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {
    Button CreateAccount ;
    ImageView addPhoto;
    TextView Login  , privacyTxt;
    EditText Username , Password , Email ;
    dialogUtilities dialogUtilities ;
    LoginUtil loginUtil ;
    Uri uploadedImg = null ;
    Bitmap uploadedPhotoBitmap;
    ActivityResultLauncher<String> activityResultRegistry ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        setContentView(R.layout.activity_register);
        Init();
        SetListenersToButtons();
    }

    private void SetListenersToButtons() {
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAcc();
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext() , LoginActivity.class));
                finish();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityResultRegistry.launch("image/jpeg");
            }
        });

    }

//    public String getMimeType(Uri uri) {
//        String mimeType = null;
//        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
//            ContentResolver cr = getApplicationContext().getContentResolver();
//            mimeType = cr.getType(uri);
//        } else {
//            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
//                    .toString());
//            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//                    fileExtension.toLowerCase());
//        }
//        return mimeType;
//    }


    private void Init() {
        CreateAccount = findViewById(R.id.CreateAccount);
        Login = findViewById(R.id.Login);
        Username = findViewById(R.id.Username);
        Password = findViewById(R.id.Password);
        Email = findViewById(R.id.email) ;
        dialogUtilities = new dialogUtilities();
        addPhoto = findViewById(R.id.add_photo);
        loginUtil = new LoginUtil(this);
        privacyTxt = findViewById(R.id.privacyTxt);
        initPrivacyTxt();
        activityResultRegistry = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Log.i("ab_do" , "onActivityResult " + result);
                if (result!=null) {
                   // Log.i("ab_do" , "mimeType = " + getMimeType(result));
                    uploadedImg = result ;
                    try {
                        uploadedPhotoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                        // uploadedPhotoBitmap = Bitmap.createScaledBitmap(uploadedPhotoBitmap, 120 , 120 , true);
                        //addPhoto.setImageBitmap(uploadedPhotoBitmap);
                        Glide.with(getBaseContext())
                                .load(result)
                                .centerCrop()
                                .into(addPhoto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

    private void CreateAcc() {
        String email = Email.getText().toString();
        if (TextUtils.isEmpty(Username.getText()) || TextUtils.isEmpty(Password.getText())) {
            Snackbar.make(CreateAccount , "يرجي ملأ كل الحقول أولا" , Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || TextUtils.isEmpty(email) || !validEmail(email)) {
            Snackbar.make(CreateAccount , "يرجي إدخال بريد إالكتروني صحيح" , Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (Password.getText().length()<6) {
            Snackbar.make(CreateAccount , "يجب أن تكون كلمة المرور أطول من 6 حروف" , Snackbar.LENGTH_SHORT).show();
            return;
        }
        //SaveAuth(email , Password.getText().toString());
        dialogUtilities.ShowDialog(this);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .checkIfEmailExits(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (response.getCode() == Constants.USER_ALREADY_EXISTS) {
                                    dialogUtilities.dismissDialog();
                                    Snackbar.make(Login , "هذا الحساب موجود بالفعل !" , Snackbar.LENGTH_SHORT).show();
                                }
                                else {
                                       sendVerifyCode(email);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialogUtilities.dismissDialog();
                                Log.i("ab_do", "error when make report");
                                Toast.makeText(getApplicationContext(), "حدث خطأ ما يرجي إعادة المحاولة", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void goToVerifyActivity(String email , int code) {
        Intent intent = new Intent(getBaseContext() , OtpVerifyActivity.class);
        intent.putExtra("email" , email);
        intent.putExtra("code" , code);
        intent.putExtra("password" , Password.getText().toString());
        intent.putExtra("username" , Username.getText().toString());
        if (uploadedImg!=null)
        intent.putExtra("photo" , uploadedImg.toString());
        startActivity(intent);
        finish();
    }

    private void sendVerifyCode(String email) {
        int code = (int)(Math.random()*9000)+1000;
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
                                if (!response.isError()) {
                                    dialogUtilities.dismissDialog();
                                    goToVerifyActivity(email  , code);
                                } else {
                                    Toast.makeText(getApplicationContext(), "حدث خطأ ما يرجي إعادة المحاولة", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "error when make report");
                                Toast.makeText(getApplicationContext(), "حدث خطأ ما يرجي إعادة المحاولة" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );

    }

    private boolean validEmail(String email) {
        return (email.contains(".com") || email.contains(".Com") && (email.contains("yahoo") || (email.contains("hotmail") || ((email.contains("gmail")) || email.contains("Gmail"))))) ;
    }


}