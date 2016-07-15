package com.symphony.utils;

import com.symphony.conjugates.Presence;

public class PresenceConverter {
	public static Presence convertfromString(String presenceStr){
		for (Presence presence : Presence.values()){
			if (presence.toString().equals(presenceStr))
				return presence;
		}
		return null;
	}
}
