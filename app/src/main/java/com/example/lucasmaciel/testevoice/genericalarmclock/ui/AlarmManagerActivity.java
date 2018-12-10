package com.example.lucasmaciel.testevoice.genericalarmclock.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lucasmaciel.testevoice.AllContacts;
import com.example.lucasmaciel.testevoice.MainActivity;
import com.example.lucasmaciel.testevoice.R;
import com.example.lucasmaciel.testevoice.genericalarmclock.adapters.WeekListAdapter;
import com.example.lucasmaciel.testevoice.genericalarmclock.alarmefunc;
import com.example.lucasmaciel.testevoice.genericalarmclock.core.AlarmReceiver;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.Alarm;
import com.example.lucasmaciel.testevoice.genericalarmclock.model.WeekInformation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by cezar on 12/8/16.
 */
public class AlarmManagerActivity extends AppCompatActivity implements RecognitionListener
{
    private Alarm vo_AlarmGeral;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private TimePicker clock;
    private ListView lstWeekDays;
    private WeekListAdapter adapterWeek;
    private int etapa = 0;
    private int vi_hora;
    private int vi_minuto;
    private Realm realm;
    private ToggleButton toggleButton;
    private Button btnGerenciar;
    private Button btnExcluir, btnVoltar;
    private Bundle bundle;
    private int vi_Alarme;
    private SpeechRecognizer speech = null;
    TextToSpeech textToSpeech;
    private TextToSpeech tts = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private String horario="";
    private String minutos="";
    private String dias="";
    private int ponteiroDia = 0;
    boolean[] ab_diasMarcados = new boolean[7];
    boolean PararApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);
        btnGerenciar = (Button) findViewById(R.id.btnAvancar);
        btnExcluir = (Button) findViewById(R.id.btnExcluir);

        preparaBancoDados();

        bundle = getIntent().getExtras();
        vi_Alarme = bundle.getInt("id_alarme");


        textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage (Locale.getDefault ());
                }
            }
        });

        toggleButton = (ToggleButton) findViewById (R.id.btnFalar);

        speech = SpeechRecognizer.createSpeechRecognizer (this);
        speech.setRecognitionListener((RecognitionListener) this);

        speech = SpeechRecognizer.createSpeechRecognizer (this);
        speech.setRecognitionListener ((RecognitionListener) this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
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
                        if(vi_Alarme == 0) {
                            String falar = "Fale excluir para apagar este alarme ou voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        }else if (horario.equals ("")) {
                            String falar = "Fale em qual hora deve despertar ou fale voltar após o sinal";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (minutos.equals ("")) {
                            String falar = "Fale em qual os minutos deve despertar ou fale voltar após o sinal";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (ponteiroDia == 0) {
                            String falar = "Fale sim ou não após o sinal se você deseja que este despertador funcione de Domingo ou fale voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (5500);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (ponteiroDia == 1) {
                            String falar = "Fale sim ou não após o sinal se você deseja que este despertador funcione de Segunda ou fale voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (5500);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (ponteiroDia == 2) {
                            String falar = "Fale sim ou não após o sinal se você deseja que este despertador funcione de Terça ou fale voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (5500);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (ponteiroDia == 3) {
                            String falar = "Fale sim ou não após o sinal se você deseja que este despertador funcione de Quarta ou fale voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (5500);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (ponteiroDia == 4) {
                            String falar = "Fale sim ou não após o sinal se você deseja que este despertador funcione de Quinta ou fale voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (5500);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (ponteiroDia == 5) {
                            String falar = "Fale sim ou não após o sinal se você deseja que este despertador funcione de Sexta ou fale voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (5500);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                        } else if (ponteiroDia == 6) {
                            String falar = "Fale sim ou não após o sinal se você deseja que este despertador funcione de Sabado ou fale voltar";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (5500);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                            ponteiroDia = 10;

                        } else {
                            return;
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

        // Carrega o relógio
        clock = (TimePicker) findViewById(R.id.clock);
        clock.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (vi_Alarme == -1)
        {
            btnExcluir.setVisibility(View.GONE);
        }else {
            btnExcluir.setVisibility(View.VISIBLE);
            vo_AlarmGeral = realm.where(Alarm.class).equalTo("_id",vi_Alarme).findFirst();
            if(vo_AlarmGeral != null) {
                clock.setCurrentHour(vo_AlarmGeral.get_hora());
                clock.setCurrentMinute(vo_AlarmGeral.get_minutos());
            }
            btnGerenciar.setVisibility (View.GONE);
        }


        // Cria a lista de dias da semana.
        lstWeekDays = (ListView) findViewById(R.id.lstDias);
        adapterWeek = new WeekListAdapter(this, WeekInformation.get_vetDiasSemana());
        lstWeekDays.setAdapter(adapterWeek);

        btnVoltar = (Button) findViewById (R.id.btnVoltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String falar = "Voltando";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                try {
                    Thread.sleep (2000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                Intent j = new Intent (getApplicationContext (), alarmefunc.class);
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

        btnExcluir.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                String falar = "Excluir alarme";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        btnGerenciar.setOnLongClickListener(new View.OnLongClickListener() {
        public boolean onLongClick(View v) {

            String falar = btnGerenciar.getText ().toString ();
            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
            return true;
            }
    });

        SharedPreferences settings = getSharedPreferences("ConfigVoz", 0);
        boolean vozenable = settings.getBoolean("voz", false);

        if(vozenable) {
            if (vi_Alarme == 0) {
                    textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                String falar = "Tela de exclusão de alarme aberta";
                                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                                try {
                                    Thread.sleep (2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace ();
                                }
                                falar = "Pressione o botão no inferior da tela e fale excluir alarme";
                                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                                try {
                                    Thread.sleep (6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace ();
                                }

                                falar = "E para voltar, pressione a parte superior da tela ou fale voltar no microfone";
                                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                            } else {
                                Log.e ("TTS", "Initilization Failed!");
                            }

                        }
                    });
                }else {
                textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            String falar = "Tela de cadastro de alarme aberta";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                            try {
                                Thread.sleep (2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }
                            falar = "Pressione o botão no inferior da tela e siga as instruções para adicionar um novo alarme";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                            try {
                                Thread.sleep (6000);
                            } catch (InterruptedException e) {
                                e.printStackTrace ();
                            }

                            falar = "E para voltar, pressione a parte superior da tela ou fale voltar no microfone";
                            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                        } else {
                            Log.e ("TTS", "Initilization Failed!");
                        }

                    }
                });
            }
            }
        }

    private void preparaBancoDados()
    {
        try {
            realm = Realm.getDefaultInstance();
        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                    exc.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void gerenciaAlarme(View v)
    {
        if(etapa == 0)
        {
            vi_hora =  clock.getCurrentHour();
            vi_minuto = clock.getCurrentMinute();
            alternaView();
            etapa++;
        }else{
            criaAlarme();
            alternaView();
            etapa = 0;
        }
    }
    public void cadastrar(final int a_hora, final int a_minuto, final boolean[] ab_diasMarcados)
    {
        realm.beginTransaction();
        criaIdSeNecessario();
        try
        {
            vo_AlarmGeral.set_hora(a_hora);
            vo_AlarmGeral.set_minutos(a_minuto);
            vo_AlarmGeral.set_diasMarcadosJson(ab_diasMarcados);
            realm.copyToRealmOrUpdate(vo_AlarmGeral);
        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                            exc.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        realm.commitTransaction();

        String falar = "Alarme cadastrado com sucesso!";
        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
        try {
            Thread.sleep (3000);
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }

        this.finish();
    }
    private void criaIdSeNecessario() {
        int id = 0;
        if(vi_Alarme == -1)
        {
            RealmResults<Alarm> lastAlarms = realm.where(Alarm.class).findAll();
            if (!lastAlarms.isEmpty()) id = lastAlarms.last().get_id() + 1;
            final int finalId = id;
            vo_AlarmGeral = new Alarm();
            vo_AlarmGeral.set_id(finalId);
        }
    }

    public void excluiAlarme(View v)
    {
        try
        {
            realm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    for (Alarm vo_alarm:realm.where(Alarm.class).equalTo("_id",vi_Alarme).findAll())
                          vo_alarm.deleteFromRealm();

                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.alarmeExcluido),
                            Toast.LENGTH_SHORT).show();
                    textToSpeech.speak ("Alarme excluído com sucesso", TextToSpeech.QUEUE_FLUSH, null);try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                }
            });
            this.finish();
        }catch (Exception exc){
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.erroGerencia) +
                    exc.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void criaAlarme()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,vi_hora);
        calendar.set(Calendar.MINUTE,vi_minuto);
        Intent myIntent = new Intent (AlarmManagerActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmManagerActivity.this, 0, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
        cadastrar(vi_hora,vi_minuto,adapterWeek.getVetMarcados());
    }
    private void alternaView()
    {
        if(etapa == 0)
        {
            lstWeekDays.setVisibility(View.VISIBLE);
            clock.setVisibility(View.GONE);
            btnGerenciar.setText(getResources().getString(R.string.concluir));
        }else{
            lstWeekDays.setVisibility(View.GONE);
            clock.setVisibility(View.VISIBLE);
            btnGerenciar.setText(getResources().getString(R.string.avancar));
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
        //returnedText.setText (errorMessage);
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

        if(vi_Alarme == 0){
            if(matches.get (0).equals ("excluir")){
                try
                {
                    realm.executeTransaction(new Realm.Transaction()
                    {
                        @Override
                        public void execute(Realm realm)
                        {
                            for (Alarm vo_alarm:realm.where(Alarm.class).equalTo("_id",vi_Alarme).findAll())
                                vo_alarm.deleteFromRealm();

                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.alarmeExcluido),
                                    Toast.LENGTH_SHORT).show();
                            textToSpeech.speak ("Alarme excluído com sucesso", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    });
                    this.finish();
                }catch (Exception exc){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.erroGerencia) +
                                    exc.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }else if(matches.get (0).equals ("voltar")){
                String falar = "Voltando";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                try {
                    Thread.sleep (2000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }

                Intent j = new Intent (getApplicationContext (), alarmefunc.class);
                startActivity (j);
                onPause ();
            }else{
                String falar = "Comando não encontrado, tente outro";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
            }
        }


            if (ponteiroDia == 10) {
                int horaConvert = Integer.parseInt (horario);
                int minutosConvert = Integer.parseInt (minutos);
                boolean ok = true;
                String resposta = matches.get (0);
                if (resposta.equalsIgnoreCase ("sim")) {
                    ab_diasMarcados[6] = true;
                } else if (resposta.equalsIgnoreCase ("não")){
                    ab_diasMarcados[6] = false;
                } else if (resposta.equalsIgnoreCase ("voltar")){
                    String falar = "voltando";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    try {
                        Thread.sleep (3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                    Intent j = new Intent (getApplicationContext (), alarmefunc.class);
                    startActivity (j);
                    onPause ();
                }else{
                    String falar = "Fale apenas sim, não ou voltar";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    try {
                        Thread.sleep (3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }

                    ok = false;
                }

                if(ok== true){
                    cadastrar (horaConvert, minutosConvert, ab_diasMarcados);
                }else{
                    ponteiroDia = 6;
                    toggleButton.setChecked (true);
                }

            }else {
                for (int i = 0; i < matches.size (); i++) {
                    if (horario.equals ("")) {
                        horario = matches.get (i);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }

                        if(!verificaValorFalado(matches.get (0))){
                            horario = "";
                        }

                        toggleButton.setChecked (true);

                        return;
                    } else if (minutos.equals ("")) {
                        minutos = matches.get (i);
                        String valorNomeFalado = matches.get (0);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                        if(!verificaValorFalado(matches.get (0))){
                            minutos = "";
                        }

                        toggleButton.setChecked (true);

                        return;
                    }
                }
                if (ponteiroDia <= 5) {
                    String resposta = matches.get (0);
                    boolean ok = true;
                    if (resposta.equalsIgnoreCase ("sim")) {
                        ab_diasMarcados[ponteiroDia] = true;
                    } else if (resposta.equalsIgnoreCase ("não")){
                        ab_diasMarcados[ponteiroDia] = false;
                    } else if (resposta.equalsIgnoreCase ("voltar")){
                        String falar = "voltando";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                        Intent j = new Intent (getApplicationContext (), alarmefunc.class);
                        startActivity (j);
                        onPause ();
                        ok = true;
                    }else{
                        String falar = "Fale apenas sim, não ou voltar";
                        Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                        textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep (3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }

                        ok = false;
                    }

                    if(ok == false){
                        toggleButton.setChecked (true);
                    }else {
                        ponteiroDia++;
                        toggleButton.setChecked (true);
                    }
                }
            }
    }
    private boolean verificaValorFalado(String s) {
        String valorfalado = s;
        String[] parts = valorfalado.split ("");


        boolean sonumeros = false;

        if(valorfalado.equals ("voltar")){
                String falar = "voltando";
                Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
            Intent j = new Intent (getApplicationContext (), alarmefunc.class);
            startActivity (j);
                onPause ();
            return false;
        }

        if (Character.isDigit (valorfalado.charAt (0)) == false) {
            String falar = "Você falou letras, porfavor fale apenas um numero por vez";
            Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
            textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
            try {
                Thread.sleep (3000);
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
            return false;
        }



        return true;
    }

}
