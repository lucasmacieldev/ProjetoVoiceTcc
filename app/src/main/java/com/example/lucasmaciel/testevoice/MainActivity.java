package com.example.lucasmaciel.testevoice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.example.lucasmaciel.testevoice.genericalarmclock.alarmefunc;
import com.example.lucasmaciel.testevoice.model1.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements RecognitionListener, View.OnClickListener, View.OnLongClickListener, LocationListener {

    final int MY_PERMISSION_REQUEST_CODE = 7171;
    GPSTracker gps;
    double lat;
    double lng;
    String city;
    TextToSpeech textToSpeech;
    Button btnFalar;
    TextView txtFalar;
    Context context;
    private TextView bateriaVal;
    private final int IDF_TEXTO_PARA_VOZ = 100;
    private TextView returnedText, horariodatatxtVar, textoLocal, txtClima;
    private ToggleButton toggleButton;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private CardView contato, ligar, clima, alarme, bateria, horadata, local, calculadora;
    LocationManager locationManager;
    String provider;

    Geocoder geocoder;
    List<Address> addresses;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

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

        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions (this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            getLocation ();
        }

        //BATERIA
        bateriaVal = (TextView) this.findViewById (R.id.bateriaIdText);
        this.registerReceiver (this.mBatInfoReceive, new IntentFilter (Intent.ACTION_BATTERY_CHANGED));

        returnedText = (TextView) findViewById (R.id.txtFalar);
        horariodatatxtVar = (TextView) findViewById (R.id.horariodatatxt);
        textoLocal = (TextView) findViewById (R.id.txtlocal);
        toggleButton = (ToggleButton) findViewById (R.id.btnFalar);
        txtClima = (TextView) findViewById (R.id.climaTxt);
        textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
            @Override
            public void onInit(int status) {
                if (status != textToSpeech.ERROR) {
                    textToSpeech.setLanguage (Locale.getDefault ());
                }
            }
        });

        //Horario
        Calendar c = Calendar.getInstance ();
        int ano = c.get (Calendar.YEAR);
        int mes = c.get (Calendar.MONTH) + 1;
        int dia = c.get (Calendar.DAY_OF_MONTH);
        int hora = c.get (Calendar.HOUR_OF_DAY);
        int minuto = c.get (Calendar.MINUTE);
        String horarioeDataval = "Data:" + dia + "/" + mes + "/" + ano + " Horário:" + hora + ":" + minuto;
        horariodatatxtVar.setText (horarioeDataval);

        speech = SpeechRecognizer.createSpeechRecognizer (this);
        speech.setRecognitionListener (this);
        recognizerIntent = new Intent (RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault ());
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName ());
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        toggleButton.setOnCheckedChangeListener (new OnCheckedChangeListener () {

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

        contato = (CardView) findViewById (R.id.contatoId);
        ligar = (CardView) findViewById (R.id.telefoneId);
        clima = (CardView) findViewById (R.id.climaId);
        alarme = (CardView) findViewById (R.id.alarmeId);
        bateria = (CardView) findViewById (R.id.bateriaId);
        horadata = (CardView) findViewById (R.id.HorarioId);
        local = (CardView) findViewById (R.id.localId);
        calculadora = (CardView) findViewById (R.id.calcId);

        contato.setOnClickListener (this);
        ligar.setOnClickListener (this);
        clima.setOnClickListener (this);
        alarme.setOnClickListener (this);
        bateria.setOnClickListener (this);
        horadata.setOnClickListener (this);
        local.setOnClickListener (this);
        calculadora.setOnClickListener (this);

        contato.setOnLongClickListener (this);
        ligar.setOnLongClickListener (this);
        clima.setOnLongClickListener (this);
        alarme.setOnLongClickListener (this);
        bateria.setOnLongClickListener (this);
        horadata.setOnLongClickListener (this);
        local.setOnLongClickListener (this);
        calculadora.setOnLongClickListener (this);

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

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    String falar = "Tela inicial";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                    try {
                        Thread.sleep (2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                    falar = "Você pode acionar o botão do microfone que fica na parte inferior da tela e falar a função que deseja, isto serve para todas as funções do aplicativo";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }

            }
        });
    }



    private void getLocation() {
        locationManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider (new Criteria (), false);


        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final android.location.Location location = locationManager.getLastKnownLocation (provider);
        if(location == null)
            Log.e("ERROR","Location is null");
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

                    try {
                        Thread.sleep (3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }

                    j = new Intent (this, AllContacts.class);
                    startActivity (j);
                    break;
                } else if (textGet.equals ("realizar ligações") || textGet.equals ("telefone") || textGet.equals ("ligar")) {
                    falar = "Abrindo telefone";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);

                    try {
                        Thread.sleep (3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }

                    j = new Intent (this, Telefone.class);
                    startActivity (j);
                    break;
                } else if (textGet.equals ("alarme") || textGet.equals ("despertador")) {
                    falar = "Abrindo alarme";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    try {
                        Thread.sleep (3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                    j = new Intent (this, alarmefunc.class);
                    startActivity (j);
                    break;
                } else if (textGet.equals ("clima") || textGet.equals ("tempo") || textGet.equals ("tempo hoje")) {
                    try {
                        gps = new GPSTracker(MainActivity.this);
                        double lat = gps.getLatitude();
                        double lng = gps.getLongitude();
                        geocoder = new Geocoder(this, Locale.getDefault());// Here 1 represent max location result to returned, by documents it recommended 1 to 5addresses = geocoder.getFromLocation (lat, lng, 1);
                        addresses = geocoder.getFromLocation (lat, lng, 1);
                        city = addresses.get(0).getLocality();
                        JSONWeatherTask task = new JSONWeatherTask();
                        task.execute(new String[]{city});

                    } catch (Exception e) {
                        falar = "Sem sinal de internet ou sem permissão de internet ou localização neste aplicativo";
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        e.printStackTrace ();
                    }
                    break;
                } else if(textGet.equals ("bateria")) {
                    falar = "Bateria em: " + bateriaVal.getText ().toString ();
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    break;
                } else if(textGet.equals ("horario") || textGet.equals ("data") || textGet.equals ("hora") || textGet.equals ("que horas são")) {
                    horarioEData ();
                    break;
                }else if(textGet.equals ("calculadora") || textGet.equals ("somar") || textGet.equals ("subitrair") || textGet.equals ("dividir") || textGet.equals ("multiplicar")){
                    falar = "Abrindo calculadora";
                    Toast.makeText (getApplicationContext (), falar, Toast.LENGTH_SHORT).show ();
                    textToSpeech.speak (falar, TextToSpeech.QUEUE_FLUSH, null);
                    try {
                        Thread.sleep (3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                        j = new Intent (this, Calc.class);
                    startActivity (j);
                    break;
                }else if(textGet.equals ("local") || textGet.equals ("onde estou") || textGet.equals ("localização") || textGet.equals ("nome da rua") || textGet.equals ("rua")){
                    try {
                        gps = new GPSTracker(MainActivity.this);

                        lat = gps.getLatitude();
                        lng = gps.getLongitude();

                        geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation (lat, lng, 1);

                        String address = addresses.get(0).getThoroughfare () + " numero " + addresses.get(0).getSubThoroughfare () + " cep " + addresses.get(0).getPostalCode ();
                        //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        //city = addresses.get(0).getLocality();
                        //String state = addresses.get(0).getAdminArea();
                        //String country = addresses.get(0).getCountryName();
                        //String postalCode = addresses.get(0).getPostalCode();
                        //String knownName = addresses.get(0).getFeatureName();

                        textoLocal.setText("Localização atual: " + address);

                        falar = "Localização atual: " +address;
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                    } catch (IOException e) {
                        falar = "Sem sinal de internet ou sem permissão de localização e internet neste aplicativo";
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        e.printStackTrace ();
                    }
                    catch (Exception e)
                    {
                        falar = "Sem sinal de internet ou sem permissão de internet e localização neste aplicativo";
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        e.printStackTrace ();
                    }
                    break;
                }else{
                    falar = "Comando não encontrado, tente outro!";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                    break;
                }
            }
    }

    public void onClick(View v){
        Intent i;
        String falar = "";
        switch (v.getId ()){
            case R.id.contatoId :
                falar = "Abrindo contato";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);

                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                i = new Intent (this, AllContacts.class);startActivity(i); onPause(); break;
            case R.id.telefoneId :
                falar = "Abrindo telefone";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);

                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                i = new Intent (this, Telefone.class);startActivity(i); onPause(); break;
            case R.id.calcId :
                falar = "Abrindo calculadora";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);

                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                i = new Intent (this, Calc.class);startActivity(i); onPause(); break;
            case R.id.climaId :
                try {
                gps = new GPSTracker(MainActivity.this);
                double lat = gps.getLatitude();
                double lng = gps.getLongitude();
                geocoder = new Geocoder(this, Locale.getDefault());// Here 1 represent max location result to returned, by documents it recommended 1 to 5addresses = geocoder.getFromLocation (lat, lng, 1);
                    city = addresses.get(0).getLocality();
                    JSONWeatherTask task = new JSONWeatherTask();
                    task.execute(new String[]{city});

                } catch (Exception e) {
                    falar = "Sem sinal de internet ou sem permissão de internet ou localização neste aplicativo";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                    e.printStackTrace ();
                }


                 break;
            case R.id.alarmeId :
                falar = "Abrindo alarme";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);

                try {
                    Thread.sleep (3000);
                } catch (InterruptedException e) {
                    e.printStackTrace ();
                }
                i = new Intent (this, alarmefunc.class);startActivity(i); onPause(); break;
            case R.id.bateriaId :
                falar = "Bateria em: " + bateriaVal.getText().toString();
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null); break;
            case R.id.HorarioId :
                horarioEData();
                break;
            case R.id.localId :
                try {
                    gps = new GPSTracker(MainActivity.this);

                    lat = gps.getLatitude();
                    lng = gps.getLongitude();

                    geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation (lat, lng, 1);

                    String address = addresses.get(0).getThoroughfare () + " numero " + addresses.get(0).getSubThoroughfare () + " cep " + addresses.get(0).getPostalCode ();
                    //String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    //city = addresses.get(0).getLocality();
                    //String state = addresses.get(0).getAdminArea();
                    //String country = addresses.get(0).getCountryName();
                    //String postalCode = addresses.get(0).getPostalCode();
                    //String knownName = addresses.get(0).getFeatureName();

                    textoLocal.setText("Localização atual: " + address);

                    falar = "Localização atual: " +address;
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                } catch (IOException e) {
                    falar = "Sem sinal de internet ou sem permissão de localização ou internet neste aplicativo";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                    e.printStackTrace ();
                }
                    catch (Exception e)
                    {
                        falar = "Sem sinal de internet ou sem permissão de internet ou localização neste aplicativo";
                        Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                        e.printStackTrace ();
                    }

                break;
            default:break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Intent i;
        String falar = "";
        switch (v.getId ()){
            case R.id.calcId :
                falar = "Calculadora";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                break;
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
                falar = "Bateria";
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
            case R.id.localId :
                falar = "Localização";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.HorarioId :
                falar = "Horario";
                Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                //horarioEData();
                break;
            default:break;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation();
                break;

        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        //new GetAddress().execute(String.format("%.4f,%.4f",lat,lng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather ();
            String data = ((new WeatherHttpClient ()).getWeatherData (params[0]));

            try {
                weather = JSONWeatherParser.getWeather (data);

                // Let's retrieve the icon
                weather.iconData = ((new WeatherHttpClient ()).getImage (weather.currentCondition.getIcon ()));

            } catch (JSONException e) {
                e.printStackTrace ();
            }
            return weather;

        }


        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute (weather);

            String textoGraus = "Clima: " + Math.round ((weather.temperature.getTemp () - 273.15)) + " Graus "
                    + " com umidade de: " + weather.currentCondition.getHumidity() + "%";
            txtClima.setText (textoGraus);

            Toast.makeText(getApplicationContext(), textoGraus, Toast.LENGTH_SHORT).show();
            textToSpeech.speak(textoGraus, TextToSpeech.QUEUE_FLUSH, null);
            //weather.currentCondition.getDescr();
            /*
            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "�C");
            hum.setText("" + weather.currentCondition.getHumidity() + "%");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " mps");
            windDeg.setText("" + weather.wind.getDeg() + "�");
            */
        }
    }
}
