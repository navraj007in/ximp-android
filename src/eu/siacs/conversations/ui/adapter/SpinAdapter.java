package eu.siacs.conversations.ui.adapter;

import java.util.List;

import com.ximp.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinAdapter extends ArrayAdapter<String>{
	private LayoutInflater mInflater;
	String list[];
	public SpinAdapter(Context context, int resource, int textViewResourceId,
			String[] objects) {
		super(context, resource, textViewResourceId, objects);
		list=objects;
		  mInflater = (LayoutInflater) context.getSystemService(
				  Activity.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}
	  @Override
	    public View getDropDownView(int position, View convertView,ViewGroup parent) {
	        return getCustomView(position, convertView, parent);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return getCustomView(position, convertView, parent);
	    }

	    // This funtion called for each row ( Called data.size() times )
	   

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		 View vi=convertView;

  		if (convertView == null) {
 			vi = mInflater.inflate(R.layout.list_row, null);
  		}
  		TextView txtItem=(TextView)vi.findViewById(R.id.textView1);
  		txtItem.setText(list[position]);
		return vi;
	}

}
