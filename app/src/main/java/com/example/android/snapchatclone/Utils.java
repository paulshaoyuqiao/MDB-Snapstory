package com.example.android.snapchatclone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by paulshao on 2/23/18.
 */

public class Utils {

    //pick an image
    public static void UtilshowPicChooser(Activity activity, int pickImageREQUEST) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageREQUEST);
    }

    //This general method helps with Firebase authentication, specifically on creating and signing up new users
    public static void UtilsLogin(String email, String password, FirebaseAuth mAuth,final Activity activity){
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("ye", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(activity, "Sign up failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(activity.getApplicationContext(), ListActivity.class);
                            activity.startActivity(intent);
                        }

                        // ...
                    }
                });
    }


    public static void transmit(ImageView snapPic,
                                final DatabaseReference databaseReference, final EditText caption,
                                FirebaseAuth mAuth, final Uri uri, final Activity activity,
                                final StorageReference storageReference){
        snapPic.setDrawingCacheEnabled(true);
        snapPic.buildDrawingCache();
        Bitmap bitmap = snapPic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //byte[] data = baos.toByteArray();


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();


        final String id = databaseReference.child("SocialsApp").push().getKey();
        final String Caption = caption.getText().toString();
        StorageReference storageReference1 = storageRef.child(id+".png");
        final String emailTitle = mAuth.getCurrentUser().getEmail();


        storageReference1.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "darn", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (!TextUtils.isEmpty(emailTitle)){
                    StorageReference filepath = storageReference.child("PostImage").child(uri.getLastPathSegment());
                    ArrayList<String> Attendance = new ArrayList<String>();
                    Attendance.add("1");
                    final ArrayList<String> attendance = Attendance;
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Snap post = new Snap (emailTitle,Caption,id);
                            databaseReference.child(id).setValue(post);
                            Toast.makeText(activity,"Upload Complete",Toast.LENGTH_LONG).show();

                        }
                    });

                }
                else
                {
                    Toast.makeText(activity,"Make sure you enter the name, description, date, and upload the picture"
                            ,Toast.LENGTH_LONG).show();
                }
            }
        });




    }

}
