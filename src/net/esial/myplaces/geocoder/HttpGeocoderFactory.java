package net.esial.myplaces.geocoder;

import com.google.inject.Inject;

import net.esial.myplaces.geoloc.GeoLocator;

public class HttpGeocoderFactory implements IGeocoderFactory{
	
	private GeoLocator geoLocator;
	
	@Inject 
	public HttpGeocoderFactory(GeoLocator geoLocator) {
		this.geoLocator = geoLocator;
	}



	@Override
	public IGeocoder create(int maxSuggestionsCount) {
		return new HttpGeocoder(geoLocator, maxSuggestionsCount);
	}

}
