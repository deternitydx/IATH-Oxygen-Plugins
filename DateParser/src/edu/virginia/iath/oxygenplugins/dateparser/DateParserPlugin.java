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

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class DateParserPlugin extends Plugin {

	private static DateParserPlugin instance = null;

	/**
	 * SamplePlugin constructor.
	 *
	 * @param descriptor Plugin descriptor object.
	 */
	public DateParserPlugin(PluginDescriptor descriptor) {
		super(descriptor);

		if (instance != null) {
			throw new IllegalStateException("Already instantiated!");
		}
		instance = this;
	}

	/**
	 * Get the plugin instance.
	 *
	 * @return the shared plugin instance.
	 */
	public static DateParserPlugin getInstance() {
		return instance;
	}

	/**
	 * Get the plugin extension
	 *
	 * @return the plugin extension
	 */
	public DateParserPluginExtension getExtension() {
		return (DateParserPluginExtension)instance.getDescriptor().getExtension("WorkspaceAccess");
	}
	
	public static void main (String args[]) {
		return;
	}

}
