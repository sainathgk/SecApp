package com.activities.rentalapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.connection.rentalapp.NetworkConnUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.rental.rentalapp.R;
import com.service.rentalapp.Constants;
import com.service.rentalapp.FetchAddressIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class PostMainActivity extends ActionBarActivity
        implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int MIN_NUM = 1;
    private static final int MAX_NUM = 1000;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Button btnSelect;
    ImageView ivImage;
    ImageButton btnDone;
    EditText mEdit;
    String mDuration;
    String textTitle;
    String textCategory;
    String textDesc;
    String textPrice;
    String textLoc;
    String textName;
    String textEmail;
    String textPhone;
    public String TAG = "CaptureImage";
    private Spinner mCategorySpinner;
    private Spinner mDurationSpinner;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    private boolean mIsImageAdded = false;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean mAddressRequested;
    private EditText mLocationEditTextView;

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private Boolean isActivityFirstCreated = false;
    private DisplayMetrics mWindowMetrics = new DisplayMetrics();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.title_activity_post);

        getWindowManager().getDefaultDisplay().getMetrics(mWindowMetrics);

        mLocationEditTextView = (EditText) findViewById(R.id.location);

        mAddressRequested = true;
        mResultReceiver = new AddressResultReceiver(new Handler());

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setTitle("Posting ...");

        buildGoogleApiClient();

        isActivityFirstCreated = true;

        mCategorySpinner = ((Spinner) findViewById(R.id.category));
        mDurationSpinner = ((Spinner) findViewById(R.id.duration_select));

        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnDone = (ImageButton) findViewById(R.id.done);
        btnDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mEdit = (EditText) findViewById(R.id.title);
                textTitle = mEdit.getText().toString();

                mEdit = (EditText) findViewById(R.id.desc);
                textDesc = mEdit.getText().toString();

                mEdit = (EditText) findViewById(R.id.price);
                textPrice = mEdit.getText().toString();

                mEdit = (EditText) findViewById(R.id.location);
                textLoc = mEdit.getText().toString();

                /*GoogleServicesUtility gLoginUtility = GoogleServicesUtility.getGoogleInstance();
                PersonProfileDetails curUserDetails = null;*/
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("RentalPrefs", MODE_PRIVATE);
                String curUserName = preferences.getString("curUserName", null);
