package com.example.android.snapchatclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by paulshao on 2/23/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CustomViewHolder>{

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView caption;
        TextView poster;
        ImageView snap;

        public CustomViewHolder(View itemView) {
            super(itemView);
            this.caption = itemView.findViewById(R.id.newCaption);
            this.poster = itemView.findViewById(R.id.poster);
            this.snap = itemView.findViewById(R.id.imageView);
        }


    }

    private Context context;
    private ArrayList<Snap> snapData;
    private ArrayList<Snap> trueData;


    public ListAdapter(Context context, ArrayList<Snap> snapData) {
        this.context = context;
        this.snapData = snapData;
        Log.d("m",""+ snapData.size());
    }


    @Override
    public ListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.CustomViewHolder holder, int position) {
        final Snap s = snapData.get(position);
        holder.poster.setText(String.format("Posted by: %s", s.email));
        holder.caption.setText(s.caption);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(s.snapURL+".png");
        Glide.with(context).using(new FirebaseImageLoader()).load(storageRef).into(holder.snap);

        FirebaseStorage.getInstance().getReference().child(s.snapURL+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Log.d("Loading Success!", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading Failed...", exception.toString());
            }
        });

    }


    public class downloadFilesTask extends AsyncTask<Void, Void, Bitmap> {
        CustomViewHolder holder;
        ArrayList<Snap> data;
        public downloadFilesTask (CustomViewHolder holder, ArrayList<Snap> data){
            this.holder = holder;
            this.data = data;
        }

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference mStorageReference = storageReference.child(data.get(0).snapURL+".png");
        final Bitmap[] bitmap = new Bitmap[1];

        @Override
        protected Bitmap doInBackground(Void... voids) {


            final long ONE_MEGABYTE = 1024 * 1024;
            mStorageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Log.d("string", String.valueOf(bitmap[0]));

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
            return bitmap[0];
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            holder.snap.setImageBitmap(bitmap);
            System.out.println(holder.snap.toString());
        }
    }

    @Override
    public int getItemCount() {
        return snapData.size();
    }

    public void setData(ArrayList<Snap> newData) {
        trueData = newData;
        notifyDataSetChanged();

    }
}
