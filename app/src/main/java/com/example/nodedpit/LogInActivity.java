package com.example.nodedpit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    EditText email;
    EditText password;

    Button logIn;

    private FirebaseAuth mAuth;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();
        logIn = (Button) findViewById(R.id.login);

        email = (EditText) findViewById(R.id.emailAdress);
        password = (EditText) findViewById(R.id.password);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String inputEmail = email.getText().toString();
                    String inputPassword = password.getText().toString();
                    signInAuth(inputEmail, inputPassword);
            }
    });
}


private void signInAuth (String inputEmail, String inputPassword){
    mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        String mUid = user.getUid();

                        try
                        {
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            intent.putExtra("UID",mUid);
                            startActivity(intent);
                            finish();
                        }
                        catch(NullPointerException e){
                            Toast.makeText(LogInActivity.this, "Log In failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // If sign in fails, display a message to the user.

                        Toast.makeText(LogInActivity.this, "Invalid credentials",
                                Toast.LENGTH_SHORT).show();
                    }
            }
    });
}}



