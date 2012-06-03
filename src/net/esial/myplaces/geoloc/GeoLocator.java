package net.esial.myplaces.geoloc;

import android.location.Location;
import android.location.LocationManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GeoLocator {
	
	private LocationManager locManager;
	
	@Inject
	public GeoLocator(LocationManager locManager) {
		this.locManager = locManager;
	}
	
	public Location getCurrentLocation() {
		return locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	}

}
