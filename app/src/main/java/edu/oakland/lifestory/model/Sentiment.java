package edu.oakland.lifestory.model;

public class Sentiment {
    String anger;
    String disgust;
    String fear;
    String joy;
    String sadness;

    public Sentiment() {
    }

    public Sentiment(String anger, String disgust, String fear, String joy, String sadness) {
        this.anger = anger;
        this.disgust = disgust;
        this.fear = fear;
        this.joy = joy;
        this.sadness = sadness;
    }

    public String getAnger() {
        return anger;
    }

    public void setAnger(String anger) {
        this.anger = anger;
    }

    public String getDisgust() {
        return disgust;
    }

    public void setDisgust(String disgust) {
        this.disgust = disgust;
    }

    public String getFear() {
        return fear;
    }

    public void setFear(String fear) {
        this.fear = fear;
    }

    public String getJoy() {
        return joy;
    }

    public void setJoy(String joy) {
        this.joy = joy;
    }

    public String getSadness() {
        return sadness;
    }

    public void setSadness(String sadness) {
        this.sadness = sadness;
    }
}