/*
                if (gLoginUtility != null && (curUserDetails = gLoginUtility.getPersonDetails()) != null
                        && (curUserName = curUserDetails.getUsername()) != null) {
*/
                if (curUserName != null) {
                    fillDb(curUserName);
                } else {
                    startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                }
            }
        });

    }

    private byte[] getByteArrayfromBitmap(Bitmap imgBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        return bytes.toByteArray();
    }

    private int randInt() {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((MAX_NUM - MIN_NUM) + 1) + MIN_NUM;

        return randomNum;
    }

    private void fillDb(String userName) {

        /*ContentValues values = new ContentValues();
        values.put("POST_TITLE", textTitle);

        values.put("POST_DESCRIPTION", textDesc);
        values.put("POST_PRICE", Integer.parseInt(textPrice));
        values.put("POST_CATEGORY", mCategorySpinner.getSelectedItem().toString());
        values.put("POST_ADDRESS", textLoc);
        values.put("POST_DURATION", mDurationSpinner.getSelectedItem().toString());
        values.put("POST_LATITUDE", mLastLocation.getLatitude());
        values.put("POST_LONGITUDE", mLastLocation.getLongitude());
        Bitmap bitmap = Bitmap.createBitmap(ivImage.getWidth(), ivImage.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        ivImage.draw(canvas);
        values.put("POST_IMAGE", getByteArrayfromBitmap(bitmap));

        getApplicationContext().getContentResolver().insert(Uri.parse("content://com.database.rentalapp/POST_TABLE"), values);*/

        if (textTitle.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Title is Mandatory, Please Input", Toast.LENGTH_SHORT).show();
            ((EditText) findViewById(R.id.title)).requestFocus();
            return;
        } else if (textDesc.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Description is Mandatory, Please Input", Toast.LENGTH_SHORT).show();
            ((EditText) findViewById(R.id.desc)).requestFocus();
            return;
        } else if (textPrice.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Price is Mandatory, Please Input", Toast.LENGTH_SHORT).show();
            ((EditText) findViewById(R.id.price)).requestFocus();
            return;
        } else if (mLastLocation == null) {
            Toast.makeText(getApplicationContext(), "Location not found, Please switch on the GPS & Post again", Toast.LENGTH_SHORT).show();
            mLocationEditTextView.requestFocus();
            return;
        }

        progress.show();

        Bitmap bitmap = Bitmap.createBitmap(ivImage.getWidth(), ivImage.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ivImage.draw(canvas);

        Log.i("Sainath", "JSON Connection is started");
        final JSONObject postItem = new JSONObject();
        try {
            JSONArray imageList = new JSONArray();
            postItem.put("id", randInt());
            postItem.put("category", mCategorySpinner.getSelectedItem().toString());
            postItem.put("subcategory", mCategorySpinner.getSelectedItem().toString());
            postItem.put("name", textTitle);
            postItem.put("description", textDesc);
            postItem.put("age", 2.0f);
            postItem.put("duration", mDurationSpinner.getSelectedItemPosition());
            postItem.put("price", Float.parseFloat(textPrice));
            postItem.put("userName", userName);
            postItem.put("startTime", System.currentTimeMillis());
            postItem.put("endTime", System.currentTimeMillis());
            postItem.put("insured", null);
            postItem.put("distance", null);
            if (mIsImageAdded) {
                imageList.put(Base64.encodeToString(getByteArrayfromBitmap(bitmap), Base64.DEFAULT));
                //postItem.put("thumbnailList", Base64.encodeToString(getByteArrayfromBitmap(bitmap), Base64.DEFAULT));
            } else {
                imageList.put(null);
                //postItem.put("thumbnailList",null);
            }
            postItem.put("binaryList", imageList);
            postItem.put("thumbnailList", null);
            if (mLastLocation != null) {
                postItem.put("latitude", mLastLocation.getLatitude());
                postItem.put("longitude", mLastLocation.getLongitude());
            }
            Log.i("Sainath", "JSON Connection is ended");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkConnUtility networkConnection = new NetworkConnUtility(getApplicationContext());
        NetworkResp networkResp = new NetworkResp();

        networkConnection.setNetworkListener(networkResp);
        networkConnection.saveItem(postItem.toString());
    }

    private class NetworkResp implements NetworkConnUtility.NetworkResponseListener {
        @Override
        public void onResponse(String urlString, String networkResult) {
            progress.dismiss();
            Toast.makeText(getApplicationContext(), "Posted Successfully " + networkResult, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PostMainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == 101) {
                mLastLocation = data.getParcelableExtra("new_Location");
                startIntentService();
            }
        }
        if (requestCode == 1001) {
            String accUser = data.getStringExtra("User");
            JSONObject userObj = null;
            try {
                userObj = new JSONObject(accUser);
                if (userObj != null) {
                    fillDb(userObj.getString("userName"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

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

        ivImage.setImageBitmap(thumbnail);
        mIsImageAdded = true;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ivImage.setImageBitmap(bm);
        mIsImageAdded = true;
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        isActivityFirstCreated = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (isActivityFirstCreated)
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            if (mAddressRequested) {
                startIntentService();
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        mLocationEditTextView.setText(mAddressOutput);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        Intent locIntent = new Intent(this, SelectLocationActivity.class);
        locIntent.putExtra("current_Location", mLastLocation);
        startActivityForResult(locIntent, 101);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            /*ArrayList<String> addressFragments = new ArrayList<String>();
            Address curAddress = resultData.getParcelable(Constants.RESULT_DATA_KEY);
            for(int i = 0; i < curAddress.getMaxAddressLineIndex(); i++) {
                addressFragments.add(curAddress.getAddressLine(i));
            }
            mAddressOutput = TextUtils.join(System.getProperty("line.separator"), addressFragments);*/
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(getApplicationContext(), getString(R.string.address_found), Toast.LENGTH_SHORT).show();
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            //updateUIWidgets();
        }
    }
}
