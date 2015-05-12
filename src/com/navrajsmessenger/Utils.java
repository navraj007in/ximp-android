package com.navrajsmessenger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.ximp.XmppActivity;

import eu.siacs.conversations.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;

public class Utils {
	public static String ADMOBID="ca-app-pub-9436114210349385/3817351351";
    public static String INTERSTITIALID="ca-app-pub-9436114210349385/5294084553";
    public static String TAG = "xChat" ;
    public static HashMap<String, String> statusMap = new HashMap<String, String>();
    public static HashMap<String,String> getStatusMap(){
    	if(statusMap!=null)
    			return statusMap;
    	else return new HashMap<String,String>();
    }
    public static String ReadPreference(Context context ,String prefName)
	{
		SharedPreferences pref = context.getSharedPreferences(XmppActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
		return pref.getString(prefName, "");
	}
    public static boolean ReadBoolPreference(Context context ,String prefName)
   	{
   		SharedPreferences pref = context.getSharedPreferences(XmppActivity.class.getSimpleName(),
                   Context.MODE_PRIVATE);
   		return pref.getBoolean(prefName, false);
   	}
	public static String WritePreference(Context context,String prefName
			,String value)
	{
		SharedPreferences pref = context.getSharedPreferences(XmppActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putString(prefName, value);
        editor.commit();

		return "";
	}public static String WritePreference(Context context,String prefName
			,boolean value)
	{
		SharedPreferences pref = context.getSharedPreferences(XmppActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putBoolean(prefName, value);
        editor.commit();

		return "";
	}
	public static void SaveBitmap(String path,Bitmap bitmap){
		FileOutputStream out = null;
		try {
		    out = new FileOutputStream(path);
		    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
		    // PNG is a lossless format, the compression factor (100) is ignored
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (out != null) {
		            out.close();
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	public static Bitmap GetBitmap(String filePath){
		File image = new File(filePath);

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
		return bitmap;


	}
	public static String parseAvatar(String avatar){
		XmlPullParser parser=Xml.newPullParser();
        String curLabel="";
        
		

        int eventType=0;
		try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
					true);

			parser.setInput(new InputStreamReader(
					new ByteArrayInputStream(avatar.getBytes())));

			eventType = parser.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT){
	            String name = null;
	            switch (eventType){
	                case XmlPullParser.START_DOCUMENT:
	                    //assetReportEntries = new ArrayList<AssetReportEntry>();
	                    break;
	                case XmlPullParser.START_TAG:
	                    name = parser.getName();
	                    if(name.equalsIgnoreCase("Binval")){
	                        curLabel = parser.nextText();
	                    }

	                    break;
	                case XmlPullParser.END_TAG:
	            }
	            eventType = parser.next();
	        }
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return curLabel;

	}

}
