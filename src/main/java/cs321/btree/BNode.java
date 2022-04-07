package cs321.btree;

import java.util.LinkedList;

/**
 * Used to create BNode objects that hold Generic Type (passed down by
 * the BTree they belong to) objects.
 * 
 * @author  Mesa Greear
 * @version Spring 2022
 *
 * @param <E> Generic Type for this BNode to hold
 */
public class BNode<E> {

	//child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
	private LinkedList<TreeObject<E>> keys; //objects in this node, also size() = n
	private LinkedList<BNode<E>> children;  //children in this node
	
	private BNode<E> parent;  //parent pointer
	private NodeType type;    //Either root, interior, or leaf node
	
	private final int DEGREE; //t -  how many keys/objects (t - 1 to 2t - 1) and
							  //children (t to 2t) this BNode can have
	
	//=================================================================================================================
	//												CONSTRUCTORS
	//=================================================================================================================
	
	/**
	 * Constructor: Create BNode with one key initialKey, a parent pointer
	 * parent, two children leftChild and rightChild, and the degree (t) of
	 * this BNode. BNode type is determined automatically by parameters.
	 * 
	 * @param degree     (t) how many keys/objects (t - 1 to 2t - 1) and
	 * 					 children (t to 2t) this BNode can have.
	 * @param intialKey  the initial object in this BNode
	 * @param parent	 pointer to the parent of this BNode
	 * @param leftChild  pointer to the child left of initialKey
	 * @param rightChild pointer to the child right of initialKey
	 */
	/*
	 * Example Demonstration:
	 * 
	 * 
	 *    new BNode(?, a, ?, /, \)
	 * 
	 * result     -  a
	 *              / \
	 */
	public BNode(int degree, TreeObject<E> initialKey, BNode<E> parent, BNode<E> leftChild, BNode<E> rightChild) {
		//initialize instance variables
		keys = new LinkedList<TreeObject<E>>();
		children = new LinkedList<BNode<E>>();
		
		keys.add(initialKey);
		children.add(leftChild);
		children.add(rightChild);
		
		DEGREE = degree;
		this.parent = parent;
		
		//determine BNode type
		//if no parent --> ROOT; if no children --> LEAF; else --> INTERIOR
		if(this.parent == null) {
			type = NodeType.ROOT;
		}
		else if((children.get(0) == null) && (children.get(1) == null)) {
			type = NodeType.LEAF;
		}
		else {
			type = NodeType.INTERIOR;
		}
	}
	
	/**
	 * Constructor: Create BNode with one key initialKey, a parent pointer
	 * parent, and two children leftChild and rightChild. BNode type is
	 * determined automatically by parameters.
	 * 
	 * @param degree     (t) how many keys/objects (t - 1 to 2t - 1) and
	 * 					 children (t to 2t) this BNode can have.
	 * @param intialKey  the initial object in this BNode
	 * @param parent	 pointer to the parent of this BNode
	 * @param leftChild  pointer to the child left of initialKey
	 * @param rightChild pointer to the child right of initialKey
	 */
	public BNode(TreeObject<E> initialKey, BNode<E> parent, BNode<E> leftChild, BNode<E> rightChild) {
		this(parent.DEGREE, initialKey, parent, leftChild, rightChild);
	}
	
	/**
	 * Constructor: Create LEAF BNode with one key initialKey and a
	 * parent pointer parent.
	 * 
	 * @param initialKey
	 * @param parent
	 */
	public BNode(TreeObject<E> initialKey, BNode<E> parent) {
		this(parent.DEGREE, initialKey, parent, null, null);
	}
	
	/**
	 * Constructor: Create ROOT BNode with one key initialKey and the degree
	 * (t).
	 * 
	 * @param degree    (t) how many keys/objects (t - 1 to 2t - 1) and
	 * 					children (t to 2t) this BNode can have.
	 * @param intialKey the initial object in this BNode
	 */
	public BNode(int degree, TreeObject<E> initialKey) {
		this(degree, initialKey, null, null, null);
	}
	
	//=================================================================================================================
	//												MAIN METHODS
	//=================================================================================================================
	
	/**
	 * Insert the given key into this BNode and insert the given child
	 * to the right of the inserted key.
	 * 
	 * @param key   TreeObject containing Object to insert
	 * @param child Child related to key to insert 
	 */
	/*
	 * Example Demonstration:
	 *
	 *
	 * keys     -  a b d e f
	 * children - # # # # # #
	 * 
	 *            insert(c, *)
	 *          
	 * keys     -  a b c d e f
	 * children - # # # * # # #
	 */
	public void insert(TreeObject<E> key, BNode<E> child) {
		
		//get to the index of the first k less than key
		//TODO: 'key.equals(keys.get(i))' needs to be something like 'key.compareTo(keys.get(i)) <= 0'
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.equals(keys.get(i)); i--){}
		
		//add new key and child to lists
		keys.add(i + 1, key);
		children.add(i + 2, child);
		
		//TODO: write to disk, probably in BTree.java
	}
	
	/**
	 * Get the child of this BNode where the given key should be
	 * inserted or would be located. Does NOT insert the key, only
	 * returns the subtree that it belongs to.
	 * 
	 * @param key Object to use to locate the appropriate subtree
	 * 
	 * @return subtree (child of this BNode) that key belongs to
	 */
	public BNode<E> getSubtree(TreeObject<E> key){
		
		//get to the index of the first k less than key
		//TODO: 'key.equals(keys.get(i))' needs to be something like 'key.compareTo(keys.get(i)) <= 0'
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.equals(keys.get(i)); i--){}
		
		return children.get(i + 1);
	}
	
	//=================================================================================================================
	//												GET/SET METHODS
	//=================================================================================================================
	
	/**
	 * Get the number of objects (n) in this BNode represented by
	 * keys.size().
	 * 
	 * @return Number of objects in this BNode
	 */
	public int getN() {
		return keys.size();
	}
	
	/**
	 * Get the type this BNode is, i.e. ROOT, INTERIOR, or LEAF.
	 * 
	 * @return this BNode's type
	 */
	public NodeType getType() {
		return type;
	}
	
	/**
	 * Set the type for this BNode, i.e. ROOT, INTERIOR, or LEAF.
	 * 
	 * @param type New type for this BNode
	 */
	public void setType(NodeType type) {
		this.type = type;
	}
	
	/**
	 * Indicates whether or not this BNode is full (n = 2t - 1)
	 * 
	 * @return true is full, false otherwise
	 */
	public boolean isFull() {
		return ((2 * DEGREE) - 1) == keys.size();
	}
}
