package com.example.eldercare.modules;

import android.content.Context;
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

    }

    public static String getLanguage(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.locale.getLanguage();
    }

}
