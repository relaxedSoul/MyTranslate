package com.melcore.mytranslate.api;

import android.content.Context;

import com.melcore.mytranslate.R;
import com.melcore.mytranslate.model.WordPair;

import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;

public class ServerApi {

    private RestAdapter adapter;
    private Map<String, String> options;

    public ServerApi(Context context) {
        adapter = new RestAdapter.Builder().setEndpoint(context.getResources().getString(R.string.url)).build();
        options = new HashMap<>();
        options.put("key", context.getResources().getString(R.string.key));
    }

    public WordPair getTranslation(String text, String fromLang, String toLang) {
        options.put("lang", fromLang + "-" + toLang);
        options.put("text", text);
        YandexResponse response = adapter.create(YandexService.class).translate(options);
        return response != null && response.getCode() == 200 ? new WordPair(text, response.getText().get(0)) : null;
    }
}
