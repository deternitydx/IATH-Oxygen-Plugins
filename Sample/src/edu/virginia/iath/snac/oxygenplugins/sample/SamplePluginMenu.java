package edu.virginia.iath.snac.oxygenplugins.sample;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

public class SamplePluginMenu extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StandalonePluginWorkspace ws = null;

	private static String name = "Sample Plugin";

	public SamplePluginMenu(StandalonePluginWorkspace spw) {
		super(name, true);
		ws = spw;


		// Add a menu item and the action for that itme
		JMenuItem menuItem1 = new JMenuItem("Make Selection all lower case");
		ActionListener action1 = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// call something on the action

				// Get the editor
				WSTextEditorPage ed = null;
				WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
					ed = (WSTextEditorPage)editorAccess.getCurrentPage();
				}

				// Get the selected string
				String selection = null;
				if (ed != null && ed.hasSelection()) {
					selection = ed.getSelectedText();
				}

				if (selection != null) {
					// Do something with the string (ALL CAPS)
					String result = selection.toLowerCase();

					// Update the text in the document
					if (!result.equals(selection)) {
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
					}

				}


			}
		};
		menuItem1.addActionListener(action1);
		this.add(menuItem1);

		// add divider
		this.addSeparator();


		// Add a menu item and the action for that itme
		JMenuItem menuItem2 = new JMenuItem("Make Selection ALL UPPER CASE");
		ActionListener action2 = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// call something on the action

				// Get the editor
				WSTextEditorPage ed = null;
				WSEditor editorAccess = ws.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null && editorAccess.getCurrentPage() instanceof WSTextEditorPage) {
					ed = (WSTextEditorPage)editorAccess.getCurrentPage();
				}

				// Get the selected string
				String selection = null;
				if (ed != null && ed.hasSelection()) {
					selection = ed.getSelectedText();
				}

				if (selection != null) {
					// Do something with the string (ALL CAPS)
					String result = selection.toUpperCase();

					// Update the text in the document
					if (!result.equals(selection)) {
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
					}

				}


			}
		};
		menuItem2.addActionListener(action2);
		this.add(menuItem2);
	}


}
