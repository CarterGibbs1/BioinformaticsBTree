package cs321.btree;

import java.io.IOException;

/**
 * Used to create BTree objects that hold Generic Type objects.
 * Notable method is insert() which is important in structuring the
 * BTree.
 *
 * @author  Carter Gibbs, Mesa Greear
 * @version Spring 2022
 *
 * @param <E> Generic Type for this BNode to hold
 */
public class BTree<E>
{

    private long root;
    private int numNodes;
    
    private final int FREQUENCY;
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
    public BTree(int degree, int k, int numNodes, long root) {
    	//instantiate variables
    	DEGREE = degree;
    	FREQUENCY = k;
    	
    	this.numNodes = numNodes;
    	this.root = root;
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
    public BTree(int degree, int k, BNode<E> initialNode) throws IOException {
    	this(degree, k, 1, getDiskSize()); //FIXME: should write new root to just after BTree Metadata, but idk
    	BReadWrite.writeBNode(initialNode);
    }
    
    
   	//=================================================================================================================
	//                                         BTREE FUNCTIONALITY METHODS
	//=================================================================================================================
    
    

//    public void insert(TreeObject<E> toInsert) {
//        BNode<E> r = root;
//        if (r.getN() == 2 * degree - 1) {
//            BNode<E> newNode = new BNode<E>(degree,null, null, r, null);
//            root = newNode;
//            root.split();
//            root.insert(toInsert, newNode);
//            numNodes++;
//        } else {
//            root.insert(toInsert, r);
//        }
//    }

    
   	//=================================================================================================================
	//                                           GET/SET/UTILITY METHODS
	//=================================================================================================================
    
    
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
    public int getFrequency() {
    	return FREQUENCY;
    }
    
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
    public int getNumNodes() {
        return numNodes;
    }
    
   
  	//=================================================================================================================
	//                                           STATIC METHODS
	//================================================================================================================= 
    
    /**
	 * Get the max size in bytes a BTree written to disk could
	 * be. This will be the degree (4), frequency (4), root address
	 * (8), and number of nodes (4) summed together.
     * 
     * @return Max disk size of a BTree's metadata
     */
    static public int getDiskSize() {
    	return Integer.BYTES + Integer.BYTES + Long.BYTES + Integer.BYTES;
    }
}
