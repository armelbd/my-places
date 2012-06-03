package net.esial.myplaces.geocoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import net.esial.myplaces.geoloc.GeoLocator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import android.location.Address;
import android.location.Location;

public class HttpGeocoder implements IGeocoder {
	
	private GeoLocator geoLocator;
	private int maxSuggestionsCount;
	
	@Inject
	public HttpGeocoder(GeoLocator geoLocator,
			@Assisted int maxSuggestionsCount) {
		this.geoLocator = geoLocator;
		this.maxSuggestionsCount = maxSuggestionsCount;
	}

	@Override
	public ArrayList<Address> geocode(String addrString) throws IOException {
		//Try to filter with the current location
		String query = "address="+addrString;
		Location current = geoLocator.getCurrentLocation();
		ArrayList<Address> local = new ArrayList<Address>();
		if (current != null) {
			String localQuery = query;
			localQuery += "&sensor=true";
			localQuery += "&bounds="+ 
					(current.getLatitude()-0.1) + "," +
					(current.getLongitude()-0.1) + "|" +
					(current.getLatitude()+0.1) + "," +
					(current.getLongitude()+0.1);
			local = getAddresses(getRawData(localQuery));
		}
		if (local.size() < maxSuggestionsCount) {
			String globalQuery = query + "&sensor=false";
			ArrayList<Address> global = getAddresses(getRawData(globalQuery));
			for (Address globalAddr : global) {
				if (!containsAddress(local,globalAddr))
					local.add(globalAddr);
			}
		}
		return local;
	}
	
	private JSONObject getRawData(String query) throws IOException {
		StringBuilder stringBuilder;
	    try {
	    	query = query.replace(" ","%20").replace(",", "%2C").replace("|", "%7C");
	    	HttpGet httpget = new HttpGet(
	    			"http://maps.google.com/maps/api/geocode/json?"+ query);
	    	HttpParams httpParameters = new BasicHttpParams();
	    	HttpClient client = new DefaultHttpClient(httpParameters);
	    	stringBuilder = new StringBuilder();
	    	HttpResponse response = client.execute(httpget);
	    	BufferedReader reader = new BufferedReader(
	    			new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
	    	int b;
	    	while ((b = reader.read()) != -1) {
	    		stringBuilder.append((char) b);
	    	}
	    } catch (ClientProtocolException e) {
	    	throw new IOException();
	    }
	    JSONObject jsonObject = new JSONObject();
	    try {
	        jsonObject = new JSONObject(stringBuilder.toString());
	    } catch (JSONException e) {
	    	throw new IOException(); 
	    }
	    return jsonObject;
	}
	
	private ArrayList<Address> getAddresses(JSONObject jsonObject) throws IOException  {
		ArrayList<Address> addresses = new ArrayList<Address>();
			try {
			JSONArray addrsJson = ((JSONArray)jsonObject.get("results"));
			for (int i = 0; i < Math.min(addrsJson.length(),maxSuggestionsCount); i++) {
				Address addr = new Address(Locale.getDefault());
				JSONObject addrJson = addrsJson.getJSONObject(i);
				addr.setAddressLine(0, addrJson.getString("formatted_address"));
				addr.setLatitude(addrJson.
						getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
				addr.setLongitude(addrJson.
						getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
				addresses.add(addr);
			}
		} catch (Exception e) {
			throw new IOException();
		}
		return addresses;
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
