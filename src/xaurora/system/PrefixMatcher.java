package xaurora.system;

import xaurora.io.DataFileIO;
import java.util.*;
public class PrefixMatcher {
	private static DataFileIO database = DataFileIO.instanceOf();
	
	public static ArrayList<String> getResult(String userInput){
		return database.getContent();
	}
}
