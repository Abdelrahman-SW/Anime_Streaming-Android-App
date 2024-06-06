package com.anyone.smardy.motaj.badtrew.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.databinding.LeadrboardItemviewBinding;
import com.anyone.smardy.motaj.badtrew.model.User;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardHolder> {
    List<User> users = new ArrayList<>();
    Context context;

    public LeaderboardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LeaderboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LeadrboardItemviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.leadrboard_itemview, parent, false);
        return new LeaderboardHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void submitList(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }


    public class LeaderboardHolder extends RecyclerView.ViewHolder {
        LeadrboardItemviewBinding binding;

        public LeaderboardHolder(@NonNull LeadrboardItemviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
           User user = users.get(pos);
           binding.usernameTxtView.setText(user.getName());
            Glide.with(context)
                    .load(user.getPhoto_url())
                    .error(R.drawable.user_profile)
                    .placeholder(R.drawable.user_profile)
                    .into(binding.userImgView);
            String des = "عدد الحلقات التي تم مشاهدتها : " + user.getWatched_episodes();
            binding.watchedEpisodesTxtView.setText(des);
            String medal = "#" + (pos+1);
            binding.medalTextView.setText(medal);
            if (pos==0) {
                binding.medalImgView.setImageResource(R.drawable.gold_medal);
            }
            else if (pos==1) {
                binding.medalImgView.setImageResource(R.drawable.silver_medal);
            }
            else if (pos==2) {
                binding.medalImgView.setImageResource(R.drawable.bronze_medal);
            }
            if (pos<=2) {
                binding.medalImgView.setVisibility(View.VISIBLE);
                binding.medalTextView.setVisibility(View.GONE);
            }
            else {
                binding.medalTextView.setVisibility(View.VISIBLE);
                binding.medalImgView.setVisibility(View.GONE);
            }
        }

    }
}
