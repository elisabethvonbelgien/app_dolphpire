package com.dolphpire.social;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IntroRegisterActivity extends AppCompatActivity {

    TextView txtSignIn;
    Button btnLetsBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_register);

        txtSignIn = findViewById(R.id.txtSignIn);
        btnLetsBegin = findViewById(R.id.btnLetsBegin);

        txtSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(IntroRegisterActivity.this, LoginActivity.class));
                finish();
                //overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

            }
        });

        btnLetsBegin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(IntroRegisterActivity.this, NameRegisterActivity.class));
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}