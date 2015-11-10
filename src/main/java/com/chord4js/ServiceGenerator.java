package com.chord4js;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * Read in a YAML file of example services and outputs a list of services with
 * generated providers.
 */
public class ServiceGenerator {
	
	public static void main(String[] args) throws Exception {
		new ServiceGenerator();
	}
	
	public ServiceGenerator() throws Exception {
		
		// read yaml file from classpath
		URL resource = getClass().getClassLoader().getResource("services.yaml");
		
		YamlReader reader = new YamlReader(new FileReader(new File(resource.toURI())));
		
		Object read = reader.read();
		List<?> list = getListOfMaps(read);
		
		// create root node
		BranchNode root = new BranchNode("");
		
		// call recursive method
		processList(root, list);
		
	}
	
	private void processList(BranchNode root, List<?> list) {
		System.out.println(list.size() + " " + list);
		
		for (Object obj : list) {
			if (obj instanceof Map) {
				// process map, recurse deeper, create branch node
				String key = (String) ((Map) obj).keySet().iterator().next();
				BranchNode branchNode = new BranchNode(key);
				root.add(branchNode);
				processList(branchNode, (List<?>) ((Map) obj).get(key));
				
			} else if (obj instanceof String) {
				// process leaf node
				root.add(new LeafNode((String) obj));
			}
		}
	}
	
	public List<Map<String, List>> getListOfMaps(Object object) {
		return (List<Map<String, List>>) object;
	}
	
	private static abstract class Node {
		protected String name;
		
		public Node(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public abstract boolean hasChildren();
	}
	
	private static class BranchNode extends Node {
		
		protected List<Node> children = new ArrayList();
		
		public BranchNode(String name) {
			super(name);
		}
		
		private List<String> getNamesOfChildren() {
			return children.stream().map(n -> n.getName()).collect(Collectors.toList());
		}
		
		@Override
		public boolean hasChildren() {
			return true;
		}
		
		public void add(Node node) {
			children.add(node);
		}
		
	}
	
	private static class LeafNode extends Node {
		
		public LeafNode(String name) {
			super(name);
		}
		
		@Override
		public boolean hasChildren() {
			return false;
		}
	}
	
}
