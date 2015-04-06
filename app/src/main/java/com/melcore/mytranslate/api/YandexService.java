package com.melcore.mytranslate.api;

import com.melcore.mytranslate.model.TranslateResponse;
import com.melcore.mytranslate.model.WordPair;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by user on 06/04/15.
 */
public interface YandexService {

    @GET("/api/v1.5/tr.json/translate?key={key}&lang={lang}&text={text}")
    TranslateResponse getTranslation(@Path("key") String key,@Path("lang") String lang, @Path("text") String text);
}
