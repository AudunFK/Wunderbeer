package com.example.s172860_mapp3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;


import android.content.Intent;

import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Spannable;
import android.text.SpannableString;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;



import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity {

	interface DelayedAction {
		public void perform();
	}

	String TAG = "IN MAINACTIVTY PARSEJASON";
	String TAG1 = "Updating objects with coordinates";
	String myjsonstring;
	String JSONforParse;
	ArrayList<Beer> beerList;
	MyApplication myApp;
	BeerListViewAdapter beerAdapter;
	double longitude,latitude; 
	Address[] mineadresser; 
	String beerAdresse;
	Beer beer;
	Double lat;
	Double Lon;
	String test;
	String tempadr; 
	
	ProgressDialog loadingDialog;
	DelayedAction loadingCompleteAction;
	ListView lv;


	ArrayList<Beer> updatingBeers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Changes the layout of a string based on the chosen font, returns the string with a set spannable
		SpannableString s = new SpannableString("WŸnderBeer");
		s.setSpan(new TypefaceSpan(this, "Ubuntu-R.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(s);

		// If test that checks if the activity was started with a bundle containting the key, if true exits the application 
		if( getIntent().getBooleanExtra("Exit me", false)){
			finish();
			return; // add this to prevent from doing unnecessary stuffs
		}


		myApp = (MyApplication) getApplication();
		updatingBeers = new ArrayList<Beer>();
		loadingDialog = new ProgressDialog(MainActivity.this);
		loadingDialog.setMessage("Getting beer locations");
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


		// test to make sure the arraylist of objects is empty and only runs Once(); 

		if(myApp.beerList.isEmpty()) {
			once();

		}

		//setting up and launching my coustumadapter to fill the listview with objects from the arraylist of objects 
		beerAdapter = new BeerListViewAdapter(this, R.layout.beer_list); 
		setContentView(R.layout.activity_main);
		lv = (ListView) findViewById(R.id.android_list);
		lv.setAdapter(beerAdapter);
		// simple compare to method to make sure the listview allways displays the cheepest beer first. 
		Collections.sort(myApp.beerList, new Comparator<Beer>(){
			  public int compare(Beer b1, Beer b2) {
			    return b1.getPrice().compareToIgnoreCase(b2.getPrice());
			  }
			});
		
		
		
		beerAdapter.addAll(myApp.beerList);
		hentkoordinater();
		// Onclick method for the listview, gets the item at the location and extracts the adress 
		// this will be sendt as a bundle to the beermap activity. 
		lv.setOnItemClickListener(getItemClickListenerForListView(lv, !myApp.hasFetchedLocationData));

	}

	/**
	 * Onclick Method that gets the object and sens the variable in a bundle with key 
	 * allso controlls if the loading is done and the dialog should be displayed
	 * @param lv
	 * @param shouldShowLoading
	 * @return
	 */
	public OnItemClickListener getItemClickListenerForListView(final ListView lv, final Boolean shouldShowLoading) {
		return new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				loadingCompleteAction = new DelayedAction() {
					public void perform() {
						Beer br = (Beer) (lv.getItemAtPosition(position));
						String send = br.getAdress();

						Intent i = new Intent(MainActivity.this, BeerMap.class);   
						i.putExtra("Addresse", send);
						startActivity(i);
					}
				};

				if(shouldShowLoading) {
					loadingDialog.show();
				} else {
					loadingCompleteAction.perform();
				}

			}
		};
	}
	/**
	 * Method called when the items is done in AsyncTask
	 * changes the MyAPPs boolean variable since the loading is complete
	 */
	public void finishedParsingLocation() {


		myApp.hasFetchedLocationData = true;
		lv.setOnItemClickListener(getItemClickListenerForListView(lv, false));

		// Let's hide the loading indicator if it's there
		if(loadingDialog.isShowing()) {
			loadingCompleteAction.perform();
			loadingDialog.dismiss();
		}


	}
	/**
	 * gets called if the arraylist is empty, handles the reading and parcing of the JSON file
	 */
	public void once() { 
		JSONforParse = ReturnJSONStringToParse();
		ParseJSONfromString(JSONforParse);

	}
	/**
	 * Returns a string contanting the JSON objects from the file in the assets folder 
	 * @return
	 */
	public String ReturnJSONStringToParse(){

		// Reading text file from assets folder
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open("jsondata.txt")));
			String temp;
			while ((temp = br.readLine()) != null)
				sb.append(temp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close(); // stops reading the file by closing it.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		myjsonstring = sb.toString();

		return myjsonstring;

	}

	/**
	 * Takes the string for the parse method and makes java objects from it, and finally places it in the arraylist of objects
	 * @param string
	 */
	public void ParseJSONfromString(String string){


		myjsonstring = string;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getAssets().open("jsondata.txt")));
			String temp;
			while ((temp = br.readLine()) != null)
				sb.append(temp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close(); // stops reading the file by closing it.
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		myjsonstring = sb.toString();

		try {
			// Creating JSONObject from String
			JSONObject jsonObjMain = new JSONObject(myjsonstring);

			// Creating JSONArray from the JSONObject i created over.
			JSONArray jsonArray = jsonObjMain.getJSONArray("Beer");

			// JSONArray has five JSONObject
			for (int i = 0; i < jsonArray.length(); i++) {

				// Creating JSONObject from JSONArray
				JSONObject jsonObj = jsonArray.getJSONObject(i);

				// Getting data from individual JSONObject
				String name = jsonObj.getString("name");
				String price = jsonObj.getString("price");
				String close_indoor = jsonObj.getString("close_indoor");
				String close_outdoor = jsonObj.getString("close_outdoor");
				String adress = jsonObj.getString("adress");

				// Append result to create POJO 
				Beer beer = new Beer();
				beer.setAdress(adress);
				beer.setCloseOutdoor(close_outdoor);
				beer.setCloseIndoor(close_indoor);
				beer.setPrice(price);
				beer.setName(name);

				// finaly place the java objects in a arraylist, 
				//this will be used to populate the listview through a costomArrayAdapter
				myApp.beerList.add(beer);


			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Method that gets called from the onPostExectue method in asynctask
	 * splits the string returned and updates the object with lat,long doubles
	 * also generates a LatLng variable with a combination of these two. 
	 * and checks if the parsing of the location is done by a testarray
	 * @param b
	 * @param s
	 */
	public void updateList(Beer b, String s){
		
		for(Beer br : myApp.beerList){

			if(br.getAdress().equals(b.getAdress())){

				String [] parts = s.split(":");
				String templat = parts[0];
				String templon = parts[1];

				double latDouble = Double.parseDouble(templat);
				double lonDouble = Double.parseDouble(templon);
				LatLng latlong = new LatLng(lonDouble,latDouble);

				br.setLatLong(latlong);
				br.setLat(latDouble);
				br.setLon(lonDouble); 

				updatingBeers.remove(br);
				if(updatingBeers.size() == 0) {
					finishedParsingLocation();
				}

			}

		}



	}

	/**
	 * Start the coordinates process by calling a new asynctask for every object in the arrayList
	 */
	public void hentkoordinater(){

		for(Beer b : myApp.beerList ) {
			beerAdresse = b.getAdress(); 

			if(b.geterUpdate() == false) {
				updatingBeers.add(b);
				GetLocationTask hentadresser=new GetLocationTask(beerAdresse,mineadresser,b);
				hentadresser.execute(); 
			}
		} 
	}

	/**
	 * Sets the actionbar & menu 
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Handles clicks on the acitonbar, contains the same test process as the onclick for checking if the loading is done
	 * and its safe to load the map since all the markes has its own coordinates for the markers
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			if(!myApp.hasFetchedLocationData) {

				final MainActivity act = this;
				loadingCompleteAction = new DelayedAction() {
					public void perform() {
						startActivity(new Intent(act, BeerMap.class));
					}
				};

				loadingDialog.show();
			} else {
				startActivity(new Intent(this, BeerMap.class));
			}
			return true;
		}
		if (id == R.id.action_contact){
			startActivity(new Intent(this, BeerContact.class));
		}

		if (id == R.id.action_exit){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * AsyncTask that helps convert the beers adress variable to latlng doubles
	 * @author audunlarsen
	 *
	 */

	private class GetLocationTask extends AsyncTask<Void, Void,String>{

		JSONObject jsonObject;
		String beerAdress; 
		Address[] addrscollection;
		String lokasjon;
		Beer b;

		// transfers a beer object through the cycle to help the validation of the data
		public GetLocationTask (String beerAdress, Address[] addrscollection,Beer b) {

			this.addrscollection = addrscollection;
			this.beerAdress = beerAdress;
			this.b = b;

		}
		/**
		 * Background operations that querys Google and extracts the correct information for the returned file
		 */

		@Override
		protected String doInBackground(Void... params) {
			String query = "http://maps.google.com/maps/api/geocode/json?address=" + beerAdress.replaceAll(" ","%20")
					+ "&sensor=false";
			Address addr = null;
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(query);
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity(); 
					InputStream stream = entity.getContent();
					int c;
					while ((c = stream.read()) != -1) {
						stringBuilder.append((char) c); }

					try {
						jsonObject = new JSONObject(stringBuilder.toString());
						addr = new Address(Locale.getDefault());
						JSONArray
						addrComp=((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONArray("address_components");
					}
					catch (JSONException e) {
						e.printStackTrace();
					}

					// Create variables to place the values  . 
					Double lon = Double.valueOf(0);
					Double lat = Double.valueOf(0);

					// gets the lat/long from the JSON file and places in local variable 
					try {
						lon =   ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");	
						lat = 	((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");				
					}

					catch (JSONException e) {
						e.printStackTrace(); }

					//creates a variable that gets sent to onPostExecute. 

					lokasjon= String.valueOf(lon) + ": " + String.valueOf(lat);
				}		
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			// gets returned to onPostExecute
			return lokasjon;
		}


		/**
		 * OnPostExecute is part of the main UI thread, even though its in the private class.
		 * I can therefore access the helper method i have created to split the string and add it to the global 
		 * arrayList of objects, with the updated coordinates received from the asyncTask. 
		 *
		 */

		protected void onPostExecute(String lokasjon) {

			updateList(b, lokasjon);

		}


	} // end of the asyncTaskClass

}






