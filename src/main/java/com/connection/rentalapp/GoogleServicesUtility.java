package com.connection.rentalapp;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by Sainath on 20-08-2015.
 */
public class GoogleServicesUtility {

    private static final String TAG = GoogleServicesUtility.class.toString();
    private GoogleApiClient mGoogleLoginClient = null;
    private static GoogleServicesUtility mGoogleService = null;
    private GoogleServicesListener mGoogleServiceCb = null;
    private PersonProfileDetails userProfile = null;
    private static final int RC_SIGN_IN = 0;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    public void setGoogleLoginClient(GoogleApiClient client) {
        mGoogleLoginClient = client;
    }

    public interface GoogleServicesListener {
        void onProfileFetch(PersonProfileDetails profile);
    }

    private GoogleServicesUtility() {
        /*mGoogleLoginClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) mContext)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) mContext)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();*/
        userProfile = new PersonProfileDetails();
    }

    public static GoogleServicesUtility getGoogleInstance() {
        if (mGoogleService == null) {
            mGoogleService = new GoogleServicesUtility();
        }
        return mGoogleService;
    }

    public void setGoogleServiceListener(GoogleServicesListener listener) {
        mGoogleServiceCb = listener;
    }

    public GoogleApiClient getGoogleLoginClient() {
        return mGoogleLoginClient;
    }

    public void loginConnect() {
        if (mGoogleLoginClient != null) {
            mGoogleLoginClient.connect();
            if (userProfile == null) {
                userProfile = new PersonProfileDetails();
            }
        }
    }

    public void loginDisconnect() {
        if (mGoogleLoginClient != null && mGoogleLoginClient.isConnected())
            mGoogleLoginClient.disconnect();
    }

    public boolean logout() {
        if (mGoogleLoginClient != null && mGoogleLoginClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleLoginClient);
            mGoogleLoginClient.disconnect();
            userProfile = null;
            return true;
        }
        return false;
    }

    public PersonProfileDetails getPersonDetails() {
        return userProfile;
    }

    /*@Override
    public void onConnected(Bundle bundle) {
        Person currentPerson = null;
        mShouldResolve = false;
        String gName = Plus.AccountApi.getAccountName(mGoogleLoginClient);

        Plus.PeopleApi.loadVisible(mGoogleLoginClient, null).setResultCallback(this);
        if (Plus.PeopleApi.getCurrentPerson(mGoogleLoginClient) != null) {
            currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleLoginClient);

            mGoogleServiceCb.onProfileFetch(makePersonProfile(currentPerson, gName));
        } else {
            Toast.makeText(mContext, "Problem occurred in People Api", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
// Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(mActivity, RC_SIGN_IN);
                    //mGoogleLoginClient.connect();
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleLoginClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

    }*/

    /*public void setGButtonClicked(boolean isClicked) {
        mShouldResolve = isClicked;
    }*/

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;

            loginConnect();
        }
    }*/

    public PersonProfileDetails makePersonProfile(Person personProfile, String personEmailId, String phoneNumber) {
        String userName = personEmailId.substring(0, personEmailId.indexOf('@'));

        if (userProfile == null) {
            userProfile = new PersonProfileDetails();
        }

        userProfile.setEmailId(personEmailId);
        userProfile.setFirstName(personProfile.getDisplayName());
        userProfile.setLastName(personProfile.getDisplayName());
        userProfile.setGender(personProfile.getGender() == 1 ? "F" : "M");
        userProfile.setInitials("Mr.");
        userProfile.setUsername(userName);
        userProfile.setPassword(userName);
        userProfile.setImage(personProfile.getImage().getUrl());
        userProfile.setMobileNumber(phoneNumber);
        userProfile.setOfficeNumber(phoneNumber);

        if (personProfile.hasBirthday())
            userProfile.setBirthDate(Long.parseLong(personProfile.getBirthday()));
        else
            userProfile.setBirthDate(System.currentTimeMillis());

        return userProfile;
    }
}
