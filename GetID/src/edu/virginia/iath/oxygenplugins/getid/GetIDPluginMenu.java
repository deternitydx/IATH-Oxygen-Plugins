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
package edu.virginia.iath.oxygenplugins.getid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import edu.virginia.iath.oxygenplugins.getid.helpers.LocalOptions;

import ro.sync.contentcompletion.xml.CIAttribute;
import ro.sync.contentcompletion.xml.WhatAttributesCanGoHereContext;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

public class GetIDPluginMenu extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ButtonGroup currentDatabases;
	private Menu setupMenu;

	private StandalonePluginWorkspace ws = null;
	private LocalOptions options = null;

	private static String name = "GetID";

	public GetIDPluginMenu(StandalonePluginWorkspace spw, LocalOptions ops) {
		super(name, true);
		ws = spw;
		options = ops;
		
		// setup the options
		options.readStorage();

		// Add the menu to select and/or add a new database
		setupMenu = new Menu("Select Project");

		// create radio-button style menu for defined document types
		currentDatabases = new ButtonGroup();
		JRadioButtonMenuItem currentDBItem;

		for (String project: options.getDatabases()) {
			currentDBItem = new JRadioButtonMenuItem(project);
			currentDBItem.setText(project);
			currentDBItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent selection) {
					options.setCurrentDB(((JRadioButtonMenuItem) selection.getSource()).getText());
				}
			});
			if (project.equals(options.getCurrentDB())) {
				currentDBItem.setSelected(true);
			} else {
				System.err.println("Database: " + project + ", does not match currentdb: " + options.getCurrentDB());
			}
			currentDatabases.add(currentDBItem);
			setupMenu.add(currentDBItem);
		}

		this.add(setupMenu);

		JMenuItem addNew = new JMenuItem("Add New Project");
		addNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String label = "Add New Project";

				JTextField projectURL = new JTextField("http://", 30);
				JTextField projectName = new JTextField("", 30);

				JPanel addPanel = new JPanel();
				java.awt.GridLayout layout = new java.awt.GridLayout(2,2); // rows, columns
				addPanel.setLayout(layout);
				addPanel.add(new JLabel("Project Name: "));
				addPanel.add(projectName);
				addPanel.add(new JLabel("Project ID URL: "));
				addPanel.add(projectURL);

				int result = JOptionPane.showConfirmDialog((java.awt.Frame)ws.getParentFrame(),
						addPanel, label, JOptionPane.OK_CANCEL_OPTION);

				// On OK, store the new database
				if (result == JOptionPane.OK_OPTION) {
					// Add the database
					options.addDatabase(projectName.getText(), projectURL.getText());
					options.setCurrentDB(projectName.getText());
					
					// Add to the menu
					JRadioButtonMenuItem currentDBItem = new JRadioButtonMenuItem(projectName.getText());
					currentDBItem.setText(projectName.getText());
					currentDBItem.setSelected(true);
					currentDatabases.clearSelection();
					currentDatabases.add(currentDBItem);
					setupMenu.add(currentDBItem);
				}
			}
		});
		this.add(addNew);




		// Add a divider
		this.addSeparator();

		// Add the insert ID menu item
		JMenuItem menuItem = new JMenuItem("Insert Unique ID");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// call something on the action

				// Get the editor
				WSTextEditorPage ed = null;
				WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
					ed = (WSTextEditorPage)editorAccess.getCurrentPage();
				}
				
				// Check that the attribute can be put here:
				boolean allowedHere = false;
		        if (ed != null && ed instanceof WSXMLTextEditorPage) {
		            WSTextEditorPage textpage = (WSXMLTextEditorPage) ed;
		            WSTextXMLSchemaManager schema = textpage.getXMLSchemaManager();
		            try {
		                // use the schema to get a context-based list of allowable elements
						int selectionOffset = ed.getSelectionStart();
		                WhatAttributesCanGoHereContext elContext = schema.createWhatAttributesCanGoHereContext(selectionOffset);
		                List<CIAttribute> attributes;
		                attributes = schema.whatAttributesCanGoHere(elContext);
		                
		                // loop through the list to see if the tag we want to add
		                // matches a name on any of the allowed elements
		                for (int i=0; attributes != null && i < attributes.size(); i++) {
		                    ro.sync.contentcompletion.xml.CIAttribute at = attributes.get(i);
		                    if (at.getName().equals("id")) {
		                        allowedHere = true;
		                        break;
		                    }
		                }
		            } catch (Exception e) {
		            	// If any exception occurs, then this is not allowed here, so we won't continue
		                allowedHere = false;
		            }
		        }


				

				// Insert the ID attribute into the document
				if (allowedHere) {
					// Grab the next ID
					String nextID = options.getNextID();

					// Plug the ID into the result
					String result = "id=\"" + nextID + "\"";
					
					ed.beginCompoundUndoableEdit();
					int selectionOffset = ed.getSelectionStart();
					ed.deleteSelection();
					javax.swing.text.Document doc = ed.getDocument();
					try {
						if (selectionOffset > 0 && !doc.getText(selectionOffset - 1, 1).equals(" "))
							result = " " + result;
						if (selectionOffset > 0 && !doc.getText(selectionOffset,1).equals(" ") && !doc.getText(selectionOffset,1).equals(">"))
							result = result + " ";
						doc.insertString(selectionOffset, result,
								javax.swing.text.SimpleAttributeSet.EMPTY);
					} catch (javax.swing.text.BadLocationException b) {
						// Okay if it doesn't work
					}
					ed.endCompoundUndoableEdit();
				}




			}
		};
		menuItem.addActionListener(action);
		this.add(menuItem);

	}


}
