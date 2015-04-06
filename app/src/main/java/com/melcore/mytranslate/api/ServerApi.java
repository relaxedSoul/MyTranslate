package com.melcore.mytranslate.api;

import android.content.Context;

import com.melcore.mytranslate.R;
import com.melcore.mytranslate.model.TranslateResponse;
import com.melcore.mytranslate.model.WordPair;

import retrofit.RestAdapter;

public class ServerApi {

    private RestAdapter restAdapter;
    private String key;

    public ServerApi(Context context) {
        restAdapter = new RestAdapter.Builder().setEndpoint(context.getResources().getString(R.string.url)).build();
        key = context.getResources().getString(R.string.key);
    }

    public WordPair getTranslation(String text, String sourceLanguage, String targetLanguage) {
        YandexService service = restAdapter.create(YandexService.class);
        TranslateResponse translation = service.getTranslation(key, sourceLanguage + "-" + targetLanguage, text);
        return translation != null && translation.getCode() == 200 ? new WordPair(text, translation.getText().get(0)) : null;
    }
}
