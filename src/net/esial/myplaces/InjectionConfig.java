package net.esial.myplaces;

import net.esial.myplaces.controllers.IAddressAutoCompleteAdapterFactory;
import net.esial.myplaces.geocoder.AndroidGeocoderFactory;
import net.esial.myplaces.geocoder.HttpGeocoderFactory;
import net.esial.myplaces.geocoder.IGeocoderFactory;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;


public class InjectionConfig extends AbstractModule{

	@Override
	protected void configure() {
		MapBinder<String, IGeocoderFactory> mapBinder = 
				MapBinder.newMapBinder(binder(), String.class, IGeocoderFactory.class);
		mapBinder.addBinding("Android").to(AndroidGeocoderFactory.class);
		mapBinder.addBinding("Http").to(HttpGeocoderFactory.class);
		install(new FactoryModuleBuilder().build(IAddressAutoCompleteAdapterFactory.class));
	}

}
