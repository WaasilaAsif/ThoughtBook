package com.example.thoughtbook;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    public static void toggleDarkMode(boolean enableDark) {
        AppCompatDelegate.setDefaultNightMode(
                enableDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
