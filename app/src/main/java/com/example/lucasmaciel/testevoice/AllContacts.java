package com.example.lucasmaciel.testevoice;


import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lucasmaciel.testevoice.genericalarmclock.alarmefunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllContacts extends AppCompatActivity implements RecognitionListener {

    RecyclerView rvContacts;
    TextToSpeech textToSpeech;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private Button btnVoltar, cadContato;
    private Intent recognizerIntent;
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    private TextView returnedText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
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
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        toggleButton = (ToggleButton) findViewById (R.id.btnFalar);

        speech = SpeechRecognizer.createSpeechRecognizer (this);
        speech.setRecognitionListener (this);
        recognizerIntent = new Intent (RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault ());
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName ());
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    speech.startListening (recognizerIntent);
                } else {
                    speech.stopListening ();
                }
            }
        });

        textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage (Locale.getDefault ());
                }
            }
        });

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);

        getAllContacts();

        btnVoltar = (Button) findViewById (R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent (getApplicationContext (), MainActivity.class);
                startActivity (j);
            }
        });

        btnVoltar.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                String falar = "Voltar";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        cadContato = (Button) findViewById (R.id.btnCadCont);
        cadContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent (getApplicationContext (), cadastrarcontato.class);
                startActivity (j);
            }
        });

        cadContato.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                String falar = "Cadastrar novo contato";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

    }


    private void getAllContacts() {
        final List<ContactVO> contactVOList = new ArrayList();
        ContactVO contactVO;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new ContactVO();
                    contactVO.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactVO.setContactNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add(contactVO);
                }

            }


            AllContactsAdapter contactAdapter = new AllContactsAdapter(contactVOList, getApplicationContext());
            rvContacts.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
            rvContacts.setAdapter(contactAdapter);

            rvContacts.addOnItemTouchListener(
                    new RecyclerItemClickListener(this, rvContacts, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Toast.makeText(getApplicationContext(), "Click Normal", Toast.LENGTH_SHORT).show();

                            String name = contactVOList.get (position).getContactName ();
                            String phone = contactVOList.get (position).getContactNumber ();

                            Intent intent = new Intent(AllContacts.this, detailcontato.class);
                            Bundle bundle = new Bundle();

                            bundle.putString("nomecontato", name);
                            bundle.putString("telefone", phone);
                            intent.putExtras(bundle);

                            startActivity(intent);
                            /* DELETAR CONTATO DO CELULAR
                            String name = contactVOList.get (position).getContactName ();
                            String phone = contactVOList.get (position).getContactNumber ();

                            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
                            Cursor cur = getApplication ().getContentResolver().query(contactUri, null, null, null, null);
                            try {
                                if (cur.moveToFirst()) {
                                    do {
                                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                                            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                                            getApplication ().getContentResolver().delete(uri, null, null);

                                        }

                                    } while (cur.moveToNext());
                                }

                            } catch (Exception e) {
                                System.out.println(e.getStackTrace());
                            } finally {
                                cur.close();
                            }*/

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            String nome = contactVOList.get (position).getContactName ();
                            String telefone = contactVOList.get (position).getContactNumber ();
                            String falar = "Nome do contato " + nome + " e telefone " + telefone;
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                        }


                    })
            );


        }

    }
    protected void onPause() {
        if (speech != null) {
            speech.destroy ();
            Log.i (LOG_TAG, "destroy");
        }

        if (textToSpeech != null) {
            textToSpeech.stop ();
            textToSpeech.shutdown ();
        }
        super.onPause ();
    }

    public void onBeginningOfSpeech() {
        Log.i (LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i (LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i (LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i (LOG_TAG, "onEndOfSpeech");
        toggleButton.setChecked (false);

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText (errorCode);
        Log.d (LOG_TAG, "Falhou " + errorMessage);
        returnedText.setText (errorMessage);
        toggleButton.setChecked (false);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Erro no audio";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Erro no lado do cliente";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Permissões insuficiente";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Erro de internet";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Sem internet";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "Não encontrado nenhuma frase";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "Serviço ocupado, tente outra vez.";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "Erro de servidor";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "Nenhuma entrada de fala";
                break;
            default:
                message = "Não entendi, por favor, tente novamente.";
                break;
        }
        return message;
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i (LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i (LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i (LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i (LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList (SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        for (int i = 0; i < matches.size (); i++) {
            String textGet = matches.get(i).toString().toLowerCase();
                if(textGet.equals ("adicionar") || textGet.equals ("novo") || textGet.equals ("novo contato") || textGet.equals ("adicionar contato")){
                    String falar = "Novo contato";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    Intent j = new Intent (this, cadastrarcontato.class);
                    startActivity (j);
                }else if(textGet.equals ("Procurar contato")){
                    comandoVoz (matches);
                }

        }
    }



    public void comandoVoz(ArrayList matches) {
        String textGet = "";
        Intent j;
        String falar = "";

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

            int i, r;
            String nomeRetuned;

            Boolean serachNotFound = false;

            for (i = 0; i < matches.size (); i++) {
                textGet = matches.get(i).toString().toLowerCase();

                for(r = 0; r < contactVOList.size (); r++){
                    contactVO = contactVOList.get (r);
                    nomeRetuned = contactVO.getContactName().toLowerCase();

                    if (nomeRetuned.contains(textGet)) {
                        //Realizar alguma ação
                    }
                    if(serachNotFound){
                        break;
                    }
                }
                if(serachNotFound){
                    break;
                }
            }

            if(serachNotFound == false){
                String encontrado = "Contato não encontrado, tente outro";
                Toast.makeText (getApplicationContext(), encontrado, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (encontrado, TextToSpeech.QUEUE_FLUSH, null);
            }
        }

    }
}

