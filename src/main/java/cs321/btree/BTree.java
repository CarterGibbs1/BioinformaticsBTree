package cs321.btree;

import java.io.IOException;

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

    private long root;
    private int numNodes;
    private short height;
    
    private final short FREQUENCY;
    private final int DEGREE;

    
   	//=================================================================================================================
	//                                               CONSTRUCTORS
	//=================================================================================================================
    
    
    /**
     * Constructor for read BTree...
     * 
     * @param degree
     * @param k
     * @param numNodes
     * @param root
     */
    public BTree(int degree, int k, int numNodes, long root, int height) {
    	//instantiate variables
    	DEGREE = degree;
    	FREQUENCY = (short)k;
    	
    	this.numNodes = numNodes;
    	this.root = root;
    	this.height = (short)height;
    }
    
    /**
     * Creates new BTree...
     * 
     * @param degree
     * @param k
     * @param initialNode
     * 
     * @throws IOException
     */
    public BTree(int degree, int k, BNode initialNode) throws IOException {
    	this(degree, k, 1, getDiskSize(), 0);
    	
    	//write new BTree and initialNode to RAF
    	BReadWrite.writeBTree(this);
    	BReadWrite.writeBNode(initialNode);
    }
    
    
   	//=================================================================================================================
	//                                         BTREE FUNCTIONALITY METHODS
	//=================================================================================================================
    
    

    public void insert(TreeObject toInsert) {
    }
    
    public long search(Object x) {
    	return -1;//TODO: temp method
    }

    
   	//=================================================================================================================
	//                                           GET/SET/UTILITY METHODS
	//=================================================================================================================
    
    /**
     * 
     * @return
     */
    public long getRoot() {
    	return root;
    }
    
    /**
     * 
     * @return
     */
    public int getDegree() {
    	return DEGREE;
    }
    
    /**
     * 
     * @return
     */
    public short getFrequency() {
    	return FREQUENCY;
    }
    
    /**
     * 
     * @return
     */
    public int getNumNodes() {
        return numNodes;
    }
    
    /**
     * 
     * @return
     */
    public short getHeight() {
    	return height;
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
}
