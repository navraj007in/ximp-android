package com.ximp;

import com.ximp.R;

import eu.siacs.conversations.services.XmppConnectionService;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class SettingsFragment extends PreferenceFragment {
	public static final String BACKGROUND_INTENT = "com.kss.xchat.BACKGROUND";
	//http://stackoverflow.com/questions/16374820/action-bar-home-button-not-functional-with-nested-preferencescreen/16800527#16800527
	private void initializeActionBar(PreferenceScreen preferenceScreen) {
		final Dialog dialog = preferenceScreen.getDialog();

		if (dialog != null) {
			View homeBtn = dialog.findViewById(android.R.id.home);

			if (homeBtn != null) {
				View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				};

				ViewParent homeBtnContainer = homeBtn.getParent();

				if (homeBtnContainer instanceof FrameLayout) {
					ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();
					if (containerParent instanceof LinearLayout) {
						((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
					} else {
						((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
					}
				} else {
					homeBtn.setOnClickListener(dismissDialogClickListener);
				}
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		Preference myPref = (Preference) findPreference("background");
		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		             public boolean onPreferenceClick(Preference preference) {
		            	 Intent intent = new Intent(getActivity(), XmppConnectionService.class);
		         		intent.setAction("ui");
		         		getActivity().startService(intent);
	                    Intent i = new Intent();
	        	        i.setAction(BACKGROUND_INTENT);
	        	        getActivity().sendBroadcast(i);

		         		//bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		            	 return false;
		             }
		         });
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference instanceof PreferenceScreen) {
			initializeActionBar((PreferenceScreen) preference);
		}
		return false;
	}
}
