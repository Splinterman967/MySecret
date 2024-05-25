package com.example.mysecret;




import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SecretActivity extends AppCompatActivity implements RecyclerViewInterface   {
    private RecyclerView recyclerViewSecrets;
    private NotesRecyclerViewAdapter recyclerViewAdapterSecrets;
    private ArrayList<Note> secretsArrayList;
    private final RecyclerViewInterface recyclerViewInterface=this;


    private final CollectionReference collectionReferenceSecret =recyclerview_notes.COLLECTION_REFERENCE_SECRET;



    private FloatingActionButton addSecretButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);



        viewSettings();

        addSecretButton.setOnClickListener(v -> {
            Intent intent=new Intent(SecretActivity.this,newNote.class);
            intent.putExtra("Secret",true);
            intent.putExtra(recyclerview_notes.COLLECTION_REFERENCE_PATH,collectionReferenceSecret.getPath());
            startActivity(intent);
        });

    }

    //Creates our MENU
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_research, menu);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView)
                menu.findItem(R.id.search_bar).getActionView();

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

        //Add back button to the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);



        return super.onCreateOptionsMenu(menu);
    }



    @SuppressLint("NotifyDataSetChanged")
    private void fillArray() {
        secretsArrayList = new ArrayList<>();

        collectionReferenceSecret.addSnapshotListener((value, error) -> {
            if(error!=null){
                Toast.makeText(SecretActivity.this, error.toString() , Toast.LENGTH_SHORT).show();
            }

            if (value!=null && !value.isEmpty()){
                secretsArrayList.clear();
                for (QueryDocumentSnapshot doc:value){
                    Note note = doc.toObject(Note.class);
                    secretsArrayList.add(note);
                    Log.d("NOTETAG", "onEvent: "+note.getTitle()+" ");
                }
                recyclerViewAdapterSecrets =new NotesRecyclerViewAdapter(secretsArrayList,recyclerViewInterface);
                recyclerViewSecrets.setAdapter(recyclerViewAdapterSecrets);
                recyclerViewAdapterSecrets.notifyDataSetChanged();
            }
        });
    }

    public void filter(String newText){
        ArrayList<Note> noteFiltered=new ArrayList<>();

        for (Note item: secretsArrayList){
            if (item.getTitle().toLowerCase().contains(newText.toLowerCase())||
                    item.getThoughts().toLowerCase().contains(newText.toLowerCase()))
            {
                noteFiltered.add(item);
            }

        }
        if (!noteFiltered.isEmpty()){
            recyclerViewAdapterSecrets.filterList(noteFiltered);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        fillArray();
    }


    private void viewSettings() {
    recyclerViewSecrets=findViewById(R.id.recyclerViewSecret);
    recyclerViewSecrets.setLayoutManager(new LinearLayoutManager(this));
    addSecretButton=findViewById(R.id.addSecretFAButton);

    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.ab_secret_title);


    }

    @Override
    public void itemClick(int position) {
        Intent intent = new Intent(SecretActivity.this, newNote.class);
        intent.putExtra("position", position);
        intent.putExtra("itemClicked", true);
        intent.putExtra(recyclerview_notes.COLLECTION_REFERENCE_PATH, recyclerview_notes.COLLECTION_REFERENCE_SECRET.getPath());
        startActivity(intent);
    }


}

