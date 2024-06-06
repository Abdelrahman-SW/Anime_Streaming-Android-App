package com.anyone.smardy.motaj.badtrew.Utilites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.activities.LoginActivity;

public class LoginDialog {

    private final Dialog dialog ;
    private final Context context ;

    public LoginDialog(Context context) {
        dialog = new Dialog(context);
        this.context = context ;
        createDialog();
    }

    private void createDialog() {
        dialog.setContentView(R.layout.login_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        ImageView close = dialog.findViewById(R.id.close);
        Button login = dialog.findViewById(R.id.login);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context , LoginActivity.class));
            }
        });
    }

    public void showDialog () {
        dialog.show();
    }

    public void dismissDialog () {
        try {
            dialog.dismiss();
        } catch (Exception exception) {
            Log.i("ab_do", "dialog login exception " + exception.getMessage());
        }
    }
}
