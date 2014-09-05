package com.yoneko.models;

public class DrawerItem {
	private String drawerTitle;
	private boolean isChecked;
	private SimpleGeofence fence;
	public boolean isChecked() {
		return isChecked;
	}
	public SimpleGeofence getFence() {
		return fence;
	}
	public void setFence(SimpleGeofence fence) {
		this.fence = fence;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public String getDrawerTitle() {
		return drawerTitle;
	}
	public void setDrawerTitle(String drawerTitle) {
		this.drawerTitle = drawerTitle;
	}
	public DrawerItem(String title, SimpleGeofence f) {
		drawerTitle = title;
		isChecked = false;
		fence = f;
	}
}
