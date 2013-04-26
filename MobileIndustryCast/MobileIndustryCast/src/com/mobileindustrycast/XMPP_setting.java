package com.mobileindustrycast;

//asmack library used is freeware distributed under apache 
//license(LICENSE fille attached in root), no NOTICE file provided by author

import android.app.Activity;


import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;





/**
 * Gather the xmpp settings and create an XMPPConnection
 */
public class XMPP_setting extends Activity {
	//values needed for connection configuaration
	public  final String HOST = "192.168.1.117";//"http://velington-pc"
	public  final int PORT_NUMBER = 5222 ;
	public  final String SERVICE = "conference.velington-pc";
	public  final String RECIPIENT = "industrycast@conference.velington-pc";

	//credentials for Openfire built-it DB
	public  final String LOGIN="boris";
	public  final String PASSWORD="malkoy666";




	Message message;//message object, packet to be transfered to anf from Openfire server


	XMPPConnection connection;//object specifying networking parameters to be used in file transfer operations on Openfire  


	public void connect()
	{	

		//Configuring connection parameters with the setting predefined by team or user's input if available
		ConnectionConfiguration config = new ConnectionConfiguration(HOST,PORT_NUMBER, SERVICE);

		// config.setDebuggerEnabled(true); <--helpful if you are encountering a problem on the XMPP side(LogCat output)


		connection = new XMPPConnection(config);

		//Connecting to selected host and service
		try {
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			connection.connect();
		} catch (XMPPException ex) {
			System.out.println("Exception at connect");
			System.out.println(ex.toString());
		}
	}

	//Set of functions necessary to perform login(from user point perspective)
	public void login() throws XMPPException {

		try {
			//user login. Needs to match the data from the DB associated with OpenFire Server
			connection.login(LOGIN, PASSWORD);						
		} catch (XMPPException ex) {
			System.out.println(ex.toString());
		}
	}



	//Function used to disconnect from the server connection(may be necessary if the server doesn't kick idle users) to be used in onDestruct()
	public void disconnect() {
		connection.disconnect();
	}


}
