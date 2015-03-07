package com.melcore.mytranslate.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.melcore.mytranslate.R;
import com.melcore.mytranslate.model.WordPair;

import java.util.ArrayList;
import java.util.List;

public class DictionaryAdapter extends BaseAdapter implements Filterable {

    private Filter filter;
    private List<WordPair> mDictionary;
    private List<WordPair> mOriginDictionary;

    private class ViewHolder {
        TextView origin;
        TextView translation;
    }

    public DictionaryAdapter() {
    }

    public DictionaryAdapter(List<WordPair> wordPairs) {
        mDictionary = wordPairs == null ? new ArrayList<WordPair>() : wordPairs;
        mOriginDictionary = new ArrayList<>(mDictionary);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new DictionaryFilter();
        }
        return filter;
    }

    @Override
    public int getCount() {
        return mDictionary.size();
    }

    @Override
    public Object getItem(int position) {
        return mDictionary.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_translation_record, parent, false);
            holder = new ViewHolder();
            holder.origin = (TextView) convertView.findViewById(R.id.origin);
            holder.translation = (TextView) convertView.findViewById(R.id.translation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WordPair data = (WordPair) getItem(position);
        holder.origin.setText(data.getOrigin());
        holder.translation.setText(data.getTranslate());
        return convertView;
    }

    public void add(WordPair pair) {
        mDictionary.add(0, pair);
        mOriginDictionary.add(0, pair);
        notifyDataSetChanged();
    }

    private class DictionaryFilter extends Filter {

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDictionary = (ArrayList<WordPair>) results.values;
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.count = mOriginDictionary.size();
                results.values = mOriginDictionary;
                return results;
            } else {
                List<WordPair> filteredArrayPairs = new ArrayList<>();
                List<WordPair> dictionary = new ArrayList<>(mOriginDictionary);
                String search = constraint.toString().toLowerCase();
                for (WordPair pair : dictionary) {
                    if (pair.getOrigin().toLowerCase().contains(search)
                            || pair.getTranslate().toLowerCase().contains(search)) {
                        filteredArrayPairs.add(pair);
                    }
                }
                results.count = filteredArrayPairs.size();
                results.values = filteredArrayPairs;
            }
            return results;
        }
    }
}