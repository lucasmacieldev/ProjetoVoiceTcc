package com.example.lucasmaciel.testevoice;
import android.Manifest;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
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

import static com.example.lucasmaciel.testevoice.MainActivity.getErrorText;

public class CallPhone extends AppCompatActivity implements TextToSpeech.OnInitListener,RecognitionListener,View.OnClickListener {

    private Intent recognizerIntent;
    RecyclerView rvContacts;
    TextToSpeech textToSpeech;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    private int[] numbotoes = {R.id.but0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.butasteristico, R.id.buthashtag};
    TextView textView;
    private String tag = Activity.class.getSimpleName();
    private TextToSpeech tts = null;
    private Button botao0 = null;
    private Button btnVoltar, btnLigar, btnApagarNumero;
    private boolean ultnumero;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_phonecall);
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

        tts = new TextToSpeech(this, this);

        textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage (Locale.getDefault ());
                }
            }
        });


        SharedPreferences settings = getSharedPreferences("ConfigVoz", 0);
        boolean vozenable = settings.getBoolean("voz", false);

        if(vozenable){
            textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        String falar = "Tela de teclado numérico aberta";
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                        falar = "Esta tela tem o botão voltar na parte superior e o botão de realizar ligação na parte inferior";
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                        falar = "De um clique simples para inserir os digitos do telefone ou um longo para escutar onde esta apertando";
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                    } else {
                        Log.e("TTS", "Initilization Failed!");
                    }

                }
            });
        }
        this.textView = (TextView) findViewById(R.id.textView);
        numerosclique();
        falarnumero();

        btnVoltar = (Button) findViewById (R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String falar = "Voltando";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                Intent j = new Intent (getApplicationContext (), Telefone.class);
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
        btnApagarNumero = (Button) findViewById (R.id.btnApagarNumero);
        btnApagarNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telefone = textView.getText ().toString ();
                int tamanho = telefone.length ();
                if(tamanho == 0){
                    String falar = "Voce não inseriu nenhum dígito ainda";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                }else{
                    String newValue = telefone.substring(0, tamanho-1);
                    String falar = "Apagando";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    textView.setText (newValue);
                }
            }
        });

        btnApagarNumero.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                String falar = "Apagar último dígito";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        btnLigar = (Button) findViewById (R.id.btnLigar);
        btnLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String falar = "Ligando para o numero digitado";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                Intent chamada = new Intent (Intent.ACTION_CALL);
                //pega a posição da pessoa
                String telefone = textView.getText ().toString ();
                chamada.setData (Uri.parse ("tel:" + telefone));
                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                if (ActivityCompat.checkSelfPermission (getApplicationContext (), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                startActivity (chamada);
            }
        });

        btnLigar.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                String falar = "Ligar";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

    }
    public void onClick(View v){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    public void speakNow(View v) {
        Log.i(tag, "speakNow [" + botao0.getText().toString() + "]");
        tts.speak(botao0.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }


    public void onInit(int status) {
        Log.i(tag, "onInit [" + status + "]");
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

    public void onResults(Bundle results) {

    }

    private void numerosclique() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                textView.append(button.getText());

                String falar = button.getText().toString();
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

            }
        };
        for (int id : numbotoes) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void falarnumero() {
        View.OnLongClickListener listener = new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                Button button = (Button) v;
                String falar = button.getText().toString();
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return false;
            }
        };
        for (int id : numbotoes) {
            findViewById(id).setOnLongClickListener (listener);
        }
    }
}
