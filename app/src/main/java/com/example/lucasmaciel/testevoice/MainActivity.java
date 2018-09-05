package com.example.lucasmaciel.testevoice;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognitionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener, View.OnLongClickListener {

    TextToSpeech textToSpeech;
    Button btnFalar;
    TextView txtFalar;
    Context context;
    private TextView bateriaVal;
    private final int ID_TEXTO_PARA_VOZ = 100;
    private TextView returnedText, horariodatatxtVar;
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private CardView contato, ligar, clima, alarme, bateria, horadata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BATERIA
        bateriaVal = (TextView) this.findViewById (R.id.bateriaIdText);
        this.registerReceiver (this.mBatInfoReceive, new IntentFilter (Intent.ACTION_BATTERY_CHANGED));

        returnedText = (TextView) findViewById(R.id.txtFalar);
        horariodatatxtVar = (TextView) findViewById(R.id.horariodatatxt);
        toggleButton = (ToggleButton) findViewById(R.id.btnFalar);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != textToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        //Horario
        Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH)+1;
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);
        String horarioeDataval = "Data:"+dia+"/"+ mes+"/"+ano+ " Horário:" + hora + ":" + minuto;
        horariodatatxtVar.setText(horarioeDataval);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    speech.startListening(recognizerIntent);
                } else {
                    speech.stopListening();
                }
            }
        });

        contato = (CardView) findViewById(R.id.contatoId);
        ligar = (CardView) findViewById(R.id.telefoneId);
        clima = (CardView) findViewById(R.id.climaId);
        alarme = (CardView) findViewById(R.id.alarmeId);
        bateria = (CardView) findViewById(R.id.bateriaId);
        horadata = (CardView) findViewById(R.id.HorarioId);

        contato.setOnClickListener(this);
        ligar.setOnClickListener(this);
        clima.setOnClickListener(this);
        alarme.setOnClickListener(this);
        bateria.setOnClickListener(this);
        horadata.setOnClickListener(this);

        contato.setOnLongClickListener(this);
        ligar.setOnLongClickListener(this);
        clima.setOnLongClickListener(this);
        alarme.setOnLongClickListener(this);
        bateria.setOnLongClickListener(this);
        horadata.setOnLongClickListener(this);

        returnedText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String falar = "Repetindo: " + returnedText.getText().toString();
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    public void horarioEData(){
        Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH)+1;
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);
        String falar =  "Data Atual: dia: " + dia + "do mês: " + mes + " :do ano de: " + ano + " :horario atual é: " + hora + " hora e " + minuto + " minutos" ;

        String toast = "Data:"+dia+"/"+ mes+"/"+ano+ " Horário:" + hora + ":" + minuto;
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
    }

    private BroadcastReceiver mBatInfoReceive = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra (BatteryManager.EXTRA_LEVEL,0);
            bateriaVal.setText(String.valueOf (level)+"%");
        }
    };

    public void comandoVoz(ArrayList matches) {
        String textGet = "";
        Intent j;
        String falar = "";

            for (int i = 0; i <= matches.size (); i++) {
                textGet = matches.get (i).toString ();
                if (textGet.equals ("contato") || textGet.equals ("agenda") || textGet.equals ("contatos")) {
                    falar = "Abrindo contato";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    j = new Intent (this, Contato.class);
                    startActivity (j);
                    break;
                } else if (textGet.equals ("realizar ligações") || textGet.equals ("telefone") || textGet.equals ("ligar")) {
                    falar = "Abrindo telefone";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    j = new Intent (this, Telefone.class);
                    startActivity (j);
                    break;
                } else if (textGet.equals ("alarme") || textGet.equals ("despertador")) {
                    falar = "Abrindo alarme";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    j = new Intent (this, Alarme.class);
                    startActivity (j);
                    break;
                } else if (textGet.equals ("clima") || textGet.equals ("tempo") || textGet.equals ("tempo hoje")) {
                    falar = "Abrindo clima";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    j = new Intent (this, Clima.class);
                    startActivity (j);
                    break;
                } else if(textGet.equals ("bateria")) {
                    falar = "Bateria em: " + bateriaVal.getText ().toString ();
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    break;
                } else if(textGet.equals ("horario") || textGet.equals ("data") || textGet.equals ("hora") || textGet.equals ("que horas são")){
                        horarioEData();
                        break;
                }
            }
    }

    public void onClick(View v){
        Intent i;
        String falar = "";
        switch (v.getId ()){
            case R.id.contatoId : i = new Intent (this, Contato.class);startActivity(i); onPause(); break;
            case R.id.telefoneId : i = new Intent (this, Telefone.class);startActivity(i); onPause(); break;
            case R.id.climaId : i = new Intent (this, Clima.class);startActivity(i); onPause(); break;
            case R.id.alarmeId : i = new Intent (this, Alarme.class);startActivity(i); onPause(); break;
            case R.id.bateriaId :
                falar = "Bateria em: " + bateriaVal.getText().toString();
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null); break;
            case R.id.HorarioId :
                horarioEData();
                break;
            default:break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Intent i;
        String falar = "";
        switch (v.getId ()){
            case R.id.contatoId :
                falar = "Contato";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.alarmeId :
                falar = "Alarme";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.bateriaId :
                falar = "Bateria em: " + bateriaVal.getText().toString();
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.telefoneId :
                falar = "Telefone";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.climaId :
                falar = "Clima";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.HorarioId :
                horarioEData();
                break;
            default:break;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        toggleButton.setChecked(false);

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "Falhou " + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        returnedText.setText(text);

        comandoVoz(matches);
    }



    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
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


}
