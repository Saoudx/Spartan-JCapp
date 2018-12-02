package com.example.zica.spartanjcapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManualActivity extends AppCompatActivity {

    private EditText manual;
    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        manual = findViewById(R.id.etManualId);
        submit = findViewById(R.id.btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String scannedUID = manual.getText().toString();

                    Intent intent = new Intent(ManualActivity.this, AddContactActivity.class);
                    intent.putExtra("SCANNED_DATA_RESULT", scannedUID);
                    startActivity(intent);
                }
            }
        });
    }

    private Boolean validate(){
        Boolean result = false;
        String scannedID = manual.getText().toString();

        if(scannedID.isEmpty()){
            Toast.makeText(this, "Pleas enter an id ", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }
        return result;
    }
}
