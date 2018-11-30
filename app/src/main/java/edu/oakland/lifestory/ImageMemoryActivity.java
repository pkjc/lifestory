package edu.oakland.lifestory;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import edu.oakland.lifestory.model.Memory;

public class ImageMemoryActivity extends AppCompatActivity implements ImageGalleryFragment.OnGalleryFragmentInteractionListener, ImageCameraFragment.OnCameraFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    ImageButton backButton = null;
    ImageButton addButton = null;
    Memory imgMemory = null;
    EditText imgMemTitle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_memory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Gallery"));
        tabLayout.addTab(tabLayout.newTab().setText("Camera"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        //Add this for adding icons for tabs
        /*tabLayout.getTabAt(0).setIcon(R.drawable.gallery);
        tabLayout.getTabAt(1).setIcon(R.drawable.camera);*/
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //enable below and add tab title
        //tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        String requestFrom = intent.getStringExtra("RequestFrom");
        backButton = toolbar.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                v.getContext().startActivity(homeIntent);
            }
        });
        imgMemTitle = findViewById(R.id.imgMemTitle);

        addButton = toolbar.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = imgMemTitle.getText().toString();
                Intent homeIntent = new Intent("edu.oakland.lifestory.ReturnHome");
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if(imgMemory != null) {
                    imgMemory.setMemoryTitle(title);
                    imgMemory.setMemoryType("ImageMemory");
                    homeIntent.putExtra("ImageMemory", imgMemory);
                }
                v.getContext().startActivity(homeIntent);
            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setImageBitmapUri(String bitmapUri) {
        //get the bitmap uri and add to list for home screen
        imgMemory = new Memory("Temp", bitmapUri, 0);
    }

    @Override
    public void setCameraImageBmUri(String uri) {
        //get the bitmap uri and add to list for home screen
        imgMemory = new Memory("Temp", uri, 0);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_memory, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int noOfTabs;

        public SectionsPagerAdapter(FragmentManager fm, int tabs) {
            super(fm);
            this.noOfTabs = tabs;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    ImageGalleryFragment tab1 = new ImageGalleryFragment();
                    return tab1;
                case 1:
                    ImageCameraFragment tab2 = new ImageCameraFragment();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            //return 2;
            return noOfTabs;
        }
    }
}
