package net.esial.myplaces.geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;

@Singleton
public class GeocoderChooser {
	
	private Application app;
	
	@Inject
	public GeocoderChooser(Application app) {
		this.app = app;		
	}
	
	/* Check if the native Android geocoder is usable.
	 * If it's presents return "Android", if not "Http"
	 */
	public String choose() {
		Geocoder geocoder = new Geocoder(app,Locale.getDefault());
		if (!Geocoder.isPresent()) {
			return "Http";
		} else {
			final String locName = "1600 Amphitheatre Parkway, Mountain View, CA";
		    try {
		        final List<Address> list = geocoder.getFromLocationName(locName, 1);
		        if ( list != null && !list.isEmpty() ) {
		        	return "Android";
		        }
		        else {
		            return "Http";
		        }
		    } catch (IOException e) {
		        return "Http";
		    }
		}
	}

}
