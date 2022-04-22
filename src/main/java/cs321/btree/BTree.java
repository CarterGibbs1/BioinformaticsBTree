package cs321.btree;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Used to create BTree objects that hold Generic Type objects.
 * Notable method is insert() which is important in structuring the
 * BTree.
 *
 * @author  Carter Gibbs, Mesa Greear, Aaron Goin
 * @version Spring 2022
 */
public class BTree
{

    private long root;//TODO?: could just be pointer maybe?
    private int numNodes;
    private short height;
    
    private final short FREQUENCY;
    private final int DEGREE;
    private final BCache CACHE;

    
   	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
    
    //TODO:
    public BTree(long root, int degree, int frequency, int numNodes, int height, int cacheSize) {
    	//instantiate variables
    	DEGREE = degree;
    	FREQUENCY = (short)frequency;
    	CACHE = new BCache(cacheSize);
    	
    	this.numNodes = numNodes;
    	this.root = root;
    	this.height = (short)height;
    	
    	//set statics
    	BNode.setDegree(degree);
    }
    
    /**
     * Constructor: Create a BTree with the address of the root of an
     * already existing BTree in the RAF.
     * 
     * @param root      The address of the root of the BTree in the RAF
	 * @param degree    (t) How many keys/objects (t - 1 to 2t - 1)
	 *                  and children (t to 2t) BNodes can have
     * @param frequency (k) How many letters are stored per key (< 32)
     * @param numNodes  How many BNodes are in this BTree
     * @param height    The height of the BTree (0, 1, 2, 3, ....)
     */
    public BTree(long root, int degree, int frequency, int numNodes, int height) {
    	this(root, degree, frequency, numNodes, height, -1);
    }
    
    /**
     * Constructor: Create a new BTree with one initial object and BNode.
     * Written to file after instantiation.
     * 
     * @param initialObject The first object this BTree will hold
	 * @param degree        (t) How many keys/objects (t - 1 to 2t - 1)
	 *                      and children (t to 2t) BNodes can have
     * @param frequency     (k) How many letters are stored per key (< 32)
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public BTree(TreeObject initialObject, int degree, int frequency) throws IOException {
    	this(BTree.getDiskSize(), degree, frequency, 1, 0);
    	
    	//write new BTree and BNode to RAF
    	BReadWrite.writeBTree(this);
    	BReadWrite.writeBNode(new BNode(initialObject, getRoot()));
    }
    
    
   	//=================================================================================================================
	//                                         BTREE FUNCTIONALITY METHODS
	//=================================================================================================================
    
    /**
     * Insert the given object into the BTree.
     * 
     * @param toInsert Object to insert
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public void insert(TreeObject toInsert) throws IOException {
		BNode currentNode = BReadWrite.readBNode(root);
		BNode nextNode;
		

		// if currentNode(root) is full, split it
		if (currentNode.isFull()) {
			root = currentNode.split(null);
			currentNode = BReadWrite.readBNode(root);
			numNodes += 2;
			height++;
		}

		// get to appropriate leaf BNode
		while (!currentNode.isLeaf()) {
			
			//get correct child
			nextNode = BReadWrite.readBNode(currentNode.getElementLocation(toInsert));
			
			// if the object to insert is in currentNode, exit
			if (nextNode.getAddress() == currentNode.getAddress()) {
				currentNode.incrementElement(toInsert);
				return;
			}

			// if the nextNode is full, split it
			if (nextNode.isFull()) {
				nextNode.split(currentNode);
				numNodes++;
				nextNode = BReadWrite.readBNode(currentNode.getElementLocation(toInsert));
			}
			
			//move on to the child
			currentNode = nextNode;
		}

		// once at leaf, insert key if it's not in the BNode already
		if(!currentNode.incrementElement(toInsert)) {
			currentNode.insert(toInsert);
		}
    }
    
    /**
     * Gets the frequency of the given object in BTree. If it does not
     * exist, 0 is returned.
     * 
     * @param toFind The Object to look for in this BTree
     * 
     * @return The frequency of the object in BTree, 0 if it doesn't exist
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public int search(TreeObject toFind) throws IOException {
    	BNode currentNode = BReadWrite.readBNode(root);
		long nextNode;
		

		// go until at lead node
		while (!currentNode.isLeaf()) {
			
			// if the object to find is in currentNode, find it's location and return it's frequency
			nextNode = currentNode.getElementLocation(toFind);
			if (nextNode == currentNode.getAddress()) {
				return currentNode.getKey(currentNode.indexOf(toFind)).getFrequency();
			}
			//else read the child
			currentNode = BReadWrite.readBNode(nextNode);
		}

		//check leaf node
		if(currentNode.indexOf(toFind) != -1) {
			return currentNode.getKey(currentNode.indexOf(toFind)).getFrequency();
		}
		return 0;
    }

    
   	//=================================================================================================================
	//                                           GET/SET/UTILITY METHODS
	//=================================================================================================================
    
    /**
     * Get the address of the root of this BTree.
     * 
     * @return The address of the root of this BTree
     */
    public long getRoot() {
    	return root;
    }
    
