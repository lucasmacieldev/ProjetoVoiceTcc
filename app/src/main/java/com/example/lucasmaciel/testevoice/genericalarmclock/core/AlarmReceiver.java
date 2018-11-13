package com.example.lucasmaciel.testevoice.genericalarmclock.core;

import android.app.Activity;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
/**
 * Created by cezar on 12/5/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(final Context context, Intent intent)
    {
        ComponentName comp = new ComponentName(context.getPackageName(),AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

}