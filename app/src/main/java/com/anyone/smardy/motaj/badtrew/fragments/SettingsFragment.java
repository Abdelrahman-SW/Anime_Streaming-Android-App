package com.anyone.smardy.motaj.badtrew.fragments;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.Utilites.MalfunctionsDialog;
import com.anyone.smardy.motaj.badtrew.Utilites.SocialMediaDialog;
import com.anyone.smardy.motaj.badtrew.activities.MainActivity;
import com.anyone.smardy.motaj.badtrew.app.Config;

import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    ColorPicker colorPicker;
    AlertDialog dialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_screen, rootKey);
    }


    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        if (preference.getKey().equals(getString(R.string.social_media_key))) {
            showSocialMediaDialog();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.feedback))) {
            sendFeedback();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.MoreApps))) {
            goToWebsite();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.rate_key))) {
            rateApp();
            return true;
        }


        if (preference.getKey().equals(getString(R.string.ApperanceKey))) {
            showPickerColorDialog();
            return true;
        }

        if (preference.getKey().equals(getString(R.string.share_key))) {
            shareApp();
            return true;
        }
        if (preference.getKey().equals(getString(R.string.malfunctions_key))) {
            showMalfunctionsDialog();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    private void showMalfunctionsDialog() {
        MalfunctionsDialog dialog = new MalfunctionsDialog(getActivity());
        dialog.showDialog();
    }

    private void showSocialMediaDialog() {
        SocialMediaDialog dialog = new SocialMediaDialog(getActivity());
        dialog.showDialog();
    }

    private void goToWebsite() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_url))));
    }

    private void rateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())));
    }

    private void shareApp() {
        Config.shareApp(requireContext());
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.contact_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "My feedback for (Anime APP)");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "هذه الخدمة غير متوفرة في جهازك", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        return true;
    }

    private void showPickerColorDialog() {
//        if (colorPicker == null)
            CreatePickerDialog();
        if (colorPicker.getDialogViewLayout().getParent() != null) {
            ((ViewGroup) colorPicker.getDialogViewLayout().getParent()).removeView(colorPicker.getDialogViewLayout());
        }
        colorPicker.show();
    }

    private void CreatePickerDialog() {
        colorPicker = new ColorPicker(requireActivity());
        colorPicker.setColorButtonMargin(10 , 8 , 10 , 8);
        if (colorPicker.getDialogViewLayout()!=null) {
            colorPicker.getDialogViewLayout().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            colorPicker.getDialogViewLayout().getRootView().setPadding(32,64,32,64);
        }
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        setThemeColor(color);
                    }

                    @Override
                    public void onCancel() {
                    }
                })
                .setRoundColorButton(true)
                .setColumns(4)
                .setDefaultColorButton(getCurrentColor())
                .addListenerButton("تطبيق", getDefaultButtonStyle(Color.BLUE , false), new ColorPicker.OnButtonListener() {
                    @Override
                    public void onClick(View v, int position, int color) {
                        colorPicker.getPositiveButton().callOnClick();
                    }
                })
                .addListenerButton("إلغاء", getDefaultButtonStyle(Color.BLACK , false), new ColorPicker.OnButtonListener() {
                    @SuppressLint("SuspiciousIndentation")
                    @Override
                    public void onClick(View v, int position, int color) {
                        colorPicker.getNegativeButton().callOnClick();
                        if (getView()!=null)
                        ((ViewGroup) getView()).removeView(v);
                    }
                })
                .addListenerButton("الإفتراضي", getDefaultButtonStyle(Color.RED , true), new ColorPicker.OnButtonListener() {
                    @SuppressLint("SuspiciousIndentation")
                    @Override
                    public void onClick(View v, int position, int color) {
                        colorPicker.dismissDialog();
                        if (getView()!=null)
                        ((ViewGroup) getView()).removeView(v);
                        setDefaultColor();
                    }
                })
                .disableDefaultButtons(true)
                .setTitle("أختر المظهر الخاص بك")
                .setColors(R.array.PickerColorsDialog);
    }

    private void setDefaultColor() {
        // set the default theme of the app
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.edit().putInt(getString(R.string.THEME_KEY) , getResources().getInteger(R.integer.default_theme)).commit();
        sharedPreferences.edit().putInt(getString(R.string.CURRENT_COLOR_KEY) , getResources().getColor(R.color.white)).commit();
        finishSettingsActivity();
    }

    @NonNull
    private Button getDefaultButtonStyle(int Color , boolean default_btn) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        Button button = new Button(requireContext());
        button.setLayoutParams(params);
        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        button.setBackgroundResource(outValue.resourceId);
        button.setTextColor(Color);
        return button;
    }

    private int getCurrentColor() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getInt(getString(R.string.CURRENT_COLOR_KEY), getResources().getColor(R.color.white));
    }

    private void setThemeColor(int color) {
        // the user has choose his color so update the theme
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (color == getResources().getColor(R.color.black)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.black_theme)).apply();
        } else if (color == getResources().getColor(R.color.white)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.default_theme)).apply();
        } else if (color == getResources().getColor(R.color.theme_Deep_Purple)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Deep_Purple)).apply();
        } else if (color == getResources().getColor(R.color.theme_Purple)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Purple)).apply();
        }else if (color == getResources().getColor(R.color.theme_Purple)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Purple)).apply();
        }else if (color == getResources().getColor(R.color.theme_green)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_green)).apply();
        }else if (color == getResources().getColor(R.color.theme_red)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_red)).apply();
        }else if (color == getResources().getColor(R.color.theme_blue)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_blue)).apply();
        }else if (color == getResources().getColor(R.color.theme_Gray)) {
            sharedPreferences.edit().putInt(getString(R.string.THEME_KEY), getResources().getInteger(R.integer.theme_Gray)).apply();
        }
        sharedPreferences.edit().putInt(getString(R.string.CURRENT_COLOR_KEY), color).commit();
        finishSettingsActivity();
    }

    private void finishSettingsActivity() {
        if (getActivity()!=null) {
            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
            getActivity().finishAffinity();
            getActivity().startActivity(intent);
        }
        else requireActivity().finish();
    }

}

