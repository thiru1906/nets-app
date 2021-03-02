//$Id$
package com.dev.news.handler;

import org.json.JSONObject;

import com.dev.news.data.Unit;

public class SimpleUnitHandler extends GenericHandler{

	public SimpleUnitHandler(Unit en) {
		super(en);
	}

	@Override
	protected void modifyInputData(String method, JSONObject input) {
		
	}

}
