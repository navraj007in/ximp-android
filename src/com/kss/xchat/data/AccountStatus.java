package com.kss.xchat.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class AccountStatus implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 4523540197524272495L;

	public HashMap<String, String> accountStatus 
	 						= new HashMap<String,String>();

	public HashMap<String, Integer> accountStatusPos 
		= new HashMap<String,Integer>();

	public static byte[] serializeObject(AccountStatus o) { 
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
	 
	    try { 
	      ObjectOutput out = new ObjectOutputStream(bos); 
	      out.writeObject(o); 
	      out.close(); 
	 
	      // Get the bytes of the serialized object 
	      byte[] buf = bos.toByteArray(); 
	 
	      return buf; 
	    } catch(IOException ioe) { 
	      Log.e("serializeObject", "error", ioe); 
	 
	      return null; 
	    } 
	  } 
	public static void saveObject(Context ctx,AccountStatus p){
        try
        {
           ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream
        		   (new File(ctx.getApplicationContext()
        				   .getFilesDir().getAbsolutePath()+"/accountstatus.bin"))); //Select where you wish to save the file...
           oos.writeObject(p); // write the class as an 'object'
           oos.flush(); // flush the stream to insure all of the information was written to 'save_object.bin'
           oos.close();// close the stream
        }
        catch(Exception ex)
        {
           Log.v("Serialization Save Error : ",ex.getMessage());
           ex.printStackTrace();
        }
   }
	public static AccountStatus loadSerializedObject(Context ctx)
    {
        try
        {
        	File f = new File(ctx.getApplicationContext().getFilesDir()
        			.getAbsolutePath()+"/accountstatus.bin");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            Object o = ois.readObject();
            return (AccountStatus)o;
        }
        catch(Exception ex)
        {
        Log.v("Serialization Read Error : ",ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}
