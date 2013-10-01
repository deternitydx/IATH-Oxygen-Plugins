/**
* The Institute for Advanced Technology in the Humanities
*
* Copyright 2013 University of Virginia. Licensed under the Educational Community License, Version 2.0 (the
* "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
* License at
*
* http://opensource.org/licenses/ECL-2.0
* http://www.osedu.org/licenses/ECL-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
* the License for the specific language governing permissions and limitations under the License.
*
*
*/
package edu.virginia.iath.oxygenplugins.getid.helpers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
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
		return databases.keySet();
	}

	public String getDatabaseString(String dbname) {
		return databases.get(dbname);
	}

	public boolean addDatabase(String name, String connect) {
		boolean success =  databases.put(name, connect) != null;
		writeDatabases();
		return success;
	}

	public String getCurrentDB() {
		return currentDB;
	}

	public void setCurrentDB(String name) {
		currentDB = name;
		writeCurrentDB();
	}

	@SuppressWarnings("unchecked")
	public void readStorage() {
		try {
			// Read object using ObjectInputStream
			ObjectInputStream reader = 
					new ObjectInputStream (new 
							FileInputStream(getWorkspace().getPreferencesDirectory() + "/GetIDPluginMap.data"));

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
							FileInputStream(getWorkspace().getPreferencesDirectory() + "/GetIDPluginCur.data"));

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

	public void writeDatabases() {
		try {

			// Grab an output stream to write the data object to disk
			ObjectOutputStream writer = new ObjectOutputStream (new 
					FileOutputStream(getWorkspace().getPreferencesDirectory() + "/GetIDPluginMap.data"));
			// Write data to disk
			writer.writeObject ( databases );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeCurrentDB() {
		try {

			// Grab an output stream to write the data object to disk
			ObjectOutputStream writer2 = new ObjectOutputStream (new 
					FileOutputStream(getWorkspace().getPreferencesDirectory() + "/GetIDPluginCur.data"));
			// Write data to disk
			writer2.writeObject ( currentDB );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Gets the next ID based on the current local options
	public String getNextID() {
		try {
			URL url = new URL(getDatabaseString(getCurrentDB()));
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			return in.readLine();
		} catch (Exception e) {
			return null;
		}
	}

}
