//$Id$
package com.dev.news.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.news.data.util.UnitUtil;

public class Unit {
	
	String unit;
	
	String apiPath;
	
	Attribute[] attributes;
	
	List<String> operations;
	
	String tableName;
	
	String handler;
	
	Attribute identifier;
	
	Attribute[] refDisplayAttributes;
	
	public Unit(JSONObject rawData) {
		
		unit = rawData.optString("name");
		apiPath = rawData.getString("api_path");
		tableName = rawData.getString("table_name");
		
		JSONArray temp1 = rawData.optJSONArray("attributes");
		setAttributes(UnitUtil.getAttributes(temp1, unit));
		
		JSONArray temp2 = rawData.optJSONArray("supported_operations");
		setOperations(UnitUtil.getOperations(temp2));
		
		handler = "com.dev.news.handler." + ( rawData.optString("handler") == null ? "GenericHandler" : rawData.optString("handler"));
		identifyRefDisplayFields();
		
		findIdentifier();
	}
	
	
	private void findIdentifier() {
		for(int i = 0 ; i < attributes.length; i++) {
			Attribute at = attributes[i];
			if(at.isIdentifier()) {
				identifier = at;
				break;
			}
		}
	}
	
	private void identifyRefDisplayFields() {
		List<Attribute> list = new ArrayList<>();
		for(int i = 0 ; i < attributes.length; i++) {
			Attribute attr = attributes[i];
			if(attr.isRefDisplay()) {
				list.add(attr);
			}
		}
		
		refDisplayAttributes = list.toArray(new Attribute[list.size()]);
	}
	
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public Attribute[] getAttributes() {
		return attributes;
	}

	public void setAttributes(Attribute[] columns) {
		this.attributes = columns;
	}

	public List<String> getOperations() {
		return operations;
	}

	public void setOperations(List<String> operations) {
		this.operations = operations;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getHandlerName() {
		return handler;
	}
	
	public Attribute[] getRefDisplayAttr() {
		return refDisplayAttributes;
	}
	
	public Attribute getIdentifier() {
		return identifier;
	}
}
