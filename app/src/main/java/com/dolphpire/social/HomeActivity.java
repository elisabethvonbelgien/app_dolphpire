package com.dolphpire.social;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.TextView;
import android.view.View;

import com.dolphpire.social.preferences.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    FirebaseFirestore db;
    RecyclerView mResultList;
    ArrayList<Users> list;
    DataAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db=FirebaseFirestore.getInstance();


        list=new ArrayList<>();
        mResultList = (RecyclerView) findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));



        firebaseUserSearch();




    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();

               // Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                try {
                    mAdapter.getFilter().filter(newText);

                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void firebaseUserSearch() {

      //  CollectionReference usersCollectionRef = db.collection("users");

       /* db.collection("users_data").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                      //  Toast.makeText(getApplicationContext(),documentSnapshot.getData().toString(),Toast.LENGTH_SHORT).show();
                        Log.d("data",documentSnapshot.getData().toString());
                    }
                }

            }
        });*/

      /*  db.collection("users_by_queries").document("users_by_email").collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                  //  QuerySnapshot snapshot =task.getResult();

                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                          Toast.makeText(getApplicationContext(),documentSnapshot.getData().toString(),Toast.LENGTH_SHORT).show();
                        Log.d("data",documentSnapshot.getData().toString());
                    }



                }


            }
        });*/


        db.collection("users_data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int s= queryDocumentSnapshots.getDocuments().size();

                List<Users> users=queryDocumentSnapshots.toObjects(Users.class);
                list.addAll(users);

              /*  for(int i=0; i<s;i++){






                    String name =users.get(i).getFirst_name();
                    String email=users.get(i).getEmail();






                  //  Toast.makeText(getApplicationContext(),name+email,Toast.LENGTH_SHORT).show();



                }*/

                mAdapter=new DataAdapter(list);
                mResultList.setAdapter(mAdapter);

              //  Toast.makeText(getApplicationContext(),list.get(0).email,Toast.LENGTH_SHORT).show();


              //  Toast.makeText(getApplicationContext(),String.valueOf(s),Toast.LENGTH_SHORT).show();

            }
        });



    }


}
