package com.melcore.mytranslate;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.melcore.mytranslate.cache.CacheUtils;
import com.squareup.okhttp.Cache;

public class MainActivity extends ActionBarActivity {

    String tag = "dictionary";
    DictionaryFragment mDictionaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDictionaryFragment = (DictionaryFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (mDictionaryFragment == null){
            mDictionaryFragment = DictionaryFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mDictionaryFragment, tag)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheUtils.releaseInstance();
    }
}
