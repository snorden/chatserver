package com.symphony.chatserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.conjugates.ChatConstants;
import com.symphony.conjugates.ClientAttributes;
import com.symphony.conjugates.Presence;
import com.symphony.utils.ConnectionUtils;
import com.symphony.utils.MessageBuilder;
import com.symphony.utils.PresenceConverter;
/**
 * Generic Handler mechanism that handles both sides of the socket to a client
 * Same code is invoked by the ChatServer as well as the ChatClient for handling their bi-directional connections
 * Code also opens relay sockets from the server to destination clients
 * 
 * @author Samphel Norden
 * @param  ChatServer - Optional parameter which is null if client is invoking 
 * @param  Socket - socket that is returned after server accepts a client connection
 * @param  Handshake - initial handshake string which is used only when client invokes and wants to connect
 * @returns Nothing
 * @version 1.0
 */

public class ChatHandler implements Runnable {
	private Socket socket;
	private ChatServer serverHandle;
	private BlockingQueue<String> bufferedMessages = new LinkedBlockingQueue<>(1000);
	private static Logger logger = LoggerFactory.getLogger(ChatHandler.class);
	private String handShake = null;
	public ChatHandler(ChatServer server,Socket socket,String handShake){
		this.serverHandle = server;
		this.socket = socket;
		this.handShake = handShake;

	}
	private static class ChatReader implements Runnable{

