package com.symphony.conjugates;

import java.io.Serializable;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

public class ClientAttributes implements Serializable{
	
	private static final long serialVersionUID = 5403721317919764655L;
	private SocketAddress remoteAddress;
	private long lastActivetime;
	private Presence presence;
	private String firstName;
	private String lastName;
	private String alias;
	private Socket clientSocket;
	
	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	public long getLastActivetime() {
		return lastActivetime;
	}
	public void setLastActivetime(long lastActivetime) {
		this.lastActivetime = lastActivetime;
	}
	public Presence getPresence() {
		return presence;
	}
	public void setPresence(Presence presence) {
		this.presence = presence;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	
	public String deregister() {
		return ChatConstants.DEREGISTERUSER+ChatConstants.DELIMITER+alias;
	}
	public ClientAttributes(String[] handshakeMsg,Socket socket){
		this.alias = handshakeMsg[1];
		this.firstName = handshakeMsg[2];
		this.lastName = handshakeMsg[3];
		this.presence = Presence.ACTIVE;
		this.clientSocket = socket;
		this.remoteAddress = socket.getRemoteSocketAddress();
		this.lastActivetime = System.currentTimeMillis();
	}
	public ClientAttributes(Socket socket) {
		this.clientSocket = socket;
	}
	
	public Socket getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	@Override
	public String toString() {
		return "ClientAttributes [ remoteAddress=" + remoteAddress + ", lastActivetime="
				+ new Date(lastActivetime) + ", presence=" + presence + ", firstName="
				+ firstName + ", lastName=" + lastName + ", alias=" + alias
				+ "]";
	}
}
