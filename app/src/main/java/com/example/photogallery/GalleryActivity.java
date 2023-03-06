package com.example.photogallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private RecyclerView mrecyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter madapter;
    private ArrayList<ImageDetails> imageDetailsArrayList;
    private static final int PERMISSION_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);
        imageDetailsArrayList=new ArrayList<>();
        mrecyclerview=findViewById(R.id.recycleview);
        mrecyclerview.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        mrecyclerview.setLayoutManager(layoutManager);
        madapter=new CustomAdapter(imageDetailsArrayList);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //We can show our custom dialog here

            }
            else {
                //It will show android's default dialog box
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }
        }
        else {
            getImage();
        }
        //getImage();
        //Log.i("info_siz",String.valueOf(imageDetailsArrayList.size()));

        mrecyclerview.setAdapter(madapter);
        /*for(int i=0; i<5; i++){
            Log.i("info"+i,imageDetailsArrayList.get(i).getMnameofImg());
        }*/
    }
    private void getImage() {
        try {
            final Cursor cur =getContentResolver().query(

                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns.DATA,MediaStore.Images.Media.DISPLAY_NAME
                    }, null, null,
                    MediaStore.Audio.Media.DATE_TAKEN );

            int count = cur.getCount();
            //Log.i("info",String.valueOf(count));
            int i = 0;
            /*if(cur!=null) Log.i("info",cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
            else Log.i("info","not null");*/
            if (cur.moveToFirst()) {

                //Log.i("info","reached here1");
                do {
                    String imageName=cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    String path=cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    //Log.i("info",cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                    imageDetailsArrayList.add(new ImageDetails(imageName,path));
                    // Log.i("info"+i,imageName);
                    i++;
                } while (cur.moveToNext());
            }
           /* while (cur.moveToNext()){
                Log.i("info","reached here1");
                String imageName=cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
                int path=cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                imageDetailsArrayList.add(new ImageDetails(imageName,path));
                Log.i("info"+i,imageName);
                i++;
            }*/

            cur.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                        getImage();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

}

