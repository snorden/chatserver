package com.symphony.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.Properties;

import javax.net.ServerSocketFactory;

import com.symphony.conjugates.ChatConstants;

public class SocketFactory {
	/** 
	 * Socket channel factory for opening server and client sockets
	 * Future Extensions: Take a secure mode parameter from properties and allow secure socket connection
	 */
	
	public static ServerSocketChannel createSocketchannel(Properties properties) throws IOException {
		try{
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			
			int serverPort = Integer.parseInt(properties.getProperty(ChatConstants.SERVERPORT,"9160"));
			serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
			serverSocketChannel.configureBlocking(false);
			return serverSocketChannel;
		}catch(Exception e){
			throw new IOException(e);
		}
	}

	public static ServerSocket createServerSocket(Properties properties) throws IOException {
		try{
			
			int serverPort = Integer.parseInt(properties.getProperty(ChatConstants.SERVERPORT,"9160"));
			ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(serverPort);
			return serverSocket;
		}catch(Exception e){
			throw new IOException(e);
		}
	}
	
	public static Socket createSocket(Properties properties) throws IOException {
		try{
			int serverPort = Integer.parseInt(properties.getProperty(ChatConstants.SERVERPORT,"9160"));
			Socket socket = new Socket(properties.getProperty(ChatConstants.SERVERHOST), serverPort);
			return socket;
			
		}catch(Exception e){
			throw new IOException(e);
		}
	}
	
}
