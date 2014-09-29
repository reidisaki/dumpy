package com.yoneko.models;

public class PhoneContact {

	String name;
	String number;
	String displayName;
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public PhoneContact(String name, String number, String displayname){
		this.name = name;
		this.number = number;
		this.displayName = displayname;
	}
}
