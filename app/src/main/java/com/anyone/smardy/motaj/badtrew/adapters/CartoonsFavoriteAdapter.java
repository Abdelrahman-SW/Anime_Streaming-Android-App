package com.anyone.smardy.motaj.badtrew.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.activities.PlayListsActivity;
import com.anyone.smardy.motaj.badtrew.databinding.LayoutRecyclercartoonItemBinding;
import com.anyone.smardy.motaj.badtrew.model.Cartoon;

import java.util.List;

public class CartoonsFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = CartoonsFavoriteAdapter.class.getSimpleName();
    private Context mContext;
    private List<Cartoon> cartoonList;

    public CartoonsFavoriteAdapter(Context mContext, List<Cartoon> cartoonList) {
        this.mContext = mContext;
        this.cartoonList = cartoonList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecyclercartoonItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_recyclercartoon_item, parent, false);

        return new CartoonHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        CartoonHolder cartoonHolder = (CartoonHolder) holder;
        final Cartoon cartoon = cartoonList.get(position);
        //cartoonHolder.mBinding.setCartoon(cartoon);

        cartoonHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayListsActivity.class);
                intent.putExtra("cartoon", cartoon);
                mContext.startActivity(intent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return cartoonList.size();
    }


    public class CartoonHolder extends RecyclerView.ViewHolder{

        LayoutRecyclercartoonItemBinding mBinding;

        public CartoonHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
