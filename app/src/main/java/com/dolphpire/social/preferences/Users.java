package com.dolphpire.social.preferences;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by rishad on 21/5/18.
 */

@IgnoreExtraProperties
public class Users {

    public String email;
    public String first_name;

    public Users(){

    }

    public Users(String email,String first_name){

        this.email=email;
        this.first_name=first_name;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getFirst_name(){return first_name;}

    public void setFirst_name(String first_name){this.first_name=first_name;}


}
