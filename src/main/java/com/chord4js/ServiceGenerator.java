package com.chord4js;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

/**
 * Read in a YAML file of example services and outputs a list of services with
 * generated providers.
 * 
 */
public class ServiceGenerator {
	
	private Random random;
	ArrayList<LeafNode> leafNodes = new ArrayList<LeafNode>();
	
	public static void main(String[] args) throws Exception {
		new ServiceGenerator(null);
	}
	
	public ServiceGenerator() throws Exception {
		this(new Random());
	}

	public ServiceGenerator(Random random) throws Exception {
		this.random = random;
		
		// read yaml file from classpath
		URL resource = getClass().getClassLoader().getResource("services.yaml");
		
		YamlReader reader = new YamlReader(new FileReader(new File(resource.toURI())));
		
		List<?> list = (List<?>) reader.read();
		
		// create root node
		BranchNode root = new BranchNode(null, null);
		
		// call recursive method
		processList(root, list);
		
		// create a bunch of random services
		List<Service> services = new ArrayList<Service>();
		for (int i = 0; i < 100; i++) {
			ServiceFactory serviceFactory = createRandomServiceFromLeafs(leafNodes);
			Service service = serviceFactory.createRandom(random);
			services.add(service);
			System.out.println(service);
		}
	}
	
	/**
	 * Chooses a random leaf node, then traverses up the tree, building the
	 * functional part of the service identifier.
	 * 
	 * @param leafNodes
	 * @return
	 */
	private ServiceFactory createRandomServiceFromLeafs(List<? extends Node> leafNodes) {
		LinkedList<String> layers = new LinkedList<String>();
		ArrayList<String> qos = new ArrayList<String>();
		
		// get a random leaf from the list
		Node currentNode = leafNodes.get(random.nextInt(leafNodes.size()));
		
		// ignore null nodes and the root node which doesn't have a name
		while (currentNode != null && currentNode.getName() != null) {
			
			// add the name of the service to the beginning of the list (since
			// we're going from child to root)
			layers.addFirst(currentNode.getName());
			// add QOS to Service, if any
			if (currentNode.getQos() != null) {
				qos.addAll(currentNode.getQos());
			}
			
			// update the current node to the node's parent
			currentNode = currentNode.getParent();
		}
		
		// add the functional layers to the Service object
		ServiceFactory serviceFactory = new ServiceFactory(layers, qos);
		
		return serviceFactory;
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
	 * @deprecated An older way to create a service
	 * @param root
	 * @return
	 */
	private List<String> createRandomService(BranchNode root) {
		// randomly walk down the tree from the root
		
		Node currentNode = root;
		List<String> service = new ArrayList<String>();
		
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
	
	private void processList(BranchNode root, List<?> list) throws Exception {
		try {
			
			// iterate over each element of the list
			for (Object obj : list) {
				
				if (obj instanceof Map) {
					// this object is a map and has children
					Map<?, ?> map = (Map<?, ?>) obj;
					
					// make an assertion that the map size should only ever be 1
					if (map.size() > 1) {
						throw new YamlException("The map size should never be greater than 1. "
								+ "Something is wrong with the yaml file");
					}
					
					// get the key, which is the name of a category. We only get
					// the first element of the map since it should be the only
					// one
					String key = (String) map.keySet().iterator().next();
					Object value = map.get(key);
					
					if ("qos".equals(key)) {
						// we're dealing with a QOS specification
						String qos = (String) value;
						System.out.println("qos " + qos);
						
						// parse and add the QOS specification to the parent
						// node
						root.addQos(qos);
						
						// continue to loop over other elements in the list
						continue;
					}
					
					// create a branch node, setting the name to the key and
					// adding it to the root node
					BranchNode branchNode = new BranchNode(root, key);
					root.add(branchNode);
					
					// recursively call this method passing it in the key's
					// value
					processList(branchNode, (List<?>) value);
					
				} else if (obj instanceof String) {
					String name = (String) obj;
					// process leaf node
					LeafNode node = new LeafNode(root, name);
					root.add(node);
					
					// add the leafNode to the leafNode list
					leafNodes.add(node);
				}
			}
			
		} catch (ClassCastException | YamlException e) {
			System.err
					.println("Invalid characters occurred. \"key: value\" pairs are invalid, use lists instead");
			throw e;
		}
	}
	
	/**
	 * The base class of the composite pattern. Store the name of this class.
	 */
	private static abstract class Node {
		protected final String name;
		private final BranchNode parent;
		
		/**
		 * Quality of Service attributes for this node and any of its children
		 */
		private List<String> qosList;
		
		public Node(BranchNode parent, String name) {
			this.parent = parent;
			this.name = name;
		}
		
		public void addQos(String qos) {
			
			// lazy initialize qosList
			if (qosList == null) {
				qosList = new ArrayList<String>();
			}
			
			qosList.add(qos);
		}
		
		public List<String> getQos() {
			return qosList;
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
		
		public Node getParent() {
			return parent;
		}
	}
	
	/**
	 * A node that contains children nodes
	 */
	private static class BranchNode extends Node {
		
		protected List<Node> children = new ArrayList<Node>();
		
		public BranchNode(BranchNode parent, String name) {
			super(parent, name);
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
		
		public LeafNode(BranchNode parent, String name) {
			super(parent, name);
		}
		
		public List<Node> getChildren() {
			return null;
		}
	}
	
	/**
	 * From the input file, return all of the ServiceIds that exist.
	 * 
	 * @return
	 */
	public Set<ServiceId> getPossibleServices() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Generate numberOfServices number of random Services.
	 * 
	 * @param numberOfServices
	 * @return
	 */
	public Set<Service> getServices(int numberOfServices) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
