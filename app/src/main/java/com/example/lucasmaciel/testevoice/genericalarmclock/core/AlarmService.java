package com.example.lucasmaciel.testevoice.genericalarmclock.core;

import android.app.IntentService;
import android.content.Intent;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.Alarm;
import com.example.lucasmaciel.testevoice.genericalarmclock.ui.WakeUpTheUserActivity;

import java.util.Calendar;

import io.realm.Realm;

/**
 * Created by cezar on 12/5/16.
 */
public class AlarmService  extends IntentService
{
    public AlarmService()
    {
        super("AlarmService");
    }
    @Override
    public void onHandleIntent(Intent intent)
    {
        Calendar vc_cal = Calendar.getInstance();
        Alarm vo_Alarm = new Alarm ();
        if(vo_Alarm.recuperaAlarmesParaHoje(vc_cal.get(Calendar.DAY_OF_WEEK),
                                            vc_cal.get(Calendar.HOUR_OF_DAY),
                                            vc_cal.get(Calendar.MINUTE),
                                            Realm.getDefaultInstance()) != null)
        {
            sendNotification();
        }
    }
    private void sendNotification()
    {
        Intent principalServiceIntent = new Intent (this,WakeUpTheUserActivity.class);
        principalServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(principalServiceIntent);
    }
}
