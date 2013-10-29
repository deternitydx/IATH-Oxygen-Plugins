package edu.virginia.iath.oxygenplugins.juel.helpers;

public class ComboBoxObject {
	public String name;
	public String id;
	public ComboBoxObject(String name, String id) {
		this.name = name; this.id = id;
	}
	public String toString() {
		return this.name;
	}
	public String debugInfo() {
		return this.name + "::" + this.id;
	}
}
