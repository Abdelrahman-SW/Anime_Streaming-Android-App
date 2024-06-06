package com.anyone.smardy.motaj.badtrew.adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.Database.SQLiteDatabaseManager;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.activities.InformationActivity;
import com.anyone.smardy.motaj.badtrew.databinding.LayoutLatestEpisodeItemBinding;
import com.anyone.smardy.motaj.badtrew.databinding.LayoutLatestEpisodeItemListBinding;
import com.anyone.smardy.motaj.badtrew.model.EpisodeWithInfo;

import java.util.List;

public class LatestEpisodesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = LatestEpisodesAdapter.class.getSimpleName();
    private Activity mContext;
    private List<EpisodeWithInfo> episodeList;
    private SQLiteDatabaseManager sqLiteDatabaseManager;

    boolean isGrid ;

    public LatestEpisodesAdapter(Activity mContext, List<EpisodeWithInfo> episodeList ,boolean isGrid) {
        this.mContext = mContext;
        this.episodeList = episodeList;
        this.isGrid = isGrid ;
        sqLiteDatabaseManager = new SQLiteDatabaseManager(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding;

        if (isGrid) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_latest_episode_item, parent, false);
            return new EpisodeHolder((LayoutLatestEpisodeItemBinding) binding);
        }
        else {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_latest_episode_item_list, parent, false);
            return new EpisodeHolder((LayoutLatestEpisodeItemListBinding) binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            final EpisodeHolder episodeHolder = (EpisodeHolder) holder;
            final EpisodeWithInfo episode = episodeList.get(position);
            if (episode==null) return;
            holder.itemView.setAnimation(AnimationUtils.loadAnimation(mContext , R.anim.anim_itemview));
            if (isGrid) {
                LayoutLatestEpisodeItemBinding gridBinding = ((EpisodeHolder) holder).gridBinding ;
                if (episode.getThumb().isEmpty())
                    gridBinding.setThumb(episode.getCartoon().getThumb());
                else
                    gridBinding.setThumb(episode.getThumb());

                gridBinding.setCartoon(episode.getCartoon().getTitle());

                if (!TextUtils.isEmpty(episode.getTitle())) {
                   gridBinding.setTitle(episode.getTitle());
                } else {
                    gridBinding.setTitle("الحلقة : " + (position + 1));
                }

                if (sqLiteDatabaseManager.isEpisodeSeen(episode.getId())) {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }

        /*episodeHolder.itemView.setOnClickListener(v -> ((MainActivity)mContext).startVideoActivity(position, episode, episodeHolder.mBinding.getTitle(),
                episode.getThumb(), "", ""));*/

                   episodeHolder.gridBinding.rate.setText(String.valueOf(episode.getWorld_rate()));
                switch (episode.getStatus()) {
                    case 1:
                        episodeHolder.gridBinding.statues.setText("مكتمل");
                        break;

                    case 2:
                        episodeHolder.gridBinding.statues.setText("مستمر");
                        break;
                    default:
                        episodeHolder.gridBinding.statues.setText("غير محدد");
                }
            }
            else {
                LayoutLatestEpisodeItemListBinding listBinding = ((EpisodeHolder) holder).listBinding ;
                if (episode.getThumb().isEmpty())
                    listBinding.setThumb(episode.getCartoon().getThumb());
                else
                    listBinding.setThumb(episode.getThumb());

                listBinding.setCartoon(episode.getCartoon().getTitle());

                listBinding.ageRate.setText(getAgeString(Integer.parseInt(episode.getCartoon().getAge_rate())));
                if (episode.getCartoon().getType() == Constants.IS_FILM) {
                    listBinding.type.setText("فيلم");
                    listBinding.title.setVisibility(View.GONE);
                }
                  else {
                    listBinding.type.setText("مسلسل");
                    listBinding.title.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(episode.getTitle())) {
                        listBinding.setTitle(episode.getTitle());
                    } else {
                        listBinding.setTitle("الحلقة " + (position + 1));
                    }
                }

                listBinding.date.setText(episode.getCartoon().getView_date());


                if (sqLiteDatabaseManager.isEpisodeSeen(episode.getId())) {
                   listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }

                listBinding.rate.setText(String.valueOf(episode.getWorld_rate()));
                switch (episode.getStatus()) {
                    case 1:
                        listBinding.statues.setText("مكتمل");
                        break;

                    case 2:
                        listBinding.statues.setText("مستمر");
                        break;
                    default:
                        listBinding.statues.setText("غير محدد");
                }
            }
            episodeHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, InformationActivity.class);
                intent.putExtra("cartoon", episode.getCartoon());
//                intent.putExtra("playlist", episode.getPlaylist());
                mContext.startActivity(intent);
            });
        }

    private String getAgeString (int _age) {
        String age ;
        switch (_age) {
            case 1:
                age = "لكل الاعمار" ;
                break;

            case 2:
                age = "+13" ;
                break;

            case 3:
                age = "+17" ;
                break;
            default:
                age = "غير محدد" ;
        }
        return age ;
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    private static class EpisodeHolder extends RecyclerView.ViewHolder{

        LayoutLatestEpisodeItemBinding gridBinding;
        LayoutLatestEpisodeItemListBinding listBinding ;

        public EpisodeHolder(LayoutLatestEpisodeItemBinding Binding) {
            super(Binding.getRoot());

            gridBinding = Binding ;
        }

        public EpisodeHolder(LayoutLatestEpisodeItemListBinding Binding) {
            super(Binding.getRoot());

            listBinding = Binding;
        }
    }

}
