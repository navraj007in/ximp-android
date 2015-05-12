package eu.siacs.conversations.utils;


import android.text.TextUtils;

public class Utils {
	public final static boolean isValidEmail(CharSequence target) {
		  return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
}
