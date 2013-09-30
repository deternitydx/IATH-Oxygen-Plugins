package edu.virginia.iath.oxygenplugins.getid;

import javax.swing.JMenuBar;

import edu.virginia.iath.oxygenplugins.getid.helpers.LocalOptions;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;


public class GetIDPluginExtension implements WorkspaceAccessPluginExtension {


	/**
	 * Plugin workspace access.
	 */
	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private LocalOptions options;

	/**
	 * On application startup, add SamplePlugin menu to top-level menubar.
	 */
	public void applicationStarted(
			final StandalonePluginWorkspace pwa) {
		this.pluginWorkspaceAccess = pwa;
		options = new LocalOptions();

		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {

			public void customizeMainMenu(JMenuBar mainMenuBar) {
				// Add the SamplePlugin to the next-to-last spot in the menu
				mainMenuBar.add(new GetIDPluginMenu(pluginWorkspaceAccess, options),
						mainMenuBar.getMenuCount() - 1);
			}
		});

	}

	public boolean applicationClosing() {
		return true;
	}

}
