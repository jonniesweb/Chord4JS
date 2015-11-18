package com.chord4js;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Service {
	
	private List<String> layers = new ArrayList<String>();
	private List<String> qos = new ArrayList<String>();
	private String provider;
	
	public void addQos(String qos2) {
		qos.add(qos2);
	}
	
	public void setLayers(List<String> layers) {
		this.layers = layers;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getProvider() {
		return provider;
		
	}
	
	@Override
	public String toString() {
		return layers + "" + provider + "" + qos;
	}
}
