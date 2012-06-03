package net.esial.myplaces.geocoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import net.esial.myplaces.geoloc.GeoLocator;


import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

public class AndroidGeocoder implements IGeocoder {
	
	private Application app;
	private int maxSuggestionsCount;
	private GeoLocator geoLocator;
	
	public AndroidGeocoder(
			Application app,
			GeoLocator geoLocator,
			int maxSuggestionsCount){
		this.maxSuggestionsCount = maxSuggestionsCount;
		this.app = app;
		this.geoLocator = geoLocator;
	}
	
	/**
	 * Take a string representing an address or a place and return a list of corresponding
	 * addresses
	 * @param addrString A String representing an address or place
	 * @return List of Address corresponding to the given address description
	 * @throws IOException If Geocoder API cannot be called
	 */
	public ArrayList<Address> geocode(String addrString) throws IOException {
		//Try to filter with the current location
		Location current = geoLocator.getCurrentLocation();
		Geocoder geocoder = new Geocoder(app,Locale.getDefault());
		ArrayList<Address> local = new ArrayList<Address>();
		if (current != null) {
			local = (ArrayList<Address>) geocoder.getFromLocationName(
					addrString,
					maxSuggestionsCount,
					current.getLatitude()-0.1,
					current.getLongitude()-0.1,
					current.getLatitude()+0.1,
					current.getLongitude()+0.1);
		}
		if (local.size() < maxSuggestionsCount) {
			ArrayList<Address> global = (ArrayList<Address>) geocoder.getFromLocationName(
					addrString, maxSuggestionsCount - local.size());
			for (Address globalAddr : global) {
				if (!containsAddress(local,globalAddr))
					local.add(globalAddr);
			}
		}
		return local;
	}
	
	/* An helper to test if a provided address is equals to one in a provided list of addresses
	 * Don't use ArrayList<Address>.contains because Address.equals seems to not do his job :/
	 */
	private boolean containsAddress(ArrayList<Address> addrList, Address addrToTest) {
		Boolean contains = false;
		for (Address addr : addrList) {
			if (addr.getLatitude() == addrToTest.getLatitude() &&
					addr.getLongitude() == addrToTest.getLongitude())
				return true;
		}
		return contains;
	}
	

}
