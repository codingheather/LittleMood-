package edu.northeastern.group26.littlemood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private LinearLayout waitLogin;
    private ConstraintLayout login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        waitLogin = findViewById(R.id.loading_login_progressBar);
        login = findViewById(R.id.login);
    }

    public void login(View view) {
        String email = ((EditText) findViewById(R.id.Email)).getText().toString();
        String password = ((EditText) findViewById(R.id.Password)).getText().toString();

        login.setVisibility(View.INVISIBLE);
        waitLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LoginActivity.class.getName(), "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Successfully log in", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();


                            waitLogin.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LoginActivity.class.getName(), "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Log in failed.",
                                    Toast.LENGTH_SHORT).show();
                            waitLogin.setVisibility(View.GONE);
                        }
                    }
                });


    }

    public void openSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

    }
}