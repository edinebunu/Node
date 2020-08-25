package com.example.nodedpit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateEvent extends AppCompatActivity {

    private static final String TAG = "CreateEvent";
    public static final int PICK_IMAGE = 10101;
    private StorageReference mStorageRef;

    private FirebaseAuth mAuth;

    EditText mName ;
    EditText mDescription;
    EditText mLocation;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextView mDisplayHour;
    private TimePickerDialog.OnTimeSetListener mHourSetListener;
    int eventYear, eventMonth, eventDay, eventHour, eventMinute;

    Bitmap mCover = null;

    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        mUid = currentUser.getUid();

        mDisplayDate = findViewById(R.id.Dateid);
        mDisplayDate.setPaintFlags(mDisplayDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int year = cal.get(Calendar.YEAR);
                final int month = cal.get(Calendar.MONTH);
                final int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateEvent.this,
                        android.R.style.Theme_Holo_Dialog,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            };
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + day + "/" + month + "/" + year);

                eventYear = year;
                eventMonth = month;
                eventDay = day;

                if(day <10 && month < 10) {
                    String date = "Date: " + "0" + day + "/" + "0" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else if(day < 10) {
                    String date = "Date: " + "0" + day + "/" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else if(month < 10) {
                    String date = "Date: " + day + "/" + "0" + month + "/" + year;
                    mDisplayDate.setText(date);
                }

                else {
                    String date = "Date: " + day + "/" + month + "/" + year;
                    mDisplayDate.setText(date);
                }
            }
        };

        mDisplayHour = findViewById(R.id.Hourid);
        mDisplayHour.setPaintFlags(mDisplayHour.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mDisplayHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                final int mHour = cal.get(Calendar.HOUR_OF_DAY);
                final int mMin = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(CreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        Log.d(TAG, "onTimeSet: date: " + hour + ":" + min );
                        eventHour = hour;
                        eventMinute = min;

                        if(hour < 10 && min < 10) {
                            String date = "Time: " + "0" + hour + ":" + "0" + min;
                            mDisplayHour.setText(date);
                        }
                        else if(hour < 10 && min > 10) {
                            String date = "Time: " + "0" + hour + ":" + min;
                            mDisplayHour.setText(date);
                        }
                        else if(hour > 10 && min < 10) {
                            String date = "Time: " + hour + ":" + "0" + min;
                            mDisplayHour.setText(date);
                        }
                        else{
                            String date = "Time: " + hour + ":" + min;
                            mDisplayHour.setText(date);
                        }
                    }
                },mHour,mMin, true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                dialog.show();
            };
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void chooseCover(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"pick an image"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult: start");
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && data != null)
        {
            try {
                InputStream inputStream = getContentResolver()
                        .openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mCover = bitmap;
                ImageView cover = findViewById(R.id.imageView);
                cover.setImageBitmap(bitmap);
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }

        }
    }


    private void handleUpload(Bitmap bitmap){
        Log.d(TAG, "handleUpload: start");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25, baos);

        try {
            final StorageReference reference = FirebaseStorage.getInstance()
                    .getReference()
                    .child("Event-Covers")
                    .child(mName.getText().toString()+".jpeg");

            reference.putBytes(baos.toByteArray())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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


    public void createEvent(View view)
    {
        mName =(EditText) findViewById(R.id.editTextTextPersonName5);
        mDescription =(EditText) findViewById(R.id.editTextTextPersonName8);
        mLocation = (EditText) findViewById(R.id.editTextTextPersonName6);

        String name = mName.getText().toString();
        String desc = mDescription.getText().toString();
        String loc = mLocation.getText().toString();

        if(!name.equals("") && !desc.equals("") && !loc.equals(""))
        {
        //Event e = new Event();
        CreateEventDb(name,desc,loc,eventYear,eventMonth,eventDay,eventHour,eventMinute,mUid);
        handleUpload(mCover);
        finish();

        }
    }



    public void CreateEventDb(final String name, String description, String location, int yy, int mm, int dd, int hh, int min, String uid)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final String ideaID = UUID.randomUUID().toString();

        Map<String, Object> info = new HashMap<>();
        info.put("Name",name);
        info.put("Description",description);
        info.put("Location", location);
        info.put("DateYear", yy);
        info.put("DateMonth", mm);
        info.put("DateDay",dd);
        info.put("DateHour", hh);
        info.put("DateMin", min);
        info.put("Timestamp",new Date());
        info.put("HostId",uid);


        db.collection("Events").document()
                .set(info)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        final String[] currentEventId = {null};

                        db.collection("Events")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if(document.getString("Name").equals(name)) {
                                                    currentEventId[0] = document.getId();
                                                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                                                    try {
                                                        BitMatrix bitMatrix = qrCodeWriter.encode(currentEventId[0], BarcodeFormat.QR_CODE, 400, 400);
                                                        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.RGB_565);

                                                        for (int x = 0; x < 400; x++) {
                                                            for (int y = 0; y < 400; y++) {
                                                                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                                            }
                                                        }

                                                        saveImage(bitmap, currentEventId[0]);

                                                    } catch (WriterException | IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }
                                        } else {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    }
                                });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    private void saveImage(Bitmap bitmap, String name) throws IOException {

        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title" , "Smt");  // Saves the image.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25, baos);

        try {
            String storageUid = name;
            final StorageReference reference = FirebaseStorage.getInstance()
                    .getReference()
                    .child("qr-codes")
                    .child(storageUid +".jpeg");

            reference.putBytes(baos.toByteArray())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            getDownloadUrl(reference);
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


    }


    private void getDownloadUrl(StorageReference ref)
    {
        Log.d(TAG, "getDownloadUrl: start");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setQrUrl(uri);
            }
        });
    }

    private void setQrUrl(Uri uri)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri).build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        Log.d(TAG, "setProfileUrl: end");
    }






}


