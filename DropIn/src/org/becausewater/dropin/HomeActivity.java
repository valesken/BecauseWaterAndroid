package org.becausewater.dropin;

import java.io.IOException;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;


public class HomeActivity extends ActionBarActivity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavDrawerFragment mNavDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private static MapFragment map;
    private static Contact_Fragment cf;
    private static Simple_Fragment sf;
    private Context context;
    private Uri storeURL;
	private boolean isDrawerOpen = false;
    private String fragmentName = "";
    protected static Intent launchBrowser;
    protected static Menu mMenu;
    public static FragmentManager fm;
    public static FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_home);
        context = this;
        fm = getSupportFragmentManager();
        map = new MapFragment();
        cf = new Contact_Fragment();
        
        if (savedInstanceState == null) {
            ft = fm.beginTransaction();
            ft.add(R.id.container, map)
              .commit();
        }

        mNavDrawerFragment = (NavDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBar actionBar = getSupportActionBar();
        
        // Set up the drawer.
        mNavDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);
        
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        	public void onDrawerClosed(View view) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.my_translucent_gray)));
        		invalidateOptionsMenu();
        	}
        	
        	public void onDrawerOpened(View drawerView) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.my_light_gray)));
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

        Button aboutBecauseWater = (Button) findViewById(R.id.about_because_water_button);
        Button aboutDropInApp = (Button) findViewById(R.id.about_drop_in_app_button);
        Button onlineStore = (Button) findViewById(R.id.online_store_button);
        Button contactUs = (Button) findViewById(R.id.contact_us_button);
        Button privacyPolicy = (Button) findViewById(R.id.privacy_policy_button);
        
        storeURL = Uri.parse("http://becausewater.org/store-2/");
        
        aboutBecauseWater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(fm.getBackStackEntryCount() > 0)
					fragmentName = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
				sf = new Simple_Fragment(true);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				if(fragmentName == "info")
					fm.popBackStack();
				ft = fm.beginTransaction();
				ft.add(R.id.container, sf)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("info")
					.commit();
			}
		});
        aboutDropInApp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(fm.getBackStackEntryCount() > 0)
					fragmentName = fm.getBackStackEntryAt(fm.getBackStackEntryCount()-1).getName();
				sf = new Simple_Fragment(false);
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				if(fragmentName == "info") 
					fm.popBackStack();
				ft = fm.beginTransaction();
				ft.add(R.id.container, sf)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("info")
					.commit();
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
				if(fragmentName == "info")
					fm.popBackStack();
				ft = fm.beginTransaction();
				ft.add(R.id.container, cf)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("info")
					.commit();
			}
		});
        privacyPolicy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
				new AlertDialog.Builder(context)
					.setMessage(R.string.privacy_policy_string)
					.setTitle("Privacy Policy")
					.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) { } // Do nothing to just go back
					})
					.show();
			}
		});
    }
    
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
            	mMenu.findItem(R.id.add_new).setVisible(false);
            else
            	mMenu.findItem(R.id.add_new).setVisible(true);
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
            	mMenu.findItem(R.id.add_new).setVisible(true);
            fm.popBackStack();
    	}
    	else
    		super.onBackPressed();
    }
    
    public static void addPin(Drop drop) {
    	map.addPin(drop);
    }
    
    public static class MapFragment extends Fragment {

    	private MapView mapView;
    	private GoogleMap map;
    	private LocationManager locationManager;
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
            LatLng myLatLng;
            CameraPosition myPosition;
            dropMap = new HashMap<Marker, Drop>();
        	
            // get MapView from layout
            mapView = (MapView) rootView.findViewById(R.id.mapview);
            mapView.onCreate(savedInstanceState);
            //get GoogleMap from MapView + initialization
            map = mapView.getMap();
            MapsInitializer.initialize(mapView.getContext());
            map.setMyLocationEnabled(true);

            int actionBarSize = getActionBarHeight();
            map.setPadding(0, actionBarSize, 0, 0);
            
            locationManager = (LocationManager) rootView.getContext().getSystemService(LOCATION_SERVICE);
            map.getUiSettings().setZoomControlsEnabled(true);
            
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
        
        private void queryDatabase() {
    		String url_part1 = "foobar";
    		String url_part2 = "&lng=";
    		String url_part3 = "&radius=10";
    		String url = "".concat(url_part1)
    				.concat(Double.toString(latitude))
    				.concat(url_part2)
    				.concat(Double.toString(longitude))
    				.concat(url_part3);
    		
        	JSONParser jParser = new JSONParser(url);
        	jParser.fetchJSON(); // Busy-waits in this method until the thread is complete
        	
        	drops = new ArrayList<Drop>();
        	jParser.getDrops(drops);
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
        	
        	dropMap.put(marker, drop); // Make drop accessible by unique LatLng
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
        private EditText enter_address;
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
            if(mMenu != null)
            	mMenu.findItem(R.id.add_new).setVisible(false);
            
            Button submit = (Button) rootView.findViewById(R.id.submit_button);
            Button useMyLoc = (Button) rootView.findViewById(R.id.use_my_loc_button);
            
            submit.setOnClickListener(new OnClickListener() {
            	@Override
            	public void onClick(View v) {
            		if(enter_address.getText().toString().length() > 0) {
            			ft = fm.beginTransaction();
            			Confirm_Fragment cf = new Confirm_Fragment(enter_address.getText().toString());
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
    	
    	private String result;
    	private Context context;
    	private double latitude, longitude;
    	private EditText locationName, locationDescription;
    	MapView mapView2;
    	GoogleMap map;
    	LocationManager locationManager;
    	
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
            	addressList = geo.getFromLocationName(result,5);
            	if(addressList != null) {
            		address = addressList.get(0);
            		/*if(address.getMaxAddressLineIndex() > 0)
            			addressLine.setText(address.getAddressLine(0));
            		if(address.getLocality() != null )
            			city.setText(address.getLocality());
            		if(address.getAdminArea() != null)
            			state.setText(address.getAdminArea());
            		if(address.getPostalCode() != null)
            			zipcode.setText(address.getPostalCode());*/
            		latitude = address.getLatitude();
            		longitude = address.getLongitude();
            		/*lat.setText(Double.toString(latitude));
            		lon.setText(Double.toString(longitude));*/
            	}
            }
            catch(IOException e) {
            	e.printStackTrace();
            }
            
        	map.addMarker(new MarkerOptions()
    			.position(new LatLng(latitude, longitude)));
        	CameraPosition myPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude))
				.zoom(16)
				.bearing(0)
				.tilt(0)
				.build();
        	map.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
            
        	locationName = (EditText) rootView.findViewById(R.id.name_of_location);
        	locationDescription = (EditText) rootView.findViewById(R.id.loc_description);
            Button confirm = (Button) rootView.findViewById(R.id.add_drop_button);
            confirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					fm.popBackStack();
					fm.popBackStack();
		            if(mMenu != null)
		            	mMenu.findItem(R.id.add_new).setVisible(true);
		            Drop drop = new Drop();
		            drop.setLatitude(latitude);
		            drop.setLongitude(longitude);
		            drop.setName(locationName.getText().toString());
		            drop.setDetails(locationDescription.getText().toString());
		            drop.setCategory("User Submitted");
		            addPin(drop);
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
    	
    	View rootView;
    	private Drop drop;
    	
    	public Info_Fragment(Drop d) {
    		this.drop = d;
    	}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		rootView = inflater.inflate(R.layout.fragment_drop_info, container, false);
    		
    		TextView dropTitle = (TextView) rootView.findViewById(R.id.drop_info_title);
    		dropTitle.setText(drop.getName());
    		TextView dropAddress = (TextView) rootView.findViewById(R.id.drop_info_address);
    		dropAddress.setText(drop.getAddress());
    		TextView dropDescription = (TextView) rootView.findViewById(R.id.drop_info_description);
    		dropDescription.setText(drop.getDetails());
    		
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
					
				}
			});
    		fromHere.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
    		
    		return rootView;
    	}
    }

    public static class Simple_Fragment extends Fragment {
    	private View rootView;
    	private boolean becauseWater;
    	
    	public Simple_Fragment(boolean bw) {
    		this.becauseWater = bw;
    	}
    	
    	@Override
    	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if(becauseWater)
            	rootView = inflater.inflate(R.layout.fragment_about_because_water, container, false);
            else
            	rootView = inflater.inflate(R.layout.fragment_about_drop_in, container, false);
            
            return rootView;
    	}
    }
}
