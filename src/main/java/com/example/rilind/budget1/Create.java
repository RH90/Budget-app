package com.example.rilind.budget1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;

public class Create extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("event3"));
        setContentView(R.layout.activity_create);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();

        }
    };

    public void create(View view){
        EditText u = (EditText) findViewById(R.id.username);
        EditText p1 = (EditText) findViewById(R.id.pass_1);
        EditText p2 = (EditText) findViewById(R.id.pass_2);
        if(p1.getText().toString().equals(p2.getText().toString())){
            SQL fc = new SQL();
            Thread thread = new Thread() {
                public void run() {
                    fc.create(getApplicationContext(),u.getText().toString(),p1.getText().toString());
                }
            };
            thread.start();
            TextView err = (TextView) findViewById(R.id.error);
            err.setText("Wait for database");

        }else{
            TextView err = (TextView) findViewById(R.id.error);
            err.setText("Password not same!");
        }

    }

}