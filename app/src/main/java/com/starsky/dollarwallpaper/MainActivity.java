package com.starsky.dollarwallpaper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.starsky.dollarwallpaper.About.AboutActivity;
import com.starsky.dollarwallpaper.Login_SignUp.LoginActivity;
import com.starsky.dollarwallpaper.MainPage.MainFragment;
import com.starsky.dollarwallpaper.Premium.PremiumFragment;
import com.starsky.dollarwallpaper.Profile.ProfileFragment;
import com.starsky.dollarwallpaper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;


    private LinearLayout nav_home, nav_premium, nav_profile;
    private View nav_home_indicator, nav_premium_indicator, nav_profile_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu));



        nav_home = findViewById(R.id.nav_home);
        nav_premium = findViewById(R.id.nav_premium);
        nav_profile = findViewById(R.id.nav_pro);

        nav_home_indicator = findViewById(R.id.nav_home_indicator);
        nav_premium_indicator = findViewById(R.id.nav_wishlist_indicator);
        nav_profile_indicator = findViewById(R.id.nav_pro_indicator);

        // Set default selected item and load initial fragment
        loadFragment(new MainFragment(), true);
        navigateToFragment(new MainFragment(), nav_home, nav_home_indicator);

        // Set onClick listeners for navigation items
        nav_home.setOnClickListener(view -> navigateToFragment(new MainFragment(), nav_home, nav_home_indicator));
        nav_premium.setOnClickListener(view -> navigateToFragment(new PremiumFragment(), nav_premium, nav_premium_indicator));
        nav_profile.setOnClickListener(view -> navigateToFragment(new ProfileFragment(), nav_profile, nav_profile_indicator));

        //permission
        if (checkPermission()) {
            // Permission already granted, proceed with your task
        } else {
            // Permission not granted, request it
            requestPermission();
        }


    }


    private void navigateToFragment(Fragment fragment, LinearLayout selectedNavItem, View selectedIndicator) {
        // Reset previously selected items
        resetNavigationSelection();

        // Show the indicator for the selected item
        selectedIndicator.setVisibility(View.VISIBLE);

        // Load the new fragment
        loadFragment(fragment, false);
    }

    private void resetNavigationSelection() {
        // Hide all indicators
        nav_home_indicator.setVisibility(View.GONE);
        nav_premium_indicator.setVisibility(View.GONE);
        nav_profile_indicator.setVisibility(View.GONE);
    }

    public void loadFragment(Fragment fragment, boolean flag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        @SuppressLint("CommitTransaction") FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (flag) {
            fragmentTransaction.add(R.id.fragment, fragment);
        } else {
            fragmentTransaction.replace(R.id.fragment, fragment);
        }
        fragmentTransaction.commit();
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        *//*if (id == R.id.aiItem) {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        }    else*//*
        if (id == R.id.privacy) {
            Toast.makeText(this, "Information", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.logOut) {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.aiItem) {
            Toast.makeText(this, "Ai Feature Coming Soon....", Toast.LENGTH_SHORT).show();
            return true;
        } else*/
        if (id == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (id == R.id.logOut) {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment);
        if (!(currentFragment instanceof MainFragment)) {
            navigateToFragment(new MainFragment(), nav_home, nav_home_indicator);
        } else {
            super.onBackPressed();
        }
    }


    //permission
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Check if permission is granted
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    // Request permission from user
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (readPermission && writePermission) {
                        // Permission granted, proceed with your task
                    } else {
                        // Permission denied, inform the user
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}