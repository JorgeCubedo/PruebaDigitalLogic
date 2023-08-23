package com.cube.pruebadigitallogic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cube.pruebadigitallogic.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String REQ = "Required";
    ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        binding.btnSignUp.setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String email = binding.edtEmail.getText().toString().trim();
        String confirmEmail = binding.edtConfirmEmail.getText().toString().trim();
        String password = binding.edtPassword.getText().toString();
        String confirmPassword = binding.edtConfirmPassword.getText().toString();
        if (checkEmail(email, confirmEmail) && checkPassword(password, confirmPassword)) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null)
                    Log.d(TAG, user.getEmail());
                Intent intent = new Intent(RegisterActivity.this, MoviesActivity.class);
                startActivity(intent);
                Toast.makeText(this, "User Created!!", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Toast.makeText(this, "Error creating new user. Try again later.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            });
            Log.d(TAG, "Signed Up");
        }
    }

    private boolean checkEmail(String email, String confirmEmail) {

        if (email.isEmpty()) {
            binding.edtEmail.setError(REQ);
            return false;
        } else if(confirmEmail.isEmpty()) {
            binding.edtConfirmEmail.setError(REQ);
            return false;
        } else if (!confirmEmail.equals(email)){
            binding.edtEmail.setError("");
            binding.edtConfirmEmail.setError("");
            Toast.makeText(this, "Email should be the same in both fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean checkPassword(String password, String confirmPassword) {

        if (password.isEmpty()) {
            binding.edtPassword.setError(REQ);
            return false;
        } else if (confirmPassword.isEmpty()) {
            binding.edtConfirmPassword.setError(REQ);
            return false;
        } else if (!confirmPassword.equals(password)) {
            binding.edtPassword.setError("");
            binding.edtConfirmPassword.setError("");
            Toast.makeText(this, "Password should be the same in both fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}