package edu.virginia.iath.oxygenplugins.getid;

import javax.swing.JMenuBar;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;


public class GetIDPluginExtension implements WorkspaceAccessPluginExtension {


	/**
	 * Plugin workspace access.
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	/**
	 * On application startup, add SamplePlugin menu to top-level menubar.
	 */
	public void applicationStarted(
			final StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;

		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {

			public void customizeMainMenu(JMenuBar mainMenuBar) {
				// Add the SamplePlugin to the next-to-last spot in the menu
				mainMenuBar.add(new GetIDPluginMenu(pluginWorkspaceAccess),
						mainMenuBar.getMenuCount() - 1);
			}
		});

	}

	public boolean applicationClosing() {
		return true;
	}

}