package com.mobileindustrycast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomListMessage {

	private String timestamp;//timestamp of the message
	private String userName;//name of the user of the application
	private String message;//body of the message
	private String location;//Location of sender
	private String status;//"buyer", "seller", "trade", "need_info"
	private String messageType;//"broadcast","post"
	private String pictureLink;//"address of the image attached"

	//basic constructor for class, assumes that type is "post" and  status is "need_info"
	public CustomListMessage(String user, String msg, String loc, String link)
	{
		timestamp=addTimestamp();
		userName=user;
		message=msg;
		location=loc;
		status="Info";
		messageType = "post";
		pictureLink = link;
	}

	//advanced constructor for broadcast type messages
	public CustomListMessage(String user, String msg, String loc, String Status, String type, String link)
	{
		timestamp=addTimestamp();
		userName= user;
		message=msg;
		location=loc;
		status=Status;
		messageType = type;
		pictureLink = link;
	}

	//turns values of the object into a single string for future use in Searching
	public String messageToString()
	{
		return userName+" "+location+" "+message;
	}

	//Adds Zulu timestamp to the custom message
	public String addTimestamp()
	{
		Date messageDate = new Date();

		SimpleDateFormat hoursAmPm = new SimpleDateFormat("hh", Locale.US);
		SimpleDateFormat minutesAmPm = new SimpleDateFormat("mm", Locale.US);
		SimpleDateFormat lettersAmPm = new SimpleDateFormat("aa", Locale.US);

		StringBuilder hours = new StringBuilder(hoursAmPm.format(messageDate));
		StringBuilder minutes = new StringBuilder(minutesAmPm.format(messageDate));
		StringBuilder letters = new StringBuilder(lettersAmPm.format(messageDate));

		String timestamp = 	"["+hours+":"+minutes+" "+letters+"]";

		return timestamp;
	}

	//returns time stamp
	public String getTimestamp()
	{
		return timestamp;
	}

	//returns user name
	public String getUserName()
	{
		return userName;
	}

	//returns message body
	public String getBody()
	{
		return message;
	}

	//returns location
	public String getLocation()
	{
		return location;
	}

	//returns message status
	public String getStatus()
	{
		return status;
	}

	//returns message status as number 1-4, used for filtering with toggle buttons
	public int getStatusEnum()
	{
		int i=0;

		if (status.equals("Buyer"))
		{i=1;}
		else if (status.equals("Seller"))
		{i=2;}
		else if (status.equals("Trade"))
		{i=3;}
		else if (status.equals("Info"))
		{i=4;}

		return i;
	}

	//returns the type of message(post/broadcast)
	public String getMessageType()
	{
		return messageType;
	}

	//returns link to the picture attached or default "no picture attached" image
	public String getPictureLink()
	{
		return pictureLink;
	}
	
	//sets timestamp to a value specified, used with addTimestamp() function
	public void setTimestamp(String timeStamp)
	{
		timestamp=timeStamp;
	}

	//sets user name
	public void setUser(String user)
	{
		userName=user;
	}

	//sets message body
	public void setBody(String msg)
	{
		message= msg;
	}

	//sets location 
	public void setLocation(String loc)
	{
		location = loc;
	}

	//sets message status
	public void setStatus(String Status)
	{
		status = Status;
	}

	//sets message type
	public void setMessageType(String type)
	{
		messageType = type;
	}

	//sets link to an image
	public void setPictureLink(String link)
	{
		pictureLink = link;
	}

}
