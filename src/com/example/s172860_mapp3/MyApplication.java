package com.example.s172860_mapp3;

import java.util.ArrayList;





import android.app.Application;

public class MyApplication extends Application {
	//declaring a global arraylist to contain all the resturant objects through out the application. 
	ArrayList<Beer> beerList;
	// declaring a global boolean to help display propper loading dialogs if data is not finished loading
	Boolean hasFetchedLocationData = false;


	public void onCreate() {
		super.onCreate();
		beerList = new ArrayList<Beer>();


	}
}
