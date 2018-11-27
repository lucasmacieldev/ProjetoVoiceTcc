package com.example.lucasmaciel.testevoice.genericalarmclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lucasmaciel.testevoice.MainActivity;
import com.example.lucasmaciel.testevoice.R;
import com.example.lucasmaciel.testevoice.genericalarmclock.adapters.AlarmListAdapter;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.Alarm;
import com.example.lucasmaciel.testevoice.genericalarmclock.ui.AlarmManagerActivity;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;

public class alarmefunc extends AppCompatActivity implements RecognitionListener
{
    private AlarmListAdapter adapter;
    private GridView list;
    private Realm realm;
    TextToSpeech textToSpeech;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private Button btnVoltar;
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private TextView returnedText;
    private Button button2;
    private Alarm vo_AlarmGeral;

    @Override
    public void onStart()
    {
        super.onStart();
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmefunc);


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
        textToSpeech = new TextToSpeech (getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        button2 = (Button) findViewById(R.id.button2);

        button2.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                String falar = "Adicionar novo alarme";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        returnedText = (TextView) findViewById (R.id.txtFalar);

        returnedText.addTextChangedListener (new TextWatcher () {
            public void afterTextChanged(Editable s) {
                //String falar = "Repetindo: " + returnedText.getText ().toString ();
                //Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                //textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        btnVoltar = (Button) findViewById (R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent (getApplicationContext (), MainActivity.class);
                startActivity (j);
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

        btnVoltar.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                String falar = "Voltar";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        preparaBancoDados();

        adapter = new AlarmListAdapter(this,realm.where(Alarm.class).findAll());
        list=(GridView)findViewById(R.id.lstAlarmesCadastrados);
        list.setAdapter(adapter);
        setGridViewHeightBasedOnChildren( list, 2 );
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String vs_id = (String) ((TextView) view.findViewById(R.id.txtIdAlarme)).getText();
                abreAlarme(Integer.parseInt(vs_id));
            }
        });

        //AQUI PORRA
        vo_AlarmGeral = realm.where(Alarm.class).equalTo("_hora",17).findFirst();

        list.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                String horarioAlarme = (String) ((TextView) view.findViewById(R.id.txtHorarioAlarme)).getText();
                String diasAlarme = (String) ((TextView) view.findViewById(R.id.txtDiasMarcados)).getText();
                String[] separated = diasAlarme.replace (".", " ").split (" ");
                String diasAjustados = "";
                String falar;

                for(int i = 0; i < separated.length; i++){
                    String valorDiaSemana = separated[i];
                    if(valorDiaSemana.equals ("Seg")){
                        diasAjustados += " Segunda";
                    }else if(valorDiaSemana.equals ("Ter")){
                        diasAjustados += " Terça";
                    }else if(valorDiaSemana.equals ("Qua")){
                        diasAjustados += " Quarta";
                    }else if(valorDiaSemana.equals ("Quin")){
                        diasAjustados += " Quinta";
                    }else if(valorDiaSemana.equals ("Sex")){
                        diasAjustados += " Sexta";
                    }else if(valorDiaSemana.equals ("Sab")){
                        diasAjustados += " Sabádo";
                    }else if(valorDiaSemana.equals ("Dom")){
                        diasAjustados += " Domingo";
                    }
                }

                if(diasAlarme.equals (null) || diasAlarme.equals ("")) {
                    falar = "Horário do alarme " + horarioAlarme + " sem dia definido";
                }else if(diasAlarme.equals ("Todos os dias")){
                    falar = "Horário do alarme " + horarioAlarme + " agendado para todos os dias";
                }else{
                    falar = "Horário do alarme " +horarioAlarme + " nos dias " + diasAjustados;
                }

                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return  true;
            }
        });

        SharedPreferences settings = getSharedPreferences("ConfigVoz", 0);
        boolean vozenable = settings.getBoolean("voz", false);

        if(vozenable) {
            textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        String falar = "Tela de alarme aberta";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                        falar = "Pressione o botão no inferior da tela e fale adicionar novo alarme";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                        try {
                            Thread.sleep (6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }

                        falar = "E para voltar para a tela inicial, pressione a parte superior da tela ou fale voltar no microfone";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                    } else {
                        Log.e ("TTS", "Initilization Failed!");
                    }

                }
            });
        }
    }
    private void preparaBancoDados()
    {
        try {
            Realm.init(getApplicationContext());
            realm = Realm.getDefaultInstance();

        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                            exc.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void adicionarAlarme(View v)
    {
        abreAlarme( -1);
    }
    private void abreAlarme(int id_Alarme)
    {
        Intent intentAddAlarm = new Intent (alarmefunc.this, AlarmManagerActivity.class);
        intentAddAlarm.putExtra("id_alarme",id_Alarme);
        alarmefunc.this.startActivity(intentAddAlarm);
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

        returnedText.setText (text);

        comandoVoz (matches);
    }

    public void comandoVoz(ArrayList matches) {
        String textGet = "";
        Intent j;
        String falar = "";

        for (int i = 0; i < matches.size (); i++) {
            textGet = matches.get (i).toString ().toLowerCase ();

            if (textGet.equals ("adicionar alarme") || textGet.equals ("adicionar") || textGet.equals ("novo alarme") || textGet.equals ("alarme novo")) {
                abreAlarme (-1);
            } else if (textGet.equals ("voltar")){
                 j = new Intent (getApplicationContext (), MainActivity.class);
                startActivity (j);
            }else{
                final int size = list.getChildCount();
                for(int h = 0; h < size; h++) {

                    ViewGroup gridChild = (ViewGroup) list.getChildAt(h);
                    int childSize = gridChild.getChildCount();

                    final View child = list.getChildAt (h);


                    for(int k = 0; k < childSize; k++) {
                        if( gridChild.getChildAt(k) instanceof TextView ) {
                            gridChild.getChildAt(k).setVisibility(View.GONE);
                        }
                    }
                }

            }
        }
    }

    public String returnItem(AdapterView parent, View view, int position, long id) {
        String vs_id = (String) ((TextView) view.findViewById(R.id.txtIdAlarme)).getText();
        return "";
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        if(items == 0){
           return;
        }

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);

    }
}





