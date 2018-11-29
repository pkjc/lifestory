package edu.oakland.lifestory.model;

import java.io.Serializable;

public class Memory implements Serializable {

    private String memoryTitle;
    private String memoryText;
    private String memoryType;
    private int imgBitmap;
    private String bitMapUri;

    public String getBitMapUri() {
        return bitMapUri;
    }

    public void setBitMapUri(String bitMapUri) {
        this.bitMapUri = bitMapUri;
    }

    public int getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(int imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

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

    public Memory() { }

    public Memory(String memoryTitle, String memoryText) {
        this.memoryTitle = memoryTitle;
        this.memoryText = memoryText;
    }

    public Memory(String memoryTitle, String bitmapUri, int bitmap){
        this.memoryTitle = memoryTitle;
        this.bitMapUri = bitmapUri;
        this.imgBitmap = bitmap;
    }
}
