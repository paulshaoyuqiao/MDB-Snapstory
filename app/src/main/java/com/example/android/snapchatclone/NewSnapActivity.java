package com.example.android.snapchatclone;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by paulshao on 2/23/18.
 */

public class NewSnapActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int pickImageREQUEST = 111;
    private static FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Uri uri = null;

    ImageView snapPic;
    EditText caption;

    Button upload;
    Button save;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        mAuth = FirebaseAuth.getInstance();

        snapPic = findViewById(R.id.snapPic);
        caption = findViewById(R.id.newCaption);

        upload = findViewById(R.id.uploadPic);
        save = findViewById(R.id.save);
        back = findViewById(R.id.Back);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("SocialsApp");

        upload.setOnClickListener(this);
        save.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == upload) {
            Utils.UtilshowPicChooser(NewSnapActivity.this,pickImageREQUEST);
        }
        //if the clicked button is to upload all the information
        else if (view == save) {
            //Compared with the last-week version, this one uses the Utils class to generalize
            //the transmit method (because originally it takes a huge space (many lines) in the
            //newPostActivity class.
            Toast.makeText(getApplicationContext(),"Make sure you enter the name, description, date, and upload the picture"
                    ,Toast.LENGTH_LONG).show();
            if (uri == null){
                Toast.makeText(getApplicationContext(),"Make sure you actually upload a picture",Toast.LENGTH_LONG).show();
            }
            else{
                Utils.transmit(snapPic,databaseReference,caption,mAuth,uri,NewSnapActivity.this,storageReference);}
        }

        //if the clicked button is to save the data and go to the mainfeed activity
        else if (view == back)
        {
            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==pickImageREQUEST && resultCode==RESULT_OK){
            uri = data.getData();
            snapPic = (ImageView)findViewById(R.id.snapPic);
            snapPic.setImageURI(uri);

        }
    }
}
