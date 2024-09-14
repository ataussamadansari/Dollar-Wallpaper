package com.starsky.admindollar.WallpaperPost;

public class UploadModelClass {
    String imageURL, desc;

    public UploadModelClass(String image, String desc) {
        this.imageURL = image;
        this.desc = desc;
    }

    public UploadModelClass() {
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
