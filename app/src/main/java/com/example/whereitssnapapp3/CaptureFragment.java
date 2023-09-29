package com.example.whereitssnapapp3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class CaptureFragment extends Fragment implements LocationListener {
    private static final int CAMERA_REQUEST = 123;
    private ImageView mImageView;
    // The filepath for the photo
    String mCurrentPhotoPath;
    // A reference to our database
    private DataManager mDataManager;
    private Location mLocation = new Location("");
    private LocationManager mLocationManager;
    private String mProvider;
    // Where the captured image is stored
    private Uri mImageUri = Uri.EMPTY;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mDataManager = new DataManager(getActivity().getApplicationContext());
        mLocationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        mProvider = mLocationManager.getBestProvider(criteria, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//Inflate the layout file then get all necessary references
        View view = inflater.inflate
                (R.layout.fragment_capture, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        Button btnCapture = (Button) view.findViewById(R.id.btnCapture);
        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        final EditText mEditTextTitle =
                (EditText) view.findViewById(R.id.editTextTitle);
        final EditText mEditTextTag1 =
                (EditText) view.findViewById(R.id.editTextTag1);
        final EditText mEditTextTag2 =
                (EditText) view.findViewById(R.id.editTextTag2);
        final EditText mEditTextTag3 =
                (EditText) view.findViewById(R.id.editTextTag3);
// Listen for clicks on the capture button
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
// Error occurred while creating the File
                    Log.e("error", "error creating file");
                }
// Continue only if the File was successfully created
                if (photoFile != null) {
                    mImageUri = Uri.fromFile(photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageUri != null) {
                    if (!mImageUri.equals(Uri.EMPTY)) {
// We have a photo to save
                        Photo photo = new Photo();
                        photo.setTitle(mEditTextTitle.getText().toString());
                        photo.setStorageLocation(mImageUri);
                        photo.setGpsLocation(mLocation);
// What is in the tags
                        String tag1 = mEditTextTag1.getText().toString();
                        String tag2 = mEditTextTag2.getText().toString();
                        String tag3 = mEditTextTag3.getText().toString();
// Assign the strings to the Photo object
                        photo.setTag1(tag1);
                        photo.setTag2(tag2);
                        photo.setTag3(tag3);
// Send the new object to our DataManager
                        mDataManager.addPhoto(photo);
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).
                                show();
                    } else {
// No image
                        Toast.makeText(getActivity(), "No image to save", Toast.
                                LENGTH_LONG).show();
                    }
                } else {
// Uri not initialized
                    Log.e("Error ", "uri is null");
                }
            }
        });
        return view;
    }

    private File createImageFile() throws IOException {
// Create an image file name
        String timeStamp = new SimpleDateFormat
                ("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName, // filename
                ".jpg", // extension
                storageDir // folder
        );
// Save for use with ACTION_VIEW Intent
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult
            (int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST &&
                resultCode == Activity.RESULT_OK) {
            try {
                mImageView.setImageURI(Uri.parse(mImageUri.toString()));
            } catch (Exception e) {
                Log.e("Error", "Uri not set");
            }
        } else {
            mImageUri = Uri.EMPTY;
        }
    }

    public void onDestroy() {
        super.onDestroy();
// Make sure we don't run out of memory
        BitmapDrawable bd = (BitmapDrawable) mImageView.getDrawable();
        bd.getBitmap().recycle();
        mImageView.setImageBitmap(null);
    }

    @Override
    public void onLocationChanged(Location location) {
// Update the location if it changed
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates
                (mProvider, 500, 1, this);
            }
    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }
}
