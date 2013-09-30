package edu.virginia.iath.oxygenplugins.getid;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import edu.virginia.iath.oxygenplugins.getid.helpers.LocalOptions;

import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

public class GetIDPluginMenu extends Menu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StandalonePluginWorkspace ws = null;

	private static String name = "GetID";

	public GetIDPluginMenu(StandalonePluginWorkspace spw) {
		super(name, true);
		ws = spw;

		// Add the menu to select and/or add a new database
		Menu setupMenu = new Menu("Select Project");

		// create radio-button style menu for defined document types
		ButtonGroup currentDatabases = new ButtonGroup();
		JRadioButtonMenuItem currentDBItem;

		for (String project: LocalOptions.getDatabases()) {
			currentDBItem = new JRadioButtonMenuItem(project);
			currentDBItem.setText(project);
			if (project.equals(LocalOptions.getCurrentDB())) {
				currentDBItem.setSelected(true);
			}
			currentDatabases.add(currentDBItem);
		}

		setupMenu.addSeparator();

		JMenuItem addNew = new JMenuItem("Add New Project");
		addNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent selection) {
				String label = "Add New Project";

				JTextField projectURL = new JTextField("http://", 80);
				JTextField projectName = new JTextField("", 40);

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
					LocalOptions.addDatabase(projectName.getText(), projectURL.getText());
				}
			}
		});
		setupMenu.add(addNew);

		this.add(setupMenu);



		// Add a divider
		this.addSeparator();

		// Add the insert ID menu item
		JMenuItem menuItem = new JMenuItem("Insert Unique ID");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		ActionListener action = new ActionListener() {
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

				// Grab the next ID
				String nextID = "E0001";

				// Plug the ID into the result
				String result = " id=\"" + nextID + "\"";

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
		};
		menuItem.addActionListener(action);
		this.add(menuItem);

	}


}