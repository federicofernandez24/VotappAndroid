package com.votapp.fede.votapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.votapp.fede.votapp.R;
import com.votapp.fede.votapp.domain.utils.ItemMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fede on 24/6/15.
 */
public class ItemMenuAdapter extends ArrayAdapter<ItemMenu> {

    private final Context context;
    private final ArrayList<ItemMenu> modelsArrayList;

    public ItemMenuAdapter(Context context, ArrayList<ItemMenu> objects) {
        super(context, R.layout.fragment_menu_index, objects);

        this.context = context;
        this.modelsArrayList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
            rowView = inflater.inflate(R.layout.fragment_menu_index, parent, false);

            // 3. Get icon,title views from the rowView
            ImageView imgView = (ImageView) rowView.findViewById(R.id.image_optEncuesta);
            TextView titleView = (TextView) rowView.findViewById(R.id.encuestas_opt);

            // 4. Set the text for textView
            imgView.setImageResource(modelsArrayList.get(position).getIcon());
            titleView.setText(modelsArrayList.get(position).getTitle());

        // 5. retrn rowView
        return rowView;
    }
}
