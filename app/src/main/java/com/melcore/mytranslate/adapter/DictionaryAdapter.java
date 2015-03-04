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
import java.util.LinkedList;

/**
 * om-nom-nom
 * Created by Melcore on 04.03.2015.
 */
public class DictionaryAdapter extends BaseAdapter implements Filterable {

    private Filter filter;
    private LinkedList<WordPair> mDictionary;
    private LinkedList<WordPair> mOriginDictionary;

    private class ViewHolder {
        TextView origin;
        TextView translation;
    }

    public DictionaryAdapter() {
    }

    public DictionaryAdapter(LinkedList<WordPair> wordPairs) {
        mDictionary = wordPairs == null ? new LinkedList<WordPair>() : wordPairs;
        mOriginDictionary = new LinkedList<>(mDictionary);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_translation_record, parent, false);
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
        mDictionary.addFirst(pair);
        mOriginDictionary.addFirst(pair);
        notifyDataSetChanged();
    }

    private class DictionaryFilter extends Filter {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDictionary = (LinkedList<WordPair>) results.values;
            notifyDataSetChanged();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            boolean isEmptyConstraint = TextUtils.isEmpty(constraint);
            ArrayList<WordPair> dictionary = new ArrayList<>(mOriginDictionary);
            if (TextUtils.isEmpty(constraint)) {
                results.count = dictionary.size();
                results.values = dictionary;
                return results;
            }
            ArrayList<WordPair> filteredArrayPairs = new ArrayList<>();
            if (!isEmptyConstraint) {
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < dictionary.size(); i++) {
                    WordPair pair = dictionary.get(i);
                    String origin = pair.getOrigin();
                    String translate = pair.getTranslate();
                    if (origin.toLowerCase().startsWith(constraint.toString())
                            || translate.toLowerCase().startsWith(constraint.toString())) {
                        filteredArrayPairs.add(pair);
                    }
                }
            }
            results.count = filteredArrayPairs.size();
            results.values = filteredArrayPairs;
            return results;
        }
    }
}