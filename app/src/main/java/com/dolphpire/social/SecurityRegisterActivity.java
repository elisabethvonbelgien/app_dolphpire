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

public class SecurityRegisterActivity extends AppCompatActivity {

    TextView txtSignIn;
    Button btnNextStep;
    String password_db, password, password_confirm;
    TextInputLayout inputPassword, inputPasswordConfirm;
    EditText regisPassword, regisPasswordConfirm;
    SharedPreferences user_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_register);

        user_register = getSharedPreferences(Register_User, MODE_PRIVATE);
        password_db = user_register.getString(RegUser_Password, "");

        txtSignIn = findViewById(R.id.txtSignIn);
        btnNextStep = findViewById(R.id.btnNextStep);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordConfirm = findViewById(R.id.inputPasswordConfirm);
        regisPassword = findViewById(R.id.regisPassword);
        regisPasswordConfirm = findViewById(R.id.regisPasswordConfirm);

        if(!password_db.isEmpty()) {
            regisPassword.setText(password_db);
            regisPasswordConfirm.setText(password_db);
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

                startActivity(new Intent(SecurityRegisterActivity.this, LoginActivity.class));
                finish();

            }
        });

        btnNextStep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate_data()){

                    password = regisPassword.getText().toString();

                    SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
                    editor.putString(RegUser_Password, password);
                    editor.apply();

                    startActivity(new Intent(SecurityRegisterActivity.this, PrivateRegisterActivity.class));
                    finish();

                }

            }
        });
    }

    public boolean validate_data() {

        boolean data = true;
        boolean pass_valid = true;

        password = regisPassword.getText().toString();
        password_confirm = regisPasswordConfirm.getText().toString();

        if (password.isEmpty()) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError(getResources().getString(R.string.password_error));
            data = false;
            pass_valid = false;
        } else if(password.length() < 6 || password.length() > 50) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError(getResources().getString(R.string.password_invalid_error));
            data = false;
            pass_valid = false;
        } else {
            inputPassword.setErrorEnabled(false);
        }

        if (password_confirm.isEmpty()) {
            inputPasswordConfirm.setErrorEnabled(true);
            inputPasswordConfirm.setError(getResources().getString(R.string.password_error));
            data = false;
            pass_valid = false;
        } else if(password_confirm.length() < 6 || password_confirm.length() > 50) {
            inputPasswordConfirm.setErrorEnabled(true);
            inputPasswordConfirm.setError(getResources().getString(R.string.password_invalid_error));
            data = false;
            pass_valid = false;
        } else {
            inputPasswordConfirm.setErrorEnabled(false);
        }

        if(pass_valid) {
            if (!password_confirm.equals(password)) {
                inputPassword.setErrorEnabled(true);
                inputPasswordConfirm.setErrorEnabled(true);
                inputPassword.setError(getResources().getString(R.string.password_different_error));
                inputPasswordConfirm.setError(getResources().getString(R.string.password_different_error));
                data = false;
            } else {
                inputPassword.setErrorEnabled(false);
                inputPasswordConfirm.setErrorEnabled(false);
            }
        }

        return data;
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(SecurityRegisterActivity.this, UserRegisterActivity.class));
        finish();

    }
}
