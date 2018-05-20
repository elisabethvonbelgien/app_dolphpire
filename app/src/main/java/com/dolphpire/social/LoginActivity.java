package com.dolphpire.social;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;

import static com.dolphpire.social.preferences.AppDataPrefs.AppData;
import static com.dolphpire.social.preferences.AppDataPrefs.AppData_LoggedIn;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData_Birthday;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData_Email;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData_FirstName;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData_Gender;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData_LastName;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData_Password;
import static com.dolphpire.social.preferences.UserDataPrefs.UserData_Username;

public class LoginActivity extends AppCompatActivity {

    TextView txtSignUp;
    Button btnSignIn;
    String log_key, pass;
    TextInputLayout inputLog, inputPass;
    EditText loginLog, loginPass;
    FirebaseFirestore db;
    String fn_fs, ln_fs, em_fs, us_fs, ps_fs, bi_fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtSignUp = findViewById(R.id.txtSignUp);
        btnSignIn = findViewById(R.id.btnLogIn);
        inputLog = findViewById(R.id.inputLog);
        inputPass = findViewById(R.id.inputPass);
        loginLog = findViewById(R.id.loginLog);
        loginPass = findViewById(R.id.loginPass);


        db = FirebaseFirestore.getInstance();

        txtSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, IntroRegisterActivity.class));
                finish();

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate_data()){

                    log_key = loginLog.getText().toString();
                    if ((android.util.Patterns.EMAIL_ADDRESS.matcher(log_key).matches())) {

                        loginByEmail();

                    } else if (TextUtils.isDigitsOnly(log_key)) {
                        Toast.makeText(getApplicationContext(),"phone number",Toast.LENGTH_SHORT).show();
                    } else if (!log_key.contains("@") && !log_key.contains("|")) {

                        loginByUsername();

                    } else {
                        Toast.makeText(getApplicationContext(),"invalid characters",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    private void loginByEmail() {

        log_key = loginLog.getText().toString();
        pass = loginPass.getText().toString();

        DocumentReference users_by_email = db.collection("users_by_queries").document("users_by_email").collection("users").document(log_key);
        users_by_email.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    if(doc.get("uid")!=null){
                        String uid = doc.getString("uid");

                        DocumentReference users_data = db.collection("users_data").document(uid);
                        users_data.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();
                                    String password = doc.getString("password");

                                    if (!pass.equals(password)) {

                                        Toast.makeText(getApplicationContext(),getString(R.string.wrong_password),Toast.LENGTH_SHORT).show();

                                    } else {

                                        succeedLogin();

                                    }

                                }

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),getString(R.string.error_network),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        Toast.makeText(getApplicationContext(),getString(R.string.error_login_key),Toast.LENGTH_SHORT).show();
                    }


                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),getString(R.string.error_network),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loginByUsername() {

        log_key = loginLog.getText().toString();
        pass = loginPass.getText().toString();

        DocumentReference users_by_username = db.collection("users_by_queries").document("users_by_username").collection("users").document(log_key);
        users_by_username.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    if(doc.get("uid")!=null){
                        final String uid = doc.getString("uid");

                        DocumentReference users_data = db.collection("users_data").document(uid);
                        users_data.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();
                                    String password = doc.getString("password");

                                    if (!pass.equals(password)) {

                                        Toast.makeText(getApplicationContext(), getString(R.string.wrong_password),Toast.LENGTH_SHORT).show();

                                    } else {

                                        fn_fs = doc.getString("first_name");
                                        ln_fs = doc.getString("last_name");
                                        em_fs = doc.getString("email");
                                        us_fs = doc.getString("username");
                                        ps_fs = doc.getString("password");
                                        bi_fs = doc.getString("birthday");
                                        succeedLogin();

                                    }

                                }

                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),getString(R.string.error_network),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else {
                        Toast.makeText(getApplicationContext(),getString(R.string.error_login_key),Toast.LENGTH_SHORT).show();
                    }


                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),getString(R.string.error_network),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public boolean validate_data() {

        boolean data = true;

        log_key = loginLog.getText().toString();
        pass = loginPass.getText().toString();

        if(log_key.isEmpty()){
            inputLog.setErrorEnabled(true);
            inputLog.setError(getResources().getString(R.string.email_login));
            data = false;
        } else {
            inputLog.setErrorEnabled(false);
        }

        if (pass.isEmpty()) {
            inputPass.setErrorEnabled(true);
            inputPass.setError(getResources().getString(R.string.password_error));
            data = false;
        } else if(pass.length() < 6 || pass.length() > 50) {
            inputPass.setErrorEnabled(true);
            inputPass.setError(getResources().getString(R.string.password_invalid_error));
            data = false;
        } else {
            inputPass.setErrorEnabled(false);
        }

        return data;
    }

    public void succeedLogin () {

        SharedPreferences.Editor editor = getSharedPreferences(UserData, MODE_PRIVATE).edit();
        editor.putString(UserData_FirstName,fn_fs);
        editor.putString(UserData_LastName, ln_fs);
        editor.putString(UserData_Email, em_fs);
        editor.putString(UserData_Username, us_fs);
        editor.putString(UserData_Password, ps_fs);
        editor.putString(UserData_Birthday, bi_fs);
        editor.apply();

        SharedPreferences.Editor app_data = getSharedPreferences(AppData, MODE_PRIVATE).edit();
        app_data.putBoolean(AppData_LoggedIn, true);
        app_data.apply();

        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
