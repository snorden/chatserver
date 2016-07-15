package com.symphony.utils;

import java.util.Collection;
import java.util.Date;

import com.symphony.conjugates.ChatConstants;
import com.symphony.conjugates.ClientAttributes;
/** 
 * Message Builder: Creates message request objects for sending on socket to trigger server commands
 * Routines include sending handshakes, generating presence requests, and contact information requests
 * 
 */

public class MessageBuilder {
	public static String generateHandshake(ClientAttributes attributes) throws Exception{
		if (attributes.getAlias() == null || attributes.getFirstName() == null || attributes.getLastName() == null)
			throw new Exception("Invalid handshake (alias,firstname,lastname must be specified)");
		return ChatConstants.REGISTERUSER+ChatConstants.DELIMITER+
				attributes.getAlias() +ChatConstants.DELIMITER+attributes.getFirstName()+ChatConstants.DELIMITER+attributes.getLastName() +ChatConstants.EOL;
	}

	public static String generatePresence(String[] originalRequest,String presence){
		StringBuilder retVal = new StringBuilder();
		for (String part : originalRequest){
			retVal.append(part).append(ChatConstants.DELIMITER);
		}
		retVal.append(presence).append(ChatConstants.EOL);
		return retVal.toString();
	}

	public static String generatePresenceAll(Collection<ClientAttributes> clientAttributes) {
		StringBuilder allPresence = new StringBuilder();
		for (ClientAttributes client : clientAttributes){
			allPresence.append(client.getAlias()).append(ChatConstants.DELIMITER).append(client.getPresence()).append(";");
		}
		allPresence.append(ChatConstants.EOL);
		return allPresence.toString();
	}
	public static String generateContactinformation(String alias, Collection<ClientAttributes> clientAttributes){
		StringBuilder contact = new StringBuilder();
		for (ClientAttributes client : clientAttributes){
			if ((alias != null && client.getAlias().equals(alias)) || (alias == null)){
				contact.append(client.getAlias()).append(ChatConstants.DELIMITER).append(client.getFirstName()).append(ChatConstants.DELIMITER)
				.append(client.getLastName()).append(ChatConstants.DELIMITER).append(new Date(client.getLastActivetime())).append(ChatConstants.DELIMITER)
				.append(client.getPresence()).append(";");
			}
		}
		contact.append(ChatConstants.EOL);
		return contact.toString();
	}
}
