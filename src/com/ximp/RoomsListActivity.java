package com.ximp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.navrajsmessenger.Utils;
import com.ximp.R;

import eu.siacs.conversations.entities.Account;
import eu.siacs.conversations.ui.adapter.TextAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;


import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class RoomsListActivity extends XmppActivity {

	String dots;
	int numDots=0;
	Thread loadThread;
	ListView lstRooms;
	ActionBar actionBar;
	private AdView adView;
	String roomsArray[];
	String roomNamesArray[];
	boolean loadingFinished=false;
	TextView txtAccount;
	OnItemClickListener roomClickListener;
	private InterstitialAd interstitial;
	private List<String> mActivatedAccounts = new ArrayList<String>();
	private String selectedAccount="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rooms_list);

		actionBar=getActionBar();
		initiateLoadAnimation();
		loadAd();
		lstRooms=(ListView)findViewById(R.id.lstRooms);

		txtAccount = (TextView) findViewById(R.id.account);
		txtAccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSelectAccountDialog();
			}
		});

		roomClickListener=new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				 Bundle conData = new Bundle();
				    conData.putString("confid", roomsArray[position]);
				    conData.putString("name", roomNamesArray[position]);
				    conData.putString("account", selectedAccount);
				    Intent intent = new Intent();
				    intent.putExtras(conData);
				    setResult(RESULT_OK, intent);
				    finish();
			}
		};
		lstRooms.setOnItemClickListener(roomClickListener);
		loadRooms();
	}
	@Override
	void onBackendConnected() {
		this.mActivatedAccounts.clear();
		for (Account account : xmppConnectionService.getAccounts()) {
			if (account.getStatus() != Account.State.DISABLED) {
				this.mActivatedAccounts.add(account.getJid().toBareJid().toString());
			}
		}
		if(mActivatedAccounts.size()>0) txtAccount.setText(mActivatedAccounts.get(0));
		selectedAccount=txtAccount.getText().toString();
	}
	private void initiateLoadAnimation(){
		new Thread(new Runnable() { 
            public void run(){        
            	try {
            		while(true){
					Thread.sleep(700);
					numDots++;
					if(loadingFinished) break;
					int rem=(numDots%4);
					dots="";
					switch(rem){
						case 0:
						dots="";
						break;
						case 1:
						dots=".";
						break;
						case 2:
						dots="..";
						break;
						case 3:
						dots="...";
						break;
						
					}
					 runOnUiThread (new Thread(new Runnable() { 
				         public void run() {
				        	 actionBar.setTitle("Loading "+dots);
				         }
				     }));
					
            		}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }).start();
		
	}
	private void populateAccountSpinner(Spinner spinner) {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_dropdown_item, mActivatedAccounts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Log.d("account", mActivatedAccounts.get(position));
				selectedAccount=mActivatedAccounts.get(position);
				txtAccount.setText(selectedAccount);
				
				//Toast.makeText(getApplicationContext(), mActivatedAccounts.get(position), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rooms_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void loadRooms()
	{
		actionBar.setTitle("Loading ");
		String tag_json_arry = "json_array_req";
		String url = "http://navraj.net/xchat/xmpp/getrooms";
		JsonArrayRequest req = new JsonArrayRequest(url,new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				// TODO Auto-generated method stub
				try{
				
				
				parseRooms(response);
				TextAdapter adap=new TextAdapter(getApplicationContext(), 
						android.R.layout.simple_list_item_1, R.id.textView1, roomNamesArray);
				lstRooms.setAdapter(adap);
				loadingFinished=true;
				actionBar.setTitle("Chat Rooms List");
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				
			}
		});


		if(xChat.getInstance()!=null)
			xChat.getInstance().addToRequestQueue(req, tag_json_arry);
	}
	private void parseRooms(JSONArray roomsJSON)
	{
		
		roomsArray=new String[roomsJSON.length()];
		roomNamesArray=new String[roomsJSON.length()];

		
		for(int i=0;i<roomsJSON.length();i++){
			try {
				JSONObject roomObject=roomsJSON.getJSONObject(i);
				roomsArray[i]=roomObject.getString("name");
				roomNamesArray[i]=roomObject.getString("naturalName");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	private void loadAd()
	{
		adView = new AdView(this);
		 adView.setAdSize(AdSize.SMART_BANNER);
	    adView.setAdUnitId(Utils.ADMOBID);

	    // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
	    layout.addView(adView);

	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	        .build();
	    adView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    // Start loading the ad in the background.
	    adView.loadAd(adRequest);
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId(Utils.INTERSTITIALID);
	    // Create ad request.
	   
	    AdRequest adRequest1 = new AdRequest.Builder()
        .build();
	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest1);

	    interstitial.setAdListener(new AdListener(){
            public void onAdLoaded(){
                interstitial.show();
            }
  });
//	    displayInterstitial();
	}
	public void displayInterstitial() {
		Log.i("admob", "Loading Interstitial");
		interstitial.show();
	    if (interstitial.isLoaded()) {
	      interstitial.show();
			Log.i("admob", "Loading Interstitial inside");

	    }
		Log.i("admob", "Interstitial load finished");

	  }
	
	@SuppressLint("InflateParams")
	protected void showSelectAccountDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_account);
		View dialogView = getLayoutInflater().inflate(R.layout.select_account_dialog, null);
		final Spinner spinner = (Spinner) dialogView.findViewById(R.id.account);
		
		
		populateAccountSpinner(spinner);
		builder.setView(dialogView);
		builder.setNegativeButton(R.string.cancel, null);
		builder.setPositiveButton(R.string.select, null);
		final AlertDialog dialog = builder.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						dialog.dismiss();
					}
				});

	}
}
