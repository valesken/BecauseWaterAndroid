package org.becausewater.dropin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;


public class HomeActivity extends ActionBarActivity {

	/*
	 * Member variables declared here. For the most part, they need to be here in order to be referenced in multiple
	 * functions and fragments and to be used in event listeners while maintaining dynamic allocation. 
	 */
	
    private NavDrawerFragment mNavDrawerFragment; // Managed behaviors, interactions, and presentation of the nav drawer
    private static MapFragment map;
    private static Contact_Fragment cf;
    private static Simple_Fragment sf;
    private static LocationManager locationManager;
    private static Drop lastDrop;
    private Context context;
    private Uri storeURL;
	private boolean isDrawerOpen = false;
    private String fragmentName = "";
    private static JSONParser jParser;
    private static ConnectivityManager connectivityManager;
    private static NetworkInfo networkInfo;
    
    protected static Intent launchBrowser;
    protected static Menu mMenu;
    
    public static FragmentManager fm;
    public static FragmentTransaction ft;

    /*
     * Member functions declared and defined here.
     */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_home);
        context = this;
        fm = getSupportFragmentManager();
        map = new MapFragment();
        cf = new Contact_Fragment();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        if (savedInstanceState == null) {
            ft = fm.beginTransaction();
            ft.add(R.id.container, map)
              .commit();
        }

        mNavDrawerFragment = (NavDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBar actionBar = getSupportActionBar();
        
        // Set up the drawer.
        mNavDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);
        
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        	public void onDrawerClosed(View view) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.my_translucent_gray)));
                setDrawerIndicatorEnabled(true);
        		invalidateOptionsMenu();
        	}
        	
        	public void onDrawerOpened(View drawerView) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.my_light_gray)));
                setDrawerIndicatorEnabled(false);
        		invalidateOptionsMenu();
        	}
        	
        	public void onDrawerSlide(View drawerView, float slideOffset) {
				if(slideOffset > .55 && !isDrawerOpen) {
        			onDrawerOpened(drawerView);
        			isDrawerOpen = true;
        		}
				else if(slideOffset < .45 && isDrawerOpen) {
					onDrawerClosed(drawerView);
					isDrawerOpen = false;
				}
				super.onDrawerSlide(drawerView, slideOffset);
        	}
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Button refreshMap = (Button) findViewById(R.id.refresh_map);
        Button aboutBecauseWater = (Button) findViewById(R.id.about_because_water_button);
        Button aboutDropInApp = (Button) findViewById(R.id.about_drop_in_app_button);
        Button onlineStore = (Button) findViewById(R.id.online_store_button);
        Button contactUs = (Button) findViewById(R.id.contact_us_button);
        Button privacyPolicy = (Button) findViewById(R.id.privacy_policy_button);
        
        storeURL = Uri.parse("http://becausewater.org/store-2/");
        
        refreshMap.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		mDrawerLayout.closeDrawer(Gravity.LEFT);
        		map.refresh();
        	}
        });
        
        aboutBecauseWater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(fm.getBackStackEntryCount() > 0)
					fragmentName = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
				sf = new Simple_Fragment(0);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				if(fragmentName != "infoBW") {
					if(fragmentName == "infoDI" || fragmentName == "cf" || fragmentName == "privacy")
						fm.popBackStack();
					ft = fm.beginTransaction();
					ft.add(R.id.container, sf)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack("infoBW")
						.commit();
				}
			}
		});
        aboutDropInApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(fm.getBackStackEntryCount() > 0)
					fragmentName = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
				sf = new Simple_Fragment(1);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				if(fragmentName != "infoDI") {
					if(fragmentName == "infoBW" || fragmentName == "cf" || fragmentName == "privacy") 
						fm.popBackStack();
					ft = fm.beginTransaction();
					ft.add(R.id.container, sf)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack("infoDI")
						.commit();
				}
			}
		});
        onlineStore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				launchBrowser = new Intent(Intent.ACTION_VIEW, storeURL); 
				startActivity(launchBrowser);
			}
		});
        contactUs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(fm.getBackStackEntryCount() > 0)
					fragmentName = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				if(fragmentName != "cf") {
					if(fragmentName == "infoBW" || fragmentName == "infoDI" || fragmentName == "privacy")
						fm.popBackStack();
					ft = fm.beginTransaction();
					ft.add(R.id.container, cf)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack("cf")
						.commit();
				}
			}
		});
        privacyPolicy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(fm.getBackStackEntryCount() > 0)
					fragmentName = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
				sf = new Simple_Fragment(2);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				if(fragmentName != "privacy") {
					if(fragmentName == "infoDI" || fragmentName == "cf" || fragmentName == "infoBW")
						fm.popBackStack();
					ft = fm.beginTransaction();
					ft.add(R.id.container, sf)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack("privacy")
						.commit();
				}
			}
		});
    }

	public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        
        LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.action_bar_layout, null);
        actionBar.setCustomView(v, layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        if (!mNavDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, mMenu);
            restoreActionBar();
            if(fm.getBackStackEntryCount() > 0)
            	mMenu.findItem(R.id.add_new).setIcon(R.drawable.ic_action_blank).setEnabled(false);
            else
            	mMenu.findItem(R.id.add_new).setIcon(R.drawable.ic_action_add).setEnabled(true);
            return true;
        }
        return super.onCreateOptionsMenu(mMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add_new) {
        	map.toAdd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
    	fragmentName = "";
    	if(fm.getBackStackEntryCount() == 1) {
            if(mMenu != null)
            	mMenu.findItem(R.id.add_new).setIcon(R.drawable.ic_action_add).setEnabled(true);
            fm.popBackStack();
    	}
    	else
    		super.onBackPressed();
    }
    
    public static void addPin(Drop drop) {
    	map.addPin(drop);
    }
    
    /*
     * Fragments begin here. Used to manage behavior of the various fragments in the activity.
     */
    
    public static class MapFragment extends Fragment {

    	private MapView mapView;
    	private GoogleMap map;
    	private double latitude = 42.3581, longitude = -71.0636;
    	private View rootView;
		private ArrayList<Drop> drops;
		private HashMap<Marker, Drop> dropMap;
        private Info_Fragment info;
        
    	
        public MapFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            dropMap = new HashMap<Marker, Drop>();
            
            if(!isConnected()) {
            	new AlertDialog.Builder(rootView.getContext())
            		.setTitle("No Connection")
            		.setIcon(android.R.drawable.ic_dialog_alert)
            		.setMessage("Internet not available. You will not be able to see drops. Please check your connectivity and then refresh or reload Drop In.")
            		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            			@Override
            			public void onClick(DialogInterface dialog, int which) { }
            		})
            		.show();
            }
        	
            // get MapView from layout
            mapView = (MapView) rootView.findViewById(R.id.mapview);
            mapView.onCreate(savedInstanceState);
            //get GoogleMap from MapView + initialization
            map = mapView.getMap();
            MapsInitializer.initialize(mapView.getContext());
            map.setMyLocationEnabled(true);

            int actionBarSize = getActionBarHeight();
            map.setPadding(0, actionBarSize, 0, actionBarSize);
            
            map.getUiSettings().setZoomControlsEnabled(true);
            
            refresh();
        	
        	map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					info = new Info_Fragment(dropMap.get(marker));
					ft = fm.beginTransaction();
					ft.add(R.id.container, info)
					  .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					  .addToBackStack("info")
					  .commit();
				}
			});
            
            return rootView;
        }
        
        private boolean isConnected() {
    		connectivityManager = (ConnectivityManager) rootView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    		networkInfo = connectivityManager.getActiveNetworkInfo();
    		
    		if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable())
    			return false;
    		
    		return true;
    	}
        
        public void toAdd() {
        	Location myLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        	latitude = myLoc.getLatitude();
        	longitude = myLoc.getLongitude();
        	ft = fm.beginTransaction();
        	Add_Fragment af = new Add_Fragment(latitude, longitude);
        	ft.add(R.id.container, af)
			  .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        	  .addToBackStack("af")
        	  .commit();
        }
        
        private void refresh() {
            LatLng myLatLng;
            CameraPosition myPosition;
            
            Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(myLocation != null) {
            	latitude = myLocation.getLatitude();
            	longitude = myLocation.getLongitude();
            }
            myLatLng = new LatLng(latitude, longitude);
        	myPosition = new CameraPosition.Builder()
    			.target(myLatLng)
    			.zoom(16)
    			.bearing(0)
    			.tilt(0)
    			.build();
        	map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
            
            queryDatabase();
            
            for(int i = 0; i < drops.size(); ++i)
            	addPin(drops.get(i));
        }
        
        private void queryDatabase() {
        	
        	// Reset map
        	if(drops != null) {
        		for(int i = 0; i < drops.size(); ++i)
        			drops.get(i).getMarker().remove();
        	}
        	
        	// Set up query address
        	String url_part1 = "foobar";
    		String url_part2 = "&lng=";
    		String url = "".concat(url_part1)
    				.concat(Double.toString(latitude))
    				.concat(url_part2)
    				.concat(Double.toString(longitude));
    		
    		// Get query results
        	jParser = new JSONParser(url);
        	jParser.fetchJSON(); // Busy-waits in this method until the thread is complete
        	
        	drops = new ArrayList<Drop>();
        	jParser.getDrops(drops);
        }
        
        public void addPin(Drop drop) {
        	Marker marker = map.addMarker(new MarkerOptions()
        		.position(new LatLng(drop.getLatitude(), drop.getLongitude()))
        		.title(drop.getName())
        		.snippet(drop.getDetails()));
        	if(drop.getCategory().equals("Drop In"))
    			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.add_drop_marker2_64));
    		else if(drop.getCategory().equals("Public Fountain"))
    			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.add_drop_marker3_55));
    		else
    			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.add_drop_marker1_55));
        	
        	drop.setMarker(marker);
        	dropMap.put(marker, drop); // Make drop accessible by unique Marker
        }
        
        private int getActionBarHeight(){
        	int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
            TypedValue typedValue = new TypedValue(); 
            TypedArray a = rootView.getContext().obtainStyledAttributes(typedValue.data, textSizeAttr);
            int textSize = a.getDimensionPixelSize(0, -1);
            a.recycle();
        	
        	return (textSize+5);
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
    	
    	private View rootView;
    	private double longitude, latitude;
    	private String result;
        private Context context;
        private EditText enter_address, locationName;
        private List<Address> addresses;
        private Address address;
        private Geocoder geocoder;
    	
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
            rootView = inflater.inflate(R.layout.fragment_add_droplet, container, false);
            context = rootView.getContext();
            setToCurrentLoc();
            locationName = (EditText) rootView.findViewById(R.id.name_of_location);
            if(mMenu != null)
            	mMenu.findItem(R.id.add_new).setIcon(R.drawable.ic_action_blank).setEnabled(false);
            
            Button submit = (Button) rootView.findViewById(R.id.confirm_button);
            Button useMyLoc = (Button) rootView.findViewById(R.id.use_my_loc_button);
            
            submit.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
        			if(locationName.getText().toString().length() > 0) {
        				if(enter_address.getText().toString().length() > 0) {
        					ft = fm.beginTransaction();
        					Confirm_Fragment cf = new Confirm_Fragment(enter_address.getText().toString(), locationName.getText().toString());
        					ft.add(R.id.container, cf)
          			  	  		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          			  	  		.addToBackStack("cf")
          			  	  		.commit();
        				}
        				else {
        					Toast.makeText(context, "Please enter an address", Toast.LENGTH_LONG)
            					.show();
        				}
        			}
        			else {
        				Toast.makeText(context, "Please enter a name for the address", Toast.LENGTH_LONG)
    						.show();
        			}
            	}
            });
            useMyLoc.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setToCurrentLoc();
				}
			});
            
            return rootView;
        }

        // Convert longitude & latitude to address and change addr's text to the address
        private void setToCurrentLoc() {

            enter_address = (EditText) rootView.findViewById(R.id.user_inaddress);
            geocoder = new Geocoder(context, Locale.getDefault());
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
        }
    	
    }
    
    public static class Confirm_Fragment extends Fragment {
    	
    	private String address_string, url_string, location_name;
    	private Context context;
    	private double latitude, longitude;
    	private EditText locationDescription, userName;
    	private URLEncoder urlEncoder;
    	MapView mapView2;
    	GoogleMap map;
    	LocationManager locationManager;
    	
    	public Confirm_Fragment() {
    		address_string = "";
    		url_string = "";
    		location_name = "";
    	}
    	
    	public Confirm_Fragment(String r) {
    		address_string = r;
    		url_string = "";
    		location_name = "";
    	}
    	
    	public Confirm_Fragment(String r, String l) {
    		address_string = r;
    		url_string = "";
    		location_name = l;
    	}
    	
    	@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_confirm, container, false);
            context = rootView.getContext();
            
            /*
             * This OnTouchListener is used in order to ensure that the user cannot accidentally touch Views from
             * the previous fragment (the Add_Fragment). E.g. Accidentally accessing the EditText @+id/user_inaddress 
             */
            rootView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
            
            mapView2 = (MapView) rootView.findViewById(R.id.mapview2);
            mapView2.onCreate(savedInstanceState);
            map = mapView2.getMap();
            MapsInitializer.initialize(mapView2.getContext());
            map.setMyLocationEnabled(false);
            locationManager = (LocationManager) rootView.getContext().getSystemService(LOCATION_SERVICE);
            map.getUiSettings().setZoomControlsEnabled(false);
            
            Geocoder geo = new Geocoder(context, Locale.getDefault());
            List<Address> addressList;
            Address address = null;
            try {
            	addressList = geo.getFromLocationName(address_string,5);
            	if(addressList != null) {
            		address = addressList.get(0);
            		latitude = address.getLatitude();
            		longitude = address.getLongitude();
            	}
            }
            catch(IOException e) {
            	e.printStackTrace();
            }
            
        	map.addMarker(new MarkerOptions()
        		.icon(BitmapDescriptorFactory.fromResource(R.drawable.add_drop_marker1_55))
    			.position(new LatLng(latitude, longitude)));
        	CameraPosition myPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude))
				.zoom(16)
				.bearing(0)
				.tilt(0)
				.build();
        	map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
            
        	locationDescription = (EditText) rootView.findViewById(R.id.loc_description);
        	userName = (EditText) rootView.findViewById(R.id.user_name);
            Button confirm = (Button) rootView.findViewById(R.id.add_drop_button);
            confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fm.popBackStack();
					fm.popBackStack();
		            if(mMenu != null)
		            	mMenu.findItem(R.id.add_new).setIcon(R.drawable.ic_action_add).setEnabled(true);
		            Drop drop = new Drop();
		            drop.setLatitude(latitude);
		            drop.setLongitude(longitude);
		            drop.setName(location_name);
		            drop.setDetails(locationDescription.getText().toString());
		            drop.setCategory("User Submitted");
		            drop.setAddress(address_string);
		            drop.setUser(userName.getText().toString());
		            try {
		            	//name, address, category, lat, lng, details, locationName, personName
		            	url_string = url_string.concat("foobar")
		            		.concat(urlEncoder.encode(Double.toString(latitude), "UTF-8"))
		            		.concat("&lng=")
		            		.concat(urlEncoder.encode(Double.toString(longitude), "UTF-8"))
		            		.concat("&name=")
		            		.concat(urlEncoder.encode(drop.getName(), "UTF-8"))
		            		.concat("&address=")
		            		.concat(urlEncoder.encode(drop.getAddress(), "UTF-8"))
		            		.concat("&category=")
		            		.concat(urlEncoder.encode(Integer.toString(3), "UTF-8"))
		            		.concat("&details=")
		            		.concat(urlEncoder.encode(drop.getDetails(), "UTF-8"))
		            		.concat("&personName=")
		            		.concat(urlEncoder.encode(drop.getUser(), "UTF-8"))
		            		.concat("&locationName=")
		            		.concat(urlEncoder.encode(drop.getName(), "UTF-8"));
		            }
		            catch (UnsupportedEncodingException e) {
		            	e.printStackTrace();
		            }
		            jParser.pushDrop(url_string);
				}
			});
            
            return rootView;
    	}

        @Override
        public void onResume() {
        	mapView2.onResume();
        	super.onResume();
        }
        
        @Override
        public void onDestroy() {
        	super.onDestroy();
        	mapView2.onDestroy();
        }
        
        @Override
        public void onLowMemory() {
        	super.onLowMemory();
        	mapView2.onLowMemory();
        }
    }

    public static class Contact_Fragment extends Fragment {
    	
    	View rootView;
    	Uri facebookURL, twitterURL, youtubeURL, pinterestURL;
    	
    	public Contact_Fragment () {
    	}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
    		
    		Button facebook = (Button) rootView.findViewById(R.id.facebook_button); 
    		facebookURL = Uri.parse("https://www.facebook.com/BeCauseWater");
    		Button twitter = (Button) rootView.findViewById(R.id.twitter_button);
    		twitterURL = Uri.parse("https://www.twitter.com/BeCauseWater");
    		Button youtube = (Button) rootView.findViewById(R.id.youtube_button);
    		youtubeURL = Uri.parse("https://www.youtube.com/becausewater");
    		Button pinterest = (Button) rootView.findViewById(R.id.pinterest_button);
    		pinterestURL = Uri.parse("http://www.pinterest.com/BeCauseWater");
    		Button email = (Button) rootView.findViewById(R.id.email_button);
    		
    		facebook.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchBrowser = new Intent(Intent.ACTION_VIEW, facebookURL); 
					getActivity().startActivity(launchBrowser);
				}
			});
    		twitter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchBrowser = new Intent(Intent.ACTION_VIEW, twitterURL); 
					getActivity().startActivity(launchBrowser);
				}
			});
    		youtube.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchBrowser = new Intent(Intent.ACTION_VIEW, youtubeURL); 
					getActivity().startActivity(launchBrowser);
				}
			});
    		pinterest.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchBrowser = new Intent(Intent.ACTION_VIEW, pinterestURL); 
					getActivity().startActivity(launchBrowser);
				}
			});
    		email.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Info@BeCauseWater.com
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"jeff.valesken@gmail.com"});
					i.putExtra(Intent.EXTRA_SUBJECT, "Test subject");
					i.putExtra(Intent.EXTRA_TEXT   , "Test body");
					try {
					    startActivity(Intent.createChooser(i, "Send mail..."));
					} catch (android.content.ActivityNotFoundException ex) {
					    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
					}
				}
			});
    		
    		
    		return rootView;
    	}
    }

    public static class Info_Fragment extends Fragment {
    	
    	private View rootView;
    	private Drop drop;
    	private double latitude, longitude;
    	private String saddr, daddr;
    	
    	public Info_Fragment() {
    		if(lastDrop == null)
    			this.drop = new Drop();
    		else
    			this.drop = lastDrop;
    		latitude = 0;
    		longitude = 0;
    	}
    	
    	public Info_Fragment(Drop d) {
    		this.drop = d;
    		latitude = 0;
    		longitude = 0;
    	}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		rootView = inflater.inflate(R.layout.fragment_drop_info, container, false);
    		lastDrop = drop;
    		
    		TextView dropTitle = (TextView) rootView.findViewById(R.id.drop_info_title);
    		dropTitle.setText(drop.getName());
    		TextView dropAddress = (TextView) rootView.findViewById(R.id.drop_info_address);
    		dropAddress.setText(drop.getAddress());
    		TextView dropDescription = (TextView) rootView.findViewById(R.id.drop_info_description);
    		dropDescription.setText(drop.getDetails());
    		
    		Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(myLocation != null) {
            	latitude = myLocation.getLatitude();
            	longitude = myLocation.getLongitude();
            }
    		
    		Button report = (Button) rootView.findViewById(R.id.report_a_problem);
    		Button toHere = (Button) rootView.findViewById(R.id.drop_info_to_here);
    		Button fromHere = (Button) rootView.findViewById(R.id.drop_info_from_here);
    		
    		report.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ft = fm.beginTransaction();
					ft.add(R.id.container, cf)
					  .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					  .addToBackStack("cf")
					  .commit();
				}
			});
    		
    		toHere.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saddr = "".concat(Double.toString(latitude)).concat(",").concat(Double.toString(longitude));
					daddr = "".concat(Double.toString(drop.getLatitude())).concat(",").concat(Double.toString(drop.getLongitude()));
					Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + saddr + "&daddr=" + daddr));
					fm.popBackStack();
					startActivity(i);
				}
			});
    		fromHere.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saddr = "".concat(Double.toString(drop.getLatitude())).concat(",").concat(Double.toString(drop.getLongitude()));
					daddr = "".concat(Double.toString(latitude)).concat(",").concat(Double.toString(longitude));
					Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + saddr + "&daddr=" + daddr));
					fm.popBackStack();
					startActivity(i);
				}
			});
    		
    		return rootView;
    	}
    }

    public static class Simple_Fragment extends Fragment {
    	private View rootView;
    	private int layoutNumber;
    	
    	public Simple_Fragment(int num) {
    		this.layoutNumber = num;
    	}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            
            switch(layoutNumber)
            {
            case 0:
            	rootView = inflater.inflate(R.layout.fragment_about_because_water, container, false);
            	break;
            case 1:
            	rootView = inflater.inflate(R.layout.fragment_about_drop_in, container, false);
            	break;
            case 2:
            	rootView = inflater.inflate(R.layout.fragment_privacy, container, false);
            	break;
            }
            
            return rootView;
    	}
    }
}
