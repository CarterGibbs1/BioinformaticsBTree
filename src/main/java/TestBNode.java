

import java.util.LinkedList;

/**
 * Similar to BNode.java, but references to other BNodes are now
 * pointers stored in memory instead of addresses to a RAF. Useful for
 * testing that BTree's are working.
 * 
 * @author  Mesa Greear
 * @version Spring 2022
 *
 * @param <E> Generic Type for this BNode to hold
 */
public class TestBNode<E> {

	//child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
	private LinkedList<TreeObject> keys; //objects/keys in this node, also size() = n
	private LinkedList<TestBNode<E>> children;  //children in this node
	
	private TestBNode<E> parent;  //parent pointer
	
	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
	
	/**
	 * Constructor: Create BNode with one key initialKey, a parent pointer
	 * parent, two children leftChild and rightChild, and the degree (t) of
	 * this BNode. BNode type is determined automatically by parameters.
	 * 
	 * @param initialKey  the initial object in this BNode
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
	public TestBNode(TreeObject initialKey, TestBNode<E> parent, TestBNode<E> leftChild, TestBNode<E> rightChild) {
		//initialize instance variables
		keys = new LinkedList<TreeObject>();
		children = new LinkedList<TestBNode<E>>();
		
		keys.add(initialKey);
		children.add(leftChild);
		children.add(rightChild);
		
		this.parent = parent;
	}
	
	/**
	 * Constructor: Create LEAF BNode with one key initialKey and a
	 * parent pointer parent. Degree (t) is determined by the parent
	 * pointer.
	 * 
	 * @param initialKey  the initial object in this BNode
	 * @param parent     pointer to the parent of this BNode
	 */
	public TestBNode(TreeObject initialKey, TestBNode<E> parent) {
		this(initialKey, parent, null, null);
	}
	
	/**
	 * Constructor: Create ROOT BNode with one key initialKey and the degree
	 * (t).
	 * 
	 * @param initialKey the initial object in this BNode
	 */
	public TestBNode(TreeObject initialKey) {
		this(initialKey, null, null, null);
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
	public void insert(TreeObject key, TestBNode<E> child) {
		
		//get to the index of the first k less than key
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--){}
		
		//add new key and child to lists
		keys.add(i + 1, key);
		children.add(i + 2, child);
		
		//TODO: write to disk, probably in BTree.java
	}
	
	/**
	 * Insert the given key into this BNode. Should only be used on LEAF
	 * nodes.
	 * 
	 * @param key TreeObject containing Object to insert
	 */
	public void insert(TreeObject key) {
		insert(key, null);
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
	public TestBNode<E> getSubtree(TreeObject key){
		
		//get to the index of the first k less than key
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--){}
		
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
	 */
	public TestBNode<E> split() throws IllegalStateException{
		//==== for better understanding when coming back to look at this method, I'm going to ====
		//==== create an example split and show how it changes through comments:              ====
		//Keys     -  a b c d e f g
		//Pointers - 0 1 2 3 4 5 6 7
		
		int originalN = keys.size();
		
		
		//remove key and two pointers right of middle and insert into new BNode 'splitRight':
		//Keys     -  a b c d f g    |  e
		//Pointers - 0 1 2 3   6 7   | 4 5
		TestBNode<E> splitRight = new TestBNode<E>(keys.remove(keys.size()/2 + 1), parent, children.remove(children.size()/2), children.remove(children.size()/2 + 1));
		
		
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
		if(isRoot()) {
			TestBNode<E> newRoot = new TestBNode<E>(keys.removeLast(), null, this, splitRight);
			this.parent = splitRight.parent = newRoot;
			
			return newRoot;
		}
		
		parent.insert(keys.removeLast(), splitRight);
		return parent;
	}
	
	//=================================================================================================================
	//                                           GET/SET/UTILITY METHODS
	//=================================================================================================================

	/**
	 * Gets the first key for the root. Used and needed for B-Tree insert.
	 *
	 * @return the key of the root
	 */
	public TreeObject rootKey() {
		return keys.getFirst();
	}

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
	 * Indicate if this node is a leaf or not
	 * 
	 * @return true if leaf, false otherwise
	 */
	public boolean isLeaf() {
		return children.get(0) == null;
	}
	
	/**
	 * Indicate if this node is the root or not
	 * 
	 * @return true if root, false otherwise
	 */
	public boolean isRoot() {
		return parent == null;
	}
	
	/**
	 * Indicates whether or not this BNode is full (n = 2t - 1)
	 * 
	 * 
	 * @param  degree the degree of this BTree
	 * 
	 * @return true is full, false otherwise
	 */
	public boolean isFull(int degree) {
		return ((2 * degree) - 1) == keys.size();
	}
	
	//returns long value for each key in a single String seperated by spaces
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder();
		for(int i = 0; i < keys.size(); i++) {
			retString.append(keys.get(i).getKey() + " "); //TODO: return letters instead
		}
		
		return retString.toString().substring(0, retString.length() - 1);
	}
}
