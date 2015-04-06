package com.melcore.mytranslate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.melcore.mytranslate.api.ServerApi;
import com.melcore.mytranslate.cache.CacheUtils;
import com.melcore.mytranslate.model.CursorEvent;
import com.melcore.mytranslate.model.WordPair;

import de.greenrobot.event.EventBus;

/**
 * Main fragment
 * <p/>
 * Created by RelaxedSoul on 02.03.2015.
 */
public class DictionaryFragment extends ListFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String STATE_QUERY = "query_state";

    private CharSequence initialQuery = null;
    private SearchView mSearchView = null;
    private EditText mOriginEditText;
    private TextView mTranslationTextView;
    private View mTranslateButton;
    private View mSaveButton;
    private View mActivityIndicator;

    private GetTranslateAsyncTask mTranslateAsyncTask;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
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
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.item_translation_add, null, false);
        getListView().addHeaderView(header, null, false);
        mOriginEditText = (EditText) header.findViewById(R.id.origin_to_add);
        mTranslationTextView = (TextView) header.findViewById(R.id.translation);
        mTranslateButton = header.findViewById(R.id.action_translate);
        mTranslateButton.setOnClickListener(mOnClickListener);
        mSaveButton = header.findViewById(R.id.action_save);
        mSaveButton.setOnClickListener(mOnClickListener);
        mActivityIndicator = header.findViewById(R.id.activity_indicator);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_translation_record,
                null, new String[]{WordPair.ORIGIN, WordPair.TRANSLATE},
                new int[]{R.id.item_origin, R.id.item_translation}, 0);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return CacheUtils.getCursorForFilter(getActivity(), constraint == null ? "" : constraint.toString());
            }
        });
        setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            initialQuery = savedInstanceState.getCharSequence(STATE_QUERY);
        } else {
            getActivity().startService(new Intent(getActivity(), MyIntentService.class)
                    .putExtra(MyIntentService.Method.class.getSimpleName(), MyIntentService.Method.GET_DEFAULT_CURSOR));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        CacheUtils.releaseInstance();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_translate:
                    getTranslation(mOriginEditText.getText().toString());
                    break;
                case R.id.action_save:
                    saveWordPair(mOriginEditText.getText().toString(), mTranslationTextView.getText().toString());
                    break;
                default:
                    break;
            }
        }
    };

    private void saveWordPair(String origin, String translate) {
        Intent intent = new Intent(getActivity(), MyIntentService.class);
        intent.putExtra(MyIntentService.Method.class.getSimpleName(), MyIntentService.Method.SAVE_PAIR);
        intent.putExtra(MyIntentService.EXTRA_ORIGIN, origin);
        intent.putExtra(MyIntentService.EXTRA_TRANSLATE, translate);
        getActivity().startService(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ((SimpleCursorAdapter) getListAdapter()).getFilterQueryProvider().runQuery(newText);
        return true;
    }

    @Override
    public boolean onClose() {
        ((SimpleCursorAdapter) getListAdapter()).getFilterQueryProvider().runQuery("");
        return true;
    }

    private void getTranslation(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (mTranslateAsyncTask != null && mTranslateAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTranslateAsyncTask.cancel(true);
        }
        mTranslateAsyncTask = new GetTranslateAsyncTask(getActivity());
        mTranslateAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, text, "ru", "en");
    }

    private class GetTranslateAsyncTask extends AsyncTask<String, Void, WordPair> {

        private ServerApi api;

        public GetTranslateAsyncTask(Context context) {
            api = new ServerApi(context);
        }

        @Override
        protected void onPreExecute() {
            mTranslateButton.setClickable(false);
            mSaveButton.setClickable(false);
            mOriginEditText.setInputType(0);
            mTranslateButton.setVisibility(View.INVISIBLE);
            mActivityIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected WordPair doInBackground(String... params) {
            return api.getTranslation(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(WordPair wordPair) {
            if (!isCancelled()) {
                if (wordPair != null) {
                    mTranslationTextView.setText(!TextUtils.isEmpty(wordPair.getTranslate()) ? wordPair.getTranslate() : "");
                } else {
                    Toast.makeText(getActivity(), R.string.conn_problem, Toast.LENGTH_SHORT).show();
                }
            }
            mTranslateButton.setVisibility(View.VISIBLE);
            mActivityIndicator.setVisibility(View.GONE);
            mTranslateButton.setClickable(true);
            mSaveButton.setClickable(true);
            mOriginEditText.setInputType(1);
            mTranslateAsyncTask = null;
        }
    }

    public void onEventMainThread(CursorEvent event) {
        if (event != null && getListAdapter() != null) {
            ((SimpleCursorAdapter) getListAdapter()).swapCursor(event.getCursor()).close();
        }
    }
}
