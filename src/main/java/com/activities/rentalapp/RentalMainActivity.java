package com.activities.rentalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.connection.rentalapp.GoogleServicesUtility;
import com.connection.rentalapp.NetworkConnUtility;
import com.connection.rentalapp.NetworkConstants;
import com.connection.rentalapp.PersonProfileDetails;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.rental.rentalapp.R;
import com.service.rentalapp.Constants;
import com.service.rentalapp.FetchAddressIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RentalMainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        View.OnClickListener,
        LocationListener, AdapterView.OnItemSelectedListener, GoogleServicesUtility.GoogleServicesListener {

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private Menu mDrawerMenu;

    private MenuItem mLoginAction;
    private MenuItem mLogoutAction;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    private GoogleMap mMap = null; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleLocation;
    private GoogleApiClient mGoogleLogin;
    private LatLng myLoc = new LatLng(12.949652, 77.710938);
    public final CameraPosition My_Location =
            new CameraPosition.Builder().target(myLoc)
                    .zoom(17f)
                    .bearing(100)
                    .build();

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private String accUser = null;
    private TextView mEmailText;
    private Spinner mCategorySpin;
    private Spinner mMeterSpin;
    private TextView mInText;
    private String accMail = null;
    private String accUserImageUrl = null;
    private String accUserName = null;
    private TextView mUsernameText;

    NetworkConnUtility networkConnection = null;
    NetworkResp networkResp = new NetworkResp();
    ImageResp imageResp = new ImageResp();
    private MenuItem mSearchByCategoryAction;
    private MenuItem mSearchByLocationAction;
    private ImageView profileImage;

    private GoogleServicesUtility mGoogleLoginService = null;
    private String mCurPostalCode = null;
    private Location mCurLocation;
    private AddressResultReceiver mResultReceiver;
    private String mAccUserName = null;
    private boolean isAddressToBeSaved = false;
    private boolean didGetItems = false;

    Float mileZoomFactor = 16f;
    private TextView mDistText;
    private SharedPreferences preferences = null;
    private ProgressBar mProgress;

    private class PostsData {
        private String mPostId = null;
        private int mItemId = -1;
        private String mPostTitle = null;
        private String mPostDescription = null;
        private String mUsername = null;
        private String mPostCategory = null;
        private LatLng mLocationCoords = null;
        private int mPostPrice = -1;
        private String mPostDuration = null;
        private byte[] mPostImage = null;

        public String getPostId() {
            return mPostId;
        }

        public void setPostId(String mPostId) {
            this.mPostId = mPostId;
        }

        public int getItemId() {
            return mItemId;
        }

        public void setItemId(int mItemId) {
            this.mItemId = mItemId;
        }

        public String getPostTitle() {
            return mPostTitle;
        }

        public void setPostTitle(String mPostTitle) {
            this.mPostTitle = mPostTitle;
        }

        public String getPostDescription() {
            return mPostDescription;
        }

        public void setPostDescription(String mPostDescription) {
            this.mPostDescription = mPostDescription;
        }

        public String getUsername() {
            return mUsername;
        }

        public void setUsername(String mUsername) {
            this.mUsername = mUsername;
        }

        public String getPostCategory() {
            return mPostCategory;
        }

        public void setPostCategory(String mPostCategory) {
            this.mPostCategory = mPostCategory;
        }

        public LatLng getLocationCoords() {
            return mLocationCoords;
        }

        public void setLocationCoords(LatLng mLocationCoords) {
            this.mLocationCoords = mLocationCoords;
        }

        public int getPostPrice() {
            return mPostPrice;
        }

        public void setPostPrice(int mPostPrice) {
            this.mPostPrice = mPostPrice;
        }

        public String getPostDuration() {
            return mPostDuration;
        }

        public void setPostDuration(String mPostDuration) {
            this.mPostDuration = mPostDuration;
        }

        public byte[] getPostImage() {
            return mPostImage;
        }

        public void setPostImage(byte[] mPostImage) {
            this.mPostImage = mPostImage;
        }
    }

    private List<PostsData> mPostDataList = new ArrayList<PostsData>();
    private HashMap<String, Integer> mIconsList = new HashMap<>();
    private HashMap<String, Float> mMeterList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_main);

        mGoogleLoginService = GoogleServicesUtility.getGoogleInstance();
        mGoogleLoginService.setGoogleServiceListener(this);

        networkConnection = new NetworkConnUtility(getApplicationContext());
        mResultReceiver = new AddressResultReceiver(new Handler());

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        mDrawerMenu = navigationView.getMenu();
        mLogoutAction = mDrawerMenu.findItem(R.id.logout);
        mLoginAction = mDrawerMenu.findItem(R.id.login_opt);

        mEmailText = ((TextView) findViewById(R.id.email));

        mUsernameText = (TextView) findViewById(R.id.username);

        profileImage = (ImageView) findViewById(R.id.profile_image);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.login_opt:
                        startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                        break;

                    case R.id.my_ads:
                        if (accUser != null)
                            Toast.makeText(getApplicationContext(), "My Ads will shown later", Toast.LENGTH_SHORT).show();
                        else
                            startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                        break;

                    case R.id.favourites:
                        if (accUser != null)
                            Toast.makeText(getApplicationContext(), "Favorites will shown later", Toast.LENGTH_SHORT).show();
                        else
                            startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                        break;

                    case R.id.invite:
                        if (accUser != null)
                            Toast.makeText(getApplicationContext(), "Invitation to Friends will come soon", Toast.LENGTH_SHORT).show();
                        else
                            startActivityForResult(new Intent(getApplicationContext(), RentalLoginActivity.class), 1001);
                        break;

                    case R.id.rateus:
                        break;

                    case R.id.logout:
                        if (mGoogleLogin != null && mGoogleLogin.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGoogleLogin);
                            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleLogin);
                            mGoogleLogin.disconnect();

                            updateNavigationView("logout");
                            isAddressToBeSaved = false;
                            Toast.makeText(getApplicationContext(), "Logout from Google Function", Toast.LENGTH_SHORT).show();
                        }
                        if (mGoogleLoginService != null) {
                            mGoogleLoginService.logout();

                            updateNavigationView("logout");
                            isAddressToBeSaved = false;

                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                if (mLogoutAction != null && mLoginAction != null && drawerView != null) {
                    if (accUser != null) {
                        mLogoutAction.setVisible(true);
                        mLoginAction.setVisible(false);
                    } else {
                        mLogoutAction.setVisible(false);
                        mLoginAction.setVisible(true);
                    }

                    drawerView.invalidate();
                }
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        mCategorySpin = (Spinner) findViewById(R.id.spinner_category);

        mCategorySpin.setOnItemSelectedListener(this);

        mMeterSpin = (Spinner) findViewById(R.id.spinner_miles);

        mMeterSpin.setOnItemSelectedListener(this);

        mInText = (TextView) findViewById(R.id.in_text);

        mDistText = (TextView) findViewById(R.id.distance_text);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        mGoogleLocation = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleLogin = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        mIconsList.put("Cars", R.drawable.ic_directions_car_black_36dp);
        mIconsList.put("Homes", R.drawable.ic_home_black_36dp);
        mIconsList.put("Books", R.drawable.book_stack);
        mIconsList.put("DVDs", R.drawable.dvd_icon);
        mIconsList.put("Bikes", R.drawable.ic_directions_bike_black_36dp);
        mIconsList.put("Cameras", R.drawable.ic_local_see_black_36dp);
        mIconsList.put("Printers", R.drawable.ic_local_printshop_black_36dp);
        mIconsList.put("Mobiles", R.drawable.ic_phone_android_black_36dp);
        mIconsList.put("Tablets", R.drawable.ic_tablet_black_36dp);
        mIconsList.put("TVs", R.drawable.ic_tv_black_36dp);
        mIconsList.put("Others", R.mipmap.ic_launcher);

        String[] milesArray = getResources().getStringArray(R.array.select_miles);
        for (int zoomIdx = 0; zoomIdx < milesArray.length; zoomIdx++) {
            mMeterList.put(milesArray[zoomIdx], mileZoomFactor - zoomIdx - 1);
        }
        mileZoomFactor = 16f;

        if (networkConnection != null) {
            networkConnection.setNetworkListener(networkResp);
            networkConnection.setImageListener(imageResp);
        }

        mProgress = (ProgressBar) toolbar.findViewById(R.id.search_progress);

        preferences = getApplicationContext().getSharedPreferences("RentalPrefs", MODE_PRIVATE);
    }

    private class ImageResp implements NetworkConnUtility.ImageResponseListener {
        @Override
        public void onImageResponse(String urlString, Bitmap imageResult) {
            profileImage.setImageBitmap(imageResult);
        }
    }

    private class NetworkResp implements NetworkConnUtility.NetworkResponseListener {
        @Override
        public void onResponse(String urlString, String networkResult) {

            if (urlString.equalsIgnoreCase(NetworkConstants.GET_ITEMS) && networkResult != null && didGetItems) {
                try {
                    JSONObject searchResult = new JSONObject(networkResult);
                    JSONObject itemDetails;
                    JSONArray resultArray = searchResult.getJSONArray("items");
                    int itemCount = resultArray.length();
                    for (int itemIdx = 0; itemIdx < itemCount; itemIdx++) {
                        try {
                            itemDetails = resultArray.getJSONObject(itemIdx);

                            PostsData postsData = new PostsData();
                            postsData.setItemId(itemDetails.getInt("id"));
                            postsData.setPostTitle(itemDetails.getString("name"));
                            postsData.setPostDescription(itemDetails.getString("description"));
                            postsData.setUsername(itemDetails.getString("userName"));
                            postsData.setPostCategory(itemDetails.getString("category"));
                            postsData.setPostPrice(itemDetails.getInt("price"));
                            String[] duration = getResources().getStringArray(R.array.post_duration);
                            postsData.setPostDuration(duration[itemDetails.getInt("duration")]);
                            JSONArray imageList = itemDetails.getJSONArray("binaryList");
                            if (imageList != null && imageList.length() > 0) {
                                postsData.setPostImage(Base64.decode(imageList.get(0).toString(), Base64.DEFAULT));
                            }
                            //postsData.setPostImage(Base64.decode(itemDetails.getString("thumbnailList"), Base64.DEFAULT));
                            Double latLocation = itemDetails.getDouble("latitude");
                            //rentalCursor.getDouble(rentalCursor.getColumnIndex("POST_LATITUDE"));
                            Double longLocation = itemDetails.getDouble("longitude");
                            //rentalCursor.getDouble(rentalCursor.getColumnIndex("POST_LONGITUDE"));

                            postsData.setLocationCoords(new LatLng(latLocation, longLocation));

                            mPostDataList.add(postsData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }

                    if (itemCount == 0) {
                        Toast.makeText(getApplicationContext(), "Sorry, No Items in this selection", Toast.LENGTH_SHORT).show();
                        mPostDataList.clear();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                mProgress.setVisibility(View.GONE);
                mSearchAction.setVisible(true);
                mMap.clear();
                addMarkersToMap();
            } else if (urlString.equalsIgnoreCase(NetworkConstants.SAVE_USER_ADDRESS)) {
                //Toast.makeText(getApplicationContext(), "Address saved Successfully " + networkResult, Toast.LENGTH_SHORT).show();
                isAddressToBeSaved = false;
            } else if (urlString.startsWith(NetworkConstants.GET_USER)) {
                if (networkResult != null && (!networkResult.equalsIgnoreCase("Failed") || !networkResult.isEmpty())) {
                    accUser = networkResult;
                    updateNavigationView(accUser);
                } else {
                    updateNavigationView("logout");
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String mCategorySelected = "Cars";
        String mMeterSelected = "1";
        if (parent.getId() == R.id.spinner_category) {
            mCategorySelected = parent.getItemAtPosition(position).toString();
            mMeterSelected = mMeterSpin.getSelectedItem().toString();

            didGetItems = false;
        } else if (parent.getId() == R.id.spinner_miles) {
            mMeterSelected = parent.getItemAtPosition(position).toString();
            mCategorySelected = mCategorySpin.getSelectedItem().toString();

            didGetItems = false;
            if (mMap != null) {
                CameraPosition Location =
                        new CameraPosition.Builder().target(myLoc)
                                .zoom(mMeterList.get(mMeterSelected))
                                .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(Location));
            }
        }

        getItemsForCategory(mCategorySelected, mMeterSelected, null);
        Toast.makeText(getApplicationContext(), "Item Selected is " + mCategorySelected + " in " + mMeterSelected + " Miles", Toast.LENGTH_SHORT).show();
        /*JSONObject searchList = new JSONObject();
        JSONObject category = new JSONObject();
        JSONObject subcategory = new JSONObject();
        JSONArray search = new JSONArray();

        try {
            category.put("name", "CATEGORY");
            category.put("value", mCategorySelected);
            subcategory.put("name", "SUBCATEGORY");
            subcategory.put("value", mCategorySelected);

            search.put(category);
            search.put(subcategory);

            searchList.put("searchList", search);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (networkConnection != null) {
            networkConnection.getItemsByCategory(searchList.toString());
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getItemsForCategory(String categorySelected, String distSelected, String searchText) {
        if (mCurLocation == null) {
            Log.i("GetItems", "Location is not yet found");
            return;
        }
        if (!didGetItems) {
            if (mMap != null) {
                mMap.clear();
            }
            if (mPostDataList != null) {
                mPostDataList.clear();
            }
            JSONObject searchList = new JSONObject();
            JSONObject category = new JSONObject();
            JSONObject subcategory = new JSONObject();
            JSONObject searchInput = new JSONObject();
            JSONArray search = new JSONArray();

            try {
                category.put("name", "CATEGORY");
                category.put("value", categorySelected);
                subcategory.put("name", "SUBCATEGORY");
                subcategory.put("value", categorySelected);

                search.put(category);
                search.put(subcategory);

                if (searchText != null) {
                    searchInput.put("name", "DESCRIPTION");
                    searchInput.put("value", searchText);
                    search.put(searchInput);
                }

                searchList.put("latitude", mCurLocation.getLatitude());
                searchList.put("longitude", mCurLocation.getLongitude());
                searchList.put("distance", distSelected);
                searchList.put("units", "Miles");
                searchList.put("searchList", search);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (networkConnection != null) {
                mProgress.setVisibility(View.VISIBLE);
                if (mSearchAction != null) {
                    mSearchAction.setVisible(false);
                }
                networkConnection.getItemsByCategory(searchList.toString());
                didGetItems = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        mSearchAction = menu.findItem(R.id.action_search);

        mSearchByCategoryAction = menu.findItem(R.id.action_by_category);
        mSearchByLocationAction = menu.findItem(R.id.action_by_location);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            handleMenuSearch();
            return true;
        }
        if (id == R.id.action_by_category) {
            if (edtSeach != null) {
                String[] exampleCategory = getResources().getStringArray(R.array.category_example);
                int category_selected = mCategorySpin.getSelectedItemPosition();
                String category = mCategorySpin.getSelectedItem().toString();
                edtSeach.setHint(category + " Eg: " + exampleCategory[category_selected]);

                mSearchByCategoryAction.setChecked(true);
                mSearchByLocationAction.setChecked(false);
            }
            return true;
        }
        if (id == R.id.action_by_location) {
            if (edtSeach != null) {
                edtSeach.setHint("Search Location");
                mSearchByCategoryAction.setChecked(false);
                mSearchByLocationAction.setChecked(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton) {
            startActivity(new Intent(this, PostMainActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        didGetItems = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMap != null) {
            mMap.clear();
            /*String cateSelected = mCategorySpin.getSelectedItem().toString();
            String distSelected = mMeterSpin.getSelectedItem().toString();
            getItemsForCategory(cateSelected, distSelected);*/
        }

        if (accUser != null) {
            updateNavigationView(accUser);
        } else {
            if (preferences != null) {
                String curUserName = preferences.getString("curUserName", null);
                if (networkConnection != null && curUserName != null) {
                    networkConnection.getUser(curUserName);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleLocation.disconnect();
        mGoogleLogin.disconnect();
        if (mGoogleLoginService != null) {
            mGoogleLoginService.loginDisconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleLoginService != null && accUser == null) {
            mGoogleLoginService.loginConnect();
        }
        mGoogleLocation.connect();
        mGoogleLogin.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && data != null) {
            accUser = data.getStringExtra("User");
            updateNavigationView(accUser);
            isAddressToBeSaved = true;
            //startIntentService();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onProfileFetch(PersonProfileDetails profile) {
        updateNavigationView(profile.toJSONString());
    }

    private void updateNavigationView(String userProfile) {

        if (userProfile.equalsIgnoreCase("logout")) {
            if (mEmailText != null) {
                mEmailText.setText("");
            }
            if (mUsernameText != null) {
                mUsernameText.setText("Welcome Guest !!!");
            }
            if (profileImage != null) {
                profileImage.setImageResource(R.drawable.ic_person_black_36dp);
            }
            accUser = null;
            accMail = null;
            accUserName = null;
            accUserImageUrl = null;

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("RentalPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.clear();
            editor.commit();
            return;
        }
        try {
            JSONObject userObj = new JSONObject(userProfile);
            if (userObj != null) {
                mAccUserName = userObj.getString("userName");
                if (mEmailText != null) {
                    accMail = userObj.getString("emailId");
                    mEmailText.setText(accMail);
                    mEmailText.setVisibility(View.VISIBLE);
                }
                if (mUsernameText != null) {
                    accUserName = userObj.getString("firstName");
                    mUsernameText.setText("Hello " + accUserName);
                }
                accUserImageUrl = userObj.getString("profileImage");
                if (networkConnection != null && accUserImageUrl != null && !accUserImageUrl.equalsIgnoreCase("null")) {
                    networkConnection.getUserProfileImage(accUserImageUrl);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (mEmailText != null) {
                mEmailText.setText("");
            }
            if (mUsernameText != null) {
                mUsernameText.setText("Welcome Guest !!!");
            }
            if (profileImage != null) {
                profileImage.setImageResource(R.drawable.ic_person_black_36dp);
            }
            accUser = null;
            accMail = null;
            accUserName = null;
            accUserImageUrl = null;

            SharedPreferences preferences = getApplicationContext().getSharedPreferences("RentalPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.clear();
            editor.commit();
            return;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent markerIntent = new Intent(this, DetailViewActivity.class);
        Bundle detailPost = new Bundle();

        int size = mPostDataList.size();
        for (int pos = 0; pos < size; pos++) {
            if (mPostDataList.get(pos).getPostId().equalsIgnoreCase(marker.getId())) {
                detailPost.putInt("itemId", mPostDataList.get(pos).getItemId());
                detailPost.putString("title", mPostDataList.get(pos).getPostTitle());
                detailPost.putString("description", mPostDataList.get(pos).getPostDescription());
                detailPost.putInt("price", mPostDataList.get(pos).getPostPrice());
                detailPost.putString("duration", mPostDataList.get(pos).getPostDuration());
                detailPost.putByteArray("image", mPostDataList.get(pos).getPostImage());
                detailPost.putString("username", mPostDataList.get(pos).getUsername());
                detailPost.putString("currentUser", accUserName);
                detailPost.putDouble("itemLatitude", mPostDataList.get(pos).getLocationCoords().latitude);
                detailPost.putDouble("itemLongitude", mPostDataList.get(pos).getLocationCoords().longitude);
                markerIntent.putExtra("post_detail", detailPost);
                break;
            }
        }

        startActivity(markerIntent);
    }

    private void addMarkersToMap() {
        LatLngBounds.Builder latlngBuilder = LatLngBounds.builder();
        if (mPostDataList != null && mPostDataList.size() > 0) {
            MarkerOptions myMarker_3 = new MarkerOptions();
            String markerTitle = "";
            String markerSnippet = "";

            for (int position = 0; position < mPostDataList.size(); position++) {
                if (myMarker_3 != null) {
                    myMarker_3.position(mPostDataList.get(position).getLocationCoords());
                    markerTitle = mPostDataList.get(position).getPostTitle() + " Owned by " + mPostDataList.get(position).getUsername();
                    markerSnippet = "Renting @ $"+mPostDataList.get(position).getPostPrice()+"/"+mPostDataList.get(position).getPostDuration();
                    myMarker_3.title(markerTitle);
                    myMarker_3.snippet(markerSnippet);
                    String category = mPostDataList.get(position).getPostCategory();
                    int resId;
                    if (mIconsList.containsKey(category)) {
                        resId = mIconsList.get(category);
                    } else {
                        resId = mIconsList.get("Others");
                    }
                    myMarker_3.icon(BitmapDescriptorFactory.fromResource(resId));
                    latlngBuilder.include(mPostDataList.get(position).getLocationCoords());
                    mPostDataList.get(position).setPostId(mMap.addMarker(myMarker_3).getId());
                    didGetItems = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Marker Options is null", Toast.LENGTH_SHORT).show();
                }
            }
            if (mPostDataList.size() == 1) {
                LatLng loc = new LatLng(mPostDataList.get(0).getLocationCoords().latitude, mPostDataList.get(0).getLocationCoords().longitude);
                CameraPosition Location =
                        new CameraPosition.Builder().target(loc)
                                .zoom(17f)
                                .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(Location));
            } else {
                LatLngBounds bounds = latlngBuilder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 75);

                mMap.animateCamera(cu);
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleLocation);
        if (mCurLocation != null) {
            Double latitude = mCurLocation.getLatitude();
            Double longitude = mCurLocation.getLongitude();
            if (preferences != null) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("userLatitude", latitude.floatValue());
                editor.putFloat("userLongitude", longitude.floatValue());
                editor.commit();
            }
            startIntentService();
            myLoc = new LatLng(latitude, longitude);
            String distSelected = mMeterSpin.getSelectedItem().toString();
            CameraPosition Location =
                    new CameraPosition.Builder().target(myLoc)
                            .zoom(mMeterList.get(distSelected))
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(Location));

            String cateSelected = mCategorySpin.getSelectedItem().toString();
            getItemsForCategory(cateSelected, distSelected, null);
        }
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String cateSelected = mCategorySpin.getSelectedItem().toString();
        String distSelected = mMeterSpin.getSelectedItem().toString();
        getItemsForCategory(cateSelected, distSelected, null);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "My Location Button is Clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

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
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
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
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                mCurPostalCode = resultData.getString("postalCode");
                String uName = null;
                if (isAddressToBeSaved) {
                    if (mGoogleLoginService != null && mGoogleLoginService.getPersonDetails() != null &&
                            (uName = mGoogleLoginService.getPersonDetails().getUsername()) != null && mAddressOutput != null) {
                        saveAddressOutputToServer(uName, mAddressOutput);
                    }
                }
                //Toast.makeText(getApplicationContext(), getString(R.string.address_found), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveAddressOutputToServer(String userName, String curAddress) {
        JSONObject addressDetails = new JSONObject();

        /*ArrayList<String> addressFragments = new ArrayList<String>();
        for (int i = 0; i < curAddress.getMaxAddressLineIndex(); i++) {
            addressFragments.add(curAddress.getAddressLine(i));
        }*/

        String[] addressFragments = TextUtils.split(curAddress, System.getProperty("line.separator"));
        int addressLength = addressFragments.length;
        if (addressLength > 5)
            addressLength = 5;

        try {
            addressDetails.put("userName", userName);
            addressDetails.put("houseNumber", addressFragments[0]);
            int line = 1;
            for (; line < addressLength; line++) {
                addressDetails.put("addressLine" + (line), addressFragments[line]);
            }
            addressDetails.put("addressLine" + (line + 1), mCurPostalCode);
            addressDetails.put("latitude", mCurLocation.getLatitude());
            addressDetails.put("longitude", mCurLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (networkConnection != null) {
            networkConnection.saveUserAddress(addressDetails.toString());
        }
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            mCategorySpin.setVisibility(View.VISIBLE);
            mMeterSpin.setVisibility(View.VISIBLE);
            mInText.setVisibility(View.VISIBLE);
            mDistText.setVisibility(View.VISIBLE);
            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_open_search));
            mSearchByLocationAction.setVisible(false);
            mSearchByCategoryAction.setVisible(false);

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            mCategorySpin.setVisibility(View.GONE);
            mMeterSpin.setVisibility(View.GONE);
            mInText.setVisibility(View.GONE);
            mDistText.setVisibility(View.GONE);

            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            String[] exampleCategory = getResources().getStringArray(R.array.category_example);
            int category_selected = mCategorySpin.getSelectedItemPosition();
            String category = mCategorySpin.getSelectedItem().toString();
            edtSeach.setHint(category + " Eg: " + exampleCategory[category_selected]);

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(edtSeach.getText().toString());
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_search));
            mSearchByLocationAction.setVisible(true);
            mSearchByCategoryAction.setVisible(true);

            isSearchOpened = true;
        }
    }

    private void doSearch(String searchText) {
        //
        if (mSearchByLocationAction.isChecked()) {
            //TODO : Search by Location
        }

        mCategorySpin.setVisibility(View.VISIBLE);
        mMeterSpin.setVisibility(View.VISIBLE);
        mInText.setVisibility(View.VISIBLE);
        mDistText.setVisibility(View.VISIBLE);
        //hides the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        edtSeach.setVisibility(View.GONE);

        mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_open_search));
        mSearchByLocationAction.setVisible(false);
        mSearchByCategoryAction.setVisible(false);

        isSearchOpened = false;

        didGetItems = false;
        String cateSelected = mCategorySpin.getSelectedItem().toString();
        String distSelected = mMeterSpin.getSelectedItem().toString();
        getItemsForCategory(cateSelected, distSelected, searchText);

    }
}
