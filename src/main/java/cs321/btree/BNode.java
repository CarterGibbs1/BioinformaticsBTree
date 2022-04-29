package cs321.btree;

import java.io.IOException;

/**
 * Used to create BNode objects that contain TreeObjects. Notable
 * methods are insert(), getElementLocation(), and incrementElement()
 * which are important in creating a functional BTree.
 * <p>
 * NOTE: BNodes do not write themselves to files.
 * 
 * @author  Mesa Greear, Aaron Goin
 * @version Spring 2022
 */
public class BNode {

	//child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
	private TreeObject[] keys;
	private long[] children;
	
	private long parent; //parent address
	private int n;
	
	private long address; //not written to RAF
	
	private static int degree; //shared degree amongst all BNodes
	
	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
	
	/**
	 * Constructor: Create BNode with one key 'initialKey,' a parent address
	 * 'parent,' and two children 'leftChild' and 'rightChild.' An address that
	 * is <= 0 is considered null. A null initialKey makes this an empty BNode.
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
	 *      new BNode(a, 20, 0, 90, 50)
	 * 
	 * result     -    0
	 *            20 = a
	 *               90 50
	 */
	public BNode(TreeObject initialKey, long thisAddress, long parent, long leftChild, long rightChild) throws IllegalStateException {
		//check that DEGREE has been set
		if(degree < 1) {
			throw new IllegalStateException("Degree is an invalid value. It might have not been set before BNodes are used.");
		}
		
		//initialize instance variables
		keys = new TreeObject[degree * 2 - 1];
		children = new long[degree * 2];
		
		keys[0] = initialKey;
		n = keys[0] == null ? 0 : 1;
		
		children[0] = leftChild;
		children[1] = rightChild;
		
		address = thisAddress;
		this.parent = parent;
	}
	
	/**
	 * Constructor: Create leaf BNode with one key 'initialKey' and
	 * a parent address 'parent.'  An address that is <= 0 is considered
	 * null. A null initialKey makes this an empty BNode.
	 * 
	 * @param intialKey  the initial object in this BNode
	 * @param address address of this BNode
	 * @param parent     address of the parent of this BNode
	 * 
	 * @throws IllegalStateException Static degree has not been set
	 */
	public BNode(TreeObject initialKey, long address, long parent) throws IllegalStateException {
		this(initialKey, address, parent, 0, 0);
	}
	
	/**
	 * Constructor: Create singular BNode with one key initialKey. An
	 * address that is <= 0 is considered null. A null initialKey
	 * makes this an empty BNode.
	 * 
	 * @param intialKey the initial object in this BNode
	 * @param address address of this BNode
	 * 
	 * @throws IllegalStateException Static degree has not been set
	 */
	public BNode(TreeObject initialKey, long address) throws IllegalStateException {
		this(initialKey, address, 0, 0, 0);
	}
	
	/**
	 * Constructor: Create singular empty BNode.
	 * 
	 * @param address address of this BNode
	 * 
	 * @throws IllegalStateException Static degree has not been set
	 */
	public BNode(long address) throws IllegalStateException {
		this(null, address);
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
	public void insert(TreeObject key, long child) throws IOException {
		//get to the index of the first k less than key and move keys/children over
		int i;
		for(i = (n - 1); i >= 0 && key.compare(keys[i]) <= 0; i--) {
			keys[i + 1] = keys[i];
			children[i + 2] = children[i + 1];
		}

		//add new key and child to lists
		keys[i + 1] = key;
		children[i + 2] = child;
		n++;
	}
	
	/**
	 * Insert the given key into this BNode, should only be used on
	 * leaf BNodes.
	 * 
	 * @param key TreeObject containing Object to insert
	 * 
	 * @throws IOException Writing to RAF may throw exception
	 */
	public void insert(TreeObject key) throws IOException {
		insert(key, 0);
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
		for(i = (n - 1); i >= 0 && key.compare(keys[i]) < 0; i--);
		
		//if this BNode contains the key, return this address
		if(i >= 0 && key.compare(keys[i]) == 0) {
			return address;
		}
		
		return children[i + 1];
	}
	
	/**
	 * Increments the frequency of the given key in this BNode.
	 * 
	 * @param key The Object to increment the frequency of
	 * 
	 * @return True if element was found and incremented, false otherwise
	 * 
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	public boolean incrementElement(TreeObject key) throws IOException {
		int index = indexOf(key);
		if(index != -1) {
			keys[index].incrementFrequency();
			return true;
		}
		return false;
	}
	
	//=================================================================================================================
	//                                           GET/SET/UTILITY METHODS
	//=================================================================================================================
	
	/**
	 * Get the number of objects (n) in this BNode.
	 * 
	 * @return Number of objects in this BNode
	 */
	public int getN() {
		return n;
	}
	
	/**
	 * Set the new N (number of objects) of this BNode.
	 * 
	 * @param newN New N of this BNode
	 */
	public void setN(int newN) {
		n = newN;
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
	
	/**
	 * Get this BNode's key at the given index.
	 * 
	 * @return TreeObject at index
	 */
	public TreeObject getKey(int index){
		return keys[index];
	}
	
	/**
	 * Get the key array of this BNode.
	 * 
	 * @return Array of TreeObjects in this BNode,
	 */
	public TreeObject[] getKeys() {
		return keys;
	}
	
	/**
	 * Get this Bnode's child at the given index.
	 * 
	 * @return Address at index
	 */
	public long getChild(int index){
		return children[index];
	}
	
	/**
	 * Get the child array of this BNode.
	 * 
	 * @return Array of child BNode addresses.
	 */
	public long[] getChildren() {
		return children;
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
		return children[0] <= 0;
	}
	
	/**
	 * Indicate if this node is the root or not
	 * 
	 * @return true if root, false otherwise
	 */
	public boolean isRoot() {
		return parent <= 0;
	}
	
	/**
	 * Indicates whether or not this BNode is full (n = 2t - 1)
	 * 
	 * @return true is full, false otherwise
	 */
	public boolean isFull() {
		return ((2 * degree ) - 1) == n;
	}
	
	/**
	 * Returns a string of the compresses (long) keys in a single line.
	 * 
	 * @return String of "key0 key1 key2 + ... + keyn"
	 */
	public String getKeysAsString() {
		StringBuilder retString = new StringBuilder();
		int i;
		for(i = 0; i < n - 1 ; i++) {
			retString.append(keys[i].getKey() + " ");
		}
		retString.append(keys[n - 1].getKey());
		
		return retString.toString();
	}
	
	/**
	 * Get a String representation of this BNode in the form
	 * of a dump.
	 * 
	 * @return String representation appropriate for dumps
	 */
	public String dump() {
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < n; i++) {
			ret.append(keys[i].toString() + "\n");
		}
		
		return ret.toString();
	}
	
	//only compares the address of the other BNode
	@Override
	public boolean equals(Object otherObject) {
		//check if other object is null
		if(otherObject == null) {
			return false;
		}
		
		//check if other object is not the same as this class
		if(otherObject.getClass() != this.getClass()) {
			return false;
		}
		
		//can 'safely' cast otherObject to HashObject and do actual comparison
		BNode other = (BNode) otherObject;
		
		return address == other.address;
	}
	
	//returns '{child [key|keyLong : frequency] child ...}'
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder("{");
		int i;
		for(i = 0; i < n ; i++) {
			retString.append(children[i]);
			retString.append(" [" + keys[i].keyToString() + "|" + keys[i].getKey() + " : " + keys[i].getFrequency() + "] ");
		}
		retString.append(children[i + 1] + "}");
		
		return retString.toString();
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