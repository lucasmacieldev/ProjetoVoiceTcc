package com.example.lucasmaciel.testevoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Contato extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_contato);

        Button view = (Button)findViewById(R.id.viewButton);
        Button add = (Button)findViewById(R.id.createButton);
        Button modify = (Button)findViewById(R.id.updateButton);
        Button delete = (Button)findViewById(R.id.deleteButton);


        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //displayContacts();
                Log.i("NativeContentProvider", "Completed Displaying Contact list");
            }
        });
    }


    
}
