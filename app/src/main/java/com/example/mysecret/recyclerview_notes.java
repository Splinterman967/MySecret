package com.example.mysecret;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuInflater;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class recyclerview_notes extends AppCompatActivity implements RecyclerViewInterface {
    private static final int INTENT_AUTHENTICATE = 1 ;
    private RecyclerView recyclerView;
    private NotesRecyclerViewAdapter recyclerViewAdapterNotes;
    private final RecyclerViewInterface recyclerViewInterface = this;
    private ArrayList<Note> NoteArray;


    public static  String COLLECTION_NOTE ;
    public static  String COLLECTION_SECRET;
    public static Boolean firstRun ;

    private  final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static  CollectionReference COLLECTION_REFERENCE_NOTE ;
    public static  CollectionReference COLLECTION_REFERENCE_SECRET;



    public static final String COLLECTION_REFERENCE_PATH ="CollectionReferencePath";

    private  Intent authIntent=new Intent();
    private boolean keepSecret,goToSecrets=false;

    public FloatingActionButton floatingActionButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        viewSetting();
        createOneUseVariables();

        MobileAds.initialize(this, initializationStatus -> {
        });

        AdView myAd=findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().build();
        myAd.loadAd(adRequest);

        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(recyclerview_notes.this, newNote.class);
            intent.putExtra(COLLECTION_REFERENCE_PATH,COLLECTION_REFERENCE_NOTE.getPath());

            startActivity(intent);
            Log.d("XD", "onCreate: "+COLLECTION_REFERENCE_NOTE.getPath());
        });

    }

    //Saving data to app using Shared Preferences
    private void createOneUseVariables() {

//getSharedPreferences() — Use this if you need multiple shared preference files identified by name,
// which you specify with the first parameter. You can call this from any Context in your app.

//getPreferences() — Use this from an Activity if you need to use only one shared preference file for the activity.
// Because this retrieves a default shared preference file that belongs to the activity, you don't need to supply

        Context context=getApplicationContext();
        SharedPreferences sharedPreferences=context.getSharedPreferences
                (getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        firstRun=sharedPreferences.getBoolean(getString(R.string.firstRunKey),true);
        if (firstRun) {


            String noteReferenceOnce = String.valueOf(Math.random());
            String secretReferenceOnce = String.valueOf(Math.random());

            editor.putString(getString(R.string.noteCollectionKey), noteReferenceOnce);
            editor.putString(getString(R.string.secretCollectionKey), secretReferenceOnce);
            editor.putBoolean(getString(R.string.firstRunKey), false);
            editor.apply();

            COLLECTION_NOTE =sharedPreferences.getString(getString(R.string.noteCollectionKey),"def");
            COLLECTION_SECRET =sharedPreferences.getString(getString(R.string.secretCollectionKey),"def");

            COLLECTION_REFERENCE_NOTE = db.collection(COLLECTION_NOTE);
            COLLECTION_REFERENCE_SECRET = db.collection(COLLECTION_SECRET);

            db.collection(COLLECTION_NOTE).orderBy("title",Query.Direction.DESCENDING);
            db.collection(COLLECTION_SECRET).orderBy("title",Query.Direction.DESCENDING);
        }
        else
        {
            COLLECTION_NOTE =sharedPreferences.getString(getString(R.string.noteCollectionKey),"def");
            COLLECTION_SECRET =sharedPreferences.getString(getString(R.string.secretCollectionKey),"def");

            COLLECTION_REFERENCE_NOTE = db.collection(COLLECTION_NOTE);
            COLLECTION_REFERENCE_SECRET = db.collection(COLLECTION_SECRET);

            db.collection(COLLECTION_NOTE).orderBy("title", Query.Direction.DESCENDING);
            db.collection(COLLECTION_SECRET).orderBy("title",Query.Direction.DESCENDING);
        }

    }



    //METHODS:

    //Creates our MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_research, menu);


        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.search_bar).getActionView();

        searchView.setQueryHint("Type to search");


        //Search Method
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //Text her değistiginde arama yapar
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });




        //Adding options bar to the menu
        menu.add("Keep a Secret").setOnMenuItemClickListener(item -> {
            keepSecret();
            return false;
        });

        menu.add("Open My Secrets").setOnMenuItemClickListener(item -> {
            goToSecrets();
            return false;
        });


        return super.onCreateOptionsMenu(menu);
    }



    //{ KEY GUARD THİNG
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==INTENT_AUTHENTICATE){
            if (resultCode==RESULT_OK){
                authIntent.putExtra("sda","sdassd");
                if (keepSecret){
                    authIntent= new Intent(recyclerview_notes.this,newNote.class);
                    authIntent.putExtra(COLLECTION_REFERENCE_PATH, COLLECTION_REFERENCE_SECRET.getPath());

                    startActivity(authIntent);
                    keepSecret=false;
                }
                else if(goToSecrets){
                    authIntent= new Intent(recyclerview_notes.this,SecretActivity.class);
                    startActivity(authIntent);
                    goToSecrets=false;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void keepSecret() {
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km.isKeyguardSecure()) {
             authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.dialog_title_auth), getString(R.string.dialog_msg_auth));
             keepSecret=true;
            startActivityForResult(authIntent,INTENT_AUTHENTICATE);
        }
    }


    private void goToSecrets() {
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km.isKeyguardSecure()) {
            authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.dialog_title_auth), getString(R.string.dialog_msg_auth));
            goToSecrets=true;
            startActivityForResult(authIntent,INTENT_AUTHENTICATE);
        }

    }

