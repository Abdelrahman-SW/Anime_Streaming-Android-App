package com.anyone.smardy.motaj.badtrew.Utilites;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.model.Redirect;

public class MessageDialog {
    private final String msg ;
    private final Dialog dialog ;
    private final Context context ;
    Button go , close ;
    MessageDialog.onMsgBtnClickListener onMsgBtnClickListener ;
    Redirect redirect ;

    public MessageDialog(Context context , Redirect redirect) {
        dialog = new Dialog(context);
        this.msg = redirect.getMessage() ;
        this.redirect = redirect ;
        this.context = context ;
        this.onMsgBtnClickListener = (MessageDialog.onMsgBtnClickListener) context;
        createDialog();
    }

    private void createDialog() {
        dialog.setContentView(R.layout.message_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        go = dialog.findViewById(R.id.go);
        close = dialog.findViewById(R.id.close);
        TextView msgTxtView = dialog.findViewById(R.id.msgTxt);
        msgTxtView.setText(msg);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMsgBtnClickListener.onReportClicked(redirect);
                dismissDialog();
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
            Log.i("ab_do", "dialog login exception " + exception.getMessage());
        }
    }
    public interface onMsgBtnClickListener {
        void onReportClicked(Redirect redirect);
    }


}
