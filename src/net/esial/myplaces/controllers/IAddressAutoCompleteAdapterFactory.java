package net.esial.myplaces.controllers;


public interface IAddressAutoCompleteAdapterFactory {
	
	public AddressAutoCompleteAdapter create(int maxSuggestionsCount);

}
