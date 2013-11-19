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
package edu.virginia.iath.oxygenplugins.juel;

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

import edu.virginia.iath.oxygenplugins.juel.helpers.ComboBoxObject;
import edu.virginia.iath.oxygenplugins.juel.helpers.LocalOptions;

import ro.sync.contentcompletion.xml.CIAttribute;
import ro.sync.contentcompletion.xml.WhatAttributesCanGoHereContext;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

public class JUELPluginMenu extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private StandalonePluginWorkspace ws = null;
	private LocalOptions options = null;

	private static String name = "JUEL";



	public JUELPluginMenu(StandalonePluginWorkspace spw, LocalOptions ops) {
		super(name, true);
		ws = spw;
		options = ops;

		// setup the options
		//options.readStorage();

		// Find names
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
							possibleVals.setEnabled(false);

							for (int i = 0; i < obj.length(); i++) {
								JSONObject cur = obj.getJSONObject(i);
								String name = cur.getString("label");
								String id = String.format("P%05d",cur.getInt("value"));
								possibleVals.addItem(new ComboBoxObject(name, id));
								possibleVals.setEnabled(true);
							}
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

			}
		});
		this.add(search);
		
		// Find places
		search = new JMenuItem("Find Place");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String label = "Find Place";

				final JTextField searchText = new JTextField("", 30);
				searchText.setPreferredSize(new Dimension(350,25));
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
							URL url = new URL("http://academical.village.virginia.edu/academical_db/places/find_places?term=" + searchText.getText());
							BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
							while ((line = in.readLine()) != null) {
								json += line;
							}
							JSONArray obj = new JSONArray(json);

							// Read the JSON and update possibleVals
							possibleVals.removeAllItems();
							possibleVals.setEnabled(false);

							for (int i = 0; i < obj.length(); i++) {
								JSONObject cur = obj.getJSONObject(i);
								String id = String.format("PL%04d",cur.getInt("value"));
								String name = cur.getString("label") + " (" + id + ")";
								possibleVals.addItem(new ComboBoxObject(name, id));
								possibleVals.setEnabled(true);
							}
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

						return;
					}
				});
				insert.setPreferredSize(new Dimension(100,25));



				java.awt.GridLayout layoutOuter = new java.awt.GridLayout(3,1);
				java.awt.FlowLayout layout = new java.awt.FlowLayout(FlowLayout.RIGHT); // rows, columns

				JPanel addPanel = new JPanel();
				JPanel addPanelInner = new JPanel();
				addPanel.setLayout(layoutOuter);
				addPanel.add(new JLabel("Search for a place name, then choose one from the list below"));
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Search Keyword: "));
				addPanelInner.add(searchText);
				addPanelInner.add(search);
				addPanel.add(addPanelInner);
				addPanelInner = new JPanel();
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Narrow Search: "));
				addPanelInner.add(possibleVals);
				addPanelInner.add(insert);
				addPanel.add(addPanelInner);

				JOptionPane.showMessageDialog((java.awt.Frame)ws.getParentFrame(), addPanel, label, JOptionPane.PLAIN_MESSAGE);

			}
		});
		this.add(search);
		
		// Find corporate bodies
		search = new JMenuItem("Find Corporate Body");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String label = "Find Corporate Body";

				final JTextField searchText = new JTextField("", 30);
				searchText.setPreferredSize(new Dimension(350,25));
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
							URL url = new URL("http://academical.village.virginia.edu/academical_db/corporate_bodies/find?term=" + searchText.getText());
							BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
							while ((line = in.readLine()) != null) {
								json += line;
							}
							JSONArray obj = new JSONArray(json);

							// Read the JSON and update possibleVals
							possibleVals.removeAllItems();
							possibleVals.setEnabled(false);

							for (int i = 0; i < obj.length(); i++) {
								JSONObject cur = obj.getJSONObject(i);
								String id = String.format("CB%04d",cur.getInt("value"));
								String name = cur.getString("label") + " (" + id + ")";
								possibleVals.addItem(new ComboBoxObject(name, id));
								possibleVals.setEnabled(true);
							}
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

						return;
					}
				});
				insert.setPreferredSize(new Dimension(100,25));



				java.awt.GridLayout layoutOuter = new java.awt.GridLayout(3,1);
				java.awt.FlowLayout layout = new java.awt.FlowLayout(FlowLayout.RIGHT); // rows, columns

				JPanel addPanel = new JPanel();
				JPanel addPanelInner = new JPanel();
				addPanel.setLayout(layoutOuter);
				addPanel.add(new JLabel("Search for a corporate body, then one from the list below"));
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Search Keyword: "));
				addPanelInner.add(searchText);
				addPanelInner.add(search);
				addPanel.add(addPanelInner);
				addPanelInner = new JPanel();
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Narrow Search: "));
				addPanelInner.add(possibleVals);
				addPanelInner.add(insert);
				addPanel.add(addPanelInner);

				JOptionPane.showMessageDialog((java.awt.Frame)ws.getParentFrame(), addPanel, label, JOptionPane.PLAIN_MESSAGE);

			}
		});
		this.add(search);
		
		// Find Courses
		search = new JMenuItem("Find Course");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String label = "Find Course";

				final JTextField searchText = new JTextField("", 30);
				searchText.setPreferredSize(new Dimension(350,25));
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
							URL url = new URL("http://academical.village.virginia.edu/academical_db/courses/find?term=" + searchText.getText());
							BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
							while ((line = in.readLine()) != null) {
								json += line;
							}
							JSONArray obj = new JSONArray(json);

							// Read the JSON and update possibleVals
							possibleVals.removeAllItems();
							possibleVals.setEnabled(false);

							for (int i = 0; i < obj.length(); i++) {
								JSONObject cur = obj.getJSONObject(i);
								String id = String.format("C%04d",cur.getInt("value"));
								String name = cur.getString("label") + " (" + id + ")";
								possibleVals.addItem(new ComboBoxObject(name, id));
								possibleVals.setEnabled(true);
							}
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

						return;
					}
				});
				insert.setPreferredSize(new Dimension(100,25));



				java.awt.GridLayout layoutOuter = new java.awt.GridLayout(3,1);
				java.awt.FlowLayout layout = new java.awt.FlowLayout(FlowLayout.RIGHT); // rows, columns

				JPanel addPanel = new JPanel();
				JPanel addPanelInner = new JPanel();
				addPanel.setLayout(layoutOuter);
				addPanel.add(new JLabel("Search for a course, then choose one from the list below"));
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Search Keyword: "));
				addPanelInner.add(searchText);
				addPanelInner.add(search);
				addPanel.add(addPanelInner);
				addPanelInner = new JPanel();
				addPanelInner.setLayout(layout);
				addPanelInner.add(new JLabel("Narrow Search: "));
				addPanelInner.add(possibleVals);
				addPanelInner.add(insert);
				addPanel.add(addPanelInner);

				JOptionPane.showMessageDialog((java.awt.Frame)ws.getParentFrame(), addPanel, label, JOptionPane.PLAIN_MESSAGE);

			}
		});
		this.add(search);
	}

	public static void main(String[] args) {
		return;
	}


}
