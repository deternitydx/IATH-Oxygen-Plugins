package edu.virginia.iath.snac.oxygenplugins.sample;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

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
	    JMenuItem menuItem1 = new JMenuItem("Item 1");
	    ActionListener action1 = new ActionListener() {
	      public void actionPerformed(ActionEvent actionEvent) {
	        // call something on the action
	      }
	    };
	    menuItem1.addActionListener(action1);
	    this.add(menuItem1);
	    
	    // add divider
	    this.addSeparator();
	    

	    // Add a menu item and the action for that itme
	    JMenuItem menuItem2 = new JMenuItem("Item 2");
	    ActionListener action2 = new ActionListener() {
	      public void actionPerformed(ActionEvent actionEvent) {
	        // call something on the action
	      }
	    };
	    menuItem2.addActionListener(action2);
	    this.add(menuItem1);
	}


}
