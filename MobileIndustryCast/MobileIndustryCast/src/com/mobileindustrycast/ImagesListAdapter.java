package com.mobileindustrycast;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressLint("DefaultLocale")
public class ImagesListAdapter extends ArrayAdapter<CustomListMessage> {
	  private Context context;
	  private ArrayList<CustomListMessage> messageList;

	  public boolean buyerSelected = true;
	  public boolean sellerSelected = true;
	  public boolean tradeSelected = true;
	  public boolean infoSelected = true;
	  

	  
	  public ImagesListAdapter(Context context, ArrayList<CustomListMessage> values) {
	    super(context, R.layout.rowlayout, values);
	    this.context = context;
	    this.messageList = values;
	  }


	  static class ViewHolder {
		    public  TextView text;
		    public   ImageView image;
		  }
	  
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    
		View v = convertView;

		  
		if (convertView == null){
		LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    v = inflater.inflate(R.layout.rowlayout, parent, false);
	    ViewHolder viewHolder = new ViewHolder();
	    viewHolder.text = (TextView) v.findViewById(R.id.label);
	    viewHolder.image = (ImageView) v
	        .findViewById(R.id.icon);
	    v.setTag(viewHolder);
	    }

		ViewHolder holder = (ViewHolder) v.getTag();
	    
		
		CustomListMessage message;
		if (position<messageList.size())
		{message = messageList.get(position);
	    
	    holder.text.setText(message.getTimestamp()+" "+message.getUserName()+", "+message.getLocation()+", "+message.getStatus()+": "+message.getBody());
	    
	    // Change the icon/color for broadcast/post messages(with post type as a default one)
	    holder.image.setImageResource(R.drawable.post);
	    holder.text.setTextColor(Color.parseColor("#90CA77"));
	    
	    if (message.getMessageType().equals("broadcast")) {
	      holder.image.setImageResource(R.drawable.broadcast);
	      holder.text.setTextColor(Color.parseColor("#9E3B33"));}
		}
	    return v;
	  }
	 // 
}
