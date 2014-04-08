/**
 * 
 */
package com.boundary.sdk;

import java.io.Serializable;
import java.util.Map;

/**
 * @author davidg
 *
 */
public class Source implements Serializable {
	
	private String mRef;
	private String mType;
	private String mName;
	private Map<String,String> mProperties;

	/**
	 * 
	 */
	public Source() {
	}
	
	public Source(String ref, String type) {
		this(ref,type,null);
	}
	
	public Source(String ref, String type, Map<String,String> properties) {
		this.mRef = ref;
		this.mType = type;
		this.mProperties = properties;
	}
	
	public void setRef(String ref) {
		this.mRef = ref;
	}
	
	public String getRef() {
		return this.mRef;
	}
	
	public void setType(String type) {
		this.mType = type;
	}
	
	public String getType() {
		return this.mType;
	}
	
	public Map<String,String> getProperties() {
		return mProperties;
	}
	
	public void setProperties(Map<String,String> properties) {
		this.mProperties = properties;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("ref: " + this.mRef);
		s.append("type: " + this.mType);
		return s.toString();
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}