package com.chord4js;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServiceIdentifier {
	
	List<String> layers = new ArrayList<String>();
	List<String> qos = new ArrayList<String>();
	
	public void addQos(List<String> qosList) {
		qos.addAll(qosList);
	}
	
	public void setLayers(LinkedList<String> layers) {
		this.layers = layers;
	}
	
	@Override
	public String toString() {
		return "layers: " + layers + " qos: " + qos;
	}
}
