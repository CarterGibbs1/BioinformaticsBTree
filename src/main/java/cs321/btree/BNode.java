package cs321.btree;

import java.io.IOException;
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
	
	//TODO: Add cache functionality

	//child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
	private LinkedList<TreeObject<E>> keys; //objects/keys in this node, also size() = n
	private LinkedList<Long> children;      //addresses to the children of this node 
	
	private long parent; //parent address
	private int n; //TODO: Not needed?
	
	private long address; //not written to RAF
	
	private static int degree; //shared degree amongst all BNodes
	
	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
	
	/**
	 * Constructor: Create BNode with one key 'initialKey,' a parent address
	 * 'parent,' and two children 'leftChild' and 'rightChild.' An address that
	 * is less than 0 is considered null.
	 * 
	 * @param intialKey   the initial object in this BNode
	 * @param thisAddress address of this BNode
	 * @param parent      address of the parent of this BNode
	 * @param leftChild   address of the child left of initialKey
	 * @param rightChild  address of the child right of initialKey
	 * 
	 * @throws IllegalStateException Static degree has not been set
	 */
	/*
	 * Example Demonstration:
	 * 
	 * 
	 *      new BNode(a, |, /, \)
	 * 
	 * result     -    |
	 *                 a
	 *                / \
	 */
	public BNode(TreeObject<E> initialKey, long thisAddress, long parent, long leftChild, long rightChild) throws IllegalStateException {
		//check that DEGREE has been set
		if(degree < 1) {
			throw new IllegalStateException("Degree is an invalid value. It might have not been set before BNodes are used.");
		}
		
		//initialize instance variables
		keys = new LinkedList<TreeObject<E>>();
		children = new LinkedList<Long>();
		
		keys.add(initialKey);
		children.add(leftChild);
		children.add(rightChild);
		n = 1;
		
		address = thisAddress;
		this.parent = parent;
	}
	
	/**
	 * Constructor: Create leaf BNode with one key 'initialKey' and
	 * a parent address 'parent.' An address that is less than 0 is
	 * considered null.
	 * 
	 * @param intialKey  the initial object in this BNode
	 * @param thisAddress address of this BNode
	 * @param parent     address of the parent of this BNode
	 * 
	 * @throws IllegalStateException Static degree has not been set
	 */
	public BNode(TreeObject<E> initialKey, long address, long parent) throws IllegalStateException {
		this(initialKey, address, parent, -1, -1);
	}
	
	/**
	 * Constructor: Create singular BNode with one key initialKey.
	 * 
	 * @param intialKey the initial object in this BNode
	 * 
	 * @throws IllegalStateException Static degree has not been set
	 */
	public BNode(TreeObject<E> initialKey, long address) throws IllegalStateException {
		this(initialKey, address, -1, -1, -1);
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
	 * @param write Whether to write to the RAF or not
	 * 
	 * @throws IOException Writing to RAF may throw exception
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
	public void insert(TreeObject<E> key, long child, boolean write) throws IOException {
		
		//get to the index of the first k less than key
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--){}
		
		//add new key and child to lists
		keys.add(i + 1, key);
		children.add(i + 2, child);
		n++;
		
		if(write) {
			BReadWrite.writeBNode(this);
		}
	}
	
	/**
	 * Insert the given key into this BNode. Should only be used on leaf
	 * nodes.
	 * <p>
	 * WRITE: This method writes this changed BNode to the RAF
	 * 
	 * @param key TreeObject containing Object to insert
	 * 
	 * @throws IOException Writing to RAF may throw exception
	 */
	public void insert(TreeObject<E> key) throws IOException {
		insert(key, -1, true);
	}
	
	/**
	 * Insert the given key into this BNode and insert the given child
	 * <p>
	 * NO WRITE: This method does not write the changed BNode to the RAF
	 * 
	 * @param key   TreeObject containing Object to insert
	 * @param child Child related to key to insert 
	 * 
	 * @throws IOException Writing to RAF may throw exception
	 */
	public void insertNoWrite(TreeObject<E> key, long child) throws IOException {
		insert(key, child, false);
	}
	
	/**
	 * Get the child of this BNode where the given key should be
	 * inserted or would be located. Does NOT insert the key, only
	 * returns the subtree that it belongs to.
	 * 
	 * @param  key Object to use to locate the appropriate subtree
	 * 
	 * @return subtree address (child of this BNode) that key
	 *         belongs to
	 */
	public long getSubtree(TreeObject<E> key){
		
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
	 * <p>
	 * WRITE: This method writes this changed BNodes to the RAF
	 * 
	 * @return the parent of the two BNodes
	 * 
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	public long split() throws IOException {
		//==== for better understanding when coming back to look at this method, I'm going to ====
		//==== create an example split and show how it changes through comments:              ====
		//Keys     -  a b c d e f g
		//Pointers - 0 1 2 3 4 5 6 7
		
		int originalN = keys.size();
		BNode<E> parentNode; //TODO: parent can be kept by BTree and passed in for performance increase
		
		
		//remove key and two pointers right of middle and insert into new BNode 'splitRight':
		//Keys     -  a b c d f g    |  e
		//Pointers - 0 1 2 3   6 7   | 4 5
		BNode<E> splitRight = new BNode<E>(keys.remove(keys.size()/2 + 1), BReadWrite.getNextAddress(), parent, children.remove(children.size()/2), children.remove(children.size()/2 + 1));
		
		//starting just to the right of the middle of this BNode, continuously remove the pointers and keys
		//at that position and insert them into splitRight:
		//Keys     -  a b c d  |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
		while((keys.size() - 1) != originalN/2) {
			splitRight.insertNoWrite(keys.remove(originalN/2 + 1), children.remove(originalN/2 + 1));
		}
		n = keys.size() - 1;
		
		//lastly, remove last node (original middle) from this and insert into parent with
		//a pointer to splitRight
		//Parent   -    .. x x d x x ..
		//                    / \
		//Keys     -  a b c d  |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
		
		//if this is the ROOT, create new parent/root to insert into
		//else read the parent and insert into
		if(isRoot()) {
			//                                           Already new node 'splitRight' at getNextAddress, so
			//                                           have to compensate with additional offset getDiskSize
			parentNode = new BNode<E>(keys.removeLast(), BReadWrite.getNextAddress() + BNode.getDiskSize(), -1, address, splitRight.getAddress());
			this.parent = splitRight.parent = parentNode.getAddress();
		}
		else {
			parentNode = BReadWrite.readBNode(parent); //TODO: see other parentNode todo, prevent read here
			parentNode.insertNoWrite(keys.removeLast(), splitRight.getAddress());
		}
		
		//write changed BNodes to RAF
		BReadWrite.writeBNode(splitRight);
		BReadWrite.writeBNode(this);
		BReadWrite.writeBNode(parentNode);
		
		
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
		return n;
	}
	
	/**
	 * Get the address of this BNode in the RAF.
	 * 
	 * @return address of this in RAF
	 */
	public long getAddress() {
		return address;
	}
	
	/**
	 * Get the address of this BNode's parent in the RAF.
	 * 
	 * @return address of parent in RAF
	 */
	public long getParent() {
		return parent;
	}
	
	/**
	 * Get this BNode's keys.
	 * 
	 * @return LinkedList of TreeObjects
	 */
	public LinkedList<TreeObject<E>> getKeys(){
		return keys;
	}
	
	/**
	 * Get this Bnode's children.
	 * 
	 * @return LinkedList of longs
	 */
	public LinkedList<Long> getChildren(){
		return children;
	}
	
	/**
	 * Indicate if this node is a leaf or not
	 * 
	 * @return true if leaf, false otherwise
	 */
	public boolean isLeaf() {
		return children.get(0) < 0;
	}
	
	/**
	 * Indicate if this node is the root or not
	 * 
	 * @return true if root, false otherwise
	 */
	public boolean isRoot() {
		return parent < 0;
	}
	
	/**
	 * Indicates whether or not this BNode is full (n = 2t - 1)
	 * 
	 * @return true is full, false otherwise
	 */
	public boolean isFull() {
		return ((2 * degree ) - 1) == keys.size();
	}
	
	//most likely temporary toString.
	//returns long value for each key in a single String separated by spaces
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder();
		for(int i = 0; i < keys.size(); i++) {
			retString.append(keys.get(i).getKey() + " "); //TODO?: return letters instead
		}
		
		return retString.toString().substring(0, retString.length() - 1);
	}
	
	//=================================================================================================================
	//                                           STATIC METHODS
	//=================================================================================================================
	
	/**
	 * Set the shared static degree of all BNodes.
	 * 
	 * @param degree (t) How many keys/objects (t - 1 to 2t - 1)
	 *               and children (t to 2t) BNodes can have
	 */
	static public void setDegree(int newDegree) {
		degree = newDegree;
	}
	
	/**
	 * Get the max size in bytes a BNode written to disk could
	 * be. This will be the parent address (8), the number of
	 * objects n (4), max number of objects ((2t - 1) * (8 + 4)),
	 * and the max number of children (2t * 8) summed together.
	 * 
	 * @return Max bytes a BNode will take up on the disk
	 */
	static public int getDiskSize() {
		return Long.BYTES + Integer.BYTES + (((2 * degree) - 1) * (Integer.BYTES + Long.BYTES)) + (2 * degree * Long.BYTES);
	}
}

















