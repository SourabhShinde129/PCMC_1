package com.technine.pcmc_1.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.technine.pcmc_1.R;


public class ConnectionDetector {

    private Context _context;
    private boolean is_showing = false;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

   /* public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }*/
   public boolean isConnectingToInternet() {
       ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
       if (activeNetwork != null) { // connected to the internet
           if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
               return true;
           } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
               return true;
           }
       } else {
           // not connected to the internet
       }
       return false;
   }

    public void showAlertDialog(Context context, Boolean status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(context.getString(R.string.str_internet_title));
        alertDialog.setMessage(context.getString(R.string.str_internet_message));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                is_showing = false;
            }
        });
        if (!is_showing)
            alertDialog.show();
        is_showing = true;
    }
}