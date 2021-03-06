package com.melcore.mytranslate;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.melcore.mytranslate.cache.CacheUtils;
import com.melcore.mytranslate.model.WordPair;
import com.melcore.mytranslate.model.event.CursorEvent;

import de.greenrobot.event.EventBus;

public class MyTranslateIntentService extends IntentService {

    public static final String EXTRA_ORIGIN = "origin";
    public static final String EXTRA_TRANSLATE = "translate";

    public enum Method {
        SAVE_PAIR, GET_DEFAULT_CURSOR
    }

    public MyTranslateIntentService() {
        super(MyTranslateIntentService.class.getSimpleName());
    }

    public MyTranslateIntentService(String name) {
        super(MyTranslateIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch ((Method) intent.getExtras().getSerializable(Method.class.getSimpleName())) {
            case SAVE_PAIR:
                savePair(intent.getExtras().getString(EXTRA_ORIGIN), intent.getExtras().getString(EXTRA_TRANSLATE));
                break;
            case GET_DEFAULT_CURSOR:
                getDefaultCursor();
                break;
            default:
                break;
        }
    }

    private void getDefaultCursor() {
        EventBus.getDefault().post(new CursorEvent(CacheUtils.getDefaultCursor(this)));
    }

    private void savePair(String origin, String translate) {
        if (CacheUtils.saveWordPair(this, new WordPair(origin, translate))) getDefaultCursor();
    }
}
