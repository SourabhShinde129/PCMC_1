package com.technine.pcmc_1.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.technine.pcmc_1.MainActivity;

import java.util.HashMap;


public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "LandandEstate";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // public static final String KEY_USERNAME = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // ID (make variable public to access from outside)
    public static final String KEY_ID = "key";


    public static final String KEY_No = "number";

    // ROLE (make variable public to access from outside)
    public static final String KEY_ROLE = "role";

    // ID (make variable public to access from outside)
    // public static final String KEY_FB_ID = "fb_id";

    // ICHECK LOGIN WITH
    public static final String KEY_FACEBOOK_LOGIN = "true";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String key, String name) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_ID, key);
        editor.putString(KEY_NAME, name);


        // Storing name in pref
     // editor.putString(KEY_PASSWORD, password);
        // editor.putString(KEY_USERNAME, email);
       // editor.putString(KEY_EMAIL, email);
        // Storing user id

        // Storing email in pref
        // commit changes
        editor.commit();
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    /**
     * Check login method wil check user login status If false it will redirect
     * user to login page Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent in = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(in);
        }

    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        // user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user id
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user fb_id
        // user.put(KEY_FB_ID, pref.getString(KEY_FB_ID, null));

        // user id
        user.put(KEY_FACEBOOK_LOGIN, pref.getString(KEY_FACEBOOK_LOGIN, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // // Closing all the Activities
        // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //
        // // Add new Flag to start new Activity
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
