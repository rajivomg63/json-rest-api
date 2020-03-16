package com.rajiv.jsonrestapi.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class FieldData {

	// common fields
	 
    Map<String, Object> details = new LinkedHashMap<>();
 
    @JsonAnySetter
    void setDetail(String key, Object value) {
        details.put(key, value);
    }
    
    public Map<String, Object> getDetail(){
    	
    	return details;
    }

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return details.toString();
	}
    
    
}
