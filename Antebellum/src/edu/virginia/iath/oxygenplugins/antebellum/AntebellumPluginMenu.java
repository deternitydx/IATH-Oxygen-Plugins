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
package edu.virginia.iath.oxygenplugins.antebellum;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.virginia.iath.oxygenplugins.antebellum.helpers.ComboBoxObject;
import edu.virginia.iath.oxygenplugins.antebellum.helpers.LocalOptions;

import ro.sync.contentcompletion.xml.CIAttribute;
import ro.sync.contentcompletion.xml.WhatAttributesCanGoHereContext;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

public class AntebellumPluginMenu extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private StandalonePluginWorkspace ws = null;
	private LocalOptions options = null;

	private static String name = "Antebellum";



	public AntebellumPluginMenu(StandalonePluginWorkspace spw, LocalOptions ops) {
		super(name, true);
		ws = spw;
		options = ops;

		// setup the options
		//options.readStorage();


		JMenuItem search = new JMenuItem("Find Name");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String label = "Find Name";

				final JTextField lastName = new JTextField("", 30);
				lastName.setPreferredSize(new Dimension(350,25));
				//JTextField projectName = new JTextField("", 30);

				final JComboBox possibleVals = new JComboBox();
				possibleVals.setEnabled(false);
				possibleVals.setPreferredSize(new Dimension(350,25));

				JButton search = new JButton("Search");
				search.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent selection) {
						// query the database
						try {
							String json = "";
							String line;
							URL url = new URL("http://academical.village.virginia.edu/academical_db/people/find_people?term=" + lastName.getText());
							BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
							while ((line = in.readLine()) != null) {
								json += line;
							}
							JSONArray obj = new JSONArray(json);

							// Read the JSON and update possibleVals
							possibleVals.removeAllItems();
							for (int i = 0; i < obj.length(); i++) {
								JSONObject cur = obj.getJSONObject(i);
								String name = cur.getString("label");
								String[] split = name.split("\\(");
								name = split[0].trim();
								String id = split[1].replace(")", "").trim();
								possibleVals.addItem(new ComboBoxObject(name, id));
							}

							possibleVals.setEnabled(true);
						} catch (Exception e) {
							e.printStackTrace();
							possibleVals.setEnabled(false);
						}

						return;
					}
				});
				search.setPreferredSize(new Dimension(100,25));

				JButton insert = new JButton("Insert");
				insert.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent sel) {
						// Insert into the page
						// Get the selected value, grab the ID, then insert into the document

						// Get the editor
						WSTextEditorPage ed = null;
						WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
						if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
							ed = (WSTextEditorPage)editorAccess.getCurrentPage();
						}

						String result = "key=\"" + ((ComboBoxObject) possibleVals.getSelectedItem()).id + "\"";

						// Update the text in the document
						ed.beginCompoundUndoableEdit();
						int selectionOffset = ed.getSelectionStart();
						ed.deleteSelection();
						javax.swing.text.Document doc = ed.getDocument();
						try {
							doc.insertString(selectionOffset, result,
									javax.swing.text.SimpleAttributeSet.EMPTY);
						} catch (javax.swing.text.BadLocationException b) {
							// Okay if it doesn't work
						}
						ed.endCompoundUndoableEdit();

						return;
					}
				});
				insert.setPreferredSize(new Dimension(100,25));



				java.awt.GridLayout layoutOuter = new java.awt.GridLayout(3,1);
				java.awt.FlowLayout layout = new java.awt.FlowLayout(FlowLayout.RIGHT); // rows, columns

				JPanel addPanel = new JPanel();
				JPanel addPanelInner = new JPanel();
				addPanel.setLayout(layoutOuter);
				addPanel.add(new JLabel("Search for last name, then choose a full name from the list below"));
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Search Last Name: "));
				addPanelInner.add(lastName);
				addPanelInner.add(search);
				addPanel.add(addPanelInner);
				addPanelInner = new JPanel();
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Narrow Search: "));
				addPanelInner.add(possibleVals);
				addPanelInner.add(insert);
				addPanel.add(addPanelInner);

				JOptionPane.showMessageDialog((java.awt.Frame)ws.getParentFrame(), addPanel, label, JOptionPane.PLAIN_MESSAGE);

				//int result = JOptionPane.showConfirmDialog((java.awt.Frame)ws.getParentFrame(),
				//		addPanel, label, JOptionPane.CANCEL_OPTION);


			}
		});
		this.add(search);




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
				} else {
					// The ID attribute is not allowed here in the document, so give an error message
					JOptionPane.showMessageDialog((java.awt.Frame) ws.getParentFrame(),
							"The 'id' attribute is not allowed in the current context.", "Warning", JOptionPane.ERROR_MESSAGE);
				}




			}
		};
		menuItem.addActionListener(action);
		//this.add(menuItem);

	}

	public static void main(String[] args) {
		return;
	}


}