package com.lifehopehealthapp.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lifehopehealthapp.R;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private final ArrayList<String> name;

    public SpinnerAdapter(Context mContext, int customspinner, ArrayList<String> industryname) {
        super(mContext, customspinner, industryname);
        this.mContext = mContext;
        this.name = industryname;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.item_custom_spinner, parent, false);
        TextView label = (TextView) row.findViewById(R.id.sptext);
        label.setText(name.get(position));
        return row;
    }
}
