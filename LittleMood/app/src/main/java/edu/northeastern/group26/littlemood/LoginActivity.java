package edu.northeastern.group26.littlemood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText email = findViewById(R.id.Email);
        final EditText password = findViewById(R.id.Password);
        Button login = findViewById(R.id.LogInButton);
        Button goToSignUp = findViewById(R.id.GoSignup);
    }

    public void login(View view) {
        Toast.makeText(this, "Successfully log in", Toast.LENGTH_SHORT).show();
    }

    public void openSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}