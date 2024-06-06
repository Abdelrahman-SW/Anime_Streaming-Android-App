package com.anyone.smardy.motaj.badtrew.Utilites;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.anyone.smardy.motaj.badtrew.R;

public class MalfunctionsDialog  {
    private final Dialog dialog ;
    private final Context context ;
    private EditText description ;
    MalfunctionsDialog.onReportClickListener onReportClickListener ;

    public MalfunctionsDialog (Context context) {
        dialog = new Dialog(context);
        this.context = context ;
        this.onReportClickListener = (MalfunctionsDialog.onReportClickListener) context;
        createDialog();
    }

    private void createDialog() {
        dialog.setContentView(R.layout.malfunctions_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        Button report = dialog.findViewById(R.id.report);
        ImageView close = dialog.findViewById(R.id.close);
        description = dialog.findViewById(R.id.description);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReportClickListener.onReportClicked(description.getText().toString() , MalfunctionsDialog.this);
            }
        });
    }


    public void showDialog () {
        if (dialog.isShowing()) {
            dismissDialog();
        }
        try {
            dialog.show();
            description.setTextDirection(View.TEXT_DIRECTION_RTL);
            description.requestFocus(View.FOCUS_RIGHT);
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

    public interface onReportClickListener {
        void onReportClicked(String description , MalfunctionsDialog malfunctionsDialog);
    }

}
