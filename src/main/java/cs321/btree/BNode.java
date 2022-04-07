package cs321.btree;

import java.util.LinkedList;


public class BNode<E> {

	//child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
	private LinkedList<TreeObject<E>> keys; //objects in this node, also size() = n
	private LinkedList<BNode<E>> children;  //children in this node
	private BNode<E> parent;  //parent pointer
	private NodeType type;    //Either root, interior, or leaf node
	
	private final int DEGREE; //t
	
	/**
	 * Constructor
	 * 
	 * @param degree
	 * @param parent
	 * @param intialKey
	 */
	public BNode(int degree, BNode<E> parent, TreeObject<E> intialKey) {
		keys = new LinkedList<TreeObject<E>>();
		children = new LinkedList<BNode<E>>();
		
		keys.add(intialKey);
		children.add(null);
		children.add(null);
		
		DEGREE = degree;
		this.parent = parent;
		
		if(this.parent == null) {
			type = NodeType.ROOT;
		}
		else {
			type = NodeType.LEAF;
		}
	}
	
	/**
	 * Insert the given key into this BNode and insert the given child
	 * to the right of the inserted key.
	 * 
	 * @param key
	 * @param child
	 */
	/*
	 * Example Demonstration:
	 *
	 * keys:      a b d e f
	 * children: # # # # # #
	 * 
	 *          insert(c, *)
	 *          
	 * keys:      a b c d e f
	 * children: # # # * # # #
	 */
	public void insert(TreeObject<E> key, BNode<E> child) {
		
		//get to the index of the first k less than key
		//TODO: 'key.equals(keys.get(i))' needs to be something like 'key.compareTo(keys.get(i)) <= 0
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.equals(keys.get(i)); i--){}
		keys.add(i + 1, key);
		children.add(i + 2, null);
		
		//TODO: write to disk, probably in BTree.java
	}
}
