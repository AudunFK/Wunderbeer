package com.example.s172860_mapp3;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class BeerContact extends Activity implements OnItemSelectedListener {
	EditText nameField,mailField ,mobileField,textField;
    Spinner subject;
	MyApplication myApp;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beercontact);

		SpannableString s = new SpannableString("Contact Form");
	    s.setSpan(new TypefaceSpan(this, "Ubuntu-R.ttf"), 0, s.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    
	    // Update the action bar title with the TypefaceSpan instance
	    ActionBar actionBar = getActionBar();
	    actionBar.setTitle(s);
	    //gets a refrence to myapplication and initalizes the textviews from id.
		myApp = (MyApplication) getApplication();
		nameField = (EditText) findViewById(R.id.editTextName);
	    mailField = (EditText) findViewById(R.id.editTextMail);      
	    textField = (EditText)findViewById(R.id.editTextFeedback);
	    subject=(Spinner)findViewById(R.id.spinner);
	    // values to place in the array that the spinner displays. 
	   String subjects[]=new String[]{"Choose a catagory","Praise or Gruble", "Cheeper Beer"};
	  
	   // sets the onclick method for the spinner, and defines the layout of the spinners dropdown layout. 
	   subject.setOnItemSelectedListener(this);
	  ArrayAdapter<String> sa = new ArrayAdapter<String>(this, 
	        android.R.layout.simple_spinner_item, subjects);
	    sa.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
	    
	    subject.setAdapter(sa);;        

	  }
	
	// displays a simple alert dialog to the user once the mail has been sendt, and navigates the user back to the applications lifescycle 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{

	    new AlertDialog.Builder(BeerContact.this)
	.setMessage("Your requested has been Accepted\nThank You")
	.setCancelable(false)
	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	public void onClick(DialogInterface dialog, int which) 
	{
	  dialog.cancel();
	    }
	})  
	    .show();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	    // TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	    // TODO Auto-generated method stub

	}

	/**
	 * inflates the classes menu 
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact, menu);
		return true;
	}
	
/**
 * handles actionbar events based on ID 
 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		
		/**
		 * Method for exiting the application, flags tested in the mainactivity. 
		 */
		if (id == R.id.action_exit){
        	
			Intent intent = new Intent(this, MainActivity.class);
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    intent.putExtra("Exit me", true);
		    startActivity(intent);
		    finish();
			
		}
		
		/**
		 * Method for sending the email, first validates the input for the textboxes 
		 * then start intent for sending the email using your prefered mailclient
		 * returns you to the apps lifecicle once the mail has been sucsessfully sendt. 
		 * 
		 */
		if (id == R.id.action_send) {
			
			 if(nameField.getText().toString().length()==0)  
	         {           
	          nameField.setError( "Please type in a name" );  
	         }  
	         else if(mailField.getText().toString().length()==0)  
	         {           
	          mailField.setError( "Enter your mail" ); 
	         }
	         else if(textField.getText().toString().length()==0)  
	         {           
	          textField.setError( "Please insert feedback" );  
	         }
	         else if(subject.getSelectedItemPosition()==0)  
	         {           
	          Toast.makeText(BeerContact.this,"Please select the Subject",Toast.LENGTH_SHORT).show();
	         }else if(subject.getSelectedItemPosition()==0)  
	         {           
	          Toast.makeText(BeerContact.this,"Please select the Subject",Toast.LENGTH_SHORT).show(); 
	         }
	         else
	         {  
	            String body=
	          "Name : "+ nameField.getText().toString()+"<br>Email :"+mailField.getText().toString()+"<br>text :"+textField.getText().toString();  

	            Intent email = new Intent(Intent.ACTION_SEND); 
	            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"aud.lasen@gmail.com"});           
	            email.putExtra(Intent.EXTRA_SUBJECT, subject.getSelectedItem().toString()); 
	            email.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body)); 
	            email.setType("message/rfc822");
	            startActivityForResult(Intent.createChooser(email, "WunderBeer"),1); 
	         }   
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	}

