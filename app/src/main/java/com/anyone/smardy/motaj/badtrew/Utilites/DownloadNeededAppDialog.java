package com.anyone.smardy.motaj.badtrew.Utilites;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.anyone.smardy.motaj.badtrew.R;

public class DownloadNeededAppDialog {
    private  String msg ;
    private final Dialog dialog ;
    Button store , website ;
    ClickListener clickListener;
    boolean isWatch;
    Activity context ;

    public DownloadNeededAppDialog(Activity context , boolean isWatch) {
        dialog = new Dialog(context);
        this.isWatch = isWatch ;
        this.context = context ;
        this.clickListener = (ClickListener) context;
        createDialog();
    }

    private void createDialog() {
        dialog.setContentView(R.layout.download_app_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        //dialog.setCancelable(false);
        //dialog.setCanceledOnTouchOutside(false);
        store = dialog.findViewById(R.id.store);
        website = dialog.findViewById(R.id.website);
        TextView msgTxtView = dialog.findViewById(R.id.msgTxt);
        if (isWatch) {
            msg = context.getString(R.string.download_video_app);
        }
        else msg = context.getString(R.string.download_downloader_app);
        msgTxtView.setText(msg);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                clickListener.onGoToStoreClicked(isWatch);
            }
        });
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                clickListener.onGoToWebsiteClicked(isWatch);
            }
        });
    }


    public void showDialog () {
        if (dialog.isShowing()) {
            dismissDialog();
        }
        try {
            dialog.show();
        } catch (Exception exception) {
            Log.i("ab_do", "dialog show exception " + exception.getMessage());
        }
    }



    public void dismissDialog () {
        try {
            dialog.dismiss();
        } catch (Exception exception) {
            Log.i("ab_do", "dialog dismiss exception " + exception.getMessage());
        }
    }
    public interface ClickListener {
        void onGoToStoreClicked(boolean isWatch);
        void onGoToWebsiteClicked(boolean isWatch);
    }

}

