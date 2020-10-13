package com.example.wwwhisper;


public class Data {
    private String chapter;
    private String audio;
    private String text;

    public Data(){

    }

    public void set_chapter(String chapter) {
        this.chapter = chapter ;
    }
    public void set_audio(String audio) {
        this.audio = audio ;
    }
    public void set_text(String text) {
        this.text = text ;
    }

    public String get_chapter() {
        return this.chapter;
    }
    public String get_audio() {
        return this.audio;
    }
    public String get_text() {
        return this.text;
    }
}
