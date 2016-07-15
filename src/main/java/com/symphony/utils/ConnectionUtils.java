package com.symphony.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
/**
 * ConnectionUtils: Cleans up existing wrapper objects
 */
public class ConnectionUtils {
	public static void closeConnection(BufferedReader bufferedReader){
		try{
			if (bufferedReader != null)
				bufferedReader.close();
		}catch(Exception e){}
	}
	public static void closeConnection(BufferedWriter bufferedWriter){
		try{
			if (bufferedWriter != null)
				bufferedWriter.close();
		}catch(Exception e){}
	}
}
