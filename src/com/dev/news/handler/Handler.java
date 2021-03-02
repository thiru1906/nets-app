//$Id$
package com.dev.news.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Attribute;
import com.dev.news.data.Unit;
import com.dev.news.data.util.UnitUtil;
import com.dev.news.util.APITransactionUtil;

public abstract class Handler {
	
	protected Unit unit;
	
	protected static final String DOT = ".";
	protected static final String SPACE = " ";
	protected static final String OPEN = "(";
	protected static final String CLOSE = ")";
	
	protected Handler(Unit en) {
		unit = en;
	}
	
	
	public abstract JSONObject add(JSONObject data,  Attribute[] attrUsed);
	
	public abstract JSONObject getList(JSONObject criteria, Attribute[] attrNeeded);
	
	public abstract JSONObject get(JSONObject criteria, Attribute[] attrUsed);
	
	public abstract JSONObject delete(long id);
	
	public abstract JSONObject edit(JSONObject data, JSONObject otherProps, Attribute[] used);
	
	protected abstract JSONObject handleAPICall(String method, JSONObject data, JSONObject otherProps);
	
	protected abstract String getSelectWithFieldsAndJoins(Attribute[] attrUsed);
	
	protected abstract String getInsertQueryWithFields(Attribute[] attrUsed);
	
	protected abstract String getUpdateQueryWithFields(Attribute[] attrUsed);
	
	protected abstract boolean isUserAuthorized(String path, String method);
	
	protected abstract void modifyInputData(String method, JSONObject input);
	
	protected abstract void modifyOutputData(JSONObject data);
	
	public final String getFinalRange(JSONObject query) {
		String range;
		
		JSONArray arr = query.optJSONArray("range");
		
		range = " limit " + (arr.getInt(1) - arr.getInt(0)) + " offset " + arr.getInt(0);
		
		return range;
	}
	
	public final String getDefaultRange() {
		String range;
		range = " limit " + 20 + " offset " + 0;
		return range;
	}
	
	public final String getSortOrder(JSONObject query) {
		String sort = " order by";
		String fieldName = query.getString("field_name");
		String order = query.getString("order");
		fieldName = UnitUtil.getAttributeByName(unit, fieldName).getColumnNameOnly();
		sort += SPACE + fieldName + SPACE + order;
		return sort;
	}
	
	public final String getDefaultSortOrder() {
		if(unit.getUnit().equals("news")) {
			return " order by news.id desc";
		} else {
			return " order by "+ unit.getIdentifier().getColumnName()+ " asc";
		}
	}
	
	public final String getFinalCriteria(JSONObject query) {
		
		String criteria =  " where ";
		
		JSONArray jArr = query.getJSONArray("criteria");
		
		for(int i = 0 ; i < jArr.length(); i++) {
			JSONObject cri = jArr.getJSONObject(i);
			criteria += getCriStringFromEachLevel(cri);
		}

		return criteria;
	}
	
	protected final JSONObject convertToJSON(ResultSet set, Attribute[] attrUsedOrNeeded, boolean isArray) throws Exception{
		JSONObject obj = new JSONObject();
		List<JSONObject> list = new ArrayList<>();
		while(set.next()) {	
			obj = eachSetRowToJSON(set, attrUsedOrNeeded);
			list.add(obj);
		}
		
		obj = new JSONObject();
		if(list.size() > 1 || isArray) {
			JSONArray arr = new JSONArray();
			for(JSONObject json : list) {
				arr.put(json);
			}
			obj.put("data", arr);
		} else if(list.size() == 1){
			obj.put("data", list.get(0));
		}
		
		return obj;
	}
	
