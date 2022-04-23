package cs321.btree;

import java.io.IOException;
import java.util.ArrayList;
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
    
    /**
     * Constructor: Instantiate BTree that has a root BNode that starts at
     * the given address.
     * 
     * @param root      Address of the root of this BTree in the RAF
	 * @param degree    (t) How many keys/objects (t - 1 to 2t - 1)
	 *                  and children (t to 2t) BNodes can have
     * @param frequency (k) How many letters are stored per key (< 32)
     * @param numNodes  How many BNodes are in this BTree
     * @param height    The height of the BTree (0, 1, 2, 3, ....)
     * @param cacheSize The max BNodes the cache can hold, < 0 for no
     *                  cache
     */
    public BTree(long root, int degree, int frequency, int numNodes, int height, int cacheSize) {
    	//instantiate variables
    	DEGREE = degree;
    	FREQUENCY = (short)frequency;
    	
    	if(cacheSize > 0) {
    		CACHE = new BCache(cacheSize);
    	}
    	else {
    		CACHE = null;
    	}
    	
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
     * @param cacheSize     The max BNodes the cache can hold, < 0 for no
     *                      cache
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public BTree(TreeObject initialObject, int degree, int frequency, int cacheSize) throws IOException {
    	this(BTree.getDiskSize(), degree, frequency, 1, 0, cacheSize);
    	
    	//write new BTree and BNode to RAF
    	BReadWrite.writeBTree(this);
    	BReadWrite.writeBNode(new BNode(initialObject, getRoot()));
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
		BNode currentNode = cacheCheck(root);
		BNode nextNode = null;
		

		// if currentNode(root) is full, split it
		if (currentNode.isFull()) {
			root = split(null, currentNode);
			currentNode = cacheCheck(root);
			numNodes += 2;
			height++;
		}

		// get to appropriate leaf BNode
		while (!currentNode.isLeaf()) {
			
			//get correct child to go into
			nextNode = cacheCheck(currentNode.getElementLocation(toInsert));
			
			// if the object to insert is in currentNode, break to the end
			if (nextNode.getAddress() == currentNode.getAddress()) {
				break;
			}

			// if the nextNode is full, split it
			if (nextNode.isFull()) {
				split(currentNode, nextNode);
				numNodes++;
				nextNode = cacheCheck(currentNode.getElementLocation(toInsert));
			}
			
			//move on to the child
			currentNode = nextNode;
		}
		
		// once at leaf, insert key if it's not in the BNode already
		if(!currentNode.incrementElement(toInsert)) {
			currentNode.insert(toInsert);
		}
		cacheCheck(currentNode);
		
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
		BNode currentNode = cacheCheck(root);
		BNode nextNode;
		

		// go until at leaf node
		while (!currentNode.isLeaf()) {
			
			// if the object to find is in currentNode, find it's location and return it's frequency
			nextNode = cacheCheck(currentNode.getElementLocation(toFind));
			if (nextNode.getAddress() == currentNode.getAddress()) {
				return currentNode.getKey(currentNode.indexOf(toFind)).getFrequency();
			}
			//else read the child
			currentNode = nextNode;
		}

		//check leaf node
		if(currentNode.indexOf(toFind) != -1) {
			return currentNode.getKey(currentNode.indexOf(toFind)).getFrequency();
		}
		return 0;
    }
    
    
	/**
	 * Split the given node into two, or three if it's a root. Adds
	 * the new BNodes to the given BTree's BCache.
	 * <p>
	 * NO WRITE: This method does not write the changed BNodes to the RAF
	 * 
	 * @param parent The parent of the BNode you're splitting, null if
	 *               this is the root
	 * @param child....
	 * 
	 * @return The address of the parent BNode
	 * 
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	public long split(BNode parent, BNode child) throws IOException {
		
		//get useful child data
		int n = child.getN();
		TreeObject[] keys = child.getKeys();
		long[] children = child.getChildren();
		
		//create the new node that'll be to the right of this node
		BNode splitRight = new BNode(keys[n/2 + 1], BReadWrite.getNextAddress(), parent != null ? parent.getAddress() : -1, children[(n + 1)/2], children[(n + 1)/2 + 1]);
		n--;
		
		//move keys/children over to new right node
		for(int i = keys.length/2 + 1; i < keys.length - 1; i++) {
			splitRight.insert(keys[i + 1], children[i + 2]);
			n--;
		}
		
		if(!child.isLeaf()) {
			n = n;
		}

		//if the child is the ROOT, create new parent/root to insert into
		//else just insert into the parent
		if(child.isRoot()) {
			//                               Already new node 'splitRight' at getNextAddress, so
			//                               have to compensate with additional offset getDiskSize
			parent = new BNode(keys[n - 1], BReadWrite.getNextAddress() + BNode.getDiskSize(), -1, child.getAddress(), splitRight.getAddress());
			child.setParent(parent.getAddress());
			splitRight.setParent(parent.getAddress());
		}
		else {
			parent.insert(keys[n - 1], splitRight.getAddress());
		}
		n--;
		child.setN(n);
		
		cacheCheck(splitRight);
		cacheCheck(child);
		cacheCheck(parent);
		
		return parent.getAddress();
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
     * @return The height of this BTree
     */
    public short getHeight() {
    	return height;
    }
    
    /**
     * Recursively get a String in In-Order-Traversal format of this
     * BTree.
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
     * Recursively get a String in In-Order-Traversal format of the
     * subtree that starts at the given root.
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
    
    
  	//=================================================================================================================
	//                                                BCACHE FUNCTIONALITY
	//================================================================================================================= 
    
    /**
     * Empty the BCache and write all the stored BNodes to the RAF.
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public void emptyBCache() throws IOException {
    	if(CACHE != null) {
    		CACHE.emptyCache();
    	}
    }
    
    /**
	 * Check the cache for the given BNode address and return the BNode with the
	 * correlating address either from the BCache or from the RAF.
	 * <p>
	 * If there is no BCache, read BNode from RAF always.
	 *
	 * @param address BNode address to search for
	 *
	 * @return Return BNode with given address, may have been read from RAF or
	 *         returned from cache
	 *
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	private BNode cacheCheck(long address) throws IOException {
		if (CACHE == null) {
			return BReadWrite.readBNode(address);
		}
		return CACHE.searchBNode(address);
	}

	/**
	 * Check that a cache exists and if it does add the given BNode to it.
	 * <p>
	 * If there is no BCache, always write the BNode to RAF.
	 *
	 * @param node BNode to add to BCache or write to RAF
	 *
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	private void cacheCheck(BNode node) throws IOException {
		if (CACHE == null) {
			BReadWrite.writeBNode(node);
		} else {
			CACHE.searchBNode(node.getAddress());
		}
	}
    
    /**
     * Cache that holds BNodes. Useful in reducing read/write
     * time in a BTree.
     * 
     * @author  Mesa Greear, Carter Gibbs
     * @version Spring 2022
     */
    private class BCache{
    	
    	private final int SIZE;

		private LinkedList<BNode> nodes;//TODO: Which is more efficient
    	//private ArrayList<BNode> nodes;
    	
		/**
		 * Constructor: Instantiate a cache that can hold a max of size number of
		 * BNodes.
		 *
		 * @param size Max size of the cache, to be stored in a constant
		 */
    	BCache(int size){
    		SIZE = size;
    		//nodes = new ArrayList<BNode>();
			nodes = new LinkedList<BNode>();
    	}
    	
		/**
		 * Get the BNode in the cache with the same address as the given address. If
		 * it's not in the cache it is read from the RAF, added to the cache and
		 * returned.
		 *
		 * @param address The BNode location in the RAF
		 *
		 * @return BNode with same address
		 * 
		 * @throws IOException Reading/Writing to RAF may throw exception
		 */
		public BNode searchBNode(long address) throws IOException{
    		//find BNode via loop
			BNode retNode;
			for(int i = 0; i < nodes.size(); i++) {
    			if (nodes.get(i).getAddress() == address) {
    				//send BNode to front if found and return
					retNode = nodes.remove(i);
					nodes.addFirst(retNode);
    				return retNode;
    			}
    		}
			//check if the cache is too full, if it is remove last node
			if (nodes.size() == SIZE) {
				nodes.removeLast();
			}
			//read BNode and add to front of cache
			retNode = BReadWrite.readBNode(address);
			nodes.addFirst(retNode);
    		
    		return retNode;
    	}
		
		/**
		 * Remove all nodes from cache and write them to the RAF.
		 *
		 * @throws IOException Reading/Writing to RAF may throw exception
		 */
		public void emptyCache() throws IOException {
			while (!nodes.isEmpty()) {
				BReadWrite.writeBNode(nodes.remove(0));
			}
		}

		/**
		 * Get the max size of this BCache.
		 * 
		 * @return Max size of this BCache
		 */
		public int getMaxSize() {
			return SIZE;
		}
    }
}
