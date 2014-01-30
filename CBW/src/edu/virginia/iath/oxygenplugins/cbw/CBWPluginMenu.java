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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import layout.SpringUtilities;

import edu.virginia.iath.oxygenplugins.cbw.helpers.ChoiceBoxObject;
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
		JMenuItem insertTypeMenuItem = new JMenuItem("Insert type");

		insertTypeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
				InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		insertTypeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String boxtitle = "Insert Type";

				final Vector<String> possibleVals = new Vector<String>();
				final Vector<ChoiceBoxObject> selections = new Vector<ChoiceBoxObject>();
				

				final JFrame frame = new JFrame(boxtitle);
				final JPanel addPanel = new JPanel(new SpringLayout());
				final JPanel selectionsPanel = new JPanel();
				
				
				// Pick which ones are useful
				WSTextEditorPage ed = null;
				WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
					ed = (WSTextEditorPage)editorAccess.getCurrentPage();
				}
				
				WSTextEditorPage textpage = (WSXMLTextEditorPage) ed;
	            WSTextXMLSchemaManager schema = textpage.getXMLSchemaManager();
	            try{
	            	// Build an element of type
	            	//WhatElementsCanGoHereContext ctxt = new WhatElementsCanGoHereContext();
	            	
	            	// Get current context and add a <type> element to ask which values this element can take
	            	WhatElementsCanGoHereContext ctxt = schema.createWhatElementsCanGoHereContext(ed.getSelectionStart());
	            	ContextElement elem = new ContextElement();
	            	elem.setQName("type"); elem.setType("type");
	            	ctxt.pushContextElement(elem, null);
	            	
	            	// Populate the list of possible Values
	            	List<CIValue> vals = schema.whatPossibleValuesHasElement(ctxt);
	            	for( CIValue val : vals) {
	            		possibleVals.add(val.getValue());
	            	}
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	System.err.println("Something went wrong");
	            }
				

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

						//String result = "key=\"" + ((ComboBoxObject) possibleVals.getSelectedItem()).id + "\"";
						String result = "";
						for (ChoiceBoxObject c : selections) {
							if (c.getValue() != null) {
								result += "<type>" + c.getValue() + "</type>";
							}
						}
						
						
						// Update the text in the document
						ed.beginCompoundUndoableEdit();
						int selectionOffset = ed.getSelectionStart();
						ed.deleteSelection();
						javax.swing.text.Document doc = ed.getDocument();
						try {
							doc.insertString(selectionOffset, result,
								javax.swing.text.SimpleAttributeSet.EMPTY);
							frame.setVisible(false);
							frame.dispose();
						} catch (javax.swing.text.BadLocationException b) {
							// Okay if it doesn't work
							System.err.println("Couldn't add to document");
						}
						ed.endCompoundUndoableEdit();

						return;
					}
				});
				insert.setPreferredSize(new Dimension(100,25));
				
				JButton addAnother = new JButton("+");
				addAnother.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent sel) {

						// Add the initial selection box
						ChoiceBoxObject c = new ChoiceBoxObject(possibleVals, selectionsPanel);
						GridLayout g = (GridLayout) selectionsPanel.getLayout();
						g.setRows(g.getRows() + 1);
						selections.add(c);
						selectionsPanel.add(c);
						selectionsPanel.revalidate();
						selectionsPanel.repaint();
						frame.pack();
						frame.repaint();
						frame.pack();
						
						return;
					}
				});
				addAnother.setPreferredSize(new Dimension(100,25));



				java.awt.GridLayout layoutOuter = new java.awt.GridLayout(4,1); // rows, columns
				java.awt.FlowLayout layout = new java.awt.FlowLayout(FlowLayout.CENTER); 

				//addPanel.setLayout(layoutOuter);
				addPanel.add(new JLabel("Choose the type of item.  Use the + button to add multple types."));
				selectionsPanel.setLayout(layout);
				addPanel.add(selectionsPanel);
				addPanel.add(addAnother);
				addPanel.add(insert);
				
				//Lay out the panel.
				SpringUtilities.makeCompactGrid(addPanel,
				                                4, 1, 		 //rows, cols
				                                6, 6,        //initX, initY
				                                6, 6);       //xPad, yPad

				
				// Add the initial selection box
				selectionsPanel.setLayout(new java.awt.GridLayout(1,1));
				ChoiceBoxObject c = new ChoiceBoxObject(possibleVals, selectionsPanel);
				selections.add(c);
				selectionsPanel.add(c);
				selectionsPanel.revalidate();
				selectionsPanel.repaint();
				//frame.repaint();
				//frame.pack();
				
				frame.setContentPane(addPanel);
				frame.setLocationRelativeTo(null);
				frame.pack();
				frame.repaint();
				frame.pack();
				frame.setVisible(true);
				//JOptionPane.showMessageDialog((java.awt.Frame)ws.getParentFrame(), addPanel, boxtitle, JOptionPane.PLAIN_MESSAGE);

			}
		});
		this.add(insertTypeMenuItem);
		
	}

	public static void main(String[] args) {
		return;
	}


}
