package com.example.mysecret;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;


import android.widget.Button;
import android.widget.EditText;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;


public class newNote extends AppCompatActivity {

    private EditText titleText;
    private EditText notesText;

    private Button buttonClear;
    private FloatingActionButton buttonDelete,buttonSave,buttonBack;

    private boolean deleteOnce=false;
    private boolean saved=false;
    private boolean updated=false;
    private boolean saveClicked=false;
    private int saveOperationCount=0;
    private String titleFireBase;
    private String thoughtsFireBase;

    //Initializing the FireStore
    private  FirebaseFirestore db;

    private  String getCollectionReferencePath;
    private  CollectionReference collectionReference;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db=FirebaseFirestore.getInstance();

        getCollectionReferencePath = getIntent().getStringExtra(recyclerview_notes.COLLECTION_REFERENCE_PATH);
        Log.d("XDNewNote", "onCreate: "+getCollectionReferencePath);
        collectionReference=db.collection(getCollectionReferencePath);

        titleText =findViewById(R.id.titleText);
        notesText =findViewById(R.id.notesText);
        buttonDelete=findViewById(R.id.delete_tic_button);
        buttonClear=findViewById(R.id.clear_button);
        buttonSave=findViewById(R.id.save_tic_button);
        buttonBack=findViewById(R.id.back_button);


    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ActivityLifecycle", "onStart: ");
        fillTexts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ActivityLifecycle", "onResume: ");
        boolean itemClicked=getIntent().getBooleanExtra("itemClicked",false);


        //DELETE Button
        buttonDelete.setOnClickListener(v -> {
            AlertDialog deleteDialog =AskDeleteOption();
            if (itemClicked){
                deleteDialog.show();
            }
        });

        //CLEAR Button
        buttonClear.setOnClickListener(v -> {
            titleText.setText("");
            notesText.setText("");
        });

        //SAVE Button
        buttonSave.setOnClickListener(v -> {
            saveOperation();
            saveClicked=true;
        });

        //BACK Button
        buttonBack.setOnClickListener(v -> {
            //BACK operation
            onBackPressed();
        });


    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ActivityLifecycle", "onStop: ");
        //SAVES Before Exit activity
        if (!saveClicked){
            saveOperation();
        }

    }



    //SAVE Operation
    private void saveOperation() {
        Intent i=getIntent();
        int position = i.getIntExtra("position", 0);
        boolean itemClicked = i.getBooleanExtra("itemClicked", false);

        String title = titleText.getText().toString();
        String note = notesText.getText().toString();


        //Saving the info to the fireStore when activity stops
        //listener checks always so it will be infinite loop infinite update
        db.collection(getCollectionReferencePath).addSnapshotListener((value, error) -> {

            if (saveOperationCount==0) {
                assert value != null;
                if (value.toObjects(Note.class).size()!=0){
                    assert value != null;
                     titleFireBase = value.toObjects(Note.class).get(position).getTitle();
                     thoughtsFireBase = value.toObjects(Note.class).get(position).getThoughts();
                }
                else{
                    titleFireBase="";
                    thoughtsFireBase="";
                }



            //Deletes if title and thoughts empty
            if (title.isEmpty() && note.isEmpty() && itemClicked && !saved && !updated) {
                deleteItem();
            }

            //Updates
            else if (itemClicked && !deleteOnce && !saved) {

                collectionReference.addSnapshotListener((value1, error1) -> {

                    if(!Objects.requireNonNull(value1).getDocuments().isEmpty()){
                        String id = value1.getDocuments().get(position).getId();

                        //once it updates it wont update always since we have checking operation.
                        if (!updated) {
                            collectionReference.document(id).update("title", title);
                            collectionReference.document(id).update("thoughts", note);
                            Log.d("itemUpdated", "onSuccess: " + id + " bu");
                        }

                        updated = true;
                        saveOperationCount++;

                    }

                });
            }

            //Saves
            else if ((!title.isEmpty() || !note.isEmpty()) && !deleteOnce)
            {
                if (!titleFireBase.equals(title) || !thoughtsFireBase.equals(note))
                {
                    Note n = new Note();
                    n.setTitle(title);
                    n.setThoughts(note);

                    collectionReference.add(n);
                    saved = true;
                    saveOperationCount++;

                    Toast.makeText(newNote.this, "Saved", Toast.LENGTH_SHORT).show();
                }
            }

            else if(!deleteOnce)
            {
                Toast.makeText(newNote.this, "Empty", Toast.LENGTH_SHORT).show();
            }
                saveOperationCount++;
        }
            onBackPressed();
    });


    }

    //DELETE Operation
    private void deleteItem(){
        int position=getIntent().getIntExtra("position",0);

        db.collection(getCollectionReferencePath).addSnapshotListener((value, error) -> {

            // we can take selected item id
            // value.getDocuments().get(position).getId();
            if (!deleteOnce && !Objects.requireNonNull(value).getDocuments().isEmpty()) {
                String id = value.getDocuments().get(position).getId();
                collectionReference.document(id).delete();
                Toast.makeText(newNote.this, "Item Deleted", Toast.LENGTH_SHORT).show();
            }
            deleteOnce = true;
        });
    }

    //FILLS the TextViews
    private void fillTexts() {
        //Intent Data
        int position=getIntent().getIntExtra("position",0);



        db.collection(getCollectionReferencePath).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (!getIntent().hasExtra("position")){

                }
                else if (position==0 && !Objects.requireNonNull(value).getDocuments().isEmpty()){
                    titleText.setText(value.toObjects(Note.class).get(position).getTitle());
                    notesText.setText(value.toObjects(Note.class).get(position).getThoughts());
                }

                else if (position!=0 && !Objects.requireNonNull(value).getDocuments().isEmpty()){
                    titleText.setText(value.toObjects(Note.class).get(position).getTitle());
                    notesText.setText(value.toObjects(Note.class).get(position).getThoughts());
                }
                else if(error!=null){
                    Toast.makeText(newNote.this, error.toString() , Toast.LENGTH_SHORT).show();
                }




            }
        });


    }

    @Override
    public void onBackPressed() {
        //finishes activity
        finish();
    }

    //Confirming to DELETE
    private AlertDialog AskDeleteOption()
    {

        return new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete ?")
                .setIcon(R.drawable.ic_baseline_delete_outline_24)

                .setPositiveButton("Delete", (dialog, whichButton) -> {
                    //your deleting code
                    deleteItem();
                    onBackPressed();
                })
                .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                .create();
    }

}