package com.melcore.mytranslate;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melcore.mytranslate.adapter.DictionaryAdapter;
import com.melcore.mytranslate.cache.CacheUtils;
import com.melcore.mytranslate.model.TranslateResponse;
import com.melcore.mytranslate.model.WordPair;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Main fragment
 * <p/>
 * Created by RelaxedSoul on 02.03.2015.
 */
public class DictionaryFragment extends ListFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String STATE_QUERY = "query_state";

    private CharSequence initialQuery = null;
    private SearchView mSearchView = null;
    private DictionaryAdapter adapter = null;
    private GetTranslateAsyncTask mTranslateAsyncTask;
    private EditText mTranslateEditText;
    private TextView mTranslationTextView;
    private View mTranslateButton;
    private View mSaveButton;
    private View mActivityIndicator;

    public static DictionaryFragment newInstance() {
        return new DictionaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View mHeader = LayoutInflater.from(getActivity()).inflate(R.layout.item_add_translation, null, false);
        getListView().addHeaderView(mHeader, null, false);
        mTranslateEditText = (EditText) mHeader.findViewById(R.id.origin_to_add);
        mTranslationTextView = (TextView) mHeader.findViewById(R.id.translation);
        mTranslateButton = mHeader.findViewById(R.id.action_translate);
        mTranslateButton.setOnClickListener(mOnClickListener);
        mSaveButton = mHeader.findViewById(R.id.action_save);
        mSaveButton.setOnClickListener(mOnClickListener);
        mActivityIndicator = mHeader.findViewById(R.id.activity_indicator);
        adapter = new DictionaryAdapter(CacheUtils.loadWordPairs(getActivity()));
        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            initialQuery = savedInstanceState.getCharSequence(STATE_QUERY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        if (!mSearchView.isIconified()) {
            state.putCharSequence(STATE_QUERY, mSearchView.getQuery());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        configureSearchView(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void configureSearchView(Menu menu) {
        MenuItem search = menu.findItem(R.id.search);

        mSearchView = (SearchView) search.getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setIconifiedByDefault(true);

        if (initialQuery != null) {
            mSearchView.setIconified(false);
            search.expandActionView();
            mSearchView.setQuery(initialQuery, true);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_translate:
                    getTranslation(mTranslateEditText.getText().toString());
                    break;
                case R.id.action_save:
                    WordPair pair = new WordPair();
                    pair.setOrigin(mTranslateEditText.getText().toString());
                    pair.setTranslate(mTranslationTextView.getText().toString());
                    if (CacheUtils.saveWordPair(getActivity(), pair)) {
                        adapter.add(pair);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(TextUtils.isEmpty(newText) ? "" : newText);
        return true;
    }

    @Override
    public boolean onClose() {
        adapter.getFilter().filter("");
        return true;
    }

    private void getTranslation(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (mTranslateAsyncTask != null && mTranslateAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTranslateAsyncTask.cancel(true);
        }
        mTranslateAsyncTask = new GetTranslateAsyncTask();
        mTranslateAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "ru", "en", text);
    }

    private class GetTranslateAsyncTask extends AsyncTask<String, Void, WordPair> {

        @Override
        protected void onPreExecute() {
            mTranslateButton.setClickable(false);
            mSaveButton.setClickable(false);
            mTranslateEditText.setInputType(0);
            mTranslateButton.setVisibility(View.INVISIBLE);
            mActivityIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected WordPair doInBackground(String... params) {
            String langSource = params[0];
            String langTarget = params[1];
            String text = params[2];
            Request request;
            request = new Request.Builder()
                    .url(getResources().getString(R.string.url)
                            + "?key=" + getResources().getString(R.string.key)
                            + "&lang=" + langSource + "-" + langTarget
                            + "&text=" + text)
                    .build();
            OkHttpClient client = new OkHttpClient();
            try {
                Response response = client.newCall(request).execute();
                TranslateResponse translation = new Gson().fromJson(response.body().string(), (new TypeToken<TranslateResponse>() {
                }).getType());
                if (translation != null && translation.getCode() == 200) {
                    WordPair pair = new WordPair();
                    pair.setOrigin(text);
                    pair.setTranslate(translation.getText().get(0));
                    return pair;
                }
            } catch (com.google.gson.JsonSyntaxException | IllegalStateException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(WordPair wordPair) {
            if (wordPair != null && !TextUtils.isEmpty(wordPair.getTranslate())) {
                mTranslationTextView.setText(wordPair.getTranslate());
            } else if (wordPair == null && !isCancelled()) {
                Activity activity = DictionaryFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, R.string.conn_problem, Toast.LENGTH_SHORT).show();
                }
            }
            mTranslateButton.setVisibility(View.VISIBLE);
            mActivityIndicator.setVisibility(View.GONE);
            mTranslateButton.setClickable(true);
            mSaveButton.setClickable(true);
            mTranslateEditText.setInputType(1);
            mTranslateAsyncTask = null;
        }
    }
}