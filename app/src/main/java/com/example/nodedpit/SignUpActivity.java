package com.example.nodedpit;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    EditText mName;
    EditText mLastName;
    EditText mEmail;
    EditText mPassword;
    CircleImageView profileImage;
    Button signup;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        signup = (Button) findViewById(R.id.button2);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = (EditText) findViewById(R.id.editTextTextPersonName3);
                mLastName = (EditText) findViewById(R.id.editTextTextPersonName2);
                mEmail = (EditText) findViewById(R.id.editTextTextPersonName);
                mPassword = (EditText) findViewById(R.id.editTextTextPersonName4);

                verifyCredentials(mEmail.getText().toString(),mPassword.getText().toString());

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void verifyCredentials(String email, String password) {

        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                String mUid = user.getUid();

                                try
                                {
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.putExtra("UID",mUid);
                                    startActivity(intent);
                                    finish();
                                }
                                catch(NullPointerException e){
                                    Toast.makeText(SignUpActivity.this, "Signup failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(SignUpActivity.this, "Invalid credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });
        }
        else{

            Toast.makeText(SignUpActivity.this, "Invalid Email",
                    Toast.LENGTH_SHORT).show();
        }
    }



}