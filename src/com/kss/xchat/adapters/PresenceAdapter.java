package com.kss.xchat.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kss.xchat.data.AccountStatus;
import com.ximp.R;
import com.ximp.StatusActivity;


public class PresenceAdapter extends ArrayAdapter<String>{
	private LayoutInflater mInflater;
    String[] data;
    ImageView iv;
    Context context;
    LinearLayout layoutMessage;
    AccountStatus as;
    int pos =0;
    int[] presenceColors = {R.drawable.available,
                                   R.drawable.idle,R.drawable.away,R.drawable.busy};
	  public PresenceAdapter (Context context, int resource,
              int textViewResourceId, String[] strings) {               
		  super(context, resource, textViewResourceId, strings);
		  mInflater = (LayoutInflater) context.getSystemService(
				  Activity.LAYOUT_INFLATER_SERVICE);
		  data=strings;
		  this.context=context;
		  if(as == null) 
				as = AccountStatus.loadSerializedObject(context);
		  try{
          pos = as.accountStatusPos.get(StatusActivity.selectedAccount);
          
		  }
		  catch(Exception e){
			  e.printStackTrace();
		  }

}

	 
		
      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {   
         View vi=convertView;
     		if (convertView == null) {
    			vi = mInflater.inflate(R.layout.presence_row, null);
     		}
     		LinearLayout layoutPresence = (LinearLayout) 
     				vi.findViewById(R.id.layoutPresence); 
            TextView txtPresence=(TextView) vi.findViewById(R.id.presence);
            ImageView imgPresence = (ImageView) vi.findViewById(R.id.imgPresence);
            ImageView imgCheck = (ImageView) vi.findViewById(R.id.imgCheck);

            try{
            if(pos == position) 
            	imgCheck.setVisibility(View.VISIBLE);
            else
            	imgCheck.setVisibility(View.GONE);
            
            }
            catch(Exception e){
            	
            }
            imgPresence.setBackground(context.getDrawable(presenceColors[position]));
            txtPresence.setText(data[position]);
            layoutPresence.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				pos = position;
				StatusActivity.position = position;
				notifyDataSetChanged();
				}
			});
            
         return vi;
}


}
