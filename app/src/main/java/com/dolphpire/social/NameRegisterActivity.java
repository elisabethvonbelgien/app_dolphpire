package com.dolphpire.social;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Birthday;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Email;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_FirstName;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Gender;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_LastName;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Password;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Username;
import static com.dolphpire.social.preferences.RegisterPrefs.Register_User;

public class NameRegisterActivity extends AppCompatActivity {

    TextView txtSignIn;
    Button btnNextStep;
    String first_name_db, last_name_db, first_name, last_name;
    TextInputLayout inputFirstName, inputLastName;
    EditText regisFirstName, regisLastName;
    SharedPreferences user_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_register);

        user_register = getSharedPreferences(Register_User, MODE_PRIVATE);
        first_name_db = user_register.getString(RegUser_FirstName, "");
        last_name_db = user_register.getString(RegUser_LastName, "");

        txtSignIn = findViewById(R.id.txtSignIn);
        btnNextStep = findViewById(R.id.btnNextStep);
        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        regisFirstName = findViewById(R.id.regisFirstName);
        regisLastName = findViewById(R.id.regisLastName);

        if(!first_name_db.isEmpty()) {
            regisFirstName.setText(first_name_db);
        }

        if(!last_name_db.isEmpty()) {
            regisLastName.setText(last_name_db);
        }

        txtSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
                editor.putString(RegUser_FirstName, "");
                editor.putString(RegUser_LastName, "");
                editor.putString(RegUser_Email, "");
                editor.putString(RegUser_Username, "");
                editor.putString(RegUser_Password, "");
                editor.putInt(RegUser_Gender, 0);
                editor.putString(RegUser_Birthday, "");
                editor.apply();

                startActivity(new Intent(NameRegisterActivity.this, LoginActivity.class));
                finish();

            }
        });

        btnNextStep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate_data()){

                    first_name = regisFirstName.getText().toString();
                    last_name = regisLastName.getText().toString();

                    SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
                    editor.putString(RegUser_FirstName, first_name);
                    editor.putString(RegUser_LastName, last_name);
                    editor.apply();

                    startActivity(new Intent(NameRegisterActivity.this, UserRegisterActivity.class));
                    finish();

                }

            }
        });
    }

    public boolean validate_data() {

        boolean data = true;

        first_name = regisFirstName.getText().toString();
        last_name = regisLastName.getText().toString();

        if (first_name.isEmpty()) {
            inputFirstName.setErrorEnabled(true);
            inputFirstName.setError(getResources().getString(R.string.first_name_error));
            inputFirstName.requestFocus();
            data = false;
        } else {
            inputFirstName.setErrorEnabled(false);
        }

        if (last_name.isEmpty()) {
            inputLastName.setErrorEnabled(true);
            inputLastName.setError(getResources().getString(R.string.last_name_error));
            inputLastName.requestFocus();
            data = false;
        } else {
            inputLastName.setErrorEnabled(false);
        }

        return data;
    }

    @Override
    public void onBackPressed() {

    }
}