		private Socket socket = null;
		private ChatServer server;
		private Map<String,BufferedWriter> clientrelayWriters = new ConcurrentHashMap<>();
		private BlockingQueue<String> localQueue; 
		private String myAlias = null;
		public ChatReader(Socket socket,BlockingQueue<String> queue,ChatServer server){
			this.socket = socket;
			this.server = server;
			this.localQueue = queue;

		}
		public void run() {
			BufferedReader bufferedReader = null;
			try{
				bufferedReader =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String msg = null;
				if (server != null){
					msg = bufferedReader.readLine();
					String[] handshakeMsg = msg.split(ChatConstants.SPLITDELIMITER,-1);

					logger.debug("Received client message "+msg);
					if (handshakeMsg.length != ChatConstants.MINIMUMHANDSHAKEPARAMETERS){
						localQueue.offer(ChatConstants.MSG_ERROR_INVALIDHANDSHAKE);
						return;
					}
					if (ChatConstants.REGISTERUSER.equals(handshakeMsg[0]) 
							&& server.getClientStatemap().containsKey(handshakeMsg[1])){
						logger.error("Already registered client with alias: {}"+handshakeMsg[1]);
						localQueue.offer(ChatConstants.MSG_ERROR_DUPLICATEUSER);
						return;
					}
					ClientAttributes newClient = new ClientAttributes(handshakeMsg,socket);
					server.getClientStatemap().put(newClient.getAlias(), newClient);
					myAlias = newClient.getAlias();
				}
				// Is this a server/relay
				boolean processing = true;
				
				
				while (processing && (msg = bufferedReader.readLine()) != null){
					logger.debug("Received msg {} at client with server = {}",msg,server);

					if (server != null){
						relayMessage(msg,myAlias);
						if (server.getClientStatemap().containsKey(myAlias)){
							server.getClientStatemap().get(myAlias).setLastActivetime(System.currentTimeMillis());
							server.getClientStatemap().get(myAlias).setPresence(Presence.ACTIVE);
						}
					}
					else{
						msg += ChatConstants.EOL;
						switch(msg){
						case ChatConstants.MSG_ERROR_INVALIDHANDSHAKE: 
						case ChatConstants.MSG_ERROR_DUPLICATEUSER: 
						case ChatConstants.MSG_ERROR_INVALIDSEND:
							processing = false;
							localQueue.offer(msg);
							break;
						default:
							System.out.println(msg); // write the message out at client
							if (msg.startsWith(ChatConstants.DEREGISTERUSER))
								processing = false;
						};
					}
				}
			}catch(Exception e){
				logger.error("Connection terminated at client with error {}",e.getMessage());
			}finally{
				if (server != null)
					server.getClientStatemap().remove(myAlias);
				ConnectionUtils.closeConnection(bufferedReader);
			}
		}
		private void relayMessage(String msg,String myAlias) {
			String[] splitMsg = msg.split(ChatConstants.SPLITDELIMITER,-1);
			String alias = null;
			if (splitMsg.length >= 2 && !msg.startsWith(ChatConstants.BROADCAST))
				alias = splitMsg[1];

			if (msg.startsWith(ChatConstants.SEND) || msg.startsWith(ChatConstants.GROUPSEND) ||
					msg.startsWith(ChatConstants.BROADCAST)){
				// find the destination client from the server's hashmap
				// format is SEND|USER_ALIAS|<msg>
				if (alias != null){
					String[] splitMembers = alias.split(ChatConstants.MEMBERSPLITTER,-1);
					for (String member : splitMembers){
						ClientAttributes destinationClient = server.getClientStatemap().get(member);
						if (destinationClient == null){
							// client hasnt registered yet so cannot do anything
							localQueue.offer(ChatConstants.MSG_ERROR_UNREACHABLE);
							continue;
						}
						else{
							BufferedWriter clientWriter = clientrelayWriters.get(member);
							try {
								if (clientWriter == null){
									clientWriter = new BufferedWriter(new OutputStreamWriter(destinationClient.getClientSocket().getOutputStream()));
									clientrelayWriters.put(member, clientWriter);						
								}
								clientWriter.write(msg+ChatConstants.EOL);
								clientWriter.flush();
							}catch (IOException e) {
								logger.error("Error when sending message to {} with exception: {}",member,e.getMessage());
								logger.debug("TRACE: "+e);
								continue;
							}
						}
					}
				} else{
					// Broadcast mode
					for (ClientAttributes destinationClient : server.getClientStatemap().values()){
						BufferedWriter clientWriter = clientrelayWriters.get(destinationClient.getAlias());
						try {
							if (clientWriter == null){
								clientWriter = new BufferedWriter(new OutputStreamWriter(destinationClient.getClientSocket().getOutputStream()));
								clientrelayWriters.put(destinationClient.getAlias(), clientWriter);						
							}
							clientWriter.write(msg+ChatConstants.EOL);
							clientWriter.flush();
						}catch (IOException e) {
							logger.error("Error when sending message to {} with exception: {}",destinationClient.getAlias(),e.getMessage());
							logger.debug("TRACE: "+e);
							continue;
						}
					}
				}


			}
			else if (msg.startsWith(ChatConstants.DEREGISTERUSER) && alias.equals(myAlias)){//DEREGISTER|<alias>
				server.getClientStatemap().remove(alias);
				if (clientrelayWriters.containsKey(alias)){
					try {
						clientrelayWriters.get(alias).write(msg+ChatConstants.EOL);
						clientrelayWriters.get(alias).flush();
					} catch (IOException e) {
						logger.error("Error when relaying deregister message with exception: {}",e.getMessage());
						logger.debug("TRACE: "+e);
						
					}					
				}
				clientrelayWriters.remove(alias);
			}

			else if (msg.startsWith(ChatConstants.GETPRESENCE)){
				if (alias != null){
					if (server.getClientStatemap().containsKey(alias)){
						localQueue.offer(MessageBuilder.generatePresence(splitMsg, server.getClientStatemap().get(alias).getPresence().toString()));
					}
				}else{
					localQueue.offer(MessageBuilder.generatePresenceAll(server.getClientStatemap().values()));

				}

			}
			else if (msg.startsWith(ChatConstants.SETPRESENCE)){
				if (server.getClientStatemap().containsKey(alias)){
					Presence newPresence = PresenceConverter.convertfromString(splitMsg[2]);
					server.getClientStatemap().get(alias).setPresence(newPresence);
				}

			}
			else if (msg.startsWith(ChatConstants.CONTACT)){
				localQueue.offer(MessageBuilder.generateContactinformation(alias, server.getClientStatemap().values()));				
			}
			
		}

	}
	private static class ChatWriter implements Runnable{
		private Socket socket = null;
		private BlockingQueue<String> bufferedQueue = null;
		private ChatServer server = null;
		private String handShake;
		public ChatWriter(Socket socket,BlockingQueue<String> bufferedMessagequeue,
				ChatServer server,String handShake){
			this.socket = socket;
			this.bufferedQueue = bufferedMessagequeue;
			this.server = server;
			this.handShake = handShake;
		}
		public void run() {
			BufferedWriter bufferedWriter = null;
			try{
				String msg = null;
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				if (server == null){
					// this is a client writer... send the handshake to the server
					bufferedWriter.write(handShake);
					bufferedWriter.flush();
				}
				boolean processing = true;
				while(processing && (msg = bufferedQueue.take()) != null){
					logger.debug("Now processing {} msg from local queue with server {}",msg,server);
					switch(msg){

					case ChatConstants.MSG_ERROR_INVALIDHANDSHAKE: 
					case ChatConstants.MSG_ERROR_DUPLICATEUSER: 
					case ChatConstants.MSG_ERROR_INVALIDSEND:
						processing = false;
						break;
					default:
						bufferedWriter.write(msg); // send to destination
						bufferedWriter.flush();
						break;
					};
					if (msg.startsWith(ChatConstants.DEREGISTERUSER))
						processing = false;
				}
				logger.info("Terminating the writer thread");
			}catch(Exception e){
				logger.error("Error while processing {}",e.getMessage());
				logger.debug("TRACE: "+e);
			}finally{
				ConnectionUtils.closeConnection(bufferedWriter);
				System.exit(-5);
			}
		}

	}
	public void run(){
		try{
			ChatReader reader = new ChatReader(socket,bufferedMessages,serverHandle);
			new Thread(reader).start();
			ChatWriter writer = new ChatWriter(socket,bufferedMessages,serverHandle,handShake);
			new Thread(writer).start(); // writer is not invoked when we are on server

		}catch(Exception e){
			logger.error("Error in client with exception {}",e.getMessage());
			logger.debug("TRACE:"+e);
		}
	}
	protected BlockingQueue<String> getBufferedMessages() {
		return bufferedMessages;
	}
	protected void setBufferedMessages(BlockingQueue<String> bufferedMessages) {
		this.bufferedMessages = bufferedMessages;
	}


}
