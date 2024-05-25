package com.example.mysecret;

public class Note {
    private String title;
    private String thoughts;



    public Note(){

    }

    public Note(String title, String thoughts) {
        this.title = title;
        this.thoughts = thoughts;

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }
}

