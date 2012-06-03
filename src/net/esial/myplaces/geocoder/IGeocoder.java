package net.esial.myplaces.geocoder;

import java.io.IOException;
import java.util.ArrayList;

import android.location.Address;

public interface IGeocoder {
	
	public ArrayList<Address> geocode(String addrString) throws IOException;

}
