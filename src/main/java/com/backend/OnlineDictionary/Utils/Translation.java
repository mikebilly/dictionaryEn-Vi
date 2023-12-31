package com.backend.OnlineDictionary.Utils;

import com.backend.OnlineDictionary.OnlineDictionaries.GoogleTranslate;

import java.io.IOException;

public class Translation {
    private String translation;

    public Translation(String text) {
        try {
            this.translation = getGoogleTranslateTranslation(text);
        } catch (IOException e) {
            e.printStackTrace();
            this.translation = null;
        }
    }

    public String getTranslation() {
        return translation;
    }

    public String getGoogleTranslateTranslation(String text) throws IOException {
        GoogleTranslate googleTranslate = new GoogleTranslate();
        return googleTranslate.getTranslation(text);
    }

    @Override
    public String toString() {
        return "Translation: " + translation;
    }

    public static void main(String[] args) {
        Translation translation = new Translation("hi");
        System.out.println(translation.getTranslation());
    }
}
