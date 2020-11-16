package com.di7ak.openspaces.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatDelegate;

import com.di7ak.openspaces.R;

public class ThemeColors {

    private static final String NAME = "ThemeColors", KEY = "color", NIGHT = "night";

    @ColorInt
    public int color;

    public ThemeColors(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        boolean enableNight = sharedPreferences.getBoolean(NIGHT, false);
        int mode = enableNight ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);

        String stringColor = sharedPreferences.getString(KEY, null);
        if(stringColor != null) {
            try {
                color = Color.parseColor(stringColor);

                if (isLightActionBar()) context.setTheme(R.style.AppTheme);

                stringColor = stringColor.replace("#", "");
                context.setTheme(context.getResources().getIdentifier("T_" + stringColor, "style", context.getPackageName()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void enableNightTheme(Activity activity, boolean enable) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(NIGHT, enable);
        editor.apply();

        activity.recreate();
    }

    public static void setNewThemeColor(Activity activity, String color) {
        int colorInt = Color.parseColor(color);
        int red = (colorInt & 0xFF0000) >> 16;
        int green = (colorInt & 0xFF00) >> 8;
        int blue = (colorInt & 0xFF);
        int colorStep = 15;
        red = red / colorStep * colorStep;
        green = green / colorStep * colorStep;
        blue = blue / colorStep * colorStep;

        String stringColor = Integer.toHexString(Color.rgb(red, green, blue)).substring(2);
        SharedPreferences.Editor editor = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY, "#" + stringColor);
        editor.apply();

        activity.recreate();
    }

    private boolean isLightActionBar() {
        int rgb = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
        return rgb > 210;
    }
}
