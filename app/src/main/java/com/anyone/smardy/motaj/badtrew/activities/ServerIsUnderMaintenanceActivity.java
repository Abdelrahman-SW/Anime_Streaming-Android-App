package com.anyone.smardy.motaj.badtrew.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.Utilities;
import com.anyone.smardy.motaj.badtrew.app.Config;

public class ServerIsUnderMaintenanceActivity extends AppCompatActivity {
    Button close ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        View decor_View = getWindow().getDecorView();
        Utilities.hideNavBar(decor_View);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_server_is_under_maintance);
        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}