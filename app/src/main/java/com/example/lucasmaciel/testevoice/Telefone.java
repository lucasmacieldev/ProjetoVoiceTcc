package com.example.lucasmaciel.testevoice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Telefone extends AppCompatActivity {

    RecyclerView rvContacts;
    TextToSpeech textToSpeech;
    private String LOG_TAG = "VoiceRecognitionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_all_contacts);
        View decorView = getWindow ().getDecorView ();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage (Locale.getDefault ());
                }
            }
        });

        rvContacts = (RecyclerView) findViewById (R.id.rvContacts);

        getAllContacts ();
    }


    private void getAllContacts() {
        final List<ContactVO> contactVOList = new ArrayList ();
        ContactVO contactVO;

        ContentResolver contentResolver = getContentResolver ();
        Cursor cursor = contentResolver.query (ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount () > 0) {
            while (cursor.moveToNext ()) {

                int hasPhoneNumber = Integer.parseInt (cursor.getString (cursor.getColumnIndex (ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString (cursor.getColumnIndex (ContactsContract.Contacts._ID));
                    String name = cursor.getString (cursor.getColumnIndex (ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new ContactVO ();
                    contactVO.setContactName (name);

                    Cursor phoneCursor = contentResolver.query (
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext ()) {
                        String phoneNumber = phoneCursor.getString (phoneCursor.getColumnIndex (ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactVO.setContactNumber (phoneNumber);
                    }

                    phoneCursor.close ();

                    Cursor emailCursor = contentResolver.query (
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext ()) {
                        String emailId = emailCursor.getString (emailCursor.getColumnIndex (ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add (contactVO);
                }

            }


            AllContactsAdapter contactAdapter = new AllContactsAdapter (contactVOList, getApplicationContext ());
            rvContacts.setLayoutManager (new GridLayoutManager (this, 2, GridLayoutManager.VERTICAL, false));
            rvContacts.setAdapter (contactAdapter);

            rvContacts.addOnItemTouchListener (
                    new RecyclerItemClickListener (this, rvContacts, new RecyclerItemClickListener.OnItemClickListener () {
                        @Override
                        public void onItemClick(View view, int position) {
                            String nome = "Ligando para " + contactVOList.get (position).getContactName().toString ();
                            String telefone = contactVOList.get (position).getContactNumber ().toString ();
                            Toast.makeText (getApplicationContext (), nome, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (nome, TextToSpeech.QUEUE_FLUSH, null);

                            Intent chamada = new Intent (Intent.ACTION_CALL);
                            //pega a posição da pessoa

                            chamada.setData (Uri.parse ("tel:" + telefone));
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                            if (ActivityCompat.checkSelfPermission (getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity (chamada);
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            String nome = contactVOList.get(position).getContactName().toString();
                            String telefone = contactVOList.get(position).getContactNumber().toString();
                            String falar = "nome do contato "+nome+" e o é telefone "+telefone;
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    })
            );
        }

    }

    protected void onPause() {
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
}