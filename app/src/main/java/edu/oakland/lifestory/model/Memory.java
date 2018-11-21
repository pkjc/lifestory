package edu.oakland.lifestory.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Memory implements Serializable {

    private String memoryTitle;
    private String memoryText;

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    private Bitmap imgBitmap;

    public String getMemoryTitle() {
        return memoryTitle;
    }

    public void setMemoryTitle(String memoryTitle) {
        this.memoryTitle = memoryTitle;
    }

    public String getMemoryText() {
        return memoryText;
    }

    public void setMemoryText(String memoryText) {
        this.memoryText = memoryText;
    }

    public Memory(String memoryTitle, String memoryText) {
        this.memoryTitle = memoryTitle;
        this.memoryText = memoryText;
    }

    public Memory(String memoryTitle, Bitmap bitmap){
        this.memoryTitle = memoryTitle;
        this.imgBitmap = bitmap;
    }
}
