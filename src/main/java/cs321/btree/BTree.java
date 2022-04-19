package cs321.btree;

import java.io.IOException;
import java.nio.BufferUnderflowException;

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
    	
    	//set statics
    	BNode.setDegree(degree);
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
    
    /**
     * 
     * 
     * @param toInsert
     * 
     * @throws IOException
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
			nextNode = currentNode.getSubtree(toInsert);
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

		// once at leaf, insert key
		currentNode.insert(toInsert);
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
