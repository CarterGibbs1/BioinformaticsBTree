package cs321.btree;

import java.util.LinkedList;

/**
 * Used to create BNode objects that hold Generic Type (passed down by
 * the BTree they belong to) objects. Notable methods are insert(),
 * getSubtree(), and split() which are important in creating a functional
 * BTree.
 * 
 * @author  Mesa Greear
 * @version Spring 2022
 *
 * @param <E> Generic Type for this BNode to hold
 */
public class BNode<E> {

	//child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
	private LinkedList<TreeObject<E>> keys; //objects/keys in this node, also size() = n
	private LinkedList<BNode<E>> children;  //children in this node
	
	private BNode<E> parent;  //parent pointer
	private NodeType type;    //Either root, interior, or leaf node
	
	private final int DEGREE; //t -  how many keys/objects (t - 1 to 2t - 1) and
	                          //children (t to 2t) this BNode can have
	
	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
	
	/**
	 * Constructor: Create BNode with one key initialKey, a parent pointer
	 * parent, two children leftChild and rightChild, and the degree (t) of
	 * this BNode. BNode type is determined automatically by parameters.
	 * 
	 * @param degree     (t) how many keys/objects (t - 1 to 2t - 1) and
	 * 					 children (t to 2t) this BNode can have.
	 * @param intialKey  the initial object in this BNode
	 * @param parent     pointer to the parent of this BNode
	 * @param leftChild  pointer to the child left of initialKey
	 * @param rightChild pointer to the child right of initialKey
	 */
	/*
	 * Example Demonstration:
	 * 
	 * 
	 *      new BNode(?, a, ?, /, \)
	 * 
	 * result     -    a
	 *                / \
	 */
	public BNode(int degree, TreeObject<E> initialKey, BNode<E> parent, BNode<E> leftChild, BNode<E> rightChild) {
		//initialize instance variables
		keys = new LinkedList<TreeObject<E>>();
		children = new LinkedList<BNode<E>>();
		
		keys.add(initialKey);
		children.add(leftChild);
		children.add(rightChild);
		
		//if degree is invalid, take the degree from whatever pointer is not null
		if(degree < 2) {
			if(parent != null) {
				DEGREE = parent.DEGREE;
			}
			else {
				DEGREE = rightChild.DEGREE;
			}
		}
		else {
			DEGREE = degree;
		}
		
		this.parent = parent;
		
		//determine BNode type
		updateType(); 
	}
	
	/**
	 * Constructor: Create BNode with one key initialKey, a parent pointer
	 * parent, and two children leftChild and rightChild. BNode type is
	 * determined automatically by parameters. Degree (t) is determined by
	 * either the parent pointer or one of the children.
	 * 
	 * @param intialKey  the initial object in this BNode
	 * @param parent     pointer to the parent of this BNode
	 * @param leftChild  pointer to the child left of initialKey
	 * @param rightChild pointer to the child right of initialKey
	 */
	public BNode(TreeObject<E> initialKey, BNode<E> parent, BNode<E> leftChild, BNode<E> rightChild) {
		this(-1, initialKey, parent, leftChild, rightChild);
	}
	
	/**
	 * Constructor: Create LEAF BNode with one key initialKey and a
	 * parent pointer parent. Degree (t) is determined by the parent
	 * pointer.
	 * 
	 * @param intialKey  the initial object in this BNode
	 * @param parent     pointer to the parent of this BNode
	 */
	public BNode(TreeObject<E> initialKey, BNode<E> parent) {
		this(-1, initialKey, parent, null, null);
	}
	
	/**
	 * Constructor: Create ROOT BNode with one key initialKey and the degree
	 * (t).
	 * 
	 * @param degree    (t) how many keys/objects (t - 1 to 2t - 1) and
	 *                  children (t to 2t) this BNode can have.
	 * @param intialKey the initial object in this BNode
	 */
	public BNode(int degree, TreeObject<E> initialKey) {
		this(degree, initialKey, null, null, null);
	}
	
	//=================================================================================================================
	//                                         BTREE FUNCTIONALITY METHODS
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
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.leftGreaterThanRight(keys.get(i)) <= 0; i--){}
		
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
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.leftGreaterThanRight(keys.get(i)) <= 0; i--){}
		
		return children.get(i + 1);
	}
	
	/**
	 * Splits the current BNode into two new BNodes, removing and
	 * inserting the middle object and a pointer to the new right
	 * BNode into the parent. The left BNode (this BNode) will
	 * contain everything to the left of the removed object and
	 * the right BNode (new BNode) will be contain everything to
	 * the right. Can only be run when the list is full.
	 * 
	 * @return the parent of the two BNodes
	 * 
	 * @throws IllegalStateException When attempting to split a not
	 *         full list.
	 */
	public BNode<E> split() throws IllegalStateException{
		
		//if this BNode is not full, throw exception. Mostly here for debugging purposes
		if(!isFull()) {
			throw new IllegalStateException("Attempted to split BNode when not full");
		}
		
		
		//==== for better understanding when coming back to look at this method, I'm going to ====
		//==== create an example split and show how it changes through comments:              ====
		//Keys     -  a b c d e f g
		//Pointers - 0 1 2 3 4 5 6 7
		
		int originalN = keys.size();
		
		
		//remove key and two pointers right of middle and insert into new BNode 'splitRight':
		//Keys     -  a b c d f g    |  e
		//Pointers - 0 1 2 3   6 7   | 4 5
		BNode<E> splitRight = new BNode<E>(keys.remove(keys.size()/2 + 1), parent, children.remove(children.size()/2), children.remove(children.size()/2 + 1));
		
		
		//starting just to the right of the middle of this BNode, continuously remove the pointers and keys
		//at that position and insert them into splitRight:
		//Keys     -  a b c d  |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
		while((keys.size() - 1) != originalN/2) {
			splitRight.insert(keys.remove(originalN/2 + 1), children.remove(originalN/2 + 1));
		}
		
		
		//lastly, remove last node (original middle) from this and insert into parent with
		//a pointer to splitRight
		//Parent   -    .. x x d x x ..
		//                    / \
		//Keys     -  a b c d  |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
		
		//if this is the ROOT, create new parent/root to insert into and update types
		if(type == NodeType.ROOT) {
			BNode<E> newRoot = new BNode<E>(keys.removeLast(), null, this, splitRight);
			this.parent = splitRight.parent = newRoot;
			this.updateType();
			splitRight.updateType();
			
			return newRoot;
		}
		
		parent.insert(keys.removeLast(), splitRight);
		return parent;
	}
	
	//=================================================================================================================
	//                                           GET/SET/UTILITY METHODS
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
	
	/**
	 * Update what type of Node this is based on parent and child
	 * pointers. If no parent --> ROOT; if no children --> LEAF;
	 * else --> INTERIOR
	 */
	public void updateType() {
		if(parent == null) {
			type = NodeType.ROOT;
		}
		else if(children.get(0) == null) {
			type = NodeType.LEAF;
		}
		else {
			type = NodeType.INTERIOR;
		}
	}
}
