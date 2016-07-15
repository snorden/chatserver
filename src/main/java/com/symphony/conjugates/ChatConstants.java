package com.symphony.conjugates;

public class ChatConstants {
	public static final String SERVERPORT="SERVER.PORT";
	public static final String BLOCKINGCHANNEL = "SERVER.BLOCKING";
	public static final String REGISTERUSER = "REGISTER";
	public static final String DEREGISTERUSER = "DEREGISTER";
	public static final String CONTACT = "CONTACT";
	public static final String SETPRESENCE = "SET_PRESENCE";
	public static final String GETPRESENCE = "GET_PRESENCE";
	public static final String SEND = "SEND";
	public static final String GROUPSEND = "GROUPSEND";
	public static final String BROADCAST = "BROADCAST";
	public static final String SERVERHOST = "SERVER.HOST";
	public static final String DELIMITER = "|";
	public static final String EOL = "\n";
	public static final int MINIMUMHANDSHAKEPARAMETERS = 4; // REGISTER|ALIAS|FIRSTNAME|LASTNAME
	public static final String MSG_SUCCESS = "SUCCESS\n";
	public static final String SUCCESS = "SUCCESS";
	public static final String IDLETIMEOUTMILLIS = "IDLETIMEOUT.MILLIS";
	public static final String AWAYTIMEOUTMILLIS = "AWAYTIMEOUT.MILLIS";
	public static final String SPLITDELIMITER = "\\|";
	public static final int MINIMUMSENDPARAMETERS = 3;//SEND|<ALIAS>|<msg>
	public static final String MSG_ERROR_INVALIDHANDSHAKE = "ERROR_INVALID_HANDSHAKE\n";
	public static final String MSG_ERROR_DUPLICATEUSER = "ERROR_DUPLICATE_USER\n";
	public static final String MSG_ERROR_UNREACHABLE = "ERROR_DESTINATION_UNREACHABLE\n";
	public static final String MSG_ERROR_INVALIDSEND = "ERROR_INVALID_SENDMESSAGE";
	public static final int MINIMUMPARAMETERS = 2;
	public static final String MEMBERSPLITTER = ";"; // used to split aliases for group-send
}
