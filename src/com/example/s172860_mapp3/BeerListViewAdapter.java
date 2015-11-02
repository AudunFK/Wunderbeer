package com.example.s172860_mapp3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * CustomList adapter that makes it posibell to fill the ListView with objects. 
 * @author audunlarsen
 *
 */
// Generates a adapter and fils it with what i want the listview to show of information. 
public class BeerListViewAdapter extends ArrayAdapter<Beer> {
	private int layoutResourceId;
	private static final String LOG_TAG = "IN BeerListViewAdapter";
	public BeerListViewAdapter (Context context, int textViewResourceId){
		super(context, textViewResourceId);
		layoutResourceId = textViewResourceId;
	}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
    try {
        Beer item = getItem(position);
        View v = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layoutResourceId, null);

        } else {
            v = convertView;
        }

        TextView header = (TextView) v.findViewById(R.id.listview_item_Adress);
        TextView description = (TextView) v.findViewById(R.id.listview_item_price);
        TextView description2 = (TextView) v.findViewById(R.id.listview_item_info);
        
        header.setText(item.getName());
        description.setText(item.getAdress());
        description2.setText("Pris: " + item.getPrice() + ",-");
        return v;
    } catch (Exception ex) {
        Log.e(LOG_TAG, "error", ex);
        return null;
    }
}
	
	
	
	
}
