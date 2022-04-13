package cs321.btree;

/**
 * Used to create BTree objects that hold Generic Type objects.
 * Notable method is insert() which is important in structuring the
 * BTree. Aaron's version, using Carter's code as baseline.
 *
 * @author  Aaron Goin
 * @version Spring 2022
 *
 * @param <E> Generic Type for this BNode to hold
 */
public class BTreeAaron<E> {
    private BNode<E> root;
    private TestBNode<E> testRoot;
    private final int degree;
    private int numNodes;

    /**
     * Create an initialized BTree with null root.
     *
     * @param degree the degree of the BTree (minimum number of children for internal nodes other than the root)
     */
    public BTreeAaron(int degree) {
        root = new BNode<E>(null);
        numNodes = 0;
        this.degree = degree;
    }

    /**
     * Inserts a new TreeObject into the tree at the appropriate position for a BTree.
     *
     * @param toInsert the TreeObject that will be inserted
     */
    public void insert(TreeObject<E> toInsert) {
        BNode<E> r = root;
        if (r.getN() == 2 * degree - 1) {
            BNode<E> newNode = new BNode<E>(degree,null, null, r, null);
            root = newNode;
            root.split();
            root.insert(toInsert, newNode);
            numNodes++;
        } else {
            root.insert(toInsert, r);
        }
    }
    
    /**
     * Inserts an element into the list. sections are labeled BInsert and
     * BInsertNotFull to show which sections somewhat match the pseudo code.
     * 
     * @param element Generic to insert into BTree
     */
    public void insert(E element) {
    	TreeObject<E> key = new TreeObject<E>(element, 1);
    	TestBNode<E> r = testRoot;
    	
    	//====BInsert==================
    	//if root is full, split it and reassign to the new root
    	if(r.isFull(degree)) {
    		testRoot = r.split(); //TODO: write to disk could be dealt in BNode or in BTree
    	}
    	
    	//====BInsertNotFull==========
    	//starting at root, search down BTree to find correct leaf node to insert into
    	TestBNode<E> currentNode = testRoot;
    	while(!currentNode.isLeaf()) {
    		currentNode = currentNode.getSubtree(key);
    		
    		//if the current node is full, split it and assign currentNode to parent
    		if(currentNode.isFull(degree)) {
    			currentNode = currentNode.split(); //TODO: write to disk could be dealt in BNode or in BTree
    		}
    	}
    	
    	//once at leaf node, insert key
    	currentNode.insert(key);
    }
}
