package com.technine.pcmc_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.technine.pcmc_1.utils.ConnectionDetector;
import com.technine.pcmc_1.utils.LocationTrack;
import com.technine.pcmc_1.utils.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    //camera
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private String userChoosenTask;
    ImageView hording_img;
    String encoded_image = "";
    // cameara

    ConnectionDetector internet;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    String userkey, UserName, Userkeyname;
    TextView dateandtime, longitude, lattitude;
    EditText tokenno, discription, type;
    private boolean mImageAdded = false;

    Button send;
    private Double latitude1;
    private Double longitude1;
    private Spinner spinner;

    LocationTrack locationTrack;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    // Spinner spinner;
    private String[] types;
    private String typeKey;

    LinearLayout linear_discription;
    LinearLayout linear_linaces;

    private ProgressDialog progressDialog;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("MyCameraApp", "failed to create directory");
//                return null;
//            }
//        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    //location final code
    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;


    String str_tokenno, str_discription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intview();
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        String dateToStr = format.format(today);
        Log.e("dateToStr", "====" + dateToStr);
        dateandtime.setText(dateToStr);

        readParamsFromSP();


        internet = new ConnectionDetector(MainActivity.this);

        hording_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageAdded) {
                    openConfirmDialog();
                } else {
                    selectImage();
                }
            }
        });




//        str_name = getIntent().getExtras().getString("name", "");
//        str_address = getIntent().getExtras().getString("address", "");
//        str_type = getIntent().getExtras().getString("type", "");
//        str_key = getIntent().getExtras().getString("key", "");


//        personname.setText(str_name);
//        address.setText(str_address);
//        type.setText(str_type);

//        str_name = personname.getText().toString();
//        str_address = address.getText().toString();
//        str_type = spinner.getSelectedItem().toString();




        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();


        //discription
        //selectImage();
//        if (typeKey == null || typeKey.isEmpty() || typeKey.equals("0")) {
//            Toast.makeText(HomeScreen.this, "Please Choose type...", Toast.LENGTH_LONG).show();
//
//        }else {
//
//        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internet.isConnectingToInternet()) {
                    str_tokenno = tokenno.getText().toString();
                    str_discription = discription.getText().toString();

                    // str_key = getIntent().getExtras().getString("key", "");
                    new PostDataTask().execute((Void[]) null);

                } else {
                    internet.showAlertDialog(MainActivity.this, false);
                }
            }
        });


    }//onCreate




    private void intview() {
        longitude = (TextView) findViewById(R.id.longitude);
        lattitude = (TextView) findViewById(R.id.lattitude);
        dateandtime = (TextView) findViewById(R.id.dateandtime);
        tokenno = (EditText) findViewById(R.id.tokenno);
        discription = (EditText) findViewById(R.id.discription);
        //property_villege = (TextView) findViewById(R.id.property_villege);
        hording_img = (ImageView) findViewById(R.id.hording_img);
        // loc = (Button) findViewById(R.id.loc);
       // spinner = (Spinner) findViewById(R.id.spinner_type);
        send = (Button) findViewById(R.id.send);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }//intview()

    public void readParamsFromSP() {
        sp = getSharedPreferences("PCMCBiometric", 0);
        editor = sp.edit();
        editor.apply();
        UserName = sp.getString("username", "");
        Log.e("get form sp username", "=======" + UserName);
        userkey = sp.getString("userkey", "");
        Log.e("get form sp userkey", "=======" + userkey);
        Userkeyname = sp.getString("userkeyname", "");
        Log.e("get form sp username", "=======" + Userkeyname);
    }

    // Confirmation Dialog for changing grievance image
    public void openConfirmDialog() {
        final AlertDialog.Builder confirmDlg = new AlertDialog.Builder(MainActivity.this);
        confirmDlg.setTitle("Change Image");
        confirmDlg.setMessage("Are you sure you want to remove this image and upload another?");
        confirmDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectImage();
            }
        });
        confirmDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        confirmDlg.show();
    }//openConfirmDialog

    //location
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            //  locationTv.setText("You need to install Google Play Services to use the App properly");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            // locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());

            lattitude.setText(Double.toString(location.getLatitude()));
            longitude.setText(Double.toString(location.getLongitude()));
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //  locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());

            lattitude.setText(Double.toString(location.getLatitude()));
            longitude.setText(Double.toString(location.getLongitude()));
        }
    }

    //cameara
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MainActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;


        }
    }//onRequestPermissionsResult


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }//selectImage

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }//galleryIntent

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }//cameraIntent

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (thumbnail != null) {
            // encoded_image = new File(encodeTobase64(thumbnail));
            encoded_image = encodeTobase64(thumbnail);
            hording_img.setImageBitmap(thumbnail);
//            Log.e("imgecamera", "======" + encoded_image);
        } else {
            hording_img.setImageResource(R.drawable.take_a_picture);
        }

//        ivImage.setImageBitmap(thumbnail);
//        encoded_image = encodeTobase64(thumbnail);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bm != null) {
            //   encoded_image = new File(encodeTobase64(bm));
            encoded_image = encodeTobase64(bm);
            hording_img.setImageBitmap(bm);
            Log.e("imgegallery", "&&&&&&&&&" + encoded_image);
        } else {

            hording_img.setImageResource(R.drawable.take_a_picture);
        }


        //ivImage.setImageBitmap(bm);
    }


    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }

    //cameara

    // Call to webservice for Grievance Reporting
    private class PostDataTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Processing...", "Posting data...", true, false);
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpClient httpClient = new DefaultHttpClient();
            JSONObject json = new JSONObject();

            try {
                HttpPost httppost = new HttpPost("http://103.224.247.133:8085/PCMCHordings/rest/service/captureDistribution");

                String log = null, lat = null;
                if (location != null) {
                    // locationTv.setText("Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());

                    lat = Double.toString(location.getLatitude());
                    log = Double.toString(location.getLongitude());
                } else {
                    lat="";
                    log="";
                }

                Intent i = new Intent();
                i.putExtra("latitude",lat);
                i.putExtra("longitude",log);



                //json.put("userkey", sp.getString("userkey",""));
                //json.put("userkey", "1");
                json.put("remark", str_discription);
                json.put("image", encoded_image);
                json.put("latitude", lat);
                json.put("longitude", log) ;
                json.put("tokan", str_tokenno);


                httppost.setEntity(new StringEntity(json.toString(), "UTF8"));
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/json");

                HttpResponse response = httpClient.execute(httppost);
                Log.e("####code1#", "response " + response);
                StatusLine statusLine = response.getStatusLine();
                Log.e("#####code2#", "statusLine " + statusLine.toString());
                HttpEntity entity12 = response.getEntity();
                InputStream is = entity12.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb1 = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb1.append(line + "\n");
                }
                is.close();
                String result = sb1.toString();
                result = result.trim();
                System.out.println(result);
                int statusCode = statusLine.getStatusCode();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Error:  " + e.getMessage();
            }
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            String toastMsg = result;
            // JSONObject jObj = null;
            try {
                JSONObject jObj = new JSONObject(toastMsg);
                if (!result.toLowerCase().contains("error")) {
                    if (jObj.length() > 0) {
                        alertDialog(jObj.getString("MSG"));

                    } else {

                        alertDialog1(jObj.getString("MSG"));
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void alertDialog(String errer) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(errer);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.pcmc);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                         Intent intent = new Intent(MainActivity.this, MainActivity.class);
                         startActivity(intent);
                        finish();
                    }
                });

        alertDialog.show();
    }//alertDialog

    private void alertDialog1(String errer) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(errer);
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.drawable.pcmc);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }//alertDialog1


}//HomeActivity

