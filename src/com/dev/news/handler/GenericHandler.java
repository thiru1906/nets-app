//$Id$
package com.dev.news.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dev.news.constants.ApiOperation;
import com.dev.news.data.Attribute;
import com.dev.news.data.Unit;
import com.dev.news.data.util.UnitUtil;
import com.dev.news.db.DBUtil;
import com.dev.news.servlet.APIServlet;
import com.dev.news.util.APIDataUtil;
import com.dev.news.util.APITransactionUtil;

public abstract class GenericHandler extends Handler{
	static final Logger LOGGER = Logger.getLogger(GenericHandler.class.toString());
	protected static final String COMMA = ",";
	
	public GenericHandler(Unit en) {
		super(en);
	}

	@Override
	protected JSONObject handleAPICall(String method, JSONObject data, JSONObject otherProps) {	
		
		Attribute[] attrUsed;
		Long getId = otherProps.optLong("get_id");
		otherProps.remove("get_id");
		
		modifyInputData(method, data);
		
		try{
			if(method.equalsIgnoreCase(ApiOperation.ADD)) {
				attrUsed = getAttributesUsedInThisAPI(data.optJSONObject("data"));
				JSONObject obj = add(data, attrUsed);
				return obj;
			} else if(method.equalsIgnoreCase(ApiOperation.EDIT)) {
				attrUsed = getAttributesUsedInThisAPI(data.optJSONObject("data"));
				return edit(data, otherProps, attrUsed);
			} else if(method.equalsIgnoreCase(ApiOperation.GET)) {
				if(getId != 0L ) {
					attrUsed = getAttributesNeededForGet(otherProps);
					JSONObject obj = get(otherProps, attrUsed);
					modifyOutputData(obj);
					return obj;
				} else {
					attrUsed = getAttributesForGetList();
					return getList(otherProps, attrUsed);
				}
			} else if(method.equalsIgnoreCase(ApiOperation.DELETE)) {
				return delete(getId);
			}
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return getDBErrorMessage();
		} finally {
			APIServlet.closeConnection();
		}
		return null;
	}
	
