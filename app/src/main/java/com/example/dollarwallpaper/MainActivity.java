package com.example.dollarwallpaper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dollarwallpaper.Login_SignUp.LoginActivity;
import com.example.dollarwallpaper.MainPage.MainFragment;
import com.example.dollarwallpaper.Premium.PremiumFragment;
import com.example.dollarwallpaper.Profile.ProfileFragment;
import com.example.dollarwallpaper.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment, mainFragment)
                .commit();


        binding.homeBtn.setBackgroundResource(R.drawable.select_cir);
        binding.homeBtn.setImageResource(R.drawable.home_dark);
        binding.proBtn.setBackground(null);
        //binding.saveBtn.setBackground(null);

        binding.homeBtn.setOnClickListener(v -> {
            binding.toolbar.setTitle(R.string.app_name);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, mainFragment)
                    .commit();

            binding.homeBtn.setBackgroundResource(R.drawable.select_cir);
            binding.homeBtn.setImageResource(R.drawable.home_dark);
            binding.proBtn.setImageResource(R.drawable.profile);
            binding.premiumBtn.setImageResource(R.drawable.ic_premium);
            binding.proBtn.setBackground(null);
            binding.premiumBtn.setBackground(null);
        });

        binding.proBtn.setOnClickListener(v -> {
            binding.toolbar.setTitle("Profile");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, profileFragment)
                    .commit();
            binding.proBtn.setBackgroundResource(R.drawable.select_cir);
            binding.proBtn.setImageResource(R.drawable.profile_dark);
            binding.homeBtn.setImageResource(R.drawable.home);
            binding.premiumBtn.setImageResource(R.drawable.ic_premium);
            binding.homeBtn.setBackground(null);
            binding.premiumBtn.setBackground(null);
        });

        binding.premiumBtn.setOnClickListener(v -> {
            binding.toolbar.setTitle("Premium");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, new PremiumFragment()) // You might need to create this fragment
                    .commit();

            // Update button backgrounds
            binding.premiumBtn.setBackgroundResource(R.drawable.select_cir);
            binding.premiumBtn.setImageResource(R.drawable.ic_premium);
            binding.proBtn.setImageResource(R.drawable.profile);
            binding.homeBtn.setImageResource(R.drawable.home);
            binding.homeBtn.setBackground(null);
            binding.proBtn.setBackground(null);
        });


        //permission
        if (checkPermission()) {
            // Permission already granted, proceed with your task
        } else {
            // Permission not granted, request it
            requestPermission();
        }


    }

    MainFragment mainFragment = new MainFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    PremiumFragment premiumFragment = new PremiumFragment();


    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.aiItem) {
            Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
            return true;
        }    else*/
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
    }

    //back
    @Override
    public void onBackPressed() {
        // Check if the profile fragment is currently displayed
        if (profileFragment.isVisible()) {
            // Replace the profile fragment with the main fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, mainFragment)
                    .commit();

            // Update UI and toolbar title
            binding.proBtn.setBackground(null);
            binding.homeBtn.setBackgroundResource(R.drawable.select_cir);
            binding.proBtn.setImageResource(R.drawable.profile);
            binding.homeBtn.setImageResource(R.drawable.home_dark);
            binding.toolbar.setTitle(R.string.app_name);
        } else if (premiumFragment.isVisible()) {
            // Replace the profile fragment with the main fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, mainFragment)
                    .commit();

            // Update UI and toolbar title
            binding.proBtn.setBackground(null);
            binding.homeBtn.setBackgroundResource(R.drawable.select_cir);
            binding.proBtn.setImageResource(R.drawable.profile);
            binding.homeBtn.setImageResource(R.drawable.home_dark);
            binding.toolbar.setTitle(R.string.app_name);
        } else {
            // If profile fragment is not displayed, proceed with default back press action
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