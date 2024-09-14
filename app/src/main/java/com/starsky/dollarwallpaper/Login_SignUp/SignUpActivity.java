package com.starsky.dollarwallpaper.Login_SignUp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.starsky.dollarwallpaper.MainActivity;
import com.starsky.dollarwallpaper.R;
import com.starsky.dollarwallpaper.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseFirestore database;
    boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage("We're creating a new account...");
        dialog.setCancelable(false);

        binding.signUpBtn.setOnClickListener(view -> {
            String name, email, phone, password;

            email = binding.email.getText().toString();
            password = binding.password.getText().toString();
            phone = binding.phone.getText().toString();
            name = binding.name.getText().toString();

            if (email.isEmpty() || password.isEmpty() || phone.isEmpty() || name.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else {
                UserModelClass user = new UserModelClass(name, email, phone, password, false);
                dialog.show();

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            database.collection("users")
                                    .document(uid)
                                    .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            Toast.makeText(SignUpActivity.this, "Successfully SignUp", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        //show and hide
        binding.password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);

        binding.password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.password.getRight() - binding.password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        binding.logBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);
        } else {
            binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
        }
        isPasswordVisible = !isPasswordVisible;
    }

}