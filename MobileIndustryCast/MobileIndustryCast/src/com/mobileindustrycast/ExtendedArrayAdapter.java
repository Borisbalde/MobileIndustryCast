package com.mobileindustrycast;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class ExtendedArrayAdapter extends ArrayAdapter<CustomListMessage> {
  private Context context;
  private ArrayList<CustomListMessage> messageList;
  private ArrayList<CustomListMessage> mOriginalValues;
  private ArrayFilter mFilter;
  public boolean buyerSelected = true;
  public boolean sellerSelected = true;
  public boolean tradeSelected = true;
  public boolean infoSelected = true;
  
  private boolean mNotifyOnChange = true;

  
  public ExtendedArrayAdapter(Context context, ArrayList<CustomListMessage> values) {
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
  
  @Override
  public Context getContext(){
	  return context;
  }
  
  
  @Override
  public int getCount() {
      return messageList.size();
  }


  @Override
  public CustomListMessage getItem(int position) {
      return messageList.get(position);
  }

  @Override
 public int getPosition(CustomListMessage item) 
  {
	  return messageList.indexOf(item);
  }
  
  @Override
  public long getItemId(int position)
  {
	  return position;
  }
  
  public Filter getFilter() {
	          if (mFilter == null) {
	              mFilter = new ArrayFilter();
	          }
	          return mFilter;
	      }
 
  private class ArrayFilter extends Filter {
	          @Override
	          protected FilterResults performFiltering(CharSequence prefix) {
	              FilterResults results = new FilterResults();
	  
	              if (mOriginalValues == null) {
	                 // synchronized (mLock) {
	                      mOriginalValues = messageList;
	                  //}
	              }
	  
	              if ((prefix == null || prefix.length() == 0) && buyerSelected && sellerSelected && tradeSelected && infoSelected) {
	                 // synchronized (mLock) {
	                      ArrayList<CustomListMessage> list = new ArrayList<CustomListMessage>(mOriginalValues);
	                      results.values = list;
	                      results.count = list.size();
	                  //}
	              }
	                  else {
	                  String prefixString = prefix.toString().toLowerCase();
	  
	                  String[] searchWords = prefixString.split(" ");
	                  int check = 0;
	                  
	                  ArrayList<CustomListMessage> values = mOriginalValues;
	                  int count = values.size();
	  
	                  ArrayList<CustomListMessage> newValues = new ArrayList<CustomListMessage>(count);
	  

	                  for (int i = 0; i < count; i++) {
	                      final CustomListMessage value = values.get(i);
	                      final String valueText = value.messageToString().toLowerCase();
	  

	                      for (int y = 0; y < searchWords.length; y++) {
	                    	  if (valueText.contains(searchWords[y])){
	                    		  check++;
	                    	  }
	                      }
	                      
	                      if (check == searchWords.length)
	                      {
	                    	  switch (value.getStatusEnum())
	                    	  {case 1:
	                    		  if(buyerSelected)
	                    		  	{
	                    		  		newValues.add(value);
	                    		  }
	                    		  break;
	                    	  case 2:
	                    		  if(sellerSelected )
	                    		  {
	                    			  newValues.add(value);
	                    		  	} 
	                    		  break;
	                    	  case 3:
	                    		  if(tradeSelected)
	                    		  	{
	                    		  		newValues.add(value);
	                    		  	} 
	                    		  break;
	                    	  case 4:
	                    		  if(infoSelected)
	                    		  	{
	                    		  		newValues.add(value);
	                    		  	} 
	                    		  break;
	                    	  }  

	                      }
                    	  check = 0;
	                      }

	  
	                  results.values = newValues;
	                  results.count = newValues.size();
	              }
	                  
	              
	              return results;
	          }
	  
	       @SuppressWarnings("unchecked")
		@Override
	       protected void publishResults(CharSequence constraint, FilterResults results) {
	           //noinspection unchecked
	           messageList = (ArrayList<CustomListMessage>) results.values;
	            if (results.count > 0) {
	             notifyDataSetChanged();
	           } else {
               notifyDataSetInvalidated();
	            }
	          }
	     }
	 
 //user is able to see what he types in filtered listView, filters do not apply to his messages to 
  //avoid problems(user must see his inputs)
  public void add(CustomListMessage object) {
	          if (mOriginalValues != null) {	             
	                  mOriginalValues.add(object);
	                  messageList.add(object);
	                  if (mNotifyOnChange) notifyDataSetChanged();
	          } else {
	              messageList.add(object);
	              if (mNotifyOnChange) notifyDataSetChanged();
	          }
	      }
  
  public void notifyDataSetChanged() {
	          super.notifyDataSetChanged();
	          mNotifyOnChange = true;
	      }
  
  public boolean getBuyer()
  {return buyerSelected;}
  
  public boolean getSeller()
  {return sellerSelected;}
  
  public boolean getTrade()
  {return tradeSelected;}
  
  public boolean getInfo()
  {return infoSelected;}
  
  public void setBuyer(boolean state)
  {buyerSelected = state;}
  
  public void setSeller(boolean state)
  {buyerSelected = state;}
  
  public void setTrade(boolean state)
  {buyerSelected = state;}
  
  public void setInfo(boolean state)
  {buyerSelected = state;}
  }
