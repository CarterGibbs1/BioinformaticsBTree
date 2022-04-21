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
 */
public class BNode {
	
	//TODO: Add cache functionality

	//TODO: Arrays or LinkedLists?
	//child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
//	private LinkedList<TreeObject> keys; //objects/keys in this node, also size() = n
//	private LinkedList<Long> children;      //addresses to the children of this node 
	
	private TreeObject[] keys;
	private long[] children;
	
//	private 
	
	private long parent; //parent address
	private int n; //TODO: Not needed in LinkedList imp
	
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
	public BNode(TreeObject initialKey, long thisAddress, long parent, long leftChild, long rightChild) throws IllegalStateException {
		//check that DEGREE has been set
		if(degree < 1) {
			throw new IllegalStateException("Degree is an invalid value. It might have not been set before BNodes are used.");
		}
		
		//initialize instance variables
//		keys = new LinkedList<TreeObject>();
//		children = new LinkedList<Long>();
		
		keys = new TreeObject[degree * 2 - 1];
		children = new long[degree * 2];
		
//		keys.add(initialKey);
//		children.add(leftChild);
//		children.add(rightChild);
		
		keys[0] = initialKey;
		
		for(int i = 0; i < children.length; i++) {//TODO: inefficient, if we end up using arrays then best just to consider 0 as null
			children[i] = -1;
		}
		
		children[0] = leftChild;
		children[1] = rightChild;
		
		
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
	public BNode(TreeObject initialKey, long address, long parent) throws IllegalStateException {
		this(initialKey, address, parent, -1, -1);
	}
	
	/**
	 * Constructor: Create singular BNode with one key initialKey.
	 * 
	 * @param intialKey the initial object in this BNode
	 * @param thisAddress address of this BNode
	 * 
	 * @throws IllegalStateException Static degree has not been set
	 */
	public BNode(TreeObject initialKey, long address) throws IllegalStateException {
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
	public void insert(TreeObject key, long child, boolean write) throws IOException {
		//get to the index of the first k less than key
		int i;
//		for(i = (keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--);
		for(i = (n - 1); i >= 0 && key.compare(keys[i]) <= 0; i--) {
			keys[i + 1] = keys[i];
			children[i + 2] = children[i + 1];
		}

		//add new key and child to lists
//		keys.add(i + 1, key);
//		children.add(i + 2, child);
		
		keys[i + 1] = key;
		children[i + 2] = child;
		
		n++;
				
		if(write) {
			BReadWrite.writeBNode(this);
		}
	}
	
	/**
	 * Insert the given key into this BNode. If the key is in this
	 * BNode, then increment that key's frequency and always write.
	 * <p>
	 * WRITE: This method writes this changed BNode to the RAF
	 * 
	 * @param key TreeObject containing Object to insert
	 * 
	 * @throws IOException Writing to RAF may throw exception
	 */
	public void insert(TreeObject key) throws IOException {
		insert(key, -1, true);
	}
	
	/**
	 * Insert the given key into this BNode and insert the given child.
	 * <p>
	 * NO WRITE: This method does not write the changed BNode to the RAF
	 * 
	 * @param key   TreeObject containing Object to insert
	 * @param child Child related to key to insert 
	 * 
	 * @throws IOException Writing to RAF may throw exception
	 */
	public void insertNoWrite(TreeObject key, long child) throws IOException {
		insert(key, child, false);
	}
	
	/**
	 * Get the child of this BNode where the given key should be
	 * inserted or would be located. Will return this BNodes address
	 * if the object is in this BNode.
	 * 
	 * @param  key Object to use to locate the appropriate subtree
	 * 
	 * @return subtree address (child of this BNode) that key
	 *         belongs to OR this BNode if it contains the key
	 *         
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	public long getElementLocation(TreeObject key) throws IOException{
		
		//get to the index of the first k less than key
		int i;
//		for(i = ( keys.size() - 1); i >= 0 && key.compare(keys.get(i)) < 0; i--){}
		for(i = (n - 1); i >= 0 && key.compare(keys[i]) < 0; i--);
		
		//if this BNode contains the key, return this address
//		if(i >= 0 && key.compare(keys.get(i)) == 0) {
//			keys.get(i).incrementFrequency();
//		}
		if(i >= 0 && key.compare(keys[i]) == 0) {
			return address;
		}
		
//		return children.get(i + 1);
		return children[i + 1];
	}
	
	/**
	 * Increments the frequency of the given key in this BNode.
	 * <p>
	 * WRITE: This method writes this changed BNode to the RAF
	 * 
	 * @param key The Object to increment the frequency of
	 * 
	 * @return True if element was found, false otherwise
	 * 
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	public boolean incrementElement(TreeObject key) throws IOException {
		int index = indexOf(key);
		if(index != -1) {
			keys[index].incrementFrequency();
			BReadWrite.writeBNode(this);
			return true;
		}
		return false;
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
		
//		int originalN = n;
		BNode parentNode; //TODO: parent can be kept by BTree and passed in for performance increase
		
		
		//remove key and two pointers right of middle and insert into new BNode 'splitRight':
		//Keys     -  a b c d f g    |  e
		//Pointers - 0 1 2 3   6 7   | 4 5
//		BNode splitRight = new BNode(keys.remove(keys.size()/2 + 1), BReadWrite.getNextAddress(), parent, children.remove(children.size()/2), children.remove(children.size()/2 + 1));
		BNode splitRight = new BNode(keys[n/2 + 1], BReadWrite.getNextAddress(), parent, children[(n + 1)/2], children[(n + 1)/2 + 1]);
		n--;
		
		//starting just to the right of the middle of this BNode, continuously remove the pointers and keys
		//at that position and insert them into splitRight:
		//Keys     -  a b c d  |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
//		while((keys.size() - 1) != originalN/2) {
//			splitRight.insertNoWrite(keys.remove(originalN/2 + 1), children.remove(originalN/2 + 1));
//		}
//		n = keys.size() - 1;
		for(int i = keys.length/2 + 1; i < keys.length - 1; i++) {
			splitRight.insertNoWrite(keys[i + 1], children[i + 2]);
			n--;
		}
		
//		while(n - 1 != keys.length/2) {
//			splitRight.insertNoWrite(keys[keys.length/2 + 1], children[keys.length/2 + 1]);
//			n--;
//		}
//		
		//lastly, remove last node (original middle) from this and insert into parent with
		//a pointer to splitRight
		//Parent   -    .. x x d x x ..
		//                    / \
		//Keys     -  a b c    |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
		
		//if this is the ROOT, create new parent/root to insert into
		//else read the parent and insert into
//		if(isRoot()) {
//			//                                           Already new node 'splitRight' at getNextAddress, so
//			//                                           have to compensate with additional offset getDiskSize
//			parentNode = new BNode(keys.removeLast(), BReadWrite.getNextAddress() + BNode.getDiskSize(), -1, address, splitRight.getAddress());
//			this.parent = splitRight.parent = parentNode.getAddress();
//		}
//		else {
//			parentNode = BReadWrite.readBNode(parent); //TODO: see other parentNode todo, prevent read here
//			parentNode.insertNoWrite(keys.removeLast(), splitRight.getAddress());
//		}
		if(isRoot()) {
			//                                           Already new node 'splitRight' at getNextAddress, so
			//                                           have to compensate with additional offset getDiskSize
			parentNode = new BNode(keys[n - 1], BReadWrite.getNextAddress() + BNode.getDiskSize(), -1, address, splitRight.getAddress());
			this.parent = splitRight.parent = parentNode.getAddress();
		}
		else {
			parentNode = BReadWrite.readBNode(parent); //TODO: see other parentNode todo, prevent read here
			parentNode.insertNoWrite(keys[n - 1], splitRight.getAddress());
		}
		n--;
		
		//if the split is happening on a non-leaf, reassign splitRight's children's parents
		//TODO: I think this is very inefficient, but it's the best I could come up with right now =====================================================
		if(!isLeaf()) {
			BReadWrite.reassignParents(splitRight);
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
	 * Set the address of this BNode's parent in the RAF.
	 * 
	 * @param parent New parent address of this BNode
	 */
	public void setParent(long parent) {
		this.parent = parent;
	}
	
//	/**
//	 * Get this BNode's keys.
//	 * 
//	 * @return LinkedList of TreeObjects
//	 */
//	public LinkedList<TreeObject> getKeys(){
//		return keys;
//	}
//	
//	/**
//	 * Get this Bnode's children.
//	 * 
//	 * @return LinkedList of longs
//	 */
//	public LinkedList<Long> getChildren(){
//		return children;
//	}
	
	/**
	 * Get this BNode's key at the given index.
	 * 
	 * @return TreeObject at index
	 */
	public TreeObject getKey(int index){
//		return keys;
		return keys[index];
	}
	
	/**
	 * Get this Bnode's child at the given index.
	 * 
	 * @return Address at index
	 */
	public long getChild(int index){
//		return children;
		return children[index];
	}
	
	/**
	 * Return's the index of the given object.
	 * 
	 * @param key Object to locate index of
	 * 
	 * @return Index of the given Object, -1 if it's not
	 *         in this BNode
	 */
	public int indexOf(TreeObject key) {
		for(int i = 0; i < n; i++) {
			if(keys[i].compare(key) == 0) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Indicate if this node is a leaf or not
	 * 
	 * @return true if leaf, false otherwise
	 */
	public boolean isLeaf() {
//		return children.get(0) < 0;
		for(int i = 0; i < n + 1; i++) {
			if(children[i] < 0) {
				return true;
			}
		}
		return false;
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
		return ((2 * degree ) - 1) == n;
	}
	
	//incomplete equals method, may be needed later
//	@SuppressWarnings("unchecked")
//	@Override
//	public boolean equals(Object otherObject) {
//		//check if other object is null
//		if(otherObject == null) {
//			return false;
//		}
//		
//		//check if other object is not the same as this class
//		if(otherObject.getClass() != this.getClass()) {
//			return false;
//		}
//		
//		//can 'safely' cast otherObject to HashObject and do actual comparison
//		BNode<E> other = (BNode<E>) otherObject;
//		
//		//check that all children are the same
//		if(children.size() != other.children.size()) {
//			return false;
//		}
//		for(int i = 0; i < children.size(); i++) {
//			if(children.get(i) != other.children.get(i)) {
//				return false;
//			}
//		}
//		
//		//check that all keys are the same
//		if(keys.size() != other.keys.size()) {
//			return false;
//		}
//		for(int i = 0; i < keys.size(); i++) {
//			if(keys.get(i).compare(other.keys.get(i)) != 0) {
//				return false;
//			}
//		}
//		
//		return address == other.address;
//	}
	
	//most likely temporary toString.
	//returns long value for each key in a single String separated by spaces
//	@Override
//	public String toString() {
//		StringBuilder retString = new StringBuilder();
//		for(int i = 0; i < keys.size(); i++) {
//			retString.append(keys.get(i).getKey() + " "); //TODO?: return letters instead
//		}
//		
//		return retString.toString().substring(0, retString.length() - 1);
//	}
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder();
		for(int i = 0; i < n ; i++) {
			retString.append(keys[i].getKey() + " "); //TODO?: return letters instead
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

















