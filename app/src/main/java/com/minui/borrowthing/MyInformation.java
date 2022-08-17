package com.minui.borrowthing;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MyInformation extends AppCompatActivity {

    Button btnInformation;
    Button btnInterestsList;
    Button btnMyWrote;
    Button btnMyLocation;
    Button btnTransaction;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        btnInformation = findViewById(R.id.btnInformation);
        btnInterestsList = findViewById(R.id.btnInterestsList);
        btnMyWrote = findViewById(R.id.btnMyWrote);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnTransaction = findViewById(R.id.btnTransaction);
        btnLogout = findViewById(R.id.btnLogout);

    }
}