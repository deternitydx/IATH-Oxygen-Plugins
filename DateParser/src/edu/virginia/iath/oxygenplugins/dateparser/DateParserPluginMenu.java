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
package edu.virginia.iath.oxygenplugins.dateparser;

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

import edu.virginia.iath.oxygenplugins.dateparser.helpers.DateParserHelper;
import edu.virginia.iath.oxygenplugins.dateparser.helpers.SNACDate;

import ro.sync.contentcompletion.xml.CIAttribute;
import ro.sync.contentcompletion.xml.WhatAttributesCanGoHereContext;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextXMLSchemaManager;
import ro.sync.exml.workspace.api.editor.page.text.xml.WSXMLTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

public class DateParserPluginMenu extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StandalonePluginWorkspace ws = null;

	private static String name = "DateParser";

	public DateParserPluginMenu(StandalonePluginWorkspace spw) {
		super(name, true);
		ws = spw;


		// Add the insert ID menu item
		JMenuItem menuItem = new JMenuItem("Parse Selected Date");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.SHIFT_MASK));
		ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// call something on the action

				// Get the editor
				WSTextEditorPage ed = null;
				WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
					ed = (WSTextEditorPage)editorAccess.getCurrentPage();
				}
				/**
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
		        }**/

		        // Get the selected string
				String selection = null;
				if (ed != null && ed.hasSelection()) {
					selection = ed.getSelectedText();
				}

				if (selection != null) {
					// Try to parse and build the date object:
					String dateStr = selection.replace("<date>", "").replace("</date>", "");
					String suspiciousDate = "http://socialarchive.iath.virginia.edu/control/term#SuspiciousDate";
					String xml = "";
					
					DateParserHelper parser = new DateParserHelper(dateStr);
					
					// Check to see if the values were parsed
					if (parser.wasParsed()) {
						
						List<SNACDate> dates = parser.getDates();
						
						// Build an XML object out of the results
						for (SNACDate d : dates) {
							// Open the tags
							if (d.getType() == SNACDate.FROM_DATE)
								xml += "<dateRange><fromDate";
							else if (d.getType() == SNACDate.TO_DATE)
								xml += "<toDate";
							else
								xml += "<date";
							
							// Add the dates to the XML
							if (!d.getParsedDate().equals("null"))
								xml += " standardDate=\"" + d.getParsedDate() + "\"";
							if (!d.getNotBefore().equals("null"))
								xml += " notBefore=\"" + d.getNotBefore() + "\"";
							if (!d.getNotAfter().equals("null"))
								xml += " notAfter=\"" + d.getNotAfter() + "\"";
							
							// Close the open tags
							xml += ">";
							
							// Add the original date passed to Java
							xml += d.getOriginalDate();
							
							// Close the tags
							if (d.getType() == SNACDate.FROM_DATE)
								xml += "</fromDate>";
							else if (d.getType() == SNACDate.TO_DATE)
								xml += "</toDate></dateRange>";
							else
								xml += "</date>";
						}
						
						
						
					} else {
						// nothing was parsed
						xml += "<date localType=\"" + suspiciousDate + "\">" + parser.getOriginalDate() +"</date>";
					}

					// Update the text in the document
					if (!xml.equals(selection)) {
						ed.beginCompoundUndoableEdit();
						int selectionOffset = ed.getSelectionStart();
						ed.deleteSelection();
						javax.swing.text.Document doc = ed.getDocument();
						try {
							doc.insertString(selectionOffset, xml,
									javax.swing.text.SimpleAttributeSet.EMPTY);
						} catch (javax.swing.text.BadLocationException b) {
							// Okay if it doesn't work
						}
						ed.endCompoundUndoableEdit();
					}

				}
				
				/**
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
				 **/



			}
		};
		menuItem.addActionListener(action);
		this.add(menuItem);

	}


}
