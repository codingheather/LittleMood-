package edu.northeastern.group26.littlemood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        final EditText usernameEditText = findViewById(R.id.Email);
        final EditText passwordEditText = findViewById(R.id.Password);
        Button loginButton = findViewById(R.id.LogInBotton);
        Button goToSignUpButton = findViewById(R.id.GoSignup);
    }

    public void login(View view) {
    }

    public void openSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}