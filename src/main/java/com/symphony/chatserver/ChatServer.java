package com.symphony.chatserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.conjugates.ChatConstants;
import com.symphony.conjugates.ClientAttributes;
import com.symphony.utils.ResourceReader;
import com.symphony.utils.SocketFactory;
/**
 * ChatServer code for starting the chatserver daemon process
 * 
 * @author Samphel Norden
 * @param  FileName - Specifies full path of file that contains server properties
 * including port and hostname and optional parameters like IDLETIMEOUT, AWAYTIMEOUT
 * @returns Nothing
 * @version 1.0
 */


public class ChatServer {
	private Properties properties = null;
	private String propFilelocation = null;
	private final Timer timer = new Timer();
	private  Map<String,ClientAttributes> clientStatemap = new ConcurrentHashMap<>();
	private static Logger logger = LoggerFactory.getLogger(ChatServer.class);
	public  Map<String,ClientAttributes> getClientStatemap() {
		return clientStatemap;
	}

	
	private ChatServer(String propFilelocation){
		this.propFilelocation = propFilelocation;
	}
	
	public static void main(String[] args){
		if (args.length != 1){
			System.err.println("Must specify properties file (full path) as argument");
			System.exit(-1);
		}
		ChatServer chatServer = new ChatServer(args[0]);
		chatServer.startServer();
	}
	
	public void startServer(){
		if (initialize()){
			timer.schedule(new CheckPointer(properties,this),1000,30000);
			acceptClients();
		}
		
	}
	private boolean initialize() {
		try{
			properties = ResourceReader.readPropertiesfromfile(propFilelocation);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	private void acceptClients() {
		// TODO Auto-generated method stub
		try{
			ServerSocket serverSocket = SocketFactory.createServerSocket(properties);
			//ServerSocketChannel serverChannel = SocketFactory.createSocketchannel(properties);
			logger.info("Chat Server started and listening to port {}",properties.getProperty(ChatConstants.SERVERPORT));
			
			while (true){
				Socket socket = serverSocket.accept();
				processConnection(socket);
			    
			}
		}catch(Exception e){
			
		}
	}

	
	
	private void processConnection(Socket socket) {
		// TODO Auto-generated method stub
		
		try{
			// spawn off a new ChatHandler Thread
			// let the handler deal with additional messages
			ChatHandler chatHandler = new ChatHandler(this,socket,null);// indicates its a server side chat handler
			Thread chatThread = new Thread(chatHandler);
			chatThread.setDaemon(true);
			chatThread.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
	
}
