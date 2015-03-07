package com.melcore.mytranslate;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.melcore.mytranslate.cache.CacheUtils;

public class MainActivity extends ActionBarActivity {

    DictionaryFragment mDictionaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDictionaryFragment = (DictionaryFragment) getSupportFragmentManager().findFragmentByTag(DictionaryFragment.class.getName());
        if (mDictionaryFragment == null) {
            mDictionaryFragment = DictionaryFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mDictionaryFragment, DictionaryFragment.class.getName())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheUtils.releaseInstance();
    }
}
