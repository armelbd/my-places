package net.esial.myplaces.geocoder;


public interface IGeocoderFactory {
	
	IGeocoder create(int maxSuggestionsCount);

}
