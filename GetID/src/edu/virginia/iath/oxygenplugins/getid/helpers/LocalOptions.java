package edu.virginia.iath.oxygenplugins.getid.helpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;


import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;

public class LocalOptions {

	private class LocalStorage implements Serializable {
		private static final long serialVersionUID = 1L;
		public HashMap<String,String> databases;
		public String currentDB;
		
		public LocalStorage() {
			databases = new HashMap<String,String>();
			currentDB = null;
		}
	}

	private LocalStorage data = null;

	public PluginWorkspace getWorkspace() {
		return PluginWorkspaceProvider.getPluginWorkspace();
	}

	public Set<String> getDatabases() {
		System.err.println("Getting databases");
		return data.databases.keySet();
	}

	public String getDatabaseString(String dbname) {
		return data.databases.get(dbname);
	}

	public boolean addDatabase(String name, String connect) {
		boolean success =  data.databases.put(name, connect) != null;
		writeStorage();
		return success;
	}

	public String getCurrentDB() {
		return data.currentDB;
	}

	public void setCurrentDB(String name) {
		data.currentDB = name;
		writeStorage();
	}

	public void readStorage() {
		try {
			// Read object using ObjectInputStream
			ObjectInputStream reader = 
					new ObjectInputStream (new 
							FileInputStream(getWorkspace().getPreferencesDirectory() + "GetIDPlugin.data"));

			// Read the object
			Object obj = reader.readObject();
			if (obj instanceof LocalStorage)
			{
				data = (LocalStorage) obj;
			}
		} catch (IOException e) {
			e.printStackTrace();
			data = new LocalStorage();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			data = new LocalStorage();
		}
	}

	public void writeStorage() {
		try {

			// Grab an output stream to write the data object to disk
			ObjectOutputStream writer = new ObjectOutputStream (new 
					FileOutputStream(getWorkspace().getPreferencesDirectory() + "GetIDPlugin.data"));
			// Write data to disk
			writer.writeObject ( data );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
