package com.anyone.smardy.motaj.badtrew.Utilites;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.anyone.smardy.motaj.badtrew.R;

public class ReportDialog {

    private final Dialog dialog ;
    private final Context context ;
    onReportClickListener onReportClickListener ;
    private int feedback_id , user_id ;

    public ReportDialog(Context context) {
        dialog = new Dialog(context);
        this.context = context ;
        this.onReportClickListener = (ReportDialog.onReportClickListener) context;
        createDialog();
    }

    private void createDialog() {
        dialog.setContentView(R.layout.report_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
        Button report = dialog.findViewById(R.id.report);
        ImageView close = dialog.findViewById(R.id.close);
        EditText description = dialog.findViewById(R.id.description);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReportClickListener.onReportClicked(description.getText().toString() , feedback_id , user_id);
                dismissDialog();
            }
        });
    }

    public void setFeedback_id(int feedback_id) {
        this.feedback_id = feedback_id;
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
    public interface onReportClickListener {
        void onReportClicked(String description, int feedback_id , int user_id);
    }

    public void setUser_id(int user_id) {
        String s = "Gg";

        this.user_id = user_id;
    }
}
