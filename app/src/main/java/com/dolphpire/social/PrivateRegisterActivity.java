package com.dolphpire.social;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Birthday;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Email;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_FirstName;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Gender;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_LastName;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Password;
import static com.dolphpire.social.preferences.RegisterPrefs.RegUser_Username;
import static com.dolphpire.social.preferences.RegisterPrefs.Register_User;

public class PrivateRegisterActivity extends AppCompatActivity {

    TextView txtSignIn;
    Button btnNextStep;
    SharedPreferences user_register;
    RadioGroup rdoGrpGender;
    RadioButton rdoBtnMale, rdoBtnFemale;
    TextInputLayout inputGender, inputBirthday;
    EditText regisBirthday;
    int gender_db, gender;
    String birthday_db, first_name_db, last_name_db, email_db, username_db, password_db, birthday;
    FirebaseFirestore db;
    String passOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_register);

        user_register = getSharedPreferences(Register_User, MODE_PRIVATE);
        gender_db = user_register.getInt(RegUser_Gender, 0);
        birthday_db = user_register.getString(RegUser_Birthday, "");

        txtSignIn = findViewById(R.id.txtSignIn);
        btnNextStep = findViewById(R.id.btnNextStep);
        rdoGrpGender = findViewById(R.id.rdoGrpGender);
        rdoBtnMale = findViewById(R.id.rdoBtnMale);
        rdoBtnFemale = findViewById(R.id.rdoBtnFemale);
        inputGender = findViewById(R.id.inputGender);
        regisBirthday = findViewById(R.id.regisBirthday);
        regisBirthday.addTextChangedListener(mDateEntryWatcher);
        inputBirthday = findViewById(R.id.inputBirthday);

        if(gender_db == 1){
            rdoBtnMale.setChecked(true);
        }else if(gender_db == 2){
            rdoBtnFemale.setChecked(true);
        }

        if(!birthday_db.isEmpty()) {
            regisBirthday.setText(birthday_db);
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

                startActivity(new Intent(PrivateRegisterActivity.this, LoginActivity.class));
                finish();

            }
        });

        btnNextStep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate_data()){

                    if(rdoBtnMale.isChecked()) {

                        SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
                        editor.putInt(RegUser_Gender, 1);
                        editor.apply();

                    } else {

                        SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
                        editor.putInt(RegUser_Gender, 2);
                        editor.apply();

                    }

                    birthday = regisBirthday.getText().toString();

                    SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
                    editor.putString(RegUser_Birthday, birthday);
                    editor.apply();

                    requestConfirmation();


                }

            }
        });
    }

    public boolean validate_data() {

        boolean data = true;
        birthday = regisBirthday.getText().toString();

        if(rdoGrpGender.getCheckedRadioButtonId() == -1) {
            inputGender.setErrorEnabled(true);
            inputGender.setError(getResources().getString(R.string.gender_error));
            inputGender.requestFocus();
            data = false;
        } else {
            inputGender.setErrorEnabled(false);
        }

        if (birthday.isEmpty() || birthday.equals("DD/MM/YYYY")) {
            inputBirthday.setErrorEnabled(true);
            inputBirthday.setError(getResources().getString(R.string.birthday_error));
            inputBirthday.requestFocus();
            data = false;
        } else if (birthday.contains("Y")) {
            inputBirthday.setErrorEnabled(true);
            inputBirthday.setError(getResources().getString(R.string.birthday_invalid_error));
            inputBirthday.requestFocus();
            data = false;
        } else {
            inputBirthday.setErrorEnabled(false);
        }

        return data;
    }

    private TextWatcher mDateEntryWatcher = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format(getString(R.string.birthday_format),day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                regisBirthday.setText(current);
                regisBirthday.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    };

    private void requestConfirmation() {
        final Dialog dialog = new Dialog(PrivateRegisterActivity.this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.register_finished_dialog);
        dialog.setCanceledOnTouchOutside(true);

        TextView txt_first_name_regis = dialog.findViewById(R.id.txt_first_name_regis);
        TextView txt_last_name_regis = dialog.findViewById(R.id.txt_last_name_regis);
        TextView txt_email_regis = dialog.findViewById(R.id.txt_email_regis);
        TextView txt_username_regis = dialog.findViewById(R.id.txt_username_regis);
        TextView txt_password_regis = dialog.findViewById(R.id.txt_password_regis);
        TextView txt_gender_regis = dialog.findViewById(R.id.txt_gender_regis);
        TextView txt_birthday_regis = dialog.findViewById(R.id.txt_birthday_regis);
        Button btnChange = dialog.findViewById(R.id.btnChange);
        Button btnRegister = dialog.findViewById(R.id.btnRegister);

        first_name_db = user_register.getString(RegUser_FirstName, "");
        last_name_db = user_register.getString(RegUser_LastName, "");
        email_db = user_register.getString(RegUser_Email, "");
        username_db = user_register.getString(RegUser_Username, "");
        password_db = user_register.getString(RegUser_Password, "");
        gender_db = user_register.getInt(RegUser_Gender, 0);
        birthday_db = user_register.getString(RegUser_Birthday, "");

        txt_first_name_regis.setText(first_name_db);
        txt_last_name_regis.setText(last_name_db);
        txt_email_regis.setText(email_db);
        txt_username_regis.setText(username_db);
        txt_password_regis.setText(password_db);
        if(gender_db == 1){
            txt_gender_regis.setText(getString(R.string.gender_male));
        }else if(gender_db == 2){
            txt_gender_regis.setText(getString(R.string.gender_female));
        }
        txt_birthday_regis.setText(birthday_db);


        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addValues();
                dialog.dismiss();
                startActivity(new Intent(PrivateRegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void addValues() {

        db = FirebaseFirestore.getInstance();

        long date = System.currentTimeMillis();
        SimpleDateFormat seconds = new SimpleDateFormat("dd/MM/yyyy; kk:mm:ss");
        String stringOfSeconds = seconds.format(date);

        first_name_db = user_register.getString(RegUser_FirstName, "");
        last_name_db = user_register.getString(RegUser_LastName, "");
        email_db = user_register.getString(RegUser_Email, "");
        username_db = user_register.getString(RegUser_Username, "");
        password_db = user_register.getString(RegUser_Password, "");
        gender_db = user_register.getInt(RegUser_Gender, 0);
        birthday_db = user_register.getString(RegUser_Birthday, "");

        generateUUID();

        Map<String, Object> newUserByEmail = new HashMap<>();
        newUserByEmail.put("uid", passOut);

        Map<String, Object> newUserByUsername = new HashMap<>();
        newUserByUsername.put("uid", passOut);

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("uid", passOut);
        newUser.put("birthday", birthday_db);
        newUser.put("email", email_db);
        newUser.put("first_name", first_name_db);
        newUser.put("followers", 0);
        newUser.put("following", 0);
        if(gender_db == 1){
            newUser.put("gender", (getString(R.string.gender_male)));
        }else if(gender_db == 2){
            newUser.put("gender", (getString(R.string.gender_female)));
        }
        newUser.put("last_activity", stringOfSeconds);
        newUser.put("last_name", last_name_db);
        newUser.put("name", first_name_db + " " + last_name_db);
        newUser.put("password", password_db);
        newUser.put("status", true);
        newUser.put("username", username_db);
        newUser.put("verified", false);

        SharedPreferences.Editor editor = getSharedPreferences(Register_User, MODE_PRIVATE).edit();
        editor.putString(RegUser_FirstName, "");
        editor.putString(RegUser_LastName, "");
        editor.putString(RegUser_Email, "");
        editor.putString(RegUser_Username, "");
        editor.putString(RegUser_Password, "");
        editor.putInt(RegUser_Gender, 0);
        editor.putString(RegUser_Birthday, "");
        editor.apply();

        db.collection("users_by_queries").document("users_by_email").collection("users").document(email_db)
                .set(newUserByEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PrivateRegisterActivity.this, "added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });

        db.collection("users_by_queries").document("users_by_username").collection("users").document(username_db)
                .set(newUserByUsername)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PrivateRegisterActivity.this, "added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });

        db.collection("users_data").document(passOut)
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PrivateRegisterActivity.this, "added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });
    }

    public void generateUUID() {

        passOut="";
        for (int much =  250; much!=0; much=much-1) {
            SecureRandom nlsR = new SecureRandom();
            int nls = nlsR.nextInt(3);
            switch (nls) {
                case  0 :
                    NumGen();
                    break;
                case 1:
                    LetGen();
                    break;
                case 2:
                    SymGen ();
                    break;

            }
        }

    }

    private void NumGen(){
        SecureRandom numR = new SecureRandom();
        int num = numR.nextInt(10);
        passOut = passOut + num;
    }

    private void LetGen(){
        char letter;
        SecureRandom letR = new SecureRandom();
        int let= letR.nextInt(52);
        letter = ' ';
        switch (let) {
            case 0: letter='Q';
                break;
            case 1: letter='W';
                break;
            case 2: letter='E';
                break;
            case 3: letter='R';
                break;
            case 4: letter='T';
                break;
            case 5: letter='Y';
                break;
            case 6: letter='U';
                break;
            case 7: letter='I';
                break;
            case 8: letter='O';
                break;
            case 9: letter='P';
                break;
            case 10: letter='A';
                break;
            case 11: letter='S';
                break;
            case 12: letter='D';
                break;
            case 13: letter='F';
                break;
            case 14: letter='G';
                break;
            case 15: letter='H';
                break;
            case 16: letter='J';
                break;
            case 17: letter='K';
                break;
            case 18: letter='L';
                break;
            case 19: letter='Z';
                break;
            case 20: letter='X';
                break;
            case 21: letter='C';
                break;
            case 22: letter='V';
                break;
            case 23: letter='B';
                break;
            case 24: letter='N';
                break;
            case 25: letter='M';
                break;
            case 26: letter='q';
                break;
            case 27: letter='w';
                break;
            case 28: letter='e';
                break;
            case 29: letter='r';
                break;
            case 30: letter='t';
                break;
            case 31: letter='y';
                break;
            case 32: letter='u';
                break;
            case 33: letter='i';
                break;
            case 34: letter='o';
                break;
            case 35: letter='p';
                break;
            case 36: letter='a';
                break;
            case 37: letter='s';
                break;
            case 38: letter='d';
                break;
            case 39: letter='f';
                break;
            case 40: letter='g';
                break;
            case 41: letter='h';
                break;
            case 42: letter='j';
                break;
            case 43: letter='k';
                break;
            case 44: letter='l';
                break;
            case 45: letter='z';
                break;
            case 46: letter='x';
                break;
            case 47: letter='c';
                break;
            case 48: letter='v';
                break;
            case 49: letter='b';
                break;
            case 50: letter='n';
                break;
            case 51: letter='m';
                break;
        }
        passOut = passOut + letter;

    }

    public void SymGen(){
        char symbol;
        SecureRandom letR = new SecureRandom();
        int let= letR.nextInt(9);
        symbol = ' ';
        switch (let) {
            case 0:
                symbol = '!';
                break;
            case 1:
                symbol = '@';
                break;
            case 2:
                symbol = '^';
                break;
            case 3:
                symbol = '*';
                break;
            case 4:
                symbol = '-';
                break;
            case 5:
                symbol = '_';
                break;
            case 6:
                symbol = '+';
                break;
            case 7:
                symbol = '=';
                break;
            case 8:
                symbol = '~';
                break;
        }
        passOut = passOut + symbol;
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(PrivateRegisterActivity.this, SecurityRegisterActivity.class));
        finish();

    }
}
