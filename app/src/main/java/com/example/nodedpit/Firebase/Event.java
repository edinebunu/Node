package com.example.nodedpit.Firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.nodedpit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Event {
    private static final String TAG = "Event";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void createEvent(final String name, String description, String location, int yy, int mm, int dd, int hh, int min, String uid)
    {
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

        //FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                                                    }
                                                    catch (WriterException e) {
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

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://node-85fa5.appspot.com/");

    public void setCoverImg(String eventName, final ImageView view) throws IOException {

        StorageReference mRef = storageReference.child("Event-Covers").child(eventName+".jpeg");
        final File file = File.createTempFile("image","jpeg");
        mRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                view.setImageBitmap(bitmap);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }


    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + "name" + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void setProfileImg(String uid, final CircleImageView view) throws IOException {
        final Bitmap mImg;
        StorageReference mRef = storageReference.child("profile-images").child(uid+".jpeg");
        final File file = File.createTempFile("image","jpeg");
        mRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                view.setImageBitmap(bitmap);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public void goingButtonPressed(final String uid, final String userId, final Button going){

        db.collection("Events").document(uid).collection("GoingUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if(document.getId().equals(userId)){
                                    going.setBackgroundResource(R.drawable.going);
                                    db.collection("Events").document(uid)
                                            .collection("GoingUsers").document(userId).delete();
                                    return;
                                }
                            }
                            Map<String, Object> user = new HashMap<>();
                            user.put("first", "Alan");
                            going.setBackgroundResource(R.drawable.going_green);
                            db.collection("Events").document(uid)
                                    .collection("GoingUsers").document(userId).set(user);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void interestedButtonPressed(final String uid, final String userId, final Button interested){
        db.collection("Events").document(uid).collection("InterestedUsers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentName = document.getId();
                                if(document.getId().equals(userId)){
                                    interested.setBackgroundResource(R.drawable.interested);
                                    db.collection("Events").document(uid)
                                            .collection("InterestedUsers").document(userId).delete();

                                    return;
                                }
                            }
                            Map<String, Object> user = new HashMap<>();
                            user.put("first", "Alan");
                            interested.setBackgroundResource(R.drawable.interested_green);
                            db.collection("Events").document(uid)
                                    .collection("InterestedUsers").document(userId).set(user);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


}
