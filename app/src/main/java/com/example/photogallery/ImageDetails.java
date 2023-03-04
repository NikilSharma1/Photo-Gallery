package com.example.photogallery;

public class ImageDetails {
    private String mnameofImg;
    private String mpath;


    public ImageDetails(String nameofImg,String path){
        mnameofImg=nameofImg;
        mpath=path;
    }
    public String getMnameofImg(){return mnameofImg;}


    public String getMpath() {
        return mpath;
    }
}
