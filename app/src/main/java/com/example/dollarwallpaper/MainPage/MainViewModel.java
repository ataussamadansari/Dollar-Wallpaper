package com.example.dollarwallpaper.MainPage;

import android.content.pm.ShortcutInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dollarwallpaper.Category.CategoryClass;
import com.example.dollarwallpaper.WallpaperPost.UploadModelClass;


import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private MutableLiveData<ArrayList<CategoryClass>> categories;
    private MutableLiveData<ArrayList<UploadModelClass>> uploadModelClasses;

    public LiveData<ArrayList<CategoryClass>> getCategories() {
        if (categories == null) {
            categories = new MutableLiveData<>();
            // Load categories here (e.g., from Firebase)
        }
        return categories;
    }

    public LiveData<ArrayList<UploadModelClass>> getUploadModelClasses() {
        if (uploadModelClasses == null) {
            uploadModelClasses = new MutableLiveData<>();
            // Load uploadModelClasses here (e.g., from Firebase)
        }
        return uploadModelClasses;
    }

    // Add methods to update categories and uploadModelClasses if needed
}

