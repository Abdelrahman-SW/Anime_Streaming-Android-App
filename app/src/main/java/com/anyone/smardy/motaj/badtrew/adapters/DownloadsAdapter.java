package com.anyone.smardy.motaj.badtrew.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.app.Config;
import com.anyone.smardy.motaj.badtrew.databinding.DownloadedEpisodeItemviewBinding;
import com.anyone.smardy.motaj.badtrew.model.Episode;
import com.anyone.smardy.motaj.badtrew.network.ApiClient;
import com.anyone.smardy.motaj.badtrew.network.ApiService;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadHolder> {

    private final String TAG = DownloadsAdapter.class.getSimpleName();
    private Activity mContext;
    private List<Episode> downloadList;
    OnRemoveDownloadedEpisode onRemoveDownloadedEpisode;

    public DownloadsAdapter(Activity mContext, List<Episode> downloadList) {
        this.mContext = mContext;
        this.downloadList = downloadList;
        this.onRemoveDownloadedEpisode = (OnRemoveDownloadedEpisode) mContext;
    }

    @NonNull
    @Override
    public DownloadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadedEpisodeItemviewBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.downloaded_episode_itemview, parent, false);

        return new DownloadHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadHolder holder, final int position) {
        Episode episode = downloadList.get(position);

        holder.mBinding.cartoonTitle.setText(episode.getCartoon().getTitle());
        String episode_title = episode.getTitle() + "   " + episode.getPlaylist().getTitle();
        holder.mBinding.episodeName.setText(episode_title);
        String imgUrl;
        if (episode.getThumb() == null || episode.getThumb().isEmpty()) {
            imgUrl = episode.getPlaylist().getThumb();
        } else imgUrl = episode.getThumb();
        Glide.with(mContext).load(imgUrl).centerCrop().into(holder.mBinding.cartoonImg);
        holder.mBinding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.video_player_package_name.isEmpty()) {
                    // load package name of video App First
                    getVideoAppPackage(episode);
                }
                else {
                    Config.openExoPlayerApp(mContext, episode.getVideo_url(), episode, null);
                }
            }
        });
//
        holder.mBinding.delete.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);

            builder.setMessage("هل تريد حذف الحلقة من التحميلات ؟");
            builder.setCancelable(true);
            builder.setPositiveButton("نعم", (dialog, which) -> {
                File file = new File(episode.getVideo_url());
                try {
                    boolean deleted = file.delete();
                } catch (Exception exception) {
                    Log.i("ab_do", "error when delete file " + exception.getMessage());
                }
                // add api call to remove episode from server
                onRemoveDownloadedEpisode.onRemove(position);
            });

            builder.setNegativeButton("لا", (dialog, which) -> dialog.cancel());

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }


    private void getVideoAppPackage(Episode episode) {
        Log.i("new_abdo" , "getVideoAppPackage" );
        Config.ShowDialog(mContext);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(mContext.getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .getVideoAppPackageName()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String package_name) {
                                Log.i("new_abdo" , package_name);
                                String[] lists = package_name.split("\\|");
                                Config.video_player_package_name = lists[0] ;
                                Config.video_player_website = lists[1] ;
                                if(Constants.SKIP_INSTALL_APPS.equals(Config.video_player_package_name)) {
                                    Config.openExoPlayerApp(mContext, episode.getVideo_url(), episode, null);
                                    return;
                                }
                                if (Config.isPackageInstalled(Config.video_player_package_name, mContext.getPackageManager())) {
                                    Config.openExoPlayerApp(mContext, episode.getVideo_url(), episode, null);
                                }
                                else {
                                    Config.dismissDialog(mContext);
                                    Config.installExoPlayerDialog(mContext);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Config.dismissDialog(mContext);
                                Toast.makeText(mContext.getApplicationContext(), "حدث خطأ ما يرجي إعادة المحاولة", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    public  class DownloadHolder extends RecyclerView.ViewHolder {

        DownloadedEpisodeItemviewBinding mBinding;

        public DownloadHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            int theme_id = sharedPreferences.getInt(mContext.getString(R.string.THEME_KEY) , mContext.getResources().getInteger(R.integer.default_theme));
            if (theme_id == mContext.getResources().getInteger(R.integer.black_theme)) {
                mBinding.play.setImageResource(R.drawable.play_white);
            }
        }
    }

    public interface OnRemoveDownloadedEpisode {
        void onRemove(int pos);
    }
}
