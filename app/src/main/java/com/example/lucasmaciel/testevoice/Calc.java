package com.example.lucasmaciel.testevoice;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.objecthunter.exp4j.ExpressionBuilder;

import net.objecthunter.exp4j.Expression;

import java.util.ArrayList;
import java.util.Locale;

import static com.example.lucasmaciel.testevoice.MainActivity.getErrorText;


public class Calc extends Activity implements TextToSpeech.OnInitListener,RecognitionListener,View.OnClickListener {

    TextView textView;

    private String tag = Activity.class.getSimpleName();

    private SpeechRecognizer speech = null;
    TextToSpeech textToSpeech;
    private TextToSpeech tts = null;
    private Intent recognizerIntent;
    private Button botao0 = null;

    private int[] numbotoes = {R.id.but0, R.id.but1, R.id.but2, R.id.but3, R.id.but4, R.id.but5, R.id.but6, R.id.but7, R.id.but8, R.id.but9};

    private int[] opebotoes = {R.id.butmais, R.id.butmen, R.id.butmult, R.id.butdiv, R.id.butponto};

    private Button btnmic, btnVoltar;

    private boolean ultnumero;

    private boolean estadoerro;

    private boolean ultimoponto;

    private String LOG_TAG = "VoiceRecognitionActivity";
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
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

            textToSpeech = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
                @Override
                public void onInit(int status) {
                    if (status != textToSpeech.ERROR) {
                        textToSpeech.setLanguage (Locale.getDefault ());
                    }
                }
            });




        }
        botao0 = findViewById(R.id.but0);

        this.textView = (TextView) findViewById(R.id.textView);
        numerosclique();
        operadoresclique();
        falarnumero();
        falaroperadores();

        tts = new TextToSpeech(this, this);

        btnmic = (Button) findViewById (R.id.butmic);
        btnmic.setOnClickListener (this);


        speech = SpeechRecognizer.createSpeechRecognizer (this);
        speech.setRecognitionListener(this);

        speech = SpeechRecognizer.createSpeechRecognizer (this);
        speech.setRecognitionListener (this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault ());
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName ());
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra (RecognizerIntent.EXTRA_MAX_RESULTS, 3);


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

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    String falar = "Tela de calculadora aberta";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                    try {
                        Thread.sleep (2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }
                    falar = "Nesta tela não existe o botão de microfone na parte inferior da tela";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);

                    try {
                        Thread.sleep (6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace ();
                    }

                    falar = "Para voltar para a tela inicial, pressione a parte superior da tela";
                    Toast.makeText(getApplicationContext(), falar, Toast.LENGTH_SHORT).show();
                    textToSpeech.speak(falar, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }

            }
        });

    }


    public void onClick(View v){

    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
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

    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    public void onInit(int status) {
        Log.i(tag, "onInit [" + status + "]");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
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


    }
    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }
    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "Falhou " + errorMessage);
        textView.setText(errorMessage);

    }


    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

        textView.setText(text);


    }

    private void numerosclique() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (estadoerro) {
                    textView.setText(button.getText());
                    estadoerro = false;

                } else {
                    textView.append(button.getText());

                }
                ultnumero = true;
                tts.speak(button.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

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
                tts.speak(button.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                return false;
            }
        };
        for (int id : numbotoes) {
            findViewById(id).setOnLongClickListener (listener);
        }
    }

    private void falaroperadores() {
        View.OnLongClickListener listener = new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                Button button = (Button) v;
                tts.speak(button.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                return false;
            }
        };
        for (int id : numbotoes) {
            findViewById(id).setOnLongClickListener (listener);
        }


        findViewById(R.id.butlimpa).setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                tts.speak( "limpar", TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        findViewById(R.id.butdiv).setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                tts.speak( "Divizão", TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        findViewById(R.id.butmais).setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                tts.speak( "Soma", TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        findViewById(R.id.butponto).setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                tts.speak( "Ponto", TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        findViewById(R.id.butmult).setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                tts.speak( "Multiplicação", TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        findViewById(R.id.butmen).setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                tts.speak( "Subtração", TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });

        findViewById(R.id.butigual).setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                tts.speak( "Resultado", TextToSpeech.QUEUE_FLUSH, null);
                return true;
            }
        });
    }

    private void operadoresclique() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ultnumero && !estadoerro) {
                    Button button = (Button) v;
                    textView.setText(textView.getText()+""+button.getText());
                    ultnumero = false;
                    ultimoponto = false;
                    Log.i(tag, "speakNow [" +button.getText().toString() + "]");
                    tts.speak(button.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                }
            }



        };
        for (int id : opebotoes) {
            findViewById(id).setOnClickListener(listener);
        }
        findViewById(R.id.butponto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ultnumero && !estadoerro && !ultimoponto) {
                    textView.setText(textView.getText()+".");
                    ultnumero = false;
                    ultimoponto = true;
                    Button button = (Button) v;
                    Log.i(tag, "speakNow [" +button.getText().toString() + "]");
                    tts.speak(button.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        findViewById(R.id.butlimpa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "speakNow [" +"limpar"+ "]");
                tts.speak( "limpar", TextToSpeech.QUEUE_FLUSH, null);

                textView.setText("");
                ultnumero = false;
                estadoerro = false;
                ultimoponto = false;
            }
        });

        findViewById(R.id.butigual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onEqual();

            }
        });
    }


    private void onEqual() {

        if (ultnumero && !estadoerro) {
            String txt = textView.getText().toString();
            Expression expression = new ExpressionBuilder(txt).build();
            try {
                double result = expression.evaluate();
                textView.setText(Double.toString(result));
                Log.i(tag, "speakNow [" + "=" +textView.getText().toString() + "]");
                tts.speak("="+textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                ultimoponto = true;
            } catch (ArithmeticException ex) {
                textView.setText("Erro");
                estadoerro = true;
                ultnumero = false;
            }
        }
    }



}