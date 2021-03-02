//$Id$
package com.dev.news.data;

import org.json.JSONObject;

public class Attribute {
	
	String name;
	
	String type;
	
	String unit, refUnit;
	
	String columnName;
	
	String displayName;
	
	boolean refDisplay = false, isIdentifier = false, isSystemAttr = false;

	public Attribute(JSONObject obj, String unit) {
		setName(obj.getString("name"));
		setType(obj.optString("type"));
		setUnit(unit);
		if("unit".equals(type)) {
			setRefUnit(obj.optString("unit"));
		}
		setRefDisplay(obj.optBoolean("ref_display"));
		setColumnName(obj.optString("column_name"));
		setDisplayName(obj.optString("display_name"));
		if(obj.has("identifier")) {
			setIdentifier(obj.getBoolean("identifier"));
		}
		
		if(obj.has("is_system")) {
			isSystemAttr = obj.getBoolean("is_system");
		}
	}
	
	void setIdentifier(boolean bool) {
		isIdentifier = bool;
	}
	
	void setRefDisplay(boolean bool) {
		refDisplay = bool;
	}
	
	public boolean isRefDisplay() {
		return refDisplay;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}


	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getRefUnitName() {
		return refUnit;
	}
	
	public void setRefUnit(String name) {
		refUnit  = name;
	}
	
	public String getColumnNameOnly() {
		return columnName.split("[.]")[1];
	}
	
	public boolean isIdentifier() {
		return isIdentifier;
	}
	
	public boolean isSystemAttribute() {
		return isSystemAttr;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
}
