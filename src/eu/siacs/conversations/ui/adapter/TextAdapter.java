package eu.siacs.conversations.ui.adapter;

import com.ximp.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TextAdapter extends ArrayAdapter<String> {
	private LayoutInflater mInflater;
	String list[];
	public TextAdapter(Context context, int resource, int textViewResourceId,
			String[] objects) {
		super(context, resource, textViewResourceId, objects);
		list=objects;
		  mInflater = (LayoutInflater) context.getSystemService(
				  Activity.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 View vi=convertView;

  		if (convertView == null) {
 			vi = mInflater.inflate(R.layout.list_row, null);
  		}
  		TextView txtItem=(TextView)vi.findViewById(R.id.textView1);
  		txtItem.setText(list[position]);
		return vi;
	}
}
