package edu.oakland.lifestory;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbsp.materialfilepicker.MaterialFilePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import edu.oakland.lifestory.model.Memory;
import edu.oakland.lifestory.utils.Constants;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
public class MemoryActivity extends AppCompatActivity {
    //boolean flag to know if main FAB is in open or closed state.
    private boolean fabExpanded = false;
    private FloatingActionButton fabAttach, fabImage, fabAudio, fabVideo, fabMap;
    //Linear layout holding the Image submenu
    private LinearLayout layoutFabImage;
    //Linear layout holding the Audio submenu
    private LinearLayout layoutFabAudio;
    private LinearLayout layoutFabMapCheckin;
    private LinearLayout layoutFabVideo;

    EditText memoryTitle, memoryContent;
    ImageButton backButton, createMemButton;
    ImageView imageView;
    String downloadUri;
    int i;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth;
    private String current_user_id;

    private Bitmap compressedImageFile;
    private Uri imgUri;
    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_REQUEST_CODE = 1;
    String mCurrentPhotoPath;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    File mAudioFile;

    boolean isRecording = false;
    boolean isPlaying = false;

    private TextView timerValue;

    Chronometer cmTimer;
    long elapsedTime;
    Boolean resume = false;
    Button recordBtn;
    Button playBtn;

    public static final int PERMISSIONS_REQUEST_CODE = 10;
    public static final int FILE_PICKER_REQUEST_CODE = 12;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 11;
    Long tsLong = System.currentTimeMillis()/1000;
    String ts = tsLong.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);
        //Hide the app name
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fabAttach = this.findViewById(R.id.fabAttach);
        layoutFabImage = this.findViewById(R.id.layoutFabImage);
        layoutFabAudio = this.findViewById(R.id.layoutFabAudio);
        layoutFabVideo = this.findViewById(R.id.layoutFabVideo);
        layoutFabMapCheckin = this.findViewById(R.id.layoutFabMapCheckin);

        fabImage = this.findViewById(R.id.fabImage);
        fabAudio = this.findViewById(R.id.fabAudio);
        fabVideo = this.findViewById(R.id.fabVideo);
        fabMap = this.findViewById(R.id.fabMap);

        playBtn = findViewById(R.id.playBtn);
        playBtn.setVisibility(View.GONE);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlaying(null);
            }
        });
        recordBtn = findViewById(R.id.recordBtn);
        recordBtn.setVisibility(View.GONE);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        cmTimer = (Chronometer) findViewById(R.id.cmTimer);
        cmTimer.setVisibility(View.GONE);
        cmTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer arg0) {
                if (!resume) {
                    long minutes = ((SystemClock.elapsedRealtime() - cmTimer.getBase())/1000) / 60;
                    long seconds = ((SystemClock.elapsedRealtime() - cmTimer.getBase())/1000) % 60;
                    elapsedTime = SystemClock.elapsedRealtime();
                    Log.d("chrono", "onChronometerTick: " + minutes + " : " + seconds);
                } else {
                    long minutes = ((elapsedTime - cmTimer.getBase())/1000) / 60;
                    long seconds = ((elapsedTime - cmTimer.getBase())/1000) % 60;
                    elapsedTime = elapsedTime + 1000;
                    Log.d("chrono", "onChronometerTick: " + minutes + " : " + seconds);
                }
            }
        });
        try {
            mAudioFile = createAudioFile(this, "demo" + ts);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        //current_user_id = "AjKLJ0N8p5at5fsnSLLuHuPL2Zr1";

        //When main Fab (Attach) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Attachment) open/close behavior
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

        askPermissions();

        fabImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Action to choose gallery or camera
                showImageDialog();
            }
        });

        fabAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Action to choose gallery or camera
                showAudioDialog();
            }
        });

        fabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDO
            }
        });

        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo
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
                    getImageUri(bitmap);
                    memory.setBitMapUri(downloadUri);
                }
                memory.setMemoryCreateDate(new Date());
                memory.setMemoryType("Memory");
                memory.setUserId(current_user_id);
                memory.setBitMapUri(downloadUri);

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

        });
    }

    private void startRecording() {
        recordBtn.setVisibility(View.VISIBLE);
        cmTimer.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        requestAudioPermissions();
    }

    void askPermissions(){
        if(ContextCompat.checkSelfPermission(this,READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //TODO
            Toast.makeText(this, "Read perm granted.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // TODO
            Toast.makeText(this, "Write perm granted.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    private static File createAudioFile(Context context, String audioName) throws IOException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS);
        File audio = File.createTempFile(
                audioName,  /* prefix */
                ".3gp",         /* suffix */
                storageDir      /* directory */
        );

        return audio;
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            recordAudio();
        }
    }

    private void recordAudio() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mAudioFile.getAbsolutePath());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }

        if (!isRecording) {
            try {
                mRecorder.prepare();
                mRecorder.start();
                isRecording = true;
                isPlaying = false;

                if(!resume) {
                    cmTimer.setBase(SystemClock.elapsedRealtime());
                    cmTimer.start();
                } else {
                    cmTimer.start();
                }

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("Audio", "prepare() failed");
            }
        } else if (isRecording) {
            isRecording = false;
            stopRecording();
        }
    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabImage.setVisibility(View.INVISIBLE);
        layoutFabAudio.setVisibility(View.INVISIBLE);
        layoutFabVideo.setVisibility(View.INVISIBLE);
        layoutFabMapCheckin.setVisibility(View.INVISIBLE);

        fabAttach.setImageResource(R.drawable.ic_attach_file_white_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabImage.setVisibility(View.VISIBLE);
        layoutFabAudio.setVisibility(View.VISIBLE);
        layoutFabVideo.setVisibility(View.VISIBLE);
        layoutFabMapCheckin.setVisibility(View.VISIBLE);
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
                            takePicAndDisplayIt();
                           break;
                    case 2: //cancel
                           break;
                }
            }
        });
        imageDialog.show();
    }

    private void showAudioDialog(){
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        imageDialog.setTitle("Select Action");
        String[] imageDialogItems = {
                "Choose Audio from Device",
                "Record Audio",
                "Cancel"
        };
        imageDialog.setItems(imageDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: //from gallery
                        chooseAudioFromDevice();
                        break;
                    case 1: //from camera
                        startRecording();
                        break;
                    case 2: //cancel
                        break;
                }
            }
        });
        imageDialog.show();
    }

    private void chooseAudioFromDevice() {
        checkPermissionsAndOpenFilePicker();
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            cmTimer.stop();
            cmTimer.setText("00:00");
            Toast.makeText(getApplicationContext(), "Recording stopped" + mAudioFile.getPath(), Toast.LENGTH_LONG).show();

            uploadAudio();
        }
    }
    private void uploadAudio(){
        Uri file = Uri.fromFile(new File(mAudioFile.getPath()));
        final StorageReference fileRef = storageReference.child("Audio/"+file.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(MemoryActivity.this, "Upload Successful!"+ downloadUri, Toast.LENGTH_SHORT).show();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }
    public void startPlaying(View view) {
        Log.e("startPlaying", ""+isPlaying);
        if(!isPlaying){
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mAudioFile.getAbsolutePath());
                mPlayer.prepare();
                mPlayer.start();
                isPlaying = true;
            } catch (IOException e) {
                Log.e("Audio", "prepare() failed");
            }
        } else if(isPlaying){
            stopPlaying();
        }
    }

    public void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
            isPlaying = false;
        }
    }

    private void chooseImageFromGallery(){
        if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            MemoryActivity.this.startActivityForResult(galleryIntent, Constants.REQUEST_PICK_IMAGE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;


        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
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
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    recordAudio();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showError();
                    recordBtn.setEnabled(false);
                }
                return;
            }
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    showError();
                }
            }
        }
    }

    private void openFilePicker() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(true)
                .withTitle("Choose an Audio File")
                .start();
    }

    private void showError() {
        Toast.makeText(this, "Required permissions not granted.", Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Toast.makeText(this, "FILE CREATED", Toast.LENGTH_SHORT).show();
        return image;
    }

    public void takePicAndDisplayIt() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            startActivityForResult(intent, Constants.REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Log.d("URI>> ", "onActivityResult: " + data.toString() + " DATA>>  " +  getImageUri((Bitmap) data.getExtras().get("data")));
                imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
                final String randomName = UUID.randomUUID().toString();
                StorageReference filepath = storageReference.child("Photos").child(randomName + ".jpg");

                filepath.putFile(getImageUri((Bitmap) data.getExtras().get("data"))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MemoryActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("", "onSuccess: uri= "+ uri.toString());
                                    downloadUri = uri.toString();
                                }
                            });

                        }else {
                            Log.d(">> TASK FAILED", "onComplete: ");
                        }
                    }
                });
            }
        }
    }

    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = "";

        if(ContextCompat.checkSelfPermission(this,READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, null, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, null, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        return Uri.parse(path);
    }

}
