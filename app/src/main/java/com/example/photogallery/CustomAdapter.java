package com.example.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<ImageDetails>mimagelist;
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView1;

        public CustomViewHolder(View itemview) {
            super(itemview);
            imageView1=itemview.findViewById(R.id.imageView);

        }
    }
    public CustomAdapter(ArrayList<ImageDetails>imagelist){//this arraylist will be passed from main activity when data will be fetched from storage
        mimagelist=imagelist;
    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create a view by inflating imageview layout
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview,parent,false);

        //this view is then passed onto the viewholder
        CustomViewHolder cv=new CustomViewHolder(v);
        // then viewholder is returned
        return cv;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ImageDetails currentItem=mimagelist.get(position);
        try {
            File imgFile = new  File(String.valueOf(currentItem.getMpath()));

//            if(imgFile.exists()){
//               // Log.i("info",String.valueOf(currentItem.getMpath()));
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//
//                holder.imageView1.setImageBitmap(myBitmap);
//
//            }
            final int THUMBSIZE = 64;

            Bitmap ThumbImage = ThumbnailUtils.createImageThumbnail(String.valueOf(imgFile), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            if(ThumbImage!=null)
            holder.imageView1.setImageBitmap(ThumbImage);
        } catch (Exception e){
            e.printStackTrace();
        }

        //holder.textView.setText("sdfsff");
    }

    @Override
    public int getItemCount() {
        return mimagelist.size();
    }


}
