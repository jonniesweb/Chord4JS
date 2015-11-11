package com.chord4js;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * Read in a YAML file of example services and outputs a list of services with
 * generated providers.
 */
public class ServiceGenerator {
	
	private Random random = new Random();
	
	public static void main(String[] args) throws Exception {
		new ServiceGenerator();
	}
	
	public ServiceGenerator() throws Exception {
		
		// read yaml file from classpath
		URL resource = getClass().getClassLoader().getResource("services.yaml");
		
		YamlReader reader = new YamlReader(new FileReader(new File(resource.toURI())));
		
		List<?> list = (List<?>) reader.read();
		
		// create root node
		BranchNode root = new BranchNode("");
		
		try {
			// call recursive method
			processList(root, list);
		} catch (ClassCastException e) {
			System.err
					.println("Invalid characters occurred. \"key: value\" pairs are invalid, use lists instead");
			throw e;
		}
		
		// create a bunch of random services
		List<List<String>> services = new ArrayList<List<String>>();
		for (int i = 0; i < 100; i++) {
			ArrayList<String> service = createRandomService(root);
			services.add(service);
			System.out.println(service);
		}
		
	}
	
	/**
	 * Creates a list consisting of multiple layers that make up the functional
	 * bits of the service identifier.
	 * 
	 * This implementation is a statistically bad way of generating services
	 * since 1 down from the root node all have the same probability to be
	 * chosen, and so forth for every other layer. This means that services with
	 * more branches at deeper levels get chosen less often than services with
	 * less branches at deeper levels.
	 * 
	 * TODO: Instead of going top-down, go bottom-up for better randomness
	 * 
	 * @param root
	 * @return
	 */
	private ArrayList<String> createRandomService(BranchNode root) {
		// randomly walk down the tree from the root
		
		Node currentNode = root;
		ArrayList<String> service = new ArrayList<String>();
		
		while (currentNode.getChildren() != null) {
			
			// get a random child from the node
			List<Node> children = currentNode.getChildren();
			Node node = children.get(random.nextInt(children.size()));
			
			// add the newly found node's name to the services list
			service.add(node.getName());
			
			// set the current node to this node in preparation for iterating
			// over it again
			currentNode = node;
		}
		return service;
	}
	
	private void processList(BranchNode root, List<?> list) {
		
		// iterate over each element of the list
		for (Object obj : list) {
			
			if (obj instanceof Map) {
				// this object is a map and has children
				Map<?, ?> map = (Map<?, ?>) obj;
				
				// get the key, which is the name of a category. We only get the
				// first element of the map since it should be the only one
				String key = (String) map.keySet().iterator().next();
				
				// make an assertion that the map size should only ever be 1
				assert map.size() == 1;
				
				// create a branch node, setting the name to the key and adding
				// it to the root node
				BranchNode branchNode = new BranchNode(key);
				root.add(branchNode);
				
				// recursively call this method passing it in the key's value
				processList(branchNode, (List<?>) map.get(key));
				
			} else if (obj instanceof String) {
				// process leaf node
				root.add(new LeafNode((String) obj));
			}
		}
	}
	
	/**
	 * The base class of the composite pattern. Store the name of this class.
	 */
	private static abstract class Node {
		protected final String name;
		
		public Node(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		/**
		 * Get a list of the children nodes of this node. Return
		 * <code>null</code> if none.
		 * 
		 * @return a list of the node's children, null if none
		 */
		public abstract List<Node> getChildren();
	}
	
	/**
	 * A node that contains children nodes
	 */
	private static class BranchNode extends Node {
		
		protected List<Node> children = new ArrayList<Node>();
		
		public BranchNode(String name) {
			super(name);
		}
		
		private List<String> getNamesOfChildren() {
			return children.stream().map(n -> n.getName()).collect(Collectors.toList());
		}
		
		public void add(Node node) {
			children.add(node);
		}
		
		@Override
		public List<Node> getChildren() {
			return children;
		}
		
	}
	
	/**
	 * A node that just contains a String
	 */
	private static class LeafNode extends Node {
		
		public LeafNode(String name) {
			super(name);
		}
		
		public List<Node> getChildren() {
			return null;
		}
	}
	
}
