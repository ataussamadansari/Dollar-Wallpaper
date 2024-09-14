package com.starsky.dollarwallpaper.Login_SignUp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.starsky.dollarwallpaper.MainActivity;
import com.starsky.dollarwallpaper.R;
import com.starsky.dollarwallpaper.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseFirestore database;
    boolean isPasswordVisible = false;

    EditText Email;
    Button fBtn;
    ImageView cancel;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");


        //forget dialog
        Dialog forget = new Dialog(this);
        Objects.requireNonNull(forget.getWindow()).setContentView(R.layout.forget_pass_dialog);
        Objects.requireNonNull(forget.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        forget.setCancelable(false);

        Email = forget.findViewById(R.id.userEmail);
        cancel = forget.findViewById(R.id.cBtn);
        fBtn = forget.findViewById(R.id.fBtn);

        binding.forgetBtn.setOnClickListener(v -> {
            forget.show();
        });

        fBtn.setOnClickListener(v -> {
            String email = Email.getText().toString().trim();
            if (!email.isEmpty()) {
                dialog.setMessage("Sending reset email...");
                dialog.show();

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                            forget.dismiss();
                        });
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v -> {
            forget.dismiss();
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

        binding.loginBtn.setOnClickListener(view -> {
            String email, password;
            email = binding.userName.getText().toString();
            password = binding.password.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill Email & Password", Toast.LENGTH_SHORT).show();
            } else {
                dialog.show();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        //login to signUp
        binding.signBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
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