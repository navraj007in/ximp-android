package com.ximp;

import java.util.ArrayList;
import java.util.List;

import com.kss.xchat.adapters.PresenceAdapter;
import com.kss.xchat.data.AccountStatus;

import eu.siacs.conversations.entities.Account;
import eu.siacs.conversations.generator.PresenceGenerator;
import eu.siacs.conversations.xmpp.stanzas.PresencePacket;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class StatusActivity extends XmppActivity {
	TextView txtAccount;
	EditText txtStatus;
	ListView lstPresence;
	public static int position=0;
	private List<String> mActivatedAccounts = new ArrayList<String>();
	public static String selectedAccount="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		txtAccount = (TextView) findViewById(R.id.account);
		txtStatus = (EditText) findViewById(R.id.status);
		lstPresence = (ListView) findViewById(R.id.lstPresence);
		PresenceAdapter presenceAdapter = new PresenceAdapter(getApplicationContext(), 
				R.layout.account_row, R.id.account ,
				getApplicationContext().getResources().getStringArray(R.array.presence));
		lstPresence.setAdapter(presenceAdapter);
		lstPresence.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				StatusActivity.position = position;
			}
		});
		txtAccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSelectAccountDialog();
			}
		});
	}
	@Override
	void onBackendConnected() {
		this.mActivatedAccounts.clear();
		for (Account account : xmppConnectionService.getAccounts()) {
			if (account.getStatus() != Account.State.DISABLED) {
				this.mActivatedAccounts.add(account.getJid().toBareJid().toString());
				
			}
		}
		if(mActivatedAccounts.size()>0) {
			txtAccount.setText(mActivatedAccounts.get(0));
			selectedAccount = txtAccount.getText().toString();
			AccountStatus as = AccountStatus.loadSerializedObject(getApplicationContext());
			if(as != null){
				txtStatus.setText(as.accountStatus.get(selectedAccount));
				
			}
		}
		selectedAccount=txtAccount.getText().toString();
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
						txtAccount.setText(selectedAccount);
						AccountStatus as = AccountStatus.loadSerializedObject(getApplicationContext());
						if(as != null){
							txtStatus.setText(as.accountStatus.get(selectedAccount));
							
						}
						dialog.dismiss();
					}
				});

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
		getMenuInflater().inflate(R.menu.status, menu);

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try
		{
			PresencePacket presencePacket = new PresencePacket();
			presencePacket.addChild("status").setContent(txtStatus.getText().toString());

			String show = getApplicationContext().getResources()
					.getStringArray(R.array.presencecodes)[position];
			presencePacket.addChild("show").setContent(show);
			Account account = xmppConnectionService.getAccount(selectedAccount);
			PresenceGenerator mPresenceGenerator = new PresenceGenerator(xmppConnectionService);
			if(account !=null)
				mPresenceGenerator.sendPresence( account,
						presencePacket);
			xmppConnectionService.sendPresencePacket(account, presencePacket);
	    	AccountStatus as = AccountStatus.loadSerializedObject(getApplicationContext());
	    	if(as == null) 
	    		as = new AccountStatus();
	    	as.accountStatus.put(selectedAccount, 
	    			txtStatus.getText().toString());
	    	as.accountStatusPos.put(selectedAccount, 
	    				StatusActivity.position);
	    	AccountStatus.
	    			saveObject(getApplicationContext(), as);
	    	Toast.makeText(getApplicationContext(), 
	    			getApplicationContext().getResources().getString(R.string.statuschanged)
	    			, Toast.LENGTH_SHORT).show();
		    switch (item.getItemId()) {
		    case R.id.action_status:
				
		    	return true;
		    default:
		            return super.onOptionsItemSelected(item);
		    }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}
}
