package com.anyone.smardy.motaj.badtrew.Utilites;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.anyone.smardy.motaj.badtrew.R;

public class dialogUtilities {

    private ProgressDialog progressDialog ;

    public void ShowDialog(Context context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        try {
            progressDialog.show();
        }catch (Exception exception) {
            Log.d("ab_do" , exception.getMessage());
            return;
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void dismissDialog() {
        try {
            progressDialog.dismiss();
        }catch (Exception exception) {
            Log.d("ab_do" , exception.getMessage());
        }
    }


}
