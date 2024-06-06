package com.anyone.smardy.motaj.badtrew.adapters;

import static com.anyone.smardy.motaj.badtrew.activities.MainActivity.searchCase;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.anyone.smardy.motaj.badtrew.Constants.Constants;
import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.activities.InformationActivity;
import com.anyone.smardy.motaj.badtrew.activities.MainActivity;
import com.anyone.smardy.motaj.badtrew.databinding.LayoutNativeAdBinding;
import com.anyone.smardy.motaj.badtrew.databinding.LayoutRecyclercartoonItemBinding;
import com.anyone.smardy.motaj.badtrew.databinding.LayoutRecyclercartoonItemListBinding;
import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;

import java.util.ArrayList;
import java.util.List;

public class CartoonsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = CartoonsAdapter.class.getSimpleName();
    private Activity mContext;



    private List<CartoonWithInfo> cartoonList;

    private final int albumView = 1;

    private boolean isGrid ;
    private boolean isEpisodesDates ;

    public CartoonsAdapter(Activity mContext, boolean isGrid , boolean isEpisodesDates) {
        this.mContext = mContext;
        this.isGrid = isGrid ;
        this.isEpisodesDates = isEpisodesDates ;
        cartoonList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewDataBinding binding ;
        if (isGrid) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclercartoon_item, parent, false);
            return new CartoonHolder((LayoutRecyclercartoonItemBinding) binding);
        }
        else {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclercartoon_item_list, parent, false);
            return new CartoonHolder((LayoutRecyclercartoonItemListBinding) binding);
        }

    }

    public void updateList(List<CartoonWithInfo> cartoonList) {
        this.cartoonList.clear();
        this.cartoonList.addAll(cartoonList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (!searchCase)
            holder.itemView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_itemview));
        CartoonHolder cartoonHolder = (CartoonHolder) holder;
        final CartoonWithInfo cartoon = cartoonList.get(position);
        if (isGrid) {
            cartoonHolder.gridBinding.setCartoon(cartoon);
            if (isEpisodesDates) {
                cartoonHolder.gridBinding.cartoonTitle.setText(cartoon.getEpisodeDateTitle());
            } else {
                cartoonHolder.gridBinding.cartoonTitle.setText(cartoon.getTitle());
            }
            switch (cartoon.getStatus()) {
                case 1:
                    cartoonHolder.gridBinding.statues.setText("مكتمل");
                    break;

                case 2:
                    cartoonHolder.gridBinding.statues.setText("مستمر");
                    break;
                default:
                    cartoonHolder.gridBinding.statues.setText("غير محدد");
            }
        }
        else {
            cartoonHolder.listBinding.setCartoon(cartoon);
            cartoonHolder.listBinding.date.setText(cartoon.getView_date());
            cartoonHolder.listBinding.category.setText(cartoon.getCategory());
            cartoonHolder.listBinding.ageRate.setText(getAgeString(Integer.parseInt(cartoon.getAge_rate())));
            if(cartoon.getType() == Constants.IS_FILM) {
                cartoonHolder.listBinding.type.setText("فيلم");
            }
            else {
                cartoonHolder.listBinding.type.setText("مسلسل");
            }
            if (isEpisodesDates) {
                cartoonHolder.listBinding.cartoonTitle.setText(cartoon.getEpisodeDateTitle());
            } else {
                cartoonHolder.listBinding.cartoonTitle.setText(cartoon.getTitle());
            }
            switch (cartoon.getStatus()) {
                case 1:
                    cartoonHolder.listBinding.statues.setText("مكتمل");
                    break;

                case 2:
                    cartoonHolder.listBinding.statues.setText("مستمر");
                    break;
                default:
                    cartoonHolder.listBinding.statues.setText("غير محدد");
            }
        }
        cartoonHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, InformationActivity.class);
                intent.putExtra("cartoon", cartoon);
                mContext.startActivity(intent);
            }
        });

        if (position == cartoonList.size() - 1 && !searchCase) {
            if (!isEpisodesDates)
                ((MainActivity) mContext).getNewCartoons();
        }
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

    public void clearData() {
        cartoonList.clear();
    }


    @Override
    public int getItemCount() {
        return cartoonList.size();
    }



    public class CartoonHolder extends RecyclerView.ViewHolder{

        LayoutRecyclercartoonItemBinding gridBinding;
        LayoutRecyclercartoonItemListBinding listBinding ;

        public CartoonHolder(LayoutRecyclercartoonItemBinding binding) {
            super(binding.getRoot());
            gridBinding = binding ;
//            CartoonWithInfo cartoon = cartoonListFiltered.get(getBindingAdapterPosition());
//            gridBinding.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, InformationActivity.class);
//                    intent.putExtra("cartoon", cartoon);
//                    mContext.startActivity(intent);
//                }
//            });
        }

        public CartoonHolder(LayoutRecyclercartoonItemListBinding binding) {
            super(binding.getRoot());
            listBinding = binding ;
//            CartoonWithInfo cartoon = cartoonListFiltered.get(getBindingAdapterPosition());
//            listBinding.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, InformationActivity.class);
//                    intent.putExtra("cartoon", cartoon);
//                    mContext.startActivity(intent);
//                }
//            });
        }
    }

}
