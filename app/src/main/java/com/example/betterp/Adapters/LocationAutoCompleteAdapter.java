package com.example.betterp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.betterp.Model.LocationEntity;

import java.util.ArrayList;
import java.util.List;

public class LocationAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<LocationEntity> originalList;
    private List<LocationEntity> filteredList;
    private LayoutInflater inflater;
    private MaterialFilter filter;

    public LocationAutoCompleteAdapter(Context context) {
        this.context = context;
        this.originalList = new ArrayList<>();
        this.filteredList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public LocationEntity getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        LocationEntity material = getItem(position);
        textView.setText(material.getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MaterialFilter();
        }
        return filter;
    }

    private class MaterialFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<LocationEntity> filtered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (LocationEntity material : originalList) {
                    if (material.getName().toLowerCase().contains(filterPattern)) {
                        filtered.add(material);
                    }
                }
            }

            results.values = filtered;
            results.count = filtered.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            filteredList.addAll((List<LocationEntity>) results.values);
            notifyDataSetChanged();
        }
    }

    public void setData(List<LocationEntity> newList) {
        originalList.clear();
        originalList.addAll(newList);
        filteredList.clear();
        filteredList.addAll(newList);
        notifyDataSetChanged();
    }

    public void clear() {
        originalList.clear();
        filteredList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<LocationEntity> items) {
        originalList.addAll(items);
        filteredList.addAll(items);
        notifyDataSetChanged();
    }
}