package com.example.eldercare.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;


import java.util.Locale;

public class LanguageManager {

    public static void setLanguage(Context context, String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        context.createConfigurationContext(configuration);
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        SharedPreferences preferences = context.getSharedPreferences("f_language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("f_language", languageCode); // Save the language code
        editor.apply();

    }
    public static void setLnaguageFromSavedprefs(Context context){
        SharedPreferences preferences = context.getSharedPreferences("f_language", Context.MODE_PRIVATE);

        String languageCode = preferences.getString("f_language", "");
        setLanguage(context, languageCode);
    }

    public static String getLanguage(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.locale.getLanguage();
    }

    public static String getLanguageFromsharedprefs(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("f_language", Context.MODE_PRIVATE);

        String languageCode = preferences.getString("f_language", "");
        return languageCode;
    }
}