    /**
     * Get the degree of this BTree.
     * 
     * @return The degree of this BTree (t)
     */
    public int getDegree() {
    	return DEGREE;
    }
    
    /**
     * Get the frequency of this BTree.
     * 
     * @return The frequency of this BTree (k)
     */
    public short getFrequency() {
    	return FREQUENCY;
    }
    
    /**
     * Get the number of nodes in this BTree.
     * 
     * @return The number of nodes in this BTree
     */
    public int getNumNodes() {
        return numNodes;
    }
    
    /**
     * Get the height of this BTree.
     * 
     * @return Thre height of this BTree
     */
    public short getHeight() {
    	return height;
    }
    
    /**
     * Recursively get a String in dump format of this BTree.
     * <p>
     * FORMAT (for each key) - key : frequency | "agcctgc : 18"
     * 
     * @return String in dump format
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public String dump() throws IOException {
    	return dump(root);
    }
    
    /**
     * Recursively get a String in dump format of the subtree that starts
     * at the given root.
     * <p>
     * FORMAT (for each key) - key : frequency | "agcctgc : 18"
     * 
     * @param rootAddress The address of the root of this subtree
     * 
     * @return String in dump format
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public String dump(long rootAddress) throws IOException {
    	//base case, the given root is non-existent
    	if(rootAddress == -1) {
    		return "";
    	}
    	
    	//return string and read this BNode
    	StringBuilder ret = new StringBuilder();
    	BNode root = BReadWrite.readBNode(rootAddress);
    	
    	//recursively construct the string
    	ret.append(dump(root.getChild(0)));
    	for(int i = 0; i < root.getN(); i++) {
    		ret.append(root.getKey(i).toString() + "\n");
    		ret.append(dump(root.getChild(i + 1)));
    	}
    	
    	return ret.toString();
    }
    
   
  	//=================================================================================================================
	//                                           STATIC METHODS
	//================================================================================================================= 
    
    
    /**
     * Recursively check that the BTree related to the given root is
     * sorted.
     * 
     * @param root  The address of the root of the BTree to check
     * 
     * @return True if this BTree is sorted, false otherwise
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    static public boolean isSorted(long root) throws IOException {
    	return isSorted(root, null, null);
    }
    
    /**
     * Recursively check that the BTree related to the given root is
     * sorted.
     * 
     * @param root  The address of the root of the BTree to check
     * @param left  The key left of the root  (null if none)
     * @param right The key right of the root (null if none)
     * 
     * @return True if this BTree is sorted, false otherwise
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
//    static public boolean isSorted(long root, TreeObject left, TreeObject right) throws IOException {
//    	//base case, the given root is non-existent
//    	if(root == -1) {
//    		return true;
//    	}
//    	
//    	//read the root from the RAF
//    	BNode current = BReadWrite.readBNode(root);
//    	LinkedList<Long> children = current.getChildren();
//    	LinkedList<TreeObject> keys = current.getKeys();
//    	
//    	//sorted will become false if any key is out of order
//    	//recursively check the first child of this root
//    	boolean sorted = isSorted(children.get(0), null, keys.get(0));
//    	
//    	//recursively check middle children and check that all keys are in sorted order
//    	for(int i = 1; i < current.getN() - 1; i++) {
//    		sorted = sorted && isSorted(children.get(i), keys.get(i - 1), keys.get(i)) &&
//    		                   keys.get(i - 1).compare(keys.get(i)) < 0;
//    	}
//    	
//    	//recursively check the last child and check that the first key is greater than left and the last key is less than right
//    	return sorted &&
//    	       isSorted(children.get(children.size() - 1), keys.get(keys.size() - 1), null) &&
//    	       ((right == null || (keys.get(keys.size() - 1).compare(right) <= 0)) &&
//    	       ( left == null  || (keys.get(0)              .compare(left)  >= 0)));
//    }
    static public boolean isSorted(long root, TreeObject left, TreeObject right) throws IOException {
    	//base case, the given root is non-existent
    	if(root == -1) {
    		return true;
    	}
    	
    	//read the root from the RAF
    	BNode current = BReadWrite.readBNode(root);
    	
    	//sorted will become false if any key is out of order
    	//recursively check the first child of this root
    	boolean sorted = isSorted(current.getChild(0), null, current.getKey(0));
    	
    	//recursively check middle children and check that all keys are in sorted order
    	for(int i = 1; i < current.getN() - 1; i++) {
    		sorted = sorted && isSorted(current.getChild(i), current.getKey(i - 1), current.getKey(i)) &&
    		                   current.getKey(i - 1).compare(current.getKey(i)) < 0;
    	}
    	
    	//recursively check the last child and check that the first key is greater than left and the last key is less than right
    	return sorted &&
    	       isSorted(current.getChild(current.getN()), current.getKey(current.getN() - 1), null) &&
    	       ((right == null || (current.getKey(current.getN() - 1).compare(right) <= 0)) &&
    	       ( left == null  || (current.getKey(0)                 .compare(left)  >= 0)));
    }
    
    /**
	 * Get the max size in bytes a BTree written to disk could
	 * be. This will be the degree (4), frequency (2), root address
	 * (8), height (2), and number of nodes (4) summed together.
     * 
     * @return Max disk size of a BTree's metadata
     */
    static public int getDiskSize() {
    	return Integer.BYTES + Short.BYTES + Long.BYTES + Integer.BYTES + Short.BYTES;
    }
    
    
    /**
     * 
     * 
     * @author  Mesa Greear
     * @version Spring 2022
     */
    private class BCache{
    	
    	private int size;
    	private LinkedList<BNode> nodes; //ArrayList?
    	
    	/**
    	 * Constructor: 
    	 * 
    	 * @param size
    	 */
    	BCache(int size){
    		this.size = size;
    		nodes = new LinkedList<BNode>();
    	}
    	
    	/**
    	 * Get the BNode in the cache with the same address as the
    	 * given address.
    	 * 
    	 * @param address The BNode location in the RAF
    	 * 
    	 * @return BNode with same address if it's in the Cache, null
    	 *         otherwise.
    	 */
		public BNode searchBNode(long address){
    		//find BNode via loop
    		for(int i = 0; i < nodes.size(); i++) {
    			if(nodes.get(i).getAddress() == address) {
    				//send BNode to front if found and return ===================================================
    				return nodes.get(i);
    			}
    		}
    		
    		
    		
    		//if not in cache read BNode from RAF, send to front, and return that BNode==========================
    		return null;
    	}
    }
}
