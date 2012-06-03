package net.esial.myplaces.geocoder;

import com.google.inject.Inject;

import net.esial.myplaces.geoloc.GeoLocator;
import android.app.Application;

public class AndroidGeocoderFactory implements IGeocoderFactory {
	
	private Application app;
	private GeoLocator geoLocator;
	
	@Inject
	public AndroidGeocoderFactory(Application app, GeoLocator geoLocator) {
		this.app = app;
		this.geoLocator = geoLocator;
	}

	@Override
	public IGeocoder create(int maxSuggestionsCount) {
		return new AndroidGeocoder(app, geoLocator, maxSuggestionsCount);
	}

}
