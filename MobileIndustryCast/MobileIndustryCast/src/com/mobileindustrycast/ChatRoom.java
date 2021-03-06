
package com.mobileindustrycast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import com.mobileindustrycast.R;
import com.mobileindustrycast.XMPP_setting;

import android.text.TextWatcher;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View.OnClickListener;

public class ChatRoom extends Activity implements BroadcastDialog.NoticeDialogListener {

	Handler mHandler; //Handler for working threads that deal with values of UI thread
	XMPP_setting xmpp = new XMPP_setting();//Declaration of XMPP setting information, see respective class for reference
	MultiUserChat muc; //Multiuser chat object used to handle group chats, such as the one used by Openfire to accommodate client's requirements
	Message message;//XMPP message, incluedes the body our code deals with and other information needed for 
	//application level file transfer between the application and the Openfire server
	ArrayList<CustomListMessage> msg = new ArrayList<CustomListMessage>();//array list used to store messages retrieved from the Openfire server
	String USERNAME="Boris";//User name to be displayed in chat
	String userLocation = "BC";//Location to be displayed in chat
	String userStatus = "Buyer";//status of the message, value "Buyer" is an example for programmers working on the code. 
	//Value dynamically changes with each message being sent

	String filter ="";//String holding the values inserted into a serch text field

	ExtendedArrayAdapter adapter;//adapter for the ListView to be used with msg array of CustomListMessage objects
	ListView mlist;//Value holding reference to the list view element of UI

	BroadcastDialog dialog = new BroadcastDialog();//Declaration of custom dialog used for broadcast status dialog

	private final int REQUEST_CODE_PICK_FILE = 2;//value specifying the level we want to be able to access in file browsing (1-folder,2-file)
	InputStream inputStream;//needed for image transfer between application and php service

