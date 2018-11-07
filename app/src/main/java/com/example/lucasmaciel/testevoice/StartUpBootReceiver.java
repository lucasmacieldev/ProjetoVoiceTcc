package com.example.lucasmaciel.testevoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartUpBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("startuptest", "StartUpBootReceiver BOOT_COMPLETED");
            Intent j = new Intent (context, MainActivity.class);
            j.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity (j);
        }
    }
}