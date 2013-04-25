package org.mark.rsc.models;

public class ModelNames {

	private int hash;
	private String name;

	public ModelNames(int hash, String name) {
		this.setHash(hash);
		this.setName(name);
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public int getHash() {
		return hash;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