	final String php_URL = "http://localhost/test";//folder containing php service
	String yourPictureLink = "http://cs410727.vk.me/v410727315/75c9/kv80Ec8oCww.jpg";//should have default "User did not attach picture to the message" image

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room);  

		//Creation of dialog trigered on click of the sell/trade broadcast list item, shows picture only, layout can be modifyed in res/layout/image_layout.xml file
		final Dialog imageDialog = new Dialog(this);
		imageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		imageDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
				, null));
		imageDialog.setCanceledOnTouchOutside(true);


		adapter = new ExtendedArrayAdapter(this, msg);//adapter for CustomListMessage array and listView element
		mlist =(ListView) findViewById(R.id.messagesListView);
		mlist.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		//mlist.setStackFromBottom(true);//-stacks messages from the bottom
		mlist.setAdapter(adapter);   	
		
		//click listener for list items
		OnItemClickListener listListener = new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
			{   	  	//you are able to see only sellers or traders goods and only if they broadcast it(i.e. no pictures displayed for "post" type messages	
				if (adapter.get(position).getMessageType().equals("broadcast") && (adapter.get(position).getStatus().equals("Trade")||
						adapter.get(position).getStatus().equals("Seller"))){
					ImageView image = (ImageView) imageDialog.findViewById(R.id.pictureHolder);
					download(adapter.get(position).getPictureLink(), image);
					imageDialog.show();
					}
			}

		};


		mlist.setOnItemClickListener(listListener);

		//Buttons of main UI, corresponding to "Post", "Broadcast" and "Attach picture"
		Button post_button = (Button) findViewById(R.id.post_btn);
		Button broadcast_button = (Button) findViewById(R.id.broadcast_btn);
		Button attach_button = (Button) findViewById(R.id.button1);

		//toggle buttons of main UI
		final ToggleButton buyer = (ToggleButton) findViewById(R.id.toggle_buyer);
		final ToggleButton seller = (ToggleButton) findViewById(R.id.toggle_seller);
		final ToggleButton trade = (ToggleButton) findViewById(R.id.toggle_trade);
		final ToggleButton info = (ToggleButton) findViewById(R.id.toggle_info);

		final Activity activityForButton = this;

		//listeners for toggle buttons state change
		buyer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					adapter.buyerSelected=true;
					adapter.getFilter().filter(filter);
				} else {
					adapter.buyerSelected=false;
					adapter.getFilter().filter(filter);
				}
			}
		});

		seller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					adapter.sellerSelected=true;
					adapter.getFilter().filter(filter);
				} else {
					adapter.sellerSelected=false;
					adapter.getFilter().filter(filter);
				}
			}
		});

		trade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					adapter.tradeSelected=true;
					adapter.getFilter().filter(filter);
				} else {
					adapter.tradeSelected=false;
					adapter.getFilter().filter(filter);
				}
			}
		});

		info.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					adapter.infoSelected=true;
					adapter.getFilter().filter(filter);
				} else {
					adapter.infoSelected=false;
					adapter.getFilter().filter(filter);
				}
			}
		});



		//Working Thread that's associated with networking function login() for XMPP, look XMPP_setting
		new Thread(new Runnable() {
			public void run() {
				try 
				{
					xmpp.connect();
					xmpp.login();
				} catch (XMPPException ex) {
					System.out.println(ex.toString());
				}

				//Setting up message listener, listener parameters for message packets require adapter to be used in UI Thread
				muc  = new MultiUserChat(xmpp.connection, xmpp.RECIPIENT);
						try{
							muc.join(USERNAME);//To include preferred user name(constrains may apply)
							ConsumerMUCMessageListener listener = new ConsumerMUCMessageListener();
							muc.addMessageListener(listener);
						} catch (XMPPException ex) {
							System.out.println(ex.toString());
						}

						//sending presence packet, showing your availability to the server
						Presence presence = new Presence(Presence.Type.available);
						xmpp.connection.sendPacket(presence);

			}
		}).start();


		//Post button event. Sends message with user defined body to the Openfire Server
		post_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String body = ((EditText) findViewById(R.id.post_text)).getText().toString();//getting message body from user input
				CustomListMessage message = new CustomListMessage(USERNAME, body, userLocation, yourPictureLink);


				//if text fiend is not empty sends the message to the server in for of extended message(timestamp and username are included in the message body)
				if (body!=null && body.length()!=0)
				{
					System.out.println("body: "+body);
					try 
					{
						sendMessage(messageContsructor(message)); 
						((EditText) findViewById(R.id.post_text)).setText("");
					} 
					catch (XMPPException e) 
					{
						System.out.println(e.toString());
					}
				}

			}
		});

		//broadcast click event handler
		broadcast_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String body = ((EditText) findViewById(R.id.post_text)).getText().toString();

				if (body.length()!=0 && body!=null)
				{
					showBroadcastDialog();
				}

			}
		});

		//Attach Picture button click event listener
		attach_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent fileExploreIntent = new Intent(
						com.mobileindustrycast.FileBrowserActivity.INTENT_ACTION_SELECT_FILE,
						null,
						activityForButton,
						com.mobileindustrycast.FileBrowserActivity.class
						);
				fileExploreIntent.putExtra(
						com.mobileindustrycast.FileBrowserActivity.startDirectoryParameter, 
						"/mnt/sdcard/DCIM/Camera");
				startActivityForResult(
						fileExploreIntent,
						REQUEST_CODE_PICK_FILE
						);       		    				
			}
		});

		//base for filtering using search field
		EditText searchWords = (EditText) findViewById(R.id.search_text);
		searchWords.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start,int before, int count) {
				filter = s.toString();
				adapter.getFilter().filter(filter);
			}
		});}

	//create list adapter function, useful if you plan to use more than one listView
	public void createListAdapter()
	{  	
		adapter = new ExtendedArrayAdapter(this, msg);
		mlist.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	//sending message to the Multi-User Chat
	public void sendMessage(String message_body) throws XMPPException {
		System.out.println("function 'sendMessage' started");

		try
		{
			muc.sendMessage(message_body);
		}
		catch (XMPPException e)
		{
			System.out.println(e.getMessage());
		}


	}

	//Turns message object into a String that can be transfered through XMPP
	public static String messageContsructor(CustomListMessage msg)
	{
		String message = "#timestamp "+msg.getTimestamp()+" timestamp#"
				+"#userName "+msg.getUserName()+" userName#"
				+"#message "+msg.getBody()+" message#"
				+"#location "+msg.getLocation()+" location#"
				+"#status "+msg.getStatus()+" status#"
				+"#messageType "+msg.getMessageType()+" messageType#"
				+"#pictureLink "+msg.getPictureLink()+" pictureLink#";
		return message;
	}

	//Turns string imput from the body of message received through XMPP into a message object 
	public static CustomListMessage messageDeConstructor(String message)
	{
		CustomListMessage msg = new CustomListMessage(message.substring(message.indexOf("#userName ")+"#userName ".length(),message.indexOf(" userName#")),
				message.substring(message.indexOf("#message ")+"#message ".length(),message.indexOf(" message#")),
				message.substring(message.indexOf("#location ")+"#location ".length(),message.indexOf(" location#")),
				message.substring(message.indexOf("#status ")+"#status ".length(),message.indexOf(" status#")),
				message.substring(message.indexOf("#messageType ")+"#messageType ".length(),message.indexOf(" messageType#")),
				message.substring(message.indexOf("#pictureLink ")+"#pictureLink ".length(),message.indexOf(" pictureLink#")));

		msg.setTimestamp(message.substring(message.indexOf("#timestamp ")+"#timestamp ".length(),message.indexOf(" timestamp#")));
		return msg;
	}

	//broadcast dialog supporting functions
	public void showBroadcastDialog() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new BroadcastDialog();
		dialog.show(getFragmentManager(), "Broadcast");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// User touched the buy button
		userStatus="Buyer";
		sendBroadcastMessage();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// User touched the trade button
		userStatus="Trade";
		sendBroadcastMessage();
	}

	@Override
	public void onDialogNeutralClick(DialogFragment dialog) {
		// User touched the sell button
		userStatus="Seller";
		sendBroadcastMessage();

	}

	//Event triggered by choosing one of three options of broadcast dialog
	public void sendBroadcastMessage()
	{
		String body = ((EditText) findViewById(R.id.post_text)).getText().toString();//getting message body from user input
		CustomListMessage message = new CustomListMessage(USERNAME, body, userLocation, userStatus, "broadcast",yourPictureLink);


		//if text fiend is not empty sends the message to the server in for of extended message(timestamp and uresname are included in the message body)
		try 
		{
			sendMessage(messageContsructor(message)); 
			((EditText) findViewById(R.id.post_text)).setText("");
		} 
		catch (XMPPException e) 
		{
			System.out.println(e.toString());
		}
	}

	protected void onDestroy()
	{
		super.onDestroy();
		xmpp.disconnect();		
	}

	//After browsing activity is done sets Picture chosen into main UI picture holder, sends picture to the php service to be stored online
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ImageView image = (ImageView) findViewById(R.id.imageView1);

		if (requestCode == REQUEST_CODE_PICK_FILE) {
			if(resultCode == RESULT_OK) {
				String newFile = data.getStringExtra(
						com.mobileindustrycast.FileBrowserActivity.returnFileParameter);
				if(newFile.contains(".png")||newFile.contains(".jpg")||
						newFile.contains(".jpeg")||newFile.contains(".gif")){
					Bitmap bMap = BitmapFactory.decodeFile(newFile);
					Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 400, 300, true);
					image.setImageBitmap(bMapScaled);
					Toast.makeText(
							this, 
							"This picture will be attached to your brodcasts from now on.",
							Toast.LENGTH_SHORT).show(); 

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bMapScaled.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
					byte [] byte_arr = stream.toByteArray();
					String image_str = Base64.encodeBytes(byte_arr);
					final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

					nameValuePairs.add(new BasicNameValuePair("image",image_str));

					//working thread for networking functions associated with php file transfer
					new Thread(new Runnable() {         
						public void run() {
							Looper.prepare();

							try{
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost(php_URL+"test.php");
								httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								HttpResponse response = httpclient.execute(httppost);
								String the_string_response = convertResponseToString(response);
								yourPictureLink = php_URL + "/" + the_string_response + ".jpg";
							}catch(Exception e){
								Toast.makeText(ChatRoom.this, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
								System.out.println("Error in http connection "+e.toString());
							}Looper.loop();
						}
					}).start();

				}
				else{
					Toast.makeText(
							this, 
							"Selected file is not an image",
							Toast.LENGTH_SHORT).show(); 
				}
			} 
			else{
				image.setImageResource(R.drawable.car_default);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//converts response from php service into a string to access the file name of image sent by the user
	public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{

		String res = "";
		StringBuffer buffer = new StringBuffer();
		inputStream = response.getEntity().getContent();
		int contentLength = (int) response.getEntity().getContentLength(); //getting content length�..
		Toast.makeText(ChatRoom.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
		if (contentLength < 0){
		}
		else{
			byte[] data = new byte[512];
			int len = 0;
			try
			{
				while (-1 != (len = inputStream.read(data)) )
				{
					buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer�..
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				inputStream.close(); // closing the stream�..
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			res = buffer.toString();     // converting stringbuffer to string�..

			Toast.makeText(ChatRoom.this, "Result : " + res, Toast.LENGTH_LONG).show();
			if(res.contains(".png")||res.contains(".jpg")||
					res.contains(".jpeg")||res.contains(".gif")){
				yourPictureLink = res;
			}
		}
		return res;
	}

	//function for receiving bitmaps from selected URL
	static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) { 
				System.out.println("ImageDownloader Error " + statusCode + " while retrieving bitmap from " + url); 
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent(); 
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();  
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or IllegalStateException
			getRequest.abort();
			System.out.println("ImageDownloader Error while retrieving bitmap from " + url+ ", "+ e.toString());
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	//fucntion combining bitmap download function to a list view using AsyncTread for networking
	public void download(String url, ImageView imageView) {
		BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		task.execute(url);
	}

	//Asynchronous task for networking functions associated with downloading bitmap
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		// Actual download method, run in the task thread
		protected Bitmap doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			return downloadBitmap(params[0]);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null) {
				ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 400, 300, true);
					imageView.setImageBitmap(bMapScaled);
				}
			}
		}
	}

	//Multi-User Chat listener class, necessary for packet(message type in this case) retrieval from the server. Adds messages received to the "messages" array  
	class ConsumerMUCMessageListener implements PacketListener {



		public void processPacket(Packet packet) {
			if ( packet instanceof Message) {
				message = (Message) packet;	
				System.out.println(message.getFrom() +": " + messageDeConstructor(message.getBody()).messageToString());
				// Add the incoming message to the list view
				final CustomListMessage messageObject = messageDeConstructor(message.getBody());
				runOnUiThread(new Runnable() {         
					public void run() {
						adapter.add(messageObject); 
					}
				});

			}          
		}
	}   

}
