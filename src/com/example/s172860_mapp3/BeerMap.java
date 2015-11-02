package com.example.s172860_mapp3;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BeerMap extends FragmentActivity {


	ArrayList<Beer> beerList;
	MyApplication myApp;
	double longitude,latitude; 
	Address[] mineadresser; 
	String beerAdresse;
	Beer beer;
	Double lat;
	Double Lon;
	String test;
	String TAG = "INBEERMAP";
	String tempadr; 
	String intentAdress;
	HashMap<Marker, Beer> markerHashMap;
	HashMap<Beer, Marker> beerMarkerHashMap;
	private GoogleMap mMap;
	LatLng zoomlevel; 
	CameraUpdate cameraUpdate;
	Beer currentBeer = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beermap);
		// Sets the actionbar title to the custom string with given font. 
		SpannableString s = new SpannableString("Beer Map");
	    s.setSpan(new TypefaceSpan(this, "Ubuntu-R.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    
	    ActionBar actionBar = getActionBar();
	    actionBar.setTitle(s);
		
		myApp = (MyApplication) getApplication();
		markerHashMap = new HashMap<Marker, Beer>();
		beerMarkerHashMap = new HashMap<Beer, Marker>();
		
		// makes a check from the bundle, extracts the the adress and sends it to the helpermethod
		// the helper extracts the object with the adress so i have a variable to check items with, simplifying the process. 	
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			currentBeer = getBeerFromAddr(extras.getString("Addresse"));
		}
		
		setUpMap();

	}

	private void setUpMap()
	{
		
		// Initialize the location variable here, since we're setting it inside an if (and it might not be set then)
		CameraUpdate beerLocationCameraUpdate = null;
		
		zoomlevel = new LatLng(59.91387,10.75225);
		CameraPosition.Builder builder = CameraPosition.builder();
		builder.target(zoomlevel);
		builder.zoom(13);
		builder.bearing(155);
		builder.tilt(0);
		CameraPosition cameraPosition = builder.build();
		cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
			
		
		// currentBeer is the testvariable from the bundle, if its null we set the map with the zoom over oslo
		if(currentBeer != null){

			LatLng templatlng = currentBeer.getLatLong();
			builder = CameraPosition.builder();
			builder.target(templatlng);
			builder.zoom(18);
			builder.bearing(155);
			builder.tilt(65);
			cameraPosition = builder.build();
			beerLocationCameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

		}

		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null)
		{
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

			// Check if we were successful in obtaining the map.

			if (mMap != null)
			{
				// No mather if we have a extras-bundle or not, we want to set oslo as the camera position
				mMap.moveCamera(cameraUpdate);
				
				// checks the testVariable and if its not null, and we havent updated the camera it executes 
				
				if(currentBeer != null && beerLocationCameraUpdate != null) {
					
					// Since callbacks needs final variables when accessing them, we'll assing the
					// beerLocationCameraUpdate to a new, final variable here
					final CameraUpdate finalBeerCameraUpdate = beerLocationCameraUpdate;
					
					// onMapLoaded in the callback is called when the map has finished rendering all it's tiles
					mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
						
						@Override
						public void onMapLoaded() {
							// Animate to our marker place. 
							animateToCameraUpdate(finalBeerCameraUpdate);
							
						}
					});
					
				} 
				
				mMap.setMyLocationEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(false);
				mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
				{
					@Override
					public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker)
					{
						marker.showInfoWindow();
						return true;
					}
				});
			}
			else
				Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
		}
	}

	// Animate to an update and show the marker when we have finished animating
	protected void animateToCameraUpdate(CameraUpdate update) {
		mMap.animateCamera(update, 2000, new GoogleMap.CancelableCallback() {
	        @Override
	        public void onFinish() {
	        	showMarker();
	        }

	        @Override
	        public void onCancel() {
	            
	        }
		});
	}
	
	// This method calls the setUpEvenSpots, only after the map has been set up, to avoid errors
	protected void onStart() {
		super.onStart();
		initateMarkerProsess();
	}
	
	// Method for showing a marker if extras (the currentBeerVarialbe) is not null 
	protected void showMarker() {
		if(currentBeer != null) {
			Marker m = beerMarkerHashMap.get(currentBeer);
			m.showInfoWindow();

		}
	}
	
	protected void onStop() {
		super.onStop();
		
		if(currentBeer != null) 
			currentBeer = null;
		
	}
	
	// Get a beer from myApp's list based on adress, makes it easy to determen what type of camera animation we want, based on the point of entry to the beermap.
	protected Beer getBeerFromAddr(String addr) {
		
		if(addr != null) {
			
			for (Beer b : myApp.beerList){
				if(b.getAdress().equals(addr)) {
					return b;
				}
			}
		}
			
		return null;
	}

	/**
	 * Initiating the marker process, once the marker has been added it is placed in a hashMap
	 * This hashmap links the marker with an object, making it posibell to display a costom infowindowadapter
	 * 
	 */
	public void initateMarkerProsess(){

		for (Beer b : myApp.beerList){

			Marker m = addMarker(mMap,b.getLatLong(),b.getAdress());

			markerHashMap.put(m, b);
			beerMarkerHashMap.put(b, m);

			mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
		}
	}
/**
 * Helpermethod that adds the marker to the map
 * @param mMap
 * @param latlng
 * @param title
 * @return
 */
	public Marker addMarker(GoogleMap mMap, LatLng latlng, String title){
		Marker m = mMap.addMarker(new MarkerOptions().position(latlng)
				.title(title));
		return m;
	}
	
	/**
	 * Initiates the actionbar with menuelements
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beermap, menu);
		return true;
	}

	/**
	 * HelperMethod that places the current location, moved from the map to keep navigation constant
	 */
	public void setMyLocation(){ 
		Location userLocation = mMap.getMyLocation();
		LatLng myLocation = null;
		if (userLocation != null) {
			myLocation = new LatLng(userLocation.getLatitude(),
					userLocation.getLongitude());
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
					mMap.getMaxZoomLevel()-5));
		}
	}
/**
 * preforms actions based on ID from the onclick in the actionbar
 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_mylocation) {
			setMyLocation();
		}
		if (id == R.id.action_exit){
			 Intent intent = new Intent(this, MainActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    intent.putExtra("Exit me", true);
			    startActivity(intent);
			    finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A internal class that changes the standar googleview paramaters, adds a costom view to disp
	 * play information on click, and shows the current markers objects information: price, adress and name.
	 * @author audunlarsen
	 *
	 */
	public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
	{
		public MarkerInfoWindowAdapter()
		{
		}

		@Override
		public View getInfoWindow(Marker marker)
		{
			return null;
		}

		@Override
		public View getInfoContents(Marker marker)
		{
			View v  = getLayoutInflater().inflate(R.layout.infowindow_layout, null);

			Beer b = markerHashMap.get(marker);

			ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

			TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

			TextView markerLabel2 = (TextView)v.findViewById(R.id.marker_label2);

			TextView markerLabel3 = (TextView)v.findViewById(R.id.marker_label3);

			markerLabel.setText(b.getName());
			markerLabel2.setText("      " + b.getAdress());
			markerLabel3.setText("      Pris: " + b.getPrice() + ",-" );


			return v;
		}
	}

}