	private JSONObject eachSetRowToJSON(ResultSet set, Attribute[] attrUsedOrNeeded) throws SQLException {
		JSONObject row = new JSONObject();
		
		for(int i = 0 ; i < attrUsedOrNeeded.length; i++) {
			
			Attribute attr = attrUsedOrNeeded[i];
			
			if(attr.getRefUnitName() != null) {
				JSONObject refObj= new JSONObject();
				Unit refUnit = UnitUtil.getUnit(attr.getRefUnitName());
				Attribute[] refAttr = refUnit.getRefDisplayAttr();
				for(int j = 0 ; j < refAttr.length; j++) {
					refObj.put(refAttr[j].getDisplayName(), set.getObject(refUnit.getTableName()+"_table_"+refAttr[j].getColumnNameOnly()));
				}
				row.put(attr.getDisplayName(), refObj);
			} else {
				row.put(attr.getDisplayName(), set.getObject(attr.getColumnNameOnly()));
			}
		}

		return row;
	}
	
	
	protected final JSONObject addSuccessMessage(JSONObject obj) {
		JSONObject json = new JSONObject();
		json.put("result", "success");
		json.put("code",200);
		return obj.put("response", json);
	}
	
	
	protected final JSONObject getDBErrorMessage() {
		return APITransactionUtil.getServerErrorResponse();
	}
	
	
	private String getCriStringFromEachLevel(JSONObject cri) {
		String criteria;
		//converting first level..
		
		String fieldName = cri.getString("field_name");
		Object value = cri.get("value");
		String condition = cri.getString("condition");
		
		condition = getCondition(condition);
		//TODO : change to buffer.
		
		fieldName = UnitUtil.getAttributeByName(unit, fieldName).getColumnNameOnly();
		
		if(value instanceof String) {
			criteria = OPEN+unit.getTableName() + DOT + fieldName + SPACE + condition + SPACE + 
					"'"+ value +"'";
		} else {
		
		criteria = OPEN+unit.getTableName() + DOT + fieldName + SPACE + condition + SPACE + 
					value;
		}
		
		if(cri.has("children")) {
			JSONArray children = cri.getJSONArray("children");
			for(int i = 0; i < children.length(); i++) {
				criteria+= SPACE + getCriStringFromEachLevel(children.getJSONObject(i));
			}
		}
		
		if(cri.has("logical_operator")) {
			criteria = cri.getString("logical_operator") + SPACE + criteria;
		}
		
		return criteria+CLOSE;
	}
	
	
	private static String getCondition(String condition) {
		
		if("eq".equals(condition)) {
			return "=";
		} else if("lte".equals(condition)) {
			return "<=";
		} else if("gte".equals(condition)) {
			return ">=";
		} else if("gt".equals(condition)) {
			return ">";
		} else if("lt".equals(condition)) {
			return "<";
		}
		return null;
	}

	
	private static boolean hasValidAttributes(JSONObject data, Unit unit) {
		Attribute[] attr = unit.getAttributes();
		
		
		if(data == null || data.optJSONObject("data") == JSONObject.NULL) {
			return true;
		}
		
		data = data.optJSONObject("data");
		for(int i = 0; i < attr.length; i++) {
			Attribute at = attr[i];
			if(!data.has(at.getDisplayName()) && !(at.isIdentifier() || at.isSystemAttribute())) {
				return false;
			}
		}
		return true;
	}
	
	public static final JSONObject operationEntry(HttpServletRequest req, HttpServletResponse res) throws Exception{
		String inputDataStr = null;
		JSONObject otherProps = null;
		
		if(req.getParameter("rule") != null) {
			inputDataStr = req.getParameter("rule");
			otherProps = new JSONObject(inputDataStr);
		} else {
			otherProps = new JSONObject();
		}
		String method = req.getMethod();
		
		String url = req.getRequestURL().toString();
		String[] splitted = url.split("/News/")[1].split("/");
		
		JSONObject inputJSON = null;
		
		String tempInput = req.getParameter("input_data");
		
		if(tempInput != null) {
			inputJSON = new JSONObject(tempInput);
		}
		/*
		 * 1st part contains "api" param
		 * 2nd part contains path name which directs to its current unit instance.
		 */
		
		String unitName = splitted[1];
		
		Unit unit = UnitUtil.getUnitByPath("/"+unitName);
		
		if(splitted.length == 3 ) {
			otherProps.put("criteria", new JSONArray().put(new JSONObject()
						.put("field_name", unit.getIdentifier().getDisplayName())
						.put("condition", "eq")
						.put("value", splitted[2])));
			
			otherProps.put("get_id",splitted[2]);
		}
		
		
		
		Handler handler = UnitUtil.getHandler(unit);

		if(handler.isUserAuthorized(unit.getApiPath(), method)) {
			if(!method.equalsIgnoreCase(ApiOperation.ADD) || hasValidAttributes(inputJSON, unit)) {
				return handler.handleAPICall(method, inputJSON, otherProps);
			} else {
				return APITransactionUtil.getInvalidInputResponse();
			}
		} else {
			return APITransactionUtil.getUnauthorizedResponse();
		}
	}
}
