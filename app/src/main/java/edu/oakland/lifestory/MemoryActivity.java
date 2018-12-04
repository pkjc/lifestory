package edu.oakland.lifestory;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import edu.oakland.lifestory.model.Memory;
import edu.oakland.lifestory.utils.Constants;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MemoryActivity extends AppCompatActivity {
    //boolean flag to know if main FAB is in open or closed state.
    private boolean fabExpanded = false;
    private FloatingActionButton fabAttach, fabImage, fabAudio;
    //Linear layout holding the Image submenu
    private LinearLayout layoutFabImage;
    //Linear layout holding the Audio submenu
    private LinearLayout layoutFabAudio;

    EditText memoryTitle, memoryContent;
    ImageButton backButton, createMemButton;
    ImageView imageView;

    int i;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        //Hide the app name
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fabAttach = (FloatingActionButton) this.findViewById(R.id.fabAttach);
        layoutFabImage = (LinearLayout) this.findViewById(R.id.layoutFabImage);
        layoutFabAudio = (LinearLayout) this.findViewById(R.id.layoutFabAudio);
        fabImage = (FloatingActionButton) this.findViewById(R.id.fabImage);
        fabAudio = (FloatingActionButton) this.findViewById(R.id.fabAudio);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        //When main Fab (Attach) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Settings) open/close behavior
        fabAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
        //Only main FAB is visible in the beginning
        closeSubMenusFab();

        fabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Action to choose gallery or camera
                showImageDialog();
            }
        });
        backButton = toolbar.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                v.getContext().startActivity(homeIntent);
            }
        });

        memoryTitle = findViewById(R.id.imgMemTitle);
        memoryContent = findViewById(R.id.memoryContent);
        imageView = findViewById(R.id.imageView);

        createMemButton = toolbar.findViewById(R.id.createMemButton);

        createMemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memoryTag = memoryTitle.getText().toString();
                String memoryText = memoryContent.getText().toString();

                //Add date and time of creation as well
                Memory memory = new Memory(memoryTag, memoryText);

                if(imageView.getDrawable() != null && ((BitmapDrawable)imageView.getDrawable()).getBitmap() != null) {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    memory.setBitMapUri(getImageUri(bitmap));
                }
                memory.setMemoryCreateDate(new Date());
                memory.setMemoryType("Memory");
                memory.setUserId(current_user_id);

                handleMemoryImgUpload();

                DocumentReference mDocRef = mFirestore.document("memories/memory"+ i);
                mFirestore.collection("memories").add(memory).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    private static final String TAG = "MemoryActivity";
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MemoryActivity.this, "Memory Added Successfully!", Toast.LENGTH_SHORT).show();
                            i++;
                        } else {
                            Toast.makeText(MemoryActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: ", task.getException());
                        }
                    }
                });

                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                homeIntent.putExtra("Memory", memory);
                v.getContext().startActivity(homeIntent);
            }

            private void handleMemoryImgUpload() {

                final String randomName = UUID.randomUUID().toString();

                //File newImageFile = new File(memory.get.getPath());

            }
        });
    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabImage.setVisibility(View.INVISIBLE);
        layoutFabAudio.setVisibility(View.INVISIBLE);
        fabAttach.setImageResource(R.drawable.ic_attach_file_white_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabImage.setVisibility(View.VISIBLE);
        layoutFabAudio.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fabAttach.setImageResource(R.drawable.ic_close_white_24dp);
        fabExpanded = true;
    }

    private void showImageDialog(){
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        imageDialog.setTitle("Select Action");
        String[] imageDialogItems = {
              "Choose Photo from Gallery",
              "Capture Photo from Camera",
              "Cancel"
            };
        imageDialog.setItems(imageDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: //from gallery
                           chooseImageFromGallery();
                           break;
                    case 1: //from camera
                           captureImageFromCamera();
                           break;
                    case 2: //cancel
                           break;
                }
            }
        });
        imageDialog.show();
    }

    private void chooseImageFromGallery(){
        if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            MemoryActivity.this.startActivityForResult(galleryIntent, Constants.REQUEST_PICK_IMAGE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void captureImageFromCamera(){
        if(ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            MemoryActivity.this.startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, Constants.PERMISSION_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
            case Constants.PERMISSION_REQUEST_CAMERA: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == this.RESULT_CANCELED){
            return;
        }
        if(requestCode == Constants.REQUEST_PICK_IMAGE){
            if(data != null){
                Uri contentUri = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                    //Toast.makeText(MemoryActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true));
                } catch(IOException ie){
                    ie.printStackTrace();
                    Toast.makeText(MemoryActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if(requestCode == Constants.REQUEST_IMAGE_CAPTURE){
                Bitmap thumbnail = (Bitmap)data.getExtras().get("data");
                imageView.setImageBitmap(thumbnail);
                Toast.makeText(MemoryActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = "";
        if(ContextCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, null, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return Uri.parse(path).toString();
    }
}
