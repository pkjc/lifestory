package edu.oakland.lifestory.utils;

public class Constants {

    public static final String FIREBASE_URL = "https://lifestory-e9bc3.firebaseio.com";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";

    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final int PERMISSION_REQUEST_CAMERA = 3;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_PICK_IMAGE = 2;
}