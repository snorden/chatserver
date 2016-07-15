package com.symphony.chatserver;

import java.util.Properties;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.symphony.conjugates.ChatConstants;
import com.symphony.conjugates.ClientAttributes;
import com.symphony.conjugates.Presence;
/**
 * CheckPointer code for printing the server/client status on the server every x minutes
 * Also Updates client presenece if client has been idle longer than threshold -> set Presence to IDLE
 * Also Updates client presenece if client has been away longer than threshold -> set Presence to AWAY
 * 
 * @author Samphel Norden
 * @param  Properties file
 * @param  ChatServer parent server process
 * @returns Status of all clients
 * @version 1.0
 */
public class CheckPointer extends TimerTask{
		private Properties prop;
		private ChatServer server;
		private Logger logger= LoggerFactory.getLogger(CheckPointer.class);
		public CheckPointer(Properties prop,ChatServer chatServer){
			this.prop = prop;
			this.server = chatServer;
		}
		@Override
		public void run() {
			long currTime = System.currentTimeMillis();
			long defaultTimeout = 120*1000;
			long awayTimeout = 300*1000;
			defaultTimeout = Integer.parseInt(prop.getProperty(ChatConstants.IDLETIMEOUTMILLIS,String.valueOf(defaultTimeout)));
			awayTimeout = Integer.parseInt(prop.getProperty(ChatConstants.AWAYTIMEOUTMILLIS,String.valueOf(awayTimeout)));
			// TODO Auto-generated method stub
			for (ClientAttributes attribute : server.getClientStatemap().values()){
				logger.info("Member:{}",attribute);
				if ((currTime - attribute.getLastActivetime()) >= awayTimeout){
					attribute.setPresence(Presence.AWAY);
				}
				else if ((currTime - attribute.getLastActivetime()) >= defaultTimeout){
					attribute.setPresence(Presence.IDLE);
				}
				
			}
			logger.info("\n");
		}
	
	
}
