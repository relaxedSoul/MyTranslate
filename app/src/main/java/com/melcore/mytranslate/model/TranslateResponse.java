package com.melcore.mytranslate.model;

import java.util.List;

/**
 * Response from yandex translate.
 *
 * Created by Melcore on 03.03.2015.
 */
public class TranslateResponse {

    private int code;
    private String lang;
    private List<String> text;

    public TranslateResponse(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

}
