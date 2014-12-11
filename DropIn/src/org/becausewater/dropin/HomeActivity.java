package org.becausewater.dropin;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static MapFragment map;
    public static FragmentManager fm;
    public static FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fm = getSupportFragmentManager();
        map = new MapFragment();
        if (savedInstanceState == null) {
            ft = fm.beginTransaction();
            ft.add(R.id.container, map)
              .commit();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by showing dialogs with About info
        switch (position) {
        case 0:
            new AlertDialog.Builder(this)
        		.setMessage(R.string.about_drop_in_app_content)
        		.setTitle(R.string.about_drop_in_app)
        		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) { } // Do nothing to just go back
    			})
        		.show();
            break;
        case 1:
            new AlertDialog.Builder(this)
        		.setMessage(R.string.about_because_water_content)
        		.setTitle(R.string.about_because_water)
        		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    				@Override
    				public void onClick(DialogInterface dialog, int which) { } // Do nothing to just go back
    			})
        		.show();
            break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void toAdd(View b) {
    	map.toAdd(b);
    }
    
    public static void addPin(double lat, double lon) {
    	map.addPin(lat, lon);
    }
    
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MapFragment extends Fragment {

    	MapView mapView;
    	GoogleMap map;
    	LocationManager locationManager;
    	double latitude, longitude;
    	View rootView;
    	
        public MapFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            LatLng myLatLng;
            CameraPosition myPosition;
        	
            // get MapView from layout
            mapView = (MapView) rootView.findViewById(R.id.mapview);
            mapView.onCreate(savedInstanceState);
            //get GoogleMap from MapView + initialization
            map = mapView.getMap();
            MapsInitializer.initialize(mapView.getContext());
            map.setMyLocationEnabled(true);
            //LocationManager locationManager = (LocationManager) rootView.getContext().getSystemService(LOCATION_SERVICE);
            locationManager = (LocationManager) rootView.getContext().getSystemService(LOCATION_SERVICE);
            map.getUiSettings().setZoomControlsEnabled(true);
            
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(myLocation != null) {
            	myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            	myPosition = new CameraPosition.Builder()
            		.target(myLatLng)
            		.zoom(16)
            		.bearing(0)
            		.tilt(0)
            		.build();
            	map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
            }
            else {
            	myLatLng = new LatLng(42.3581, -71.0636); // Boston
            	myPosition = new CameraPosition.Builder()
            		.target(myLatLng)
            		.zoom(16)
            		.bearing(0)
            		.tilt(0)
            		.build();
            	map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
            }
            
            return rootView;
        }
        
        // Testing
        public void toAdd(View b) {
        	Location myLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        	latitude = myLoc.getLatitude();
        	longitude = myLoc.getLongitude();
        	ft = fm.beginTransaction();
        	Add_Fragment af = new Add_Fragment(latitude, longitude);
        	ft.add(R.id.container, af)
        	  .addToBackStack("af")
        	  .commit();
        }
        
        public void addPin(double lat, double lon) {
        	map.addMarker(new MarkerOptions()
        	   .position(new LatLng(lat, lon))
        	   .title("test"));
        }
        
        @Override
        public void onResume() {
        	mapView.onResume();
        	super.onResume();
        }
        
        @Override
        public void onDestroy() {
        	super.onDestroy();
        	mapView.onDestroy();
        }
        
        @Override
        public void onLowMemory() {
        	super.onLowMemory();
        	mapView.onLowMemory();
        }
    }
    
    public static class Add_Fragment extends Fragment {
    	
    	private double longitude, latitude;
    	private String result;
        private Context context;
        private EditText enter_address;
    	
    	public Add_Fragment() {
    		latitude = 0;
    		longitude = 0;
    	}
    	
    	public Add_Fragment(double lat, double lon) {
    		latitude = lat;
    		longitude = lon;
    	}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_droplet, container, false);
            List<Address> addresses;
            context = rootView.getContext();

            // Convert longitude & latitude to address and change addr's text to the address
            enter_address = (EditText) rootView.findViewById(R.id.user_inaddress);
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            Address address;
            result = "42 Bay State Rd, Boston, MA, 02215";
            try{
            	for(int i = 0; i < 10; ++i) {
            		addresses = geocoder.getFromLocation(latitude, longitude, 1);
            		if(!addresses.isEmpty()) {
            			address = addresses.get(0);
            			
            			result = String.format("%s, %s, %s, %s", 
            					(address.getMaxAddressLineIndex() > 0) ? address.getAddressLine(0) : "", 
            					(address.getLocality() != null) ? address.getLocality() : "",
            					(address.getAdminArea() != null) ? address.getAdminArea() : "",
            					(address.getPostalCode() != null) ? address.getPostalCode() : "");
            			break;
            		}
            	}
            }
            catch(IOException e) {
            	e.printStackTrace();
            }
            enter_address.setText(result);
            
            Button cancel = (Button) rootView.findViewById(R.id.cancel_add_button);
            cancel.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				fm.popBackStack();
    			}
    		});
            
            Button submit = (Button) rootView.findViewById(R.id.submit_button);
            submit.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		if(enter_address.getText().toString().length() > 0) {
            			ft = fm.beginTransaction();
            			Confirm_Fragment cf = new Confirm_Fragment(enter_address.getText().toString());
            			ft.add(R.id.container, cf)
            			  .addToBackStack("cf")
            			  .commit();
            		}
            		else {
            			Toast.makeText(context, "Please enter an address", Toast.LENGTH_LONG)
            				.show();
            		}
            	}
            });
            
            return rootView;
        }
    	
    }
    
    public static class Confirm_Fragment extends Fragment {
    	
    	private String result;
    	private Context context;
    	private double latitude, longitude;
    	
    	public Confirm_Fragment() {
    		result = "";
    	}
    	
    	public Confirm_Fragment(String r) {
    		result = r;
    	}
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_confirm, container, false);
            context = rootView.getContext();
            
            EditText addressLine = (EditText) rootView.findViewById(R.id.address);
            EditText city = (EditText) rootView.findViewById(R.id.city);
            EditText state = (EditText) rootView.findViewById(R.id.state);
            EditText zipcode = (EditText) rootView.findViewById(R.id.zipcode);
            EditText lat = (EditText) rootView.findViewById(R.id.latitude);
            EditText lon = (EditText) rootView.findViewById(R.id.longitude);
            
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addressList;
            Address address = null;
            try {
            	addressList = geo.getFromLocationName(result,5);
            	if(addressList != null) {
            		address = addressList.get(0);
            		if(address.getMaxAddressLineIndex() > 0)
            			addressLine.setText(address.getAddressLine(0));
            		if(address.getLocality() != null )
            			city.setText(address.getLocality());
            		if(address.getAdminArea() != null)
            			state.setText(address.getAdminArea());
            		if(address.getPostalCode() != null)
            			zipcode.setText(address.getPostalCode());
            		latitude = address.getLatitude();
            		longitude = address.getLongitude();
            		lat.setText(Double.toString(latitude));
            		lon.setText(Double.toString(longitude));
            	}
            }
            catch(IOException e) {
            	e.printStackTrace();
            }

            Button back = (Button) rootView.findViewById(R.id.back_button);
            back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fm.popBackStack();
				}
			});
            
            Button confirm = (Button) rootView.findViewById(R.id.confirm_button);
            confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fm.popBackStack();
					fm.popBackStack();
					addPin(latitude, longitude);
				}
			});
            
            return rootView;
    	}
    }
}
