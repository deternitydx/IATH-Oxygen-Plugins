package edu.virginia.iath.oxygenplugins.getid.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

public class LocalOptions {
	
	public static String CURRENTDB = "IATH::GetID::CurrentDatabase";
	public static String DBLIST = "IATH::GetID::StoredDatabase";
	public static String PREFIX = "IATH::GetID::DBConnect::";
	public static String DELIMITER = "|";
	
	public static PluginWorkspace getWorkspace() {
        return PluginWorkspaceProvider.getPluginWorkspace();
    }
	
	public static List<String> getDatabases() {
		String dbs = LocalOptions.getWorkspace().getOptionsStorage().getOption(DBLIST, "");
		List<String> ret = new ArrayList<String>(Arrays.asList(dbs.split(DELIMITER)));
		return ret;
	}
	
	public static String getDatabaseString(String dbname) {
		String dbstring = LocalOptions.getWorkspace().getOptionsStorage().getOption(PREFIX + dbname, "");
		return dbstring;
	}
	
	public static boolean addDatabase(String name, String connect) {
		List<String> currentDBs = LocalOptions.getDatabases();
		currentDBs.add(name);

		// Update the current list of databases to include the new database
		String curStr = "";
		for (String cdb : currentDBs) {
			curStr += DELIMITER + cdb;
		}
		if (curStr.length() > 0)
			curStr = curStr.substring(1);
		
		LocalOptions.getWorkspace().getOptionsStorage().setOption(DBLIST, curStr);
		// Add the connect string to the database
		LocalOptions.getWorkspace().getOptionsStorage().setOption(PREFIX + name, connect);
		
		if (LocalOptions.getDatabaseString(name).equals(connect))
			return true;
		return false;
		
	}
	
	public static String getCurrentDB() {
		return LocalOptions.getWorkspace().getOptionsStorage().getOption(CURRENTDB, "");
	}
	
	public static void setCurrentDB(String name) {
		LocalOptions.getWorkspace().getOptionsStorage().setOption(CURRENTDB, name);
	}

}
