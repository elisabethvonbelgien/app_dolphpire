package com.dolphpire.social;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Birthday;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Email;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_FirstName;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Gender;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_LastName;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Password;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Username;
import static com.dolphpire.social.preferences.RegisterPrefs.Register_User;

public class UserRegisterActivity extends AppCompatActivity {

    TextView txtSignIn;
    Button btnNextStep;
    String email_db, username_db, email, username;
    TextInputLayout inputEmail, inputUsername;
    EditText regisEmail, regisUsername;
    SharedPreferences user_register;
    FirebaseFirestore db;
    boolean vaildate_email = true, validate_username = true;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        db = FirebaseFirestore.getInstance();

        user_register = getSharedPreferences(Register_User, MODE_PRIVATE);
        email_db = user_register.getString(RegUser_Email, "");
        username_db = user_register.getString(RegUser_Username, "");

        txtSignIn = findViewById(R.id.txtSignIn);
        btnNextStep = findViewById(R.id.btnNextStep);
        inputEmail = findViewById(R.id.inputLog);
        inputUsername = findViewById(R.id.inputUsername);
        regisEmail = findViewById(R.id.regisEmail);
        regisUsername = findViewById(R.id.regisUsername);

        if(!email_db.isEmpty()) {
            regisEmail.setText(email_db);
        }

        if(!username_db.isEmpty()) {
            regisUsername.setText(username_db);
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

                startActivity(new Intent(UserRegisterActivity.this, LoginActivity.class));
                finish();

            }
        });

        btnNextStep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(UserRegisterActivity.this,
                        R.style.Register_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.show();

                email_valid();
                username_valid();

                if(validate_data()){

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {

                                    if (vaildate_email && validate_username) {

                                        SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
                                        editor.putString(RegUser_Email, email);
                                        editor.putString(RegUser_Username, username);
                                        editor.apply();
                                        progressDialog.dismiss();

                                        startActivity(new Intent(UserRegisterActivity.this, SecurityRegisterActivity.class));
                                        finish();

                                    }
                                }
                            }, 2000);

                }

            }
        });
    }

    public boolean validate_data() {

        boolean data = true;

        email = regisEmail.getText().toString();
        username = regisUsername.getText().toString();

        if(email.isEmpty()){
            inputEmail.setErrorEnabled(true);
            inputEmail.setError(getResources().getString(R.string.email_error));
            data = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setErrorEnabled(true);
            inputEmail.setError(getResources().getString(R.string.email_invalid_error));
            data = false;
        } else {
            inputEmail.setErrorEnabled(false);
        }

        if (username.isEmpty()) {
            inputUsername.setErrorEnabled(true);
            inputUsername.setError(getResources().getString(R.string.username_error));
            data = false;
        } else if(username.length() < 4 || username.length() > 40) {
            inputUsername.setErrorEnabled(true);
            inputUsername.setError(getResources().getString(R.string.username_invalid_error));
            data = false;
        } else if(username.contains("@") || username.contains("|")) {
            inputUsername.setErrorEnabled(true);
            inputUsername.setError(getResources().getString(R.string.username_character));
            data = false;
        } else {
            inputUsername.setErrorEnabled(false);
        }

        return data;
    }

    private void email_valid() {

        email = regisEmail.getText().toString();

        DocumentReference email_exist = db.collection("users_by_queries").document("users_by_email").collection("users").document(email);
        email_exist.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    if(doc.get("uid")!=null){

                        inputEmail.setErrorEnabled(true);
                        inputEmail.setError(getResources().getString(R.string.email_exist));
                        vaildate_email = false;
                        progressDialog.dismiss();

                    } else {

                        vaildate_email = true;

                    }


                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void username_valid() {

        username = regisUsername.getText().toString();

        DocumentReference username_exist = db.collection("users_by_queries").document("users_by_username").collection("users").document(username);
        username_exist.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    if(doc.get("uid")!=null){

                        inputUsername.setErrorEnabled(true);
                        inputUsername.setError(getResources().getString(R.string.username_exist));
                        validate_username = false;
                        progressDialog.dismiss();

                    } else {

                        validate_username = true;

                    }

                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(UserRegisterActivity.this, NameRegisterActivity.class));
        finish();

    }
}
