package net.esial.myplaces.views;

import net.esial.myplaces.R;
import net.esial.myplaces.controllers.AddressAutoCompleteAdapter;
import net.esial.myplaces.controllers.IAddressAutoCompleteAdapterFactory;
import net.esial.myplaces.model.DataApi;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;

import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class AddressBarFragment extends RoboSherlockFragment {
	
	@Inject private IAddressAutoCompleteAdapterFactory fact;
	@Inject private DataApi dataApi;
	
	private static final int MAX_ADDRESS_SUGGESTION = 10;
	private ActionBarAutoCompleteTextView autoCompAddrTV;

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Configure the autocomplete text view for addresses */
        autoCompAddrTV = (ActionBarAutoCompleteTextView) 
        		getLayoutInflater(savedInstanceState).inflate(R.layout.autocomplete_address,null);
        AddressAutoCompleteAdapter addrautoCompAddapter = fact.create(MAX_ADDRESS_SUGGESTION);
        autoCompAddrTV.setAdapter(addrautoCompAddapter);
        
        autoCompAddrTV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Address> adapter = (ArrayAdapter<Address>) parent.getAdapter();
				Address addr = (Address) parent.getAdapter().getItem(position);
				dataApi.insertPlace(adapter.getFilter().convertResultToString(addr).toString(),
						addr.getLatitude(), addr.getLongitude());
			}
		});
		
        // Enable menu support for this fragment
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// Return null because this fragment doesn't have an UI.
		// It creates a menu item for the attached activity.
		return null;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Set the menu item for auto complete address
        menu.findItem(R.id.new_places).setActionView(autoCompAddrTV);
        
	}
}
