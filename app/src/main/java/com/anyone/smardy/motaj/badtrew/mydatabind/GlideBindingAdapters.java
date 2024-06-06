package com.anyone.smardy.motaj.badtrew.mydatabind;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.anyone.smardy.motaj.badtrew.R;
import com.bumptech.glide.Glide;


public class GlideBindingAdapters {

    @BindingAdapter("imgUrl")
    public static void setImage(ImageView imageView, String imgUrl){
        Context context = imageView.getContext();
        if(imgUrl != null && !imgUrl.equals("")){
            Glide.with(context)
                    .load(imgUrl)
//                    .placeholder(R.drawable.prog)
//                    .error(R.drawable.prog)
                    .centerCrop()
                    .into(imageView);
        }
        else{
            Glide.with(context)
                    .asGif()
                    .load(R.raw.loading_1)
                    .centerCrop()
                    .into(imageView);
        }

    }
}
