package com.anyone.smardy.motaj.badtrew.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.LoginUtil;
import com.anyone.smardy.motaj.badtrew.Utilites.ReportDialog;
import com.anyone.smardy.motaj.badtrew.adapters.CartoonFeedbacksAdapter;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.ActivityCommentBinding;
import com.anyone.smardy.motaj.badtrew.model.Feedback;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FeedbacksActivity extends AppCompatActivity implements ReportDialog.onReportClickListener , CartoonFeedbacksAdapter.ShouldLoginMsg {
    int user_id, cartoon_id;
    CompositeDisposable disposable;
    ApiService apiService;
    ActivityCommentBinding binding;
    List<Feedback> loaded_feedbacks = new ArrayList<>();
    List<Integer> feedbackLikesIDs = new ArrayList<>();
    List<Integer> feedbackDisLikesIDs = new ArrayList<>();
    LoginUtil loginUtil ;
    CartoonFeedbacksAdapter feedbacksAdapter ;
    private boolean isRefresh = false;
    boolean desc = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadFeedbacks();
        binding.progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void progressBar(View view) {
        Toast.makeText(this, "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
    }

    private void init() {
        loginUtil = new LoginUtil(this) ;
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        if (loginUtil.getCurrentUser()!=null)
        user_id = loginUtil.getCurrentUser().getId();
        else
        user_id = -1 ;
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        cartoon_id = getIntent().getIntExtra(Constants.CARTOON_ID, -1);
        initToolbar();
        feedbacksAdapter = new CartoonFeedbacksAdapter(this , user_id , apiService , disposable , false);
        binding.recycleView.setAdapter(feedbacksAdapter);
        binding.recycleView.setItemAnimator(null);
        binding.sendFeedbackImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = binding.feedbackEditText.getText().toString();
                if (feedback.trim().isEmpty()) {
                    showSnackMsg("يرجي ملئ الحقل أولا !");
                }
                else {
                    if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null)
                        addFeedback(feedback);
                else {
                        showSnackMsg("عفوا يرجي تسجيل الدخول أولا ");
                    }
                }
            }
        });
        initSwipeRefresh();
    }


    private void initSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                 binding.swipeRefreshLayout.setRefreshing(true);
                 isRefresh = true;
                 loadFeedbacks();
            }
        });
    }

    private void addFeedback(String feedback) {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        // add api call to save the feedback :)
        disposable.add(
                apiService
                        .addCartoonFeedback(user_id , cartoon_id , feedback,loginUtil.getCurrentUser().getName() , loginUtil.getCurrentUser().getPhoto_url() , String.valueOf(System.currentTimeMillis()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                Log.i("ab_do" , "onSuccess addedFeedback");
                                if (!response.isError()) {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    binding.feedbackEditText.setText("");
                                    binding.feedbackEditText.clearFocus();
                                    addNewFeedbackToAdapter(response.getReturned_id() , feedback);
                                }
                                else {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("حدث خطا ما أثناء إضافة التوصية الخاصة بك");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do" , "onError addedFeedback " + e.getMessage());
                                binding.progressBarLayout.setVisibility(View.GONE);
                                showSnackMsg("حدث خطا ما أثناء إضافة التوصية الخاصة بك");
                            }
                        })
        );
    }

    private void addNewFeedbackToAdapter(int returned_id, String feedback) {
        Feedback _feedback = new Feedback(returned_id , cartoon_id , user_id , feedback , loginUtil.getCurrentUser().getName() , loginUtil.getCurrentUser().getPhoto_url() , 0 , 0 , String.valueOf(System.currentTimeMillis()));
        int pos = desc ? 0 : loaded_feedbacks.size() ;
        feedbacksAdapter.addFeedback(pos , _feedback);
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

    private void initToolbar() {
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setTitle("التوصيات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadFeedbacks() {
        if (desc) {
            disposable.add(
                    apiService
                            .getFeedbacksDesc(cartoon_id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<Feedback>>() {
                                @Override
                                public void onSuccess(List<Feedback> feedbacks) {
                                    loaded_feedbacks.clear();
                                    loaded_feedbacks = feedbacks;
                                    loadFeedbackLikesIDS();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.i("ab_do" , "onError " + e.getMessage());
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                }
                            })
            );
        }
        else {
            disposable.add(
                    apiService
                            .getFeedbacksAsc(cartoon_id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<Feedback>>() {
                                @Override
                                public void onSuccess(List<Feedback> feedbacks) {
                                    loaded_feedbacks.clear();
                                    loaded_feedbacks = feedbacks;
                                    loadFeedbackLikesIDS();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                }
                            })
            );
        }

    }

    private void loadFeedbackLikesIDS() {
        disposable.add(
                apiService
                        .getFeedbacksLikesIds(user_id, cartoon_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> ids) {
                                feedbackLikesIDs = ids;
                                loadFeedbackDisLikesIDS();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadFeedbackDisLikesIDS() {
        disposable.add(
                apiService
                        .getFeedbacksDisLikesIds(user_id, cartoon_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> ids) {
                                feedbackDisLikesIDs = ids;
                                binding.progressBarLayout.setVisibility(View.GONE);
                                if (isRefresh) {
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    isRefresh = false;
                                }
                                updateAdapter();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void updateAdapter() {
        Log.i("ab_do" , "updateAdapter");
        feedbacksAdapter.setFeedbackLikesIDs(feedbackLikesIDs);
        feedbacksAdapter.setFeedbackDisLikesIDs(feedbackDisLikesIDs);
        feedbacksAdapter.submitList(loaded_feedbacks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.change_order) {
            desc = !desc ;
            binding.progressBarLayout.setVisibility(View.VISIBLE);
            loadFeedbacks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void sendReport(int user_id , int feedback_id, String description) {
        Log.i("ab_do" , "id = " + feedback_id + " " + "description = " + description);
        disposable.add(
                apiService
                        .makeFeedbackReport(user_id , feedback_id , description)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    Log.i("ab_do", "onSuccess make report");
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إرسال الإبلاغ بنجاح");

                                } else {
                                    Log.i("ab_do", "error when make report");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "error when make report");
                            }
                        })
        );
    }

    @Override
    public void onReportClicked(String description, int feedback_id , int user_id) {
         binding.progressBarLayout.setVisibility(View.VISIBLE);
         sendReport(user_id , feedback_id , description);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.feedback_menu , menu) ;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void show() {
        showSnackMsg("عفوا يرجي تسجيل الدخول أولا ");
    }

}