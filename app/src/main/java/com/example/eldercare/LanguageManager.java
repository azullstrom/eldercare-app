package com.example.eldercare;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;

import java.util.Locale;

public class LanguageManager {

    public static void setLanguage(Context context, String languageCode) {

        Locale newLocale = new Locale(languageCode);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();

        configuration.setLocale(newLocale);
        LocaleList localeList = new LocaleList(newLocale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

    }

    public static String getLanguage(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.getLocales().toLanguageTags();
    }

}
