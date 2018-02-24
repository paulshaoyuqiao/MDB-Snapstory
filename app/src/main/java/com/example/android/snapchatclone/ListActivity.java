package com.example.android.snapchatclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by paulshao on 2/23/18.
 */

public class ListActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SocialsApp");
    final ArrayList<Snap> snaps = new ArrayList<>();
    ListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //create recyclerview
        RecyclerView recyclerAdapter = findViewById(R.id.RecyclerView);
        recyclerAdapter.setHasFixedSize(true);
        recyclerAdapter.setLayoutManager(new LinearLayoutManager(this));

        //set the new adapter with the posts
        adapter = new ListAdapter(getApplicationContext(),snaps);
        recyclerAdapter.setAdapter(adapter);

        //Add Button
        Button add = findViewById(R.id.addSnap);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NewSnapActivity.class);
                startActivity(intent);
            }
        });

        //to detect and retrieve once new values/events are added
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/SocialsApp");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                snaps.clear();
                //load the new post into the temporary arraylist
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {

                    Snap post = dataSnapshot1.getValue(Snap.class);
                    snaps.add(post);
                }
                Collections.reverse(snaps);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }
}
