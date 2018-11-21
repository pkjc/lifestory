package edu.oakland.lifestory;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Half;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.oakland.lifestory.model.Memory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ImageGalleryFragment extends Fragment {
    TextView imgTitle = null;
    EditText imgMemTitle = null;
    Button browseBtn, addImgBtn, cancelBtn = null;
    View view = null;
    ImageView imgMemory = null;
    private int CHOOSE_IMAGE_REQUEST = 1;

    private OnGalleryFragmentInteractionListener mListener;

    /*public ImageGalleryFragment() {
        // Required empty public constructor
    }*/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * @return A new instance of fragment ImageGalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageGalleryFragment newInstance(String param1, String param2) {
        ImageGalleryFragment fragment = new ImageGalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_image_gallery, container, false);
        browseBtn = view.findViewById(R.id.browseBtn);
        imgMemory = view.findViewById(R.id.imgMemory);
        addImgBtn = view.findViewById(R.id.addImgBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        imgMemTitle = view.findViewById(R.id.imgMemTitle);

        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, CHOOSE_IMAGE_REQUEST);
            }
        });

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitMap = ((BitmapDrawable)imgMemory.getDrawable()).getBitmap();
                Memory imgMemory = new Memory(imgMemTitle.getText().toString(), bitMap);


            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == 0){
            return;
        }
        InputStream stream = null;
        if(requestCode == CHOOSE_IMAGE_REQUEST && data != null){
            /*Uri contentURI = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(contentURI, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();*/
            //imgMemory.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            try {
                stream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap original = BitmapFactory.decodeStream(stream);
                Bitmap finalBM = Bitmap.createScaledBitmap(original, original.getWidth(), original.getHeight(), true);
                imgMemory.setImageBitmap(finalBM);
                mListener.setImageBitmap(finalBM);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGalleryFragmentInteractionListener) {
            mListener = (OnGalleryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnGalleryFragmentInteractionListener {
        // TODO: Update argument type and name
        void setImageBitmap(Bitmap bitmap);
    }
}
