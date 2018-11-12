package com.vss.app.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 2000;
    ImageView cameraImage;
    Button camera, gallery, profile;
    Activity activity;
    boolean connected = false;
    LinearLayout linearLayout;
    boolean clicked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraImage = findViewById(R.id.img_cam);
        gallery = findViewById(R.id.btn_gallery);
        camera = findViewById(R.id.btn_cam);
        profile = findViewById(R.id.btn_update);
        linearLayout = findViewById(R.id.view_linear);
        activity = this;

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermission();
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhoto();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        checkConnection();

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                getCamera();
                                break;
                            case 1:
                                getPhoto();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }



    private void checkConnection() {
//        boolean isConnected = ConnectivityReceiver.isConnected();
//        showSnack(isConnected);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
            Toast.makeText(activity, "no internet ", Toast.LENGTH_LONG).show();
        }

    }

    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Good! Connected to Internet";
        } else {
            message = "Sorry! Not connected to internet";
        }

        Snackbar snackbar = Snackbar
                .make(linearLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void getCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void getPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_REQUEST);
    }

    public void getPermission() {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);

        } else {
            getCamera();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            cameraImage.setImageBitmap(photo);

        } else if (requestCode == GALLERY_REQUEST) {
            Bitmap bm = null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    cameraImage.setImageBitmap(bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCamera();
                } else {

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  MyApplication.getInstance().setConnectivityListener(CameraActivity.this);
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        checkConnection();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

}
