package edu.virginia.iath.snac.oxygenplugins.sample;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

public class SamplePlugin extends Plugin {

	private static SamplePlugin instance = null;

	/**
	 * SamplePlugin constructor.
	 *
	 * @param descriptor Plugin descriptor object.
	 */
	public SamplePlugin(PluginDescriptor descriptor) {
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
	public static SamplePlugin getInstance() {
		return instance;
	}

	/**
	 * Get the plugin extension
	 *
	 * @return the plugin extension
	 */
	public SamplePluginExtension getExtension() {
		return (SamplePluginExtension)instance.getDescriptor().getExtension("WorkspaceAccess");
	}

}
