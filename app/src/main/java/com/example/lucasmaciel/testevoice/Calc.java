package com.example.lucasmaciel.testevoice;


import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public class Calc extends Activity implements TextToSpeech.OnInitListener {

    TextView textView;

    private String tag = Activity.class.getSimpleName();



    private TextToSpeech tts = null;

    private Button botao0 = null;

    private int[] numbotoes = {R.id.but0, R.id.but1, R.id.but2, R.id.but3, R.id.but4, R.id.but5, R.id.but6, R.id.but7, R.id.but8, R.id.but9};

    private int[] opebotoes = {R.id.butmais, R.id.butmen, R.id.butmult, R.id.butdiv};

    private boolean ultnumero;

    private boolean estadoerro;

    private boolean ultimoponto;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        botao0 = findViewById(R.id.but0);

        this.textView = (TextView) findViewById(R.id.textView);
        numerosclique();
        operadoresclique();

        tts = new TextToSpeech(this, this);

         
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
                Log.i(tag, "speakNow [" +button.getText().toString() + "]");
                tts.speak(button.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

            }
        };
        for (int id : numbotoes) {

            findViewById(id).setOnClickListener(listener);



        }
    }


    private void operadoresclique() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ultnumero && !estadoerro) {
                    Button button = (Button) v;
                    textView.append(button.getText());
                    ultnumero = false;
                    ultimoponto = false;    
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
                    textView.append(".");
                    ultnumero = false;
                    ultimoponto = true;
                }
            }
        });
        findViewById(R.id.butlimpa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                ultimoponto = true; 
            } catch (ArithmeticException ex) {
                textView.setText("Erro");
                estadoerro = true;
                ultnumero = false;
            }
        }
    }

}