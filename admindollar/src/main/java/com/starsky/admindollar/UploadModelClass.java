package com.starsky.admindollar;

public class UploadModelClass {
    String imageURL;

    public UploadModelClass(String image) {
        this.imageURL = image;
    }

    public UploadModelClass() {
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
