package com.example.samsungapp.network;

import android.os.AsyncTask;

public class NetworkTread extends AsyncTask<Void, Void, Boolean> {

    public Boolean doInBackground(Void... params) {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }


}
