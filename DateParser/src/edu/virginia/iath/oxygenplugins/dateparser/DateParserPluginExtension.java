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

import javax.swing.JMenuBar;

import edu.virginia.iath.oxygenplugins.dateparser.helpers.LocalOptions;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;


public class DateParserPluginExtension implements WorkspaceAccessPluginExtension {


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
				mainMenuBar.add(new DateParserPluginMenu(pluginWorkspaceAccess, options),
						mainMenuBar.getMenuCount() - 1);
			}
		});

	}

	public boolean applicationClosing() {
		return true;
	}

}
