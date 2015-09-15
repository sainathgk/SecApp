package com.connection.rentalapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sainath on 20-08-2015.
 */
public class PersonProfileDetails {
    private String mUsername = null;
    private String mPassword = null;
    private String mFirstName = null;
    private String mLastName = null;
    private String mEmailId = null;
    private String mMobileNumber = null;
    private String mOfficeNumber = null;
    private String mInitials = null;
    private String mGender = null;
    private Long mBirthDate = null;
    private String mImage = null;
    JSONObject userProfile = new JSONObject();

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getEmailId() {
        return mEmailId;
    }

    public void setEmailId(String mEmailId) {
        this.mEmailId = mEmailId;
    }

    public String getMobileNumber() {
        return mMobileNumber;
    }

    public void setMobileNumber(String mMobileNumber) {
        this.mMobileNumber = mMobileNumber;
    }

    public String getOfficeNumber() {
        return mOfficeNumber;
    }

    public void setOfficeNumber(String mOfficeNumber) {
        this.mOfficeNumber = mOfficeNumber;
    }

    public String getInitials() {
        return mInitials;
    }

    public void setInitials(String mInitials) {
        this.mInitials = mInitials;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String mGender) {
        this.mGender = mGender;
    }

    public Long getBirthDate() {
        return mBirthDate;
    }

    public void setBirthDate(Long mBirthDate) {
        this.mBirthDate = mBirthDate;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public String toJSONString() {
        try {
            userProfile.put("userName", getUsername());
            userProfile.put("password", getPassword());
            userProfile.put("firstName", getFirstName());
            userProfile.put("lastName", getLastName());
            userProfile.put("initials", getInitials());
            userProfile.put("birthDate", getBirthDate());
            userProfile.put("gender", getGender());
            userProfile.put("profileImage", getImage());
            userProfile.put("emailId", getEmailId());
            userProfile.put("mobileNumber", getMobileNumber());
            userProfile.put("officeNumber", getOfficeNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userProfile.toString();
    }
}
