package com.example.nodedpit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    public static final int PICK_IMAGE = 10101;
    private static final String TAG = "SignUpActivity";
    Bitmap mPicture = null;



    @SuppressLint("SetTextI18n")
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

                                final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);

                                    intent.putExtra("UID",mUid);
                                    handleUpload(mPicture, intent);

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(SignUpActivity.this, "Invalid credentials",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{

            Toast.makeText(SignUpActivity.this, "Invalid Email",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void handleImageClick(View view)
    {
        Log.d(TAG, "handleImageClick: start");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"pick an image"), PICK_IMAGE);

        Log.d(TAG, "handleImageClick: end");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult: start");
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE)
        {
            try {
                InputStream inputStream = getContentResolver()
                        .openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                profileImage = findViewById(R.id.profile_image);
                profileImage.setImageBitmap(bitmap);
                mPicture = bitmap;
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void handleUpload(Bitmap bitmap, final Intent intent){
        Log.d(TAG, "handleUpload: start");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);

        try {
            String storageUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference reference = FirebaseStorage.getInstance()
                    .getReference()
                    .child("profile-images")
                    .child(storageUid+".jpeg");

            reference.putBytes(baos.toByteArray())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            getDownloadUrl(reference, intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
        catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this, "Error Firebase Unreached", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "handleUpload: end");
    }

    private void getDownloadUrl(StorageReference reference,final Intent intent)
    {
        Log.d(TAG, "getDownloadUrl: start");
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setProfileUrl(uri, intent);
            }
        });
        Log.d(TAG, "getDownloadUrl: end");
    }

    private void setProfileUrl(Uri uri, final Intent intent)
    {
        Log.d(TAG, "setProfileUrl: start");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri).build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "Profile image failed", Toast.LENGTH_SHORT).show();
                    }
                });
        Log.d(TAG, "setProfileUrl: end");
    }
}
