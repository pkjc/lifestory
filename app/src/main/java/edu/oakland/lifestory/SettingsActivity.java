package edu.oakland.lifestory;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.oakland.lifestory.utils.SharedPrefManager;

public class SettingsActivity extends AppCompatActivity {

    TextView userName, userEmail;

    Context mContext = this;

    SharedPrefManager sharedPrefManager;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private CircleImageView mProfileImageView;
    private String mUsername, mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProfileImageView = (CircleImageView)findViewById(R.id.imageView2);
        userEmail = findViewById(R.id.userEmail);
        userName = findViewById(R.id.userName);

        // create an object of sharedPreferenceManager and get stored user data
        sharedPrefManager = new SharedPrefManager(mContext);
        mUsername = sharedPrefManager.getName();
        mEmail = sharedPrefManager.getUserEmail();
        String uri = sharedPrefManager.getPhoto();
        Uri mPhotoUri = Uri.parse(uri);

        //Set data gotten from SharedPreference to the Navigation Header view
        userName.setText(mUsername);
        userEmail.setText(mEmail);

        Picasso.get()
                .load(mPhotoUri)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(mProfileImageView);

    }
}