	@Override
	public JSONObject add(JSONObject data, Attribute[] attrUsed) {
		
		String query = getInsertQueryWithFields(attrUsed);
		Object[] value = getValueObjectFromInputJSON(data.optJSONObject("data"));
		ResultSet set = null;
		try (PreparedStatement statement = APIServlet.getStatement(query)){
			set =  DBUtil.addOrUpdate(statement , value);
			if(set.next()) {
				long id = Long.parseLong(set.getObject(1).toString());
				JSONObject obj = APIDataUtil.getCriteria(unit.getIdentifier().getDisplayName(), id, "eq");
				obj.put("get_id", id);
				return handleAPICall("get", null, obj);
			} else {
				return APITransactionUtil.getServerErrorResponse();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return getDBErrorMessage();
		} finally {
			if(set != null) {
				try {
					set.close();
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}
	
	
	@Override
	public JSONObject getList(JSONObject criteria, Attribute[] attrNeeded) {
		String query = getSelectWithFieldsAndJoins(attrNeeded);
		
		
		if(criteria != null) {			
			
			if(criteria.has("criteria")) {
				query += getFinalCriteria(criteria);
			}
			if(criteria.has("sort_order")) {
				query += getSortOrder(criteria.getJSONObject("sort_order"));
			} else {
				query += getDefaultSortOrder();
			}
			
			if(criteria.has("range")) {
				query += getFinalRange(criteria);
			} else {
				query += getDefaultRange();
			}
			
			
		} else {
			query += getDefaultSortOrder();
			query += getDefaultRange();
		}
		ResultSet set = null;
		try(PreparedStatement statement =  APIServlet.getStatement(query)){
			set = DBUtil.get(statement);
			JSONObject obj = convertToJSON(set, attrNeeded, true);
			return addSuccessMessage(obj);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return getDBErrorMessage();
		} finally {
			if(set != null) {
				try {
					set.close();
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}
	
	
	@Override
	public JSONObject edit(JSONObject data, JSONObject otherProps , Attribute[] attrUsed) {
		
		String query = getUpdateQueryWithFields(attrUsed);
		Object[] value = getValueObjectFromInputJSON(data.getJSONObject("data"));
		if(otherProps != null) {
			if(otherProps.has("criteria")) {
				query += getFinalCriteria(otherProps);
			}
			if(otherProps.has("sort_order")) {
				query += getSortOrder(otherProps);
			}
			
			if(otherProps.has("range")) {
				query += getFinalRange(otherProps);
			}
		}
		
		
		query += getReturningId();
		ResultSet set = null;
		try(PreparedStatement statement = APIServlet.getStatement(query)) {
			set = DBUtil.addOrUpdate(statement, value);
			set.next();
			long id = set.getLong(1); 
			JSONObject obj = APIDataUtil.getCriteria(unit.getIdentifier().getDisplayName(), id, "eq");
			obj.put("get_id", id);			
			return handleAPICall("get", null, obj);
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return getDBErrorMessage();
		} finally {
			if(set != null) {
				try {
					set.close();
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	public JSONObject delete(long id) {
		String query = getDeleteQuery(id);
		try(PreparedStatement statement = APIServlet.getStatement(query)) {
			DBUtil.delete(statement);
			return addSuccessMessage(new JSONObject());
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return getDBErrorMessage();
		} 
	}
	
	@Override
	public JSONObject get(JSONObject criteria, Attribute[] attrUsed) {
		if(attrUsed == null) {
			attrUsed = unit.getAttributes();
		}
		String query = getSelectWithFieldsAndJoins(attrUsed);
		
		if(criteria != null) {			
			if(criteria.has("criteria")) {
				query += getFinalCriteria(criteria);
			}
		}
		ResultSet set = null;
		try(PreparedStatement statement = APIServlet.getStatement(query)) {
			set = DBUtil.get(statement);
			JSONObject obj = convertToJSON(set, attrUsed, false);
			return addSuccessMessage(obj);
		}catch(Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return getDBErrorMessage();
		} finally {
			//closing result set..
			if(set != null) {
				try {
					set.close();
				} catch (SQLException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	protected void modifyOutputData(JSONObject data) {}

	protected final String getReturningId() {
		return " returning id";
	}
	
	protected final String getDeleteQuery(long id) {
		StringBuffer query = new StringBuffer();
		query.append("delete from ");
		query.append(unit.getTableName());
		query.append(SPACE);
		query.append("where");
		query.append(SPACE);
		query.append("id=");
		query.append(id);
		
		return query.toString();
		
	}
	
	protected final Attribute[] getAttributesNeededForGet(JSONObject json) {
		if(json == null || json.optJSONArray("fields_required") == null || json.optJSONArray("fields_required") == JSONObject.NULL) {
			return null;
		}
		JSONArray arr = json.getJSONArray("fields_required");
		Attribute[] attr = unit.getAttributes();
		List<Attribute> list= new ArrayList<>();
		for(int i = 0 ; i < attr.length; i++) {
			
			Attribute at = attr[i];
			String name = at.getName();
			
			
			arr.forEach(obj -> {
				String in = (String) obj;
				if(name.equals(in)) {
					list.add(at);
				}
			});
		}
		
		return list.toArray(new Attribute[list.size()]);
	}
	
	protected final Attribute[] getAttributesForGetList() {
		return unit.getRefDisplayAttr();
	}
	
	
	
	protected final Object[] getValueObjectFromInputJSON(JSONObject inputJSON) {
		
		Object[] value = new Object[inputJSON.length()];
		int i = 0;
		
		for(String key : inputJSON.keySet()) {
			value[i++] = inputJSON.get(key);
		}
		
		return value;
	}
	
	protected final Attribute[] getAttributesUsedInThisAPI(JSONObject data){
		Attribute[] attr = new Attribute[data.keySet().size()];
		int i = 0; 
		for(String key : data.keySet()) {
			Attribute at = UnitUtil.getAttributeByName(unit, key);
			//TODO : Need to handle group holders here.
			attr[i++] = (at);
		}
		
		return attr;
	}
	
	@Override
	protected String getSelectWithFieldsAndJoins(Attribute[] attrUsed) {
		StringBuffer finalQuery = new StringBuffer();
		finalQuery.append("select");
		finalQuery.append(SPACE);
		
		StringBuffer joinQuery = new StringBuffer();
		for(int i = 0 ; i < attrUsed.length; i++) {
			Attribute attr = attrUsed[i];
			
			
			
			//ref table columns to be included..
			if(attr.getRefUnitName() != null) {
				joinQuery.append(getJoin(attr));
				Attribute[] secAttr = UnitUtil.getUnit(attr.getRefUnitName()).getRefDisplayAttr();
				for(int j = 0 ; j < secAttr.length; j++) {
					finalQuery.append(secAttr[j].getColumnName()+" as "+ attr.getRefUnitName()+"_table_"+secAttr[j].getColumnNameOnly());
					if(i+1 != attrUsed.length || j+1 != secAttr.length) {
						finalQuery.append(COMMA);
					}
				}
			} else {
				finalQuery.append(attr.getColumnName());
				if(i+1 != attrUsed.length) {
					finalQuery.append(COMMA);
				}
			}
		}
		
		finalQuery.append(" from ");
		//special handling for ""User"" table name.
		if(unit.getTableName().equals("User")) {
			finalQuery.append("\"");
		}
		finalQuery.append(unit.getTableName());
		if(unit.getTableName().equals("User")) {
			finalQuery.append("\"");
		}
		finalQuery.append(SPACE);
		
		finalQuery.append(joinQuery);
		
		return finalQuery.toString();
	}
	
	protected final String getJoin(Attribute attr) {
		String join;
		Unit secondaryUnit = UnitUtil.getUnit(attr.getRefUnitName());
		join = " join " + secondaryUnit.getTableName() + " on " + attr.getColumnName() + " = " + secondaryUnit.getTableName()+".id";
//		join = " join " + unit.getTableName() + " on " + secondaryUnit.getTableName() + ".id = " + attr.getColumnName();
		return join;
	}
	
	
	@Override
	protected String getUpdateQueryWithFields(Attribute[] attrUsed) {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("update ");
		buffer.append(unit.getTableName());
		buffer.append(SPACE);
		buffer.append("set ");
		
		for(int i = 0 ; i < attrUsed.length; i++) {
			Attribute attr = attrUsed[i];
			buffer.append(attr.getColumnNameOnly());
			buffer.append("= ?");
			
			if(i+1 != attrUsed.length) {
				buffer.append(COMMA);
			}
		}
		
		return buffer.toString();
	}
	
	
	@Override
	protected String getInsertQueryWithFields(Attribute[] attrUsed) {
		StringBuffer buffer = new StringBuffer();
		StringBuffer quesBuffer = new StringBuffer();
		
		buffer.append("insert into ");
		buffer.append(unit.getTableName());
		buffer.append(SPACE);
		buffer.append(OPEN);
		
		quesBuffer.append(OPEN);
		
		for(int i = 0; i < attrUsed.length; i++) {
			buffer.append(attrUsed[i].getColumnName().split("[.]")[1]);
			quesBuffer.append("?");
			if(i+1 != attrUsed.length) {
				buffer.append(COMMA);
				quesBuffer.append(COMMA);
			}
		}
		
		buffer.append(CLOSE);
		quesBuffer.append(CLOSE);
		buffer.append(" values");
		buffer.append(quesBuffer);
		buffer.append(" returning id");
		return buffer.toString();
	}
	
	@Override
	protected boolean isUserAuthorized(String path , String method) {
		return true;
	}


}
