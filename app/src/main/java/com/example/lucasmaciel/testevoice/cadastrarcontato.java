package com.example.lucasmaciel.testevoice;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class cadastrarcontato extends AppCompatActivity implements RecognitionListener {

    private Intent recognizerIntent;
    RecyclerView rvContacts;
    TextToSpeech textToSpeech;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    TextInputEditText nomedocontato,  telefone;
    private int valorTelefone = 0;
    private String valorTelefoneNumero="";

    private Button btnVoltar, btnCadastrarNovo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.cadastrarcontato);
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

        toggleButton = (ToggleButton) findViewById (R.id.btnFalar);

        speech = SpeechRecognizer.createSpeechRecognizer (this);
        speech.setRecognitionListener ((RecognitionListener) this);
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
                    nomedocontato = (TextInputEditText) findViewById(R.id.nomeContato);
                    telefone =(TextInputEditText) findViewById(R.id.telefonecontato);

                    //String nomeContato = nomedocontato.getText().toString ();
                    //String numeroContato = telefone.getText().toString ();

                    if(valorTelefone == 0) {
                        String falar = "Responda qual o tipo de contato sendo residencial ou celular após o sinal";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                    }

                    if(nomedocontato.getText().toString ().equals ("") && valorTelefone != 0) {
                        String falar = "Fale o nome do contato após o sinal";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }

                    }

                    if(!nomedocontato.getText().toString ().equals ("") && valorTelefone != 0) {
                        String falar = "Fale um digito do numero por vez após o sinal";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }

                    }
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


        btnVoltar = (Button) findViewById (R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent j = new Intent (getApplicationContext (), AllContacts.class);
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

        btnCadastrarNovo = (Button) findViewById (R.id.btnCadastrarNovo);
        btnCadastrarNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 nomedocontato = (TextInputEditText) findViewById(R.id.nomeContato);
                 telefone =(TextInputEditText) findViewById(R.id.telefonecontato);

                String nomeContato = nomedocontato.getText().toString ();
                String numeroContato = telefone.getText().toString ();

                if(nomeContato.equals ("") || nomeContato.equals (null)){
                    String falar = "Porfavor informar o nome do contato";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                    TextView t = (TextView) findViewById(R.id.returnErro);
                    t.setText("Porfavor informar o nome do contato");
                    t.setVisibility(View.VISIBLE);

                }else if(numeroContato.equals ("") || numeroContato.equals (null)){
                    String falar = "Porfavor informar o numero do contato";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                    TextView t = (TextView) findViewById(R.id.returnErro);
                    t.setText("Porfavor informar o numero do contato");
                    t.setVisibility(View.VISIBLE);
                }else{
                    cadastrarNovoContato(numeroContato, nomeContato);
                    TextView t = (TextView) findViewById(R.id.returnErro);
                    t.setVisibility(View.GONE);
                }


            }
        });


    }

    public void cadastrarNovoContato(String numeroContato, String nomeContato){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();
        ContentResolver contentResolver = getContentResolver();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Phone Number
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, numeroContato)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1").build());

        //Display name/Contact name
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nomeContato)
                .build());

        try {
            ContentProviderResult[] res = getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, ops);
            String falar = "Contato cadastrado com sucesso";
            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

        nomedocontato = (TextInputEditText) findViewById(R.id.nomeContato);
        telefone =(TextInputEditText) findViewById(R.id.telefonecontato);

        String nomeContato = nomedocontato.getText().toString ();
        String numeroContato = telefone.getText().toString ();


        if(valorTelefone == 0 && (valorTelefone != 8 || valorTelefone!=9)){
            String valorNomeFalado = matches.get(0);

            if(valorNomeFalado.equals ("residencial")){
                valorTelefone = 8;
            }else if(valorNomeFalado.equals ("celular")){
                valorTelefone = 9;
            }else{
                valorTelefone = 0;
            }
            try {
                Thread.sleep (3000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
            toggleButton.setChecked (true);
            return;
        }

        if(nomedocontato.getText().toString ().equals ("")){
            String valorNomeFalado = matches.get(0);
            nomedocontato.setText(valorNomeFalado);
            try {
                Thread.sleep (3000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
            toggleButton.setChecked (true);
        }else{
            if(valorTelefone != 0){
                valorTelefone--;
                valorTelefoneNumero += matches.get (0);
                telefone.setText (valorTelefoneNumero);
                if(valorTelefone == 0){
                    cadastrarNovoContato(numeroContato, nomeContato);
                    speech.stopListening ();
                }else{
                    toggleButton.setChecked (true);
                }

            }
        }
    }



}









