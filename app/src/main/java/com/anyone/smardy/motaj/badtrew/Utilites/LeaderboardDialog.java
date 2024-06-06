package com.anyone.smardy.motaj.badtrew.Utilites;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.anyone.smardy.motaj.badtrew.R;

public class LeaderboardDialog {

    private final Dialog dialog;
    private final Context context;

    public LeaderboardDialog(Context context) {
        dialog = new Dialog(context);
        this.context = context;
        createDialog();
    }

    private void createDialog() {
        dialog.setContentView(R.layout.leadrboard_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        ImageView close = dialog.findViewById(R.id.close_Btn);
        Button ok = dialog.findViewById(R.id.ok_Btn);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        try {
            dialog.dismiss();
        } catch (Exception exception) {
            Log.i("ab_do", "dialog login exception " + exception.getMessage());
        }
    }
}
