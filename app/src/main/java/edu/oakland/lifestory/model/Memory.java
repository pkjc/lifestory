package edu.oakland.lifestory.model;

import java.io.Serializable;

public class Memory implements Serializable {

    private String memoryTitle;
    private String memoryText;

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
}
