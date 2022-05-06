

import java.io.IOException;
import java.util.ArrayList;

/**
 * Used to create BTree objects that hold String representations of objects.
 * Used in conjunction with BReadWrite to write massive amounts of data to
 * Random Access Files.
 *
 * @author  Carter Gibbs, Mesa Greear, Aaron Goin
 * @version Spring 2022
 */
public class BTree
{

    private BNode root;
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
     * @param cacheSize The max BNodes the cache can hold, <= 0 for no
     *                  cache
     *                  
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public BTree(long root, int degree, int frequency, int numNodes, int height, int cacheSize) throws IOException {
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
    	this.root = BReadWrite.getRAFSize() <= BTree.getDiskSize() ? null : cacheCheck(root);
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
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public BTree(long root, int degree, int frequency, int numNodes, int height) throws IOException {
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
     * @param cacheSize     The max BNodes the cache can hold, <= 0 for no
     *                      cache
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public BTree(TreeObject initialObject, int degree, int frequency, int cacheSize) throws IOException {
    	this(BTree.getDiskSize(), degree, frequency, 1, 0, cacheSize);
    	
    	//write new BTree and root BNode to RAF
    	root = new BNode(initialObject, BTree.getDiskSize());
    	BReadWrite.writeBTree(this);
    	BReadWrite.writeBNode(root);
    }
    
    /**
     * Constructor: Create a new BTree with one initial object and BNode.
     * Does not use a Cache. Written to file after instantiation.
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
    	
    	//write new BTree and root BNode to RAF
    	root = new BNode(initialObject, BTree.getDiskSize());
    	BReadWrite.writeBTree(this);
    	BReadWrite.writeBNode(root);
    }
    
    /**
     * Constructor: Create a new BTree with one empty BNode. Written to
     * file after instantiation.
     * 
	 * @param degree        (t) How many keys/objects (t - 1 to 2t - 1)
	 *                      and children (t to 2t) BNodes can have
     * @param frequency     (k) How many letters are stored per key (< 32)
     * @param cacheSize     The max BNodes the cache can hold, <= 0 for no
     *                      cache
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public BTree(int degree, int frequency, int cacheSize) throws IOException {
    	this(BTree.getDiskSize(), degree, frequency, 1, 0, cacheSize);
    	
    	//write new BTree and root BNode to RAF
    	root = new BNode(BTree.getDiskSize());
    	BReadWrite.writeBTree(this);
    	BReadWrite.writeBNode(root);
    }
    
    /**
     * Constructor: Create a new BTree with one empty BNode. Does not use
     * a Cache. Written to file after instantiation.
     * 
	 * @param degree        (t) How many keys/objects (t - 1 to 2t - 1)
	 *                      and children (t to 2t) BNodes can have
     * @param frequency     (k) How many letters are stored per key (< 32)
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public BTree(int degree, int frequency) throws IOException {
    	this(BTree.getDiskSize(), degree, frequency, 1, 0);
    	
    	//write new BTree and root BNode to RAF
    	root = new BNode(BTree.getDiskSize());
    	BReadWrite.writeBTree(this);
    	BReadWrite.writeBNode(root);
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
		// if root is full, split it
		if (root.isFull()) {
			root = split(null, root);
			numNodes += 2;
			height++;
		}
		
		BNode currentNode = root;
		BNode nextNode = null;

		// get to appropriate leaf BNode
		while (!currentNode.isLeaf()) {
		
			// if the object to insert is in currentNode, break to the end
			if (currentNode.incrementElement(toInsert)) {
				cacheCheck(currentNode);
				return;
			}
			
			//get correct child to go into
			nextNode = cacheCheck(currentNode.getElementLocation(toInsert));
		
			// if the nextNode is full, split it and begin again at parent
			if (nextNode.isFull()) {
				currentNode = split(currentNode, nextNode);
				numNodes++;
			}
			//else move on to the child
			else {
				currentNode = nextNode;
			}
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
		BNode currentNode = root;
		BNode nextNode;
		

		// go until at leaf node
		while (!currentNode.isLeaf()) {
			
			// if the object to find is in currentNode, find it's location and return it's frequency
			nextNode = cacheCheck(currentNode.getElementLocation(toFind));
			if (currentNode.equals(nextNode)) {
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
	 * Split the given node into two or three if it's a root. Adds
	 * the new BNodes to the given BTree's BCache if it has one.
	 * 
	 * @param parent The parent of the BNode you're splitting, null if
	 *               this is the root
	 * @param child  The BNode being split
	 * 
	 * @return The parent BNode
	 * 
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	public BNode split(BNode parent, BNode child) throws IOException {
		//get useful child data
		int n = child.getN();
		TreeObject[] keys = child.getKeys();
		long[] children = child.getChildren();
		
		//create the new node that'll be to the right of this node
		BNode splitRight = new BNode(keys[n/2 + 1], getNextAddress(), parent != null ? parent.getAddress() : 0, children[(n + 1)/2], children[(n + 1)/2 + 1]);
		n--;
		
		//move keys/children over to new right node
		for(int i = keys.length/2 + 1; i < keys.length - 1; i++) {
			splitRight.insert(keys[i + 1], children[i + 2]);
			n--;
		}

		//if the child is the ROOT, create new parent/root to insert into
		//else just insert into the parent
		if(child.isRoot()) {
			//                               Already new node 'splitRight' at getNextAddress, so
			//                               have to compensate with additional offset getDiskSize
			parent = new BNode(keys[n - 1], getNextAddress() + BNode.getDiskSize(), 0, child.getAddress(), splitRight.getAddress());
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
		
		return parent;
	}

    
   	//=================================================================================================================
	//                                           GET/SET/UTILITY METHODS
	//=================================================================================================================
    
    /**
     * Get the BNode root of this BTree.
     * 
     * @return The root of this BTree
     */
    public BNode getRoot() {
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
     * Get the next available spot in the RAF to write a BNode to.
     * 
     * @return Address in RAF open to being written to
     */
    public long getNextAddress() {
    	return (numNodes * BNode.getDiskSize()) + BTree.getDiskSize();
    }
    
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
    public boolean isSorted() throws IOException {
    	return isSorted(root.getAddress(), null, null);
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
    public boolean isSorted(long root, TreeObject left, TreeObject right) throws IOException {
    	//base case, the given root is non-existent
    	if(root == 0) {
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
     * Recursively fill the given list with all the BNodes in this BTree.
     * <p>
     * DEBUGGING: This method is for debugging and could drastically slow
     * down a program if used casually.
     * 
     * @param list The list to add BNodes to
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public void getAllBNodes(ArrayList<BNode> list) throws IOException {
    	getAllBNodes(list, root.getAddress());
    }
    
    /**
     * Recursively fill the given list with all the BNodes in this subtree.
     * <p>
     * DEBUGGING: This method is for debugging and could drastically slow
     * down a program if used casually.
     * 
     * @param list        The list to add BNodes to
     * @param rootAddress The root of this subtree
     * 
	 * @throws IOException Reading/Writing to RAF may throw exception
     */
    public void getAllBNodes(ArrayList<BNode> list, long rootAddress) throws IOException{
    	//base case, the given root is non-existent
    	if(rootAddress <= 0) {
    		return;
    	}
    	
    	//return string and read this BNode
    	BNode subRoot = BReadWrite.readBNode(rootAddress);
    	list.add(subRoot);
    	
    	//recursively construct the string
    	getAllBNodes(list, subRoot.getChild(0));
    	for(int i = 0; i < subRoot.getN(); i++) {
    		getAllBNodes(list, subRoot.getChild(i + 1));
    	}
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
    	return dump(root.getAddress());
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
    	if(rootAddress == 0) {
    		return "";
    	}
    	
    	//return string and read this BNode
    	StringBuilder ret = new StringBuilder();
    	BNode root = cacheCheck(rootAddress);
    	
    	//recursively construct the string
    	ret.append(dump(root.getChild(0)));
    	for(int i = 0; i < root.getN(); i++) {
    		ret.append(root.getKey(i).toString() + "\n"); //for some reason "\r\n" are sometimes needed to match example dumps
    		ret.append(dump(root.getChild(i + 1)));
    	}
    	
    	return ret.toString();
    }
    
   
  	//=================================================================================================================
	//                                           STATIC METHODS
	//================================================================================================================= 
    
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
     * Get the max size of this BTree's BCache.
     * 
     * @return Max size of BCache, -1 if it doesn't exist
     */
    public int getCacheSize() {
    	if(CACHE != null) {
    		return CACHE.getMaxSize();
    	}
    	return -1;
    }
    
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
	 * @param node   BNode to add to BCache or write to RAF
	 *
	 * @throws IOException Reading/Writing to RAF may throw exception
	 */
	private void cacheCheck(BNode node) throws IOException {
		if (CACHE == null) {
			BReadWrite.writeBNode(node);
		} else {
			CACHE.searchBNode(node.getAddress(), node);
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
    	private ArrayList<BNode> nodes;
    	
		/**
		 * Constructor: Instantiate a cache that can hold a max of size number of
		 * BNodes.
		 *
		 * @param size Max size of the cache, to be stored in a constant
		 */
    	BCache(int size){
    		SIZE = size;
    		nodes = new ArrayList<BNode>();
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
    		return searchBNode(address, null);
    	}
    	
		/**
		 * Get the BNode in the cache with the same address as the given address. If
		 * it's not in the cache it is read from the RAF, added to the cache and
		 * returned.
		 *
		 * @param address The BNode location in the RAF (-1 for ignore)
		 * @param node    BNode to add to cache (null for ignore)
		 *
		 * @return BNode with same address
		 * 
		 * @throws IOException Reading/Writing to RAF may throw exception
		 */
		public BNode searchBNode(long address, BNode node) throws IOException{
			// find BNode via loop
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i).getAddress() == address) {
					// send BNode to front if found and return
					nodes.add(0, nodes.remove(i));
					return nodes.get(0);
				}
			}

			// check if the cache is too full, if it is remove last node and write it
			if (nodes.size() >= SIZE) {
				BReadWrite.writeBNode(nodes.remove(SIZE - 1));
			}
			
			// if the given node is not null and not in cache, then add to front of cache
			if (node != null) {
				nodes.add(0, node);
			}
			//else read BNode and add to front of cache if not found
			else {
				nodes.add(0, BReadWrite.readBNode(address));
			}
			return nodes.get(0);
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
