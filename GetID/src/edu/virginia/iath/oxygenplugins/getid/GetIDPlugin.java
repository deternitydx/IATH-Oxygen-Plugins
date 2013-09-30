package edu.virginia.iath.oxygenplugins.getid;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class GetIDPlugin extends Plugin {

	private static GetIDPlugin instance = null;

	/**
	 * SamplePlugin constructor.
	 *
	 * @param descriptor Plugin descriptor object.
	 */
	public GetIDPlugin(PluginDescriptor descriptor) {
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
	public static GetIDPlugin getInstance() {
		return instance;
	}

	/**
	 * Get the plugin extension
	 *
	 * @return the plugin extension
	 */
	public GetIDPluginExtension getExtension() {
		return (GetIDPluginExtension)instance.getDescriptor().getExtension("WorkspaceAccess");
	}

}
