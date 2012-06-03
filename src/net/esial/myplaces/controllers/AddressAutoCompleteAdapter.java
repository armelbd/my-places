package net.esial.myplaces.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import net.esial.myplaces.R;
import net.esial.myplaces.geocoder.GeocoderChooser;
import net.esial.myplaces.geocoder.IGeocoder;
import net.esial.myplaces.geocoder.IGeocoderFactory;

import android.app.Application;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class AddressAutoCompleteAdapter extends ArrayAdapter<Address> {
	
	private LayoutInflater layoutInflater;
	private ArrayList<Address> suggested;
	private ArrayList<Address> lastSuggested;
	private String lastInput;
	private IGeocoder geocoder;
	
	@Inject
	public AddressAutoCompleteAdapter(
			LayoutInflater layoutInflater,
			Map<String, IGeocoderFactory> geocodersFact,
			Application app,
			GeocoderChooser geocoderChooser,
			@Assisted int maxSuggestionsCount) {
		super(app,R.layout.autocomplete_address_item);
		this.layoutInflater = layoutInflater;
		suggested = new ArrayList<Address>();
		lastSuggested = new ArrayList<Address>();
		String geocoderToUse = geocoderChooser.choose();
		Log.i("MyPlaces", "used geocoder: " + geocoderToUse);
		geocoder = geocodersFact.get(geocoderToUse).create(maxSuggestionsCount);
	}
	
	@Override
    public int getCount() {
        return suggested.size();
    }

    @Override
    public Address getItem(int index) {
        return suggested.get(index);
    }
    
    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
        	
        	@Override
        	public CharSequence convertResultToString (Object resultValue) {
        		Address addr = (Address) resultValue;
        		String addrString = "";
        		if (addr!= null) {
                    for (int i = 0; i < addr.getMaxAddressLineIndex()+1; i++) {
                    	addrString += addr.getAddressLine(i);
                    	if (i != addr.getMaxAddressLineIndex()) {
                    		addrString += ", ";
                    	}
                    }          
                }
        		return addrString;
        	}
        	
			@SuppressWarnings("unchecked")
			@Override
            protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    // Load address
                    try {
                    	suggested = geocoder.geocode(constraint.toString()); 
                    }
                    catch(IOException e) {
                    	e.printStackTrace();
                    	Log.d("MyPlaces","No data conection avilable");
                    	/* ToDo : put something useful here */
                    }
                    // If the address loader returns some results
                    if (suggested.size() > 0) {
                    	filterResults.values = suggested;
                    	filterResults.count = suggested.size();
                    // If there are no result with the given input we check last input and result.
                    // If the new input is more accurate than the previous, display result for the
                    // previous one.
                    } else if (lastInput != null && constraint.toString().contains(lastInput)) {
                    	filterResults.values = lastSuggested;
                    	filterResults.count = lastSuggested.size();
                    } else {
                    	filterResults.values = new ArrayList<Address>();
                    	filterResults.count = 0;
                    }
                    
                    if ( filterResults.count > 0) {
                    	lastSuggested = (ArrayList<Address>) filterResults.values;
                    }
                    lastInput = constraint.toString();
                }
                return filterResults;
            }

			@Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
				if(results != null && results.count > 0) 
					notifyDataSetChanged();
				else
					notifyDataSetInvalidated();
            }
        };
        return myFilter;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
    	TextView view = (TextView) super.getView(position, convertView, parent);
        if (view == null) {
            view = (TextView) layoutInflater.inflate(R.layout.autocomplete_address_item,null);
        }

        Address addr = this.getItem(position);
        if (addr!= null) {
            String addrString = (String) getFilter().convertResultToString(addr); 
            view.setText(addrString);       
         }
        return view;
    }

}
