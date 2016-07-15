package com.symphony.chatserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.conjugates.ChatConstants;
import com.symphony.conjugates.ClientAttributes;
import com.symphony.utils.MessageBuilder;
import com.symphony.utils.ResourceReader;
import com.symphony.utils.SocketFactory;
/**
 * Client program that handles opening a connection to the Symphony ChatServer
 * 
 * @author Samphel Norden
 * @param  FileName - Specifies full path of file that contains client properties including 
 * client alias,firstname,lastname,server host/port
 * @returns Nothing
 * @version 1.0
 */
public class ChatClient {
	private Properties properties = null;
	private String propFilelocation = null;
	private ClientAttributes attributes ;
	BufferedReader stdinReader = null;
	Socket clientSocket = null;
	private Logger logger = LoggerFactory.getLogger(ChatClient.class);
	public ChatClient(String fileName){

		this.propFilelocation = fileName;

	}
	/** 
	 * Opens connection to server after reading from properties file
	 * Uses SocketFactory (non secure)
	 * Future Extensions: Take a secure mode parameter from properties and allow secure socket connection
	 */
	public void connect(){
		try{
			properties = ResourceReader.readPropertiesfromfile(propFilelocation);
			clientSocket = SocketFactory.createSocket(properties);
			initialize(clientSocket);	
			logger.info("Chat Client {} connected to server {}",attributes.getAlias(),attributes.getClientSocket().getRemoteSocketAddress()+":"+attributes.getClientSocket().getPort());
			
			String msg = null;
			
			ChatHandler chatHandler = new ChatHandler(null,clientSocket,MessageBuilder.generateHandshake(attributes));// null indicates this is not a server side chat handler
			Thread chatThread = new Thread(chatHandler);
			chatThread.setDaemon(true);
			chatThread.start();
			stdinReader = new BufferedReader(new InputStreamReader(System.in));
			while ((msg = stdinReader.readLine()) != null){
				if (msg.startsWith(ChatConstants.DEREGISTERUSER)){
					String[] split = msg.split(ChatConstants.SPLITDELIMITER);
					if (!split[1].equals(attributes.getAlias()))
						continue; // dont allow this user to deregister some other user
				}
				logger.debug("Adding {} from STDIN to queue",msg);
				
				chatHandler.getBufferedMessages().put(msg+ChatConstants.EOL);
				if (msg.startsWith(ChatConstants.DEREGISTERUSER)){
					try{
						Thread.sleep(1000);						
					}catch(InterruptedException e1){}
					break;
				}
			}
		}catch(Exception e){
			logger.error("Error in client with exception {}",e.getMessage());
			logger.debug("TRACE:"+e);
			
		}finally{
			closeConnection();
		}
	}
	private void closeConnection(){

		try{
			if (stdinReader != null)
				stdinReader.close();
		}catch(Exception e){}
		try{
			if (clientSocket != null)
				clientSocket.close();
		}catch(Exception e){}

	}
	/** 
	 * Key identifiers needed for any client are specified below
	 * Note that alias must be unique
	 */
	private boolean initialize(Socket socket) {
		try{
			attributes = new ClientAttributes(socket);
			attributes.setAlias(properties.getProperty("CLIENT.ALIAS"));
			attributes.setFirstName(properties.getProperty("CLIENT.FIRSTNAME"));
			attributes.setLastName(properties.getProperty("CLIENT.LASTNAME"));

		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;

	}
	public static void main(String[] args){
		if (args.length != 1){
			System.err.println("Must specify properties file (full path) as argument");
			System.exit(-1);
		}
		ChatClient chatClient = new ChatClient(args[0]);
		chatClient.connect();
	}
}
