package cs321.btree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
	private LinkedList<Long> children;      //addresses to the children of this node 
	
	private long parent; //parent address
	private int n;
	
	static private FileChannel RAF; //TODO: final?
	static private int DEGREE; 
	static private ByteBuffer buffer;
	
	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
	
	/**
	 * Constructor: Create BNode with one key 'initialKey,' a parent address
	 * 'parent,' and two children 'leftChild' and 'rightChild.' An address that
	 * is less than 0 is considered null.
	 * 
	 * @param intialKey  the initial object in this BNode
	 * @param parent     address of the parent of this BNode
	 * @param leftChild  address of the child left of initialKey
	 * @param rightChild address of the child right of initialKey
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
	public BNode(TreeObject<E> initialKey, long parent, long leftChild, long rightChild) {
		//initialize instance variables
		keys = new LinkedList<TreeObject<E>>();
		children = new LinkedList<Long>();
		
		keys.add(initialKey);
		children.add(leftChild);
		children.add(rightChild);
		n = 1;
		
		this.parent = parent;
	}
	
	/**
	 * Constructor: Create leaf BNode with one key 'initialKey' and
	 * a parent address 'parent.' An address that is less than 0 is
	 * considered null.
	 * 
	 * @param intialKey  the initial object in this BNode
	 * @param parent     address of the parent of this BNode
	 */
	public BNode(TreeObject<E> initialKey, long parent) {
		this(initialKey, parent, -1, -1);
	}
	
	/**
	 * Constructor: Create root BNode with one key initialKey.
	 * 
	 * @param intialKey the initial object in this BNode
	 */
	public BNode(TreeObject<E> initialKey) {
		this(initialKey, -1, -1, -1);
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
	public void insert(TreeObject<E> key, long child) {
		
		//get to the index of the first k less than key
		int i;
		for(i = ( keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--){}
		
		//add new key and child to lists
		keys.add(i + 1, key);
		children.add(i + 2, child);
		n++;
		
		//TODO: write to disk, probably in BTree.java
	}
	
	/**
	 * Insert the given key into this BNode. Should only be used on leaf
	 * nodes.
	 * 
	 * @param key TreeObject containing Object to insert
	 */
	public void insert(TreeObject<E> key) {
		insert(key, -1);
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
	 * 
	 * @param  address The address of this BNode in the RAF
	 * 
	 * @return the parent of the two BNodes
	 */
	public long split(long address) {
		//==== for better understanding when coming back to look at this method, I'm going to ====
		//==== create an example split and show how it changes through comments:              ====
		//Keys     -  a b c d e f g
		//Pointers - 0 1 2 3 4 5 6 7
		
		int originalN = keys.size();
		BNode<E> parentNode = new BNode<E>(new TreeObject<E>(null, 1), 1);//TODO: read parent from RAF
		
		
		//remove key and two pointers right of middle and insert into new BNode 'splitRight':
		//Keys     -  a b c d f g    |  e
		//Pointers - 0 1 2 3   6 7   | 4 5
		BNode<E> splitRight = new BNode<E>(keys.remove(keys.size()/2 + 1), parent, children.remove(children.size()/2), children.remove(children.size()/2 + 1));
		long splitRightAddress = 0; //TODO: find next available address to write new BNode
		
		//starting just to the right of the middle of this BNode, continuously remove the pointers and keys
		//at that position and insert them into splitRight:
		//Keys     -  a b c d  |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
		while((keys.size() - 1) != originalN/2) {
			splitRight.insert(keys.remove(originalN/2 + 1), children.remove(originalN/2 + 1));
		}
		n = keys.size() - 1;
		
		//lastly, remove last node (original middle) from this and insert into parent with
		//a pointer to splitRight
		//Parent   -    .. x x d x x ..
		//                    / \
		//Keys     -  a b c d  |  e f g
		//Pointers - 0 1 2 3   | 4 5 6 7
		
		//if this is the ROOT, create new parent/root to insert into and update types
		if(isRoot()) {
			BNode<E> newRoot = new BNode<E>(keys.removeLast(), -1, address, splitRightAddress);
			long newRootAddress = 0; //TODO: find next available address to write new BNode
			this.parent = splitRight.parent = newRootAddress;
			
			//TODO: write changes to RAF;
			return newRootAddress;
		}
		
		//TODO: write changes to RAF;
		parentNode.insert(keys.removeLast(), splitRightAddress);
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
	 * 
	 * @param  degree the degree of this BTree
	 * 
	 * @return true is full, false otherwise
	 */
	public boolean isFull(int degree) {
		return ((2 * degree) - 1) == keys.size();
	}
	
	//most likely temporary toString.
	//returns int value for each key in a single, no space, String
	@Override
	public String toString() {
		StringBuilder retString = new StringBuilder();
		for(int i = 0; i < keys.size(); i++) {
			retString.append(keys.get(i).getKey()); //TODO: return letters instead
		}
		
		return retString.toString();
	}
	
	
	
	
	
	
//	static public void setStatics(FileChannel i, int degree) {
//		RAF = i;
//		DEGREE = degree;
//	}
	
	
	static public int getDiskSize(int degree) {
		return Long.BYTES + (((2 * degree) - 1) * (Integer.BYTES + Long.BYTES)) + (2 * degree * Long.BYTES);
	}
	
	/**
	 * Write the given BNode to the RAF at the specified address.
	 * Writes the n and then the lists: child0, key0, child1, key1,
	 * child2 ... childn, keyn, childn + 1.
	 * 
	 * @param <E>     Generic type this BTree holds
	 * @param node    BNode to write to RAF
	 * @param address Address to write this BNode to
	 * 
	 * @throws IOException Writing to RAF may throw exception
	 */
	static public <E> void writeBNode(BNode<E> node, long address) throws IOException{
		//start at address and make buffer ready to read
		RAF.position(address);
		buffer.clear();
		
		//write parent
		buffer.putLong(address);
		//store and write n
		int n = node.getN();
		buffer.putInt(n);
		
		//write the children and keys in alternating order
		buffer.putLong(node.children.get(0));
		for(int i = 0; i < n; i++) {
			buffer.putLong(node.keys.get(i).getKey());
			buffer.putInt(node.keys.get(i).getFrequency());
			
			buffer.putLong(node.children.get(i + 1));
		}
		
		//make buffer ready to write and then write to RAF
		buffer.flip();
		RAF.write(buffer);
	}
	
	/**
	 * 
	 * @param <E>     Generic type this BTree holds
	 * @param address Address that BNode is located
	 * 
	 * @return BNode stored in RAF at the given address
	 * 
	 * @throws IOException Reading RAF may throw exception
	 */
	static public <E> BNode<E> readBNode(long address) throws IOException {
		//start at address and make buffer ready to read
		RAF.position(address);
		buffer.clear();
		RAF.read(buffer);
		buffer.flip();
		
		//get parent and n
		long parent = buffer.getLong();
		int n = buffer.getInt();
		
		//get first key and two children
		long leftChild = buffer.getLong();
		TreeObject<E> initialKey = new TreeObject<E>(null, buffer.getInt()); //TODO: additional TreeObject constructor
		long rightChild = buffer.getLong();
		
		//construct the return BNode and insert the other n - 1 keys and children
		BNode<E> retNode = new BNode<E>(initialKey, parent, leftChild, rightChild);
		for(int i = 1; i < n; i++) {
			retNode.insert(new TreeObject<E>(null, buffer.getInt()), buffer.getLong());
		}
		
		return retNode;
	}
}

















