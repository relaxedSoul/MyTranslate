package com.melcore.mytranslate.api;

import java.util.List;

/**
 * Response from yandex translate.
 * <p/>
 * Created by Melcore on 03.03.2015.
 */
class YandexResponse {

    private int code;
    private String lang;
    private List<String> text;

    public YandexResponse() {

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
