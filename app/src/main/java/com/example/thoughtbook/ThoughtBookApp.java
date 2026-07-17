package com.example.thoughtbook;

import android.app.Application;

public class ThoughtBookApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManager.applySavedTheme(this);
    }
}