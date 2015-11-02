package com.example.s172860_mapp3;



import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
 
/**
 * Class that start when the application is lanched, showcased the application logo
 * @author audunlarsen
 *
 */
public class SplashScreen extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SpannableString s = new SpannableString("Welcome to WŸnderBeer");
	    s.setSpan(new TypefaceSpan(this, "Ubuntu-R.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    
	    ActionBar actionBar = getActionBar();
	    actionBar.setTitle(s);
		
 
        new Handler().postDelayed(new Runnable() {
 

 
            @Override
            public void run() {
                // This method is executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}


