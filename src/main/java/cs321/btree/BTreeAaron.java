package cs321.btree;


/**
 * Used to create BTree objects that hold Generic Type objects.
 * Notable method is insert() which is important in structuring the
 * BTree. Aaron's version, using Carter's code as baseline.
 *
 * @author  Aaron Goin, Mesa Greear, Carter Gibbs
 * @version Spring 2022
 *
 */
public class BTreeAaron {
    private TestBNode root;
    private final int degree;
    private int numNodes;

    /**
     * Create an initialized BTree with null root.
     *
     * @param degree the degree of the BTree (minimum number of children for internal nodes other than the root)
     */
    public BTreeAaron(int degree) {
        root = new TestBNode(null);
        numNodes = 0;
        this.degree = degree;
    }

    /**
     * Inserts an element into the list. sections are labeled BInsert and
     * BInsertNotFull to show which sections somewhat match the pseudo code.
     *
     * @param element Generic to insert into BTree
     */
    public void insert(String element) {
        TreeObjectNoE k = new TreeObject(element, 1);
        TestBNodeNoE r = root;// step 1 of pseudo code

        //====BInsert==================
        //if root is full, split it and reassign to the new root
        if(r.isFull(degree)) {//step 2
            TestBNode s = null; // part of step 3, need to allocate
            root = s; // step 4, step 5 and 6 may already be covered
            s.insert(r.rootKey(), r);// step 7?
            s = r.split();//step 8 //TODO: write to disk could be dealt in BNode or in BTree
            insertNonFull(s, k);
            //increment numNodes
            numNodes++;
        }
        else  {
            insertNonFull(r, k);
        }

    }

    /**
     * Inserts key into nonfull node
     *
     * @param x the node that isn't full
     * @param key the key that is being inserted
     */
    private void insertNonFull(TestBNode x, TreeObject key) {
        //starting at root, search down BTree to find correct leaf node to insert into
        int i = x.getN();//step 1
        while(!x.isLeaf()) {
            x = x.getSubtree(key);

            //if the current node is full, split it and assign currentNode to parent
            if(x.isFull(degree)) {
                x = x.split(); //TODO: write to disk could be dealt in BNode or in BTree
            }
        }
        //once at leaf node, insert key
        x.insert(key);
    }

    public TestBNode getRoot() {
        return root;
    }

    public int getNumNodes() {
        return numNodes;
    }

//    public String toString() {
//
//    }
}

