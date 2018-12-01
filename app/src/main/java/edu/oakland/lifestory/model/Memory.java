package edu.oakland.lifestory.model;

import java.io.Serializable;
import java.util.Date;

public class Memory implements Serializable {

    private String memoryTitle;
    private String memoryText;
    private String memoryType;
    private String bitMapUri;
    private Date memoryCreateDate;

    public String getBitMapUri() {
        return bitMapUri;
    }

    public void setBitMapUri(String bitMapUri) {
        this.bitMapUri = bitMapUri;
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

    public Date getMemoryCreateDate() {
        return memoryCreateDate;
    }

    public void setMemoryCreateDate(Date memoryCreateDate) {
        this.memoryCreateDate = memoryCreateDate;
    }

    public Memory() { }

    public Memory(String memoryTitle, String memoryText) {
        this.memoryTitle = memoryTitle;
        this.memoryText = memoryText;
    }

    /*public Memory(String memoryTitle, String bitmapUri){
        this.memoryTitle = memoryTitle;
        this.bitMapUri = bitmapUri;
    }*/
}
