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
package edu.virginia.iath.oxygenplugins.cbw;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import edu.virginia.iath.oxygenplugins.cbw.helpers.ComboBoxObject;
import edu.virginia.iath.oxygenplugins.cbw.helpers.LocalOptions;

import ro.sync.contentcompletion.xml.CIAttribute;
import ro.sync.contentcompletion.xml.CIValue;
import ro.sync.contentcompletion.xml.ContextElement;
import ro.sync.contentcompletion.xml.WhatAttributesCanGoHereContext;
import ro.sync.contentcompletion.xml.WhatElementsCanGoHereContext;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

public class CBWPluginMenu extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private StandalonePluginWorkspace ws = null;
	private LocalOptions options = null;

	private static String name = "CBW";



	public CBWPluginMenu(StandalonePluginWorkspace spw, LocalOptions ops) {
		super(name, true);
		ws = spw;
		options = ops;

		// setup the options
		//options.readStorage();

		// Find names
		JMenuItem search = new JMenuItem("Insert Item");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String boxtitle = "";

				String[] items = {"Select One", "Persona Type", "Stage of Life", "Event", "Persona Description", "Discourse"};
				final JComboBox itemType = new JComboBox(items);
				itemType.setPreferredSize(new Dimension(350,25));
				//itemType.addItem("");

				final JComboBox possibleVals = new JComboBox();
				possibleVals.setEnabled(false);
				possibleVals.setPreferredSize(new Dimension(350,25));

				final Map<String,String> types = new HashMap<String,String>();
            	types.put("Persona Type", "personaType");
            	types.put("Stage of Life", "stageOfLife");
            	types.put("Event", "event");
            	types.put("Persona Description", "topos");
            	types.put("Discourse", "discourse");
				
				itemType.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent selection) {
						
						if (itemType.getSelectedItem().equals("Select One"))
							possibleVals.setEnabled(false);
						else {
							// Pick which ones are useful
							WSTextEditorPage ed = null;
							WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
							if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
								ed = (WSTextEditorPage)editorAccess.getCurrentPage();
							}
							
							WSTextEditorPage textpage = (WSXMLTextEditorPage) ed;
				            WSTextXMLSchemaManager schema = textpage.getXMLSchemaManager();
				            possibleVals.removeAllItems();
				            try{
				            	// Build an element of type
				            	//WhatElementsCanGoHereContext ctxt = new WhatElementsCanGoHereContext();
				            	
				            	// Get current context and add item, then ask for values
				            	WhatElementsCanGoHereContext ctxt = schema.createWhatElementsCanGoHereContext(ed.getSelectionStart());
				            	ContextElement elem = new ContextElement();
				            	elem.setQName(types.get(itemType.getSelectedItem())); 
				            	elem.setType(types.get(itemType.getSelectedItem()));
				            	ctxt.pushContextElement(elem, null);
				            	elem = new ContextElement();
				            	elem.setQName("type"); elem.setType("type");
				            	ctxt.pushContextElement(elem, null);
				            	
				            	List<CIValue> vals = schema.whatPossibleValuesHasElement(ctxt);
				            	for( CIValue val : vals) {
				            		possibleVals.addItem(val.getValue());
				            	}
				            } catch (Exception e) {
				            	e.printStackTrace();
				            	System.err.println("Something went wrong");
				            }
							
							// Set the box enabled
							possibleVals.setEnabled(true);
						}
		            	return;
					}
				});

				JButton insert = new JButton("Insert");
				insert.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent sel) {

						String item = types.get(itemType.getSelectedItem());
						// Insert into the page
						// Get the selected value, grab the ID, then insert into the document

						// Get the editor
						WSTextEditorPage ed = null;
						WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
						if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
							ed = (WSTextEditorPage)editorAccess.getCurrentPage();
						}

						//String result = "key=\"" + ((ComboBoxObject) possibleVals.getSelectedItem()).id + "\"";
						String result = "<" + item + ">\n";
						result += "<textUnitReference></textUnitReference>\n";
						result += "<type>" + ((String) possibleVals.getSelectedItem()) + "</type>\n";
						result += "</" + item + ">\n";
						
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
							System.err.println("Couldn't add to document");
						}
						ed.endCompoundUndoableEdit();

						return;
					}
				});
				insert.setPreferredSize(new Dimension(100,25));



				java.awt.GridLayout layoutOuter = new java.awt.GridLayout(5,1);
				java.awt.FlowLayout layout = new java.awt.FlowLayout(FlowLayout.CENTER); // rows, columns

				JPanel addPanel = new JPanel();
				JPanel addPanelInner = new JPanel();
				addPanel.setLayout(layoutOuter);
				addPanel.add(new JLabel("Choose an item type to insert."));
				addPanelInner.setLayout(layout);
				addPanelInner.add(itemType);
				addPanel.add(addPanelInner);
				addPanel.add(new JLabel("Then, choose the type of item.  Use the + button to add multple types."));
				addPanelInner = new JPanel();
				addPanelInner.setLayout(layout);
				addPanelInner.add(possibleVals);
				addPanel.add(addPanelInner);
				addPanel.add(insert);

				JOptionPane.showMessageDialog((java.awt.Frame)ws.getParentFrame(), addPanel, boxtitle, JOptionPane.PLAIN_MESSAGE);

			}
		});
		this.add(search);
		
	}

	public static void main(String[] args) {
		return;
	}


}
