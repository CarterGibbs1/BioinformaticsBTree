package cs321.btree;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Used to create BTree objects that hold Generic Type objects.
 * Notable method is insert() which is important in structuring the
 * BTree. TODO:
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

    
   	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
    
    
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
    	//instantiate variables
    	DEGREE = degree;
    	FREQUENCY = (short)frequency;
    	
    	this.numNodes = numNodes;
    	this.root = root;
    	this.height = (short)height;
    	
    	//set statics
    	BNode.setDegree(degree);
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
		long nextNode;
		

		// if currentNode(root) is full, split it
		if (currentNode.isFull()) {
			root = currentNode.split();
			currentNode = BReadWrite.readBNode(root);
			numNodes += 2;
			height++;
		}

		// get to appropriate leaf BNode
		while (!currentNode.isLeaf()) {
			
			// if the object to insert is in currentNode, exit
			nextNode = currentNode.getElementLocation(toInsert);
			if (nextNode == currentNode.getAddress()) {
				return;
			}
			//else read the child
			currentNode = BReadWrite.readBNode(nextNode);

			// if the currentNode is full, split it and start again at parent
			if (currentNode.isFull()) {
				currentNode = BReadWrite.readBNode(currentNode.split());
				numNodes++;
			}
		}

		// once at leaf, insert key if it's not in the lead already
		if(currentNode.getElementLocation(toInsert) != currentNode.getAddress()) {
			currentNode.insert(toInsert);
		}
    }
    
    public long search(Object x) {
    	return -1;//TODO: temp method
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
    static public boolean isSorted(long root, TreeObject left, TreeObject right) throws IOException {
    	//base case, the given root is non-existent
    	if(root == -1) {
    		return true;
    	}
    	
    	//read the root from the RAF
    	BNode current = BReadWrite.readBNode(root);
    	LinkedList<Long> children = current.getChildren();
    	LinkedList<TreeObject> keys = current.getKeys();
    	
    	//sorted will become false if any key is out of order
    	//recursively check the first child of this root
    	boolean sorted = isSorted(children.get(0), null, keys.get(0));
    	
    	//recursively check middle children and check that all keys are in sorted order
    	for(int i = 1; i < current.getN() - 1; i++) {
    		sorted = sorted && isSorted(children.get(i), keys.get(i - 1), keys.get(i)) &&
    		                   keys.get(i - 1).compare(keys.get(i)) < 0;
    	}
    	
    	//recursively check the last child and check that the first key is greater than left and the last key is less than right
    	return sorted &&
    	       isSorted(children.get(children.size() - 1), keys.get(keys.size() - 1), null) &&
    	       ((right == null || (keys.get(keys.size() - 1).compare(right) <= 0)) &&
    	       ( left == null  || (keys.get(0)              .compare(left)  >= 0)));
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
}