//KEY GUARD THİNG ENDS }




    @SuppressLint("NotifyDataSetChanged")
    public void fillArray() {
        NoteArray = new ArrayList<>();
        ArrayList<String> NoteArrayTitle= new ArrayList<>();


        //SnapshotListener read and write data in real time so fast
        COLLECTION_REFERENCE_NOTE.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(recyclerview_notes.this, "Error " + error, Toast.LENGTH_SHORT).show();
            }

            if (value != null && !value.isEmpty() && !Objects.requireNonNull(value).getDocuments().isEmpty()) {
                // adding list clear function. If data is fetched successfully then the list gets cleared old data and fills new data.
                NoteArray.clear();
                for (QueryDocumentSnapshot docs : value) {
                    Note note = docs.toObject(Note.class);
                    NoteArray.add(note);
                    NoteArrayTitle.add(note.getTitle());
                    Log.d("dataPart", "onEvent: ");

                }

                recyclerViewAdapterNotes = new NotesRecyclerViewAdapter(NoteArray, recyclerViewInterface);
                recyclerView.setAdapter(recyclerViewAdapterNotes);
                recyclerViewAdapterNotes.notifyDataSetChanged();



            }
            else{
                NoteArray.removeAll(NoteArray);
                recyclerViewAdapterNotes = new NotesRecyclerViewAdapter(NoteArray, recyclerViewInterface);
                recyclerView.setAdapter(recyclerViewAdapterNotes);
                recyclerViewAdapterNotes.notifyDataSetChanged();
            }

        });

    }

    public void filter(String newText){
        ArrayList<Note> noteFiltered=new ArrayList<>();

        for (Note item: NoteArray){
            if (item.getTitle().toLowerCase().contains(newText.toLowerCase())||
                    item.getThoughts().toLowerCase().contains(newText.toLowerCase()))
            {
                noteFiltered.add(item);
            }

        }
        if (!noteFiltered.isEmpty()){
            recyclerViewAdapterNotes.filterList(noteFiltered);
        }


    }


    private void viewSetting() {
        recyclerView = findViewById(R.id.recyclerViewSecret);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        floatingActionButton = findViewById(R.id.addSecretFAButton);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setTitle(R.string.ab_note_title);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);


    }

    @Override
    public void itemClick(int position) {
        Intent intent = new Intent(recyclerview_notes.this, newNote.class);
        intent.putExtra("position", position);
        intent.putExtra("itemClicked", true);
        intent.putExtra(COLLECTION_REFERENCE_PATH, COLLECTION_REFERENCE_NOTE.getPath());
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle", "onStart: ");
        fillArray();
    }



}
