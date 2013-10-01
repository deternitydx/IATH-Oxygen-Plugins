package edu.virginia.iath.oxygenplugins.getid.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;


import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

public class LocalOptions {

	private HashMap<String,String> databases;
	private String currentDB;

	public PluginWorkspace getWorkspace() {
		return PluginWorkspaceProvider.getPluginWorkspace();
	}

	public Set<String> getDatabases() {
		System.err.println("Getting databases");
		return databases.keySet();
	}

	public String getDatabaseString(String dbname) {
		return databases.get(dbname);
	}

	public boolean addDatabase(String name, String connect) {
		boolean success =  databases.put(name, connect) != null;
		writeStorage();
		return success;
	}

	public String getCurrentDB() {
		return currentDB;
	}

	public void setCurrentDB(String name) {
		currentDB = name;
		writeStorage();
	}

	@SuppressWarnings("unchecked")
	public void readStorage() {
		try {
			
			System.err.println("Reading local information from: " + getWorkspace().getPreferencesDirectory());
			// Read object using ObjectInputStream
			ObjectInputStream reader = 
					new ObjectInputStream (new 
							FileInputStream(getWorkspace().getPreferencesDirectory() + "GetIDPluginMap.data"));

			// Read the object
			Object obj = reader.readObject();
			if (obj instanceof HashMap)
			{
				databases = (HashMap<String,String>) obj;
			}
		} catch (IOException e) {
			e.printStackTrace();
			databases = new HashMap<String,String>();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			databases = new HashMap<String,String>();
		}
		

		try {
			// Read object using ObjectInputStream
			ObjectInputStream reader = 
					new ObjectInputStream (new 
							FileInputStream(getWorkspace().getPreferencesDirectory() + "GetIDPluginCur.data"));

			// Read the object
			Object obj = reader.readObject();
			if (obj instanceof String)
			{
				currentDB = (String) obj;
			}
		} catch (IOException e) {
			e.printStackTrace();
			currentDB = null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			currentDB = null;
		}
	}

	public void writeStorage() {
		try {

			// Grab an output stream to write the data object to disk
			ObjectOutputStream writer = new ObjectOutputStream (new 
					FileOutputStream(getWorkspace().getPreferencesDirectory() + "GetIDPluginMap.data"));
			// Write data to disk
			writer.writeObject ( databases );
			

			// Grab an output stream to write the data object to disk
			writer = new ObjectOutputStream (new 
					FileOutputStream(getWorkspace().getPreferencesDirectory() + "GetIDPluginCur.data"));
			// Write data to disk
			writer.writeObject ( currentDB );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
