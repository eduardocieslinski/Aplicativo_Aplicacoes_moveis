package com.example.unccrudapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MenuActivity extends AppCompatActivity {

    private Button botao02, botao03, botao04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        botao02 = (Button) findViewById(R.id.botao02);
        botao03 = (Button) findViewById(R.id.botao03);
        botao04 = (Button) findViewById(R.id.botao04);

        botao02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                botao02Activity();

            }
        });
        botao03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                botao03Activity();

            }
        });
        botao04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                botao04Activity();

            }
        });



    }




    private void botao02Activity() {

        startActivity(new Intent(MenuActivity.this, UserActivity.class));

    }
    private void botao03Activity() {
        startActivity(new Intent(MenuActivity.this, LojaActivity.class));
    }
    private void botao04Activity() {
        startActivity(new Intent(MenuActivity.this, ProdutoActivity.class));
    }

}