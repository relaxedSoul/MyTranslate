package com.melcore.mytranslate.api;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

interface YandexService {

    @GET("/api/v1.5/tr.json/translate")
    YandexResponse translate(@QueryMap() Map<String,String> options);
}
