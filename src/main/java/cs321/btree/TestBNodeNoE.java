package cs321.btree;


/**
 * Used to create TestBNodeNoE objects that hold Generic Type (passed down by
 * the BTree they belong to) objects. Notable methods are insert(),
 * getSubtree(), and split() which are important in creating a functional
 * BTree.
 *
 * @author  Mesa Greear
 * @version Spring 2022
 *
 */
public class TestBNodeNoE {

    //TODO: Add cache functionality

    //child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
    private TreeObjectNoE[] keys; //objects/keys in this node, also size() = n
    private long[] children;      //allocated addresses for each child

    private long parent;//parent address

    private long address;//not written to RAF

    private static int degree;//shared degree amongst all BNodes

    private boolean isLeaf;//needed to implement the algorithms

    private int n;// it's needed to implement the algorithms

    //=================================================================================================================
    //                                               CONSTRUCTORS
    //=================================================================================================================

    /**
     * Constructor: Create TestBNodeNoE with one key 'initialKey,' a parent address
     * 'parent,' and two children 'leftChild' and 'rightChild.' An address that
     * is less than 0 is considered null.
     *
     * @param initialKey   the initial object in this TestBNodeNoE
     * @param thisAddress address of this TestBNodeNoE
     * @param parent      address of the parent of this TestBNodeNoE
     * @param leftChild   address of the child left of initialKey
     * @param rightChild  address of the child right of initialKey
     *
     * @throws IllegalStateException Static degree has not been set
     */
    /*
     * Example Demonstration:
     *
     *
     *      new TestBNodeNoE(a, |, /, \)
     *
     * result     -    |
     *                 a
     *                / \
     */
    public TestBNodeNoE(TreeObjectNoE initialKey, long thisAddress, long parent, long leftChild, long rightChild) throws IllegalStateException {
        setDegree(degree);
        //check that DEGREE has been set
        if(degree < 1) {
            throw new IllegalStateException("Degree is an invalid value. It might have not been set before BNodes are used.");
        }

        //initialize instance variables
        keys = new TreeObjectNoE[(2 * degree) + 1];
        children = new long[2 * degree + 2];
        children[0] = -2;// distinguish from other values
//        for (int i = 1; i < children.length; i++) {
//        	children[i] = -1;
//        }
        if (initialKey != null) {
            keys[1] = initialKey;
        }
        if (leftChild != -1) {
            children[1] = leftChild;
        }
        if (rightChild != -1) {
            children[2] = rightChild;
        }
        this.address = thisAddress;
        this.parent = -1;
        this.isLeaf = true;
        this.n = 0;
    }

    /**
     * Constructor: Create leaf TestBNodeNoE with one key 'initialKey' and
     * a parent address 'parent.' An address that is less than 0 is
     * considered null.
     *
     * @param initialKey  the initial object in this TestBNodeNoE
     * @param address address of this TestBNodeNoE
     * @param parent     address of the parent of this TestBNodeNoE
     *
     * @throws IllegalStateException Static degree has not been set
     */
    public TestBNodeNoE(TreeObjectNoE initialKey, long address, long parent) throws IllegalStateException {
        this(initialKey, address, parent, -1, -1);
    }

    /**
     * Constructor: Create singular TestBNodeNoE with one key initialKey.
     *
     * @param initialKey the initial object in this TestBNodeNoE
     *
     * @throws IllegalStateException Static degree has not been set
     */
    public TestBNodeNoE(TreeObjectNoE initialKey, long address) throws IllegalStateException {
        this(initialKey, address, -1, -1, -1);
    }

    public TestBNodeNoE(long address) {//for testing purposes, blank node
        this(null, address, -1, -1, -1);
    }

//    public TestBNodeNoE(long address, int degree) {//for testing purposes, blank node
//    	setDegree(degree);
//        keys = new TreeObjectNoE[2 * degree];
//        children = new long[2 * degree + 1];
//        this.address = address;
//        this.parent = -1;
//        this.isLeaf = true;
//        this.n = 0;
//    }

    //=================================================================================================================
    //                                         BTREE FUNCTIONALITY METHODS
    //=================================================================================================================

    /**
     * Insert the given key into this TestBNodeNoE and insert the given child
     * to the right of the inserted key.
     *
     * @param key   TreeObject containing Object to insert
     * @param child Child related to key to insert
     * @param write Whether to write to the RAF or not
     *
     * @throws IOException Writing to RAF may throw exception
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
//    public void insert(TreeObjectNoE key, long child, boolean write) /*throws IOException*/ {
//
//        //get to the index of the first k less than key
//        int i;
//        for(i = ( keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--){}
//
//        //add new key and child to lists
//        keys.add(i + 1, key);
//        children.add(i + 2, child);
//
//        if(write) {
//            //BReadWrite.writeBNode(this);
//        }
//    }

    /**
     * Insert the given key into this TestBNodeNoE. Should only be used on leaf
     * nodes.
     * <p>
     * WRITE: This method writes this changed TestBNodeNoE to the RAF
     *
     * @param key TreeObject containing Object to insert
     *
     * @throws IOException Writing to RAF may throw exception
     */
//    public void insert(TreeObjectNoE key) /*throws IOException*/ {
//        insert(key, -1, true);
//    }

    /**
     * Insert the given key into this TestBNodeNoE and insert the given child
     * <p>
     * NO WRITE: This method does not write the changed TestBNodeNoE to the RAF
     *
     * @param key   TreeObject containing Object to insert
     * @param child Child related to key to insert
     *
     * @throws IOException Writing to RAF may throw exception
     */
//    public void insertNoWrite(TreeObjectNoE key, long child) /*throws IOException*/ {
//        insert(key, child, false);
//    }

//    /**
//     * Get the child of this TestBNodeNoE where the given key should be
//     * inserted or would be located. Does NOT insert the key, only
//     * returns the subtree that it belongs to.
//     *
//     * @param  key Object to use to locate the appropriate subtree
//     *
//     * @return subtree address (child of this TestBNodeNoE) that key
//     *         belongs to
//     */
//    public long getSubtree(TreeObjectNoE key){
//
//        //get to the index of the first k less than key
//        int i;
//        for(i = ( keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--){}
//
//        return children.get(i + 1);
//    }

    /**
     * Splits the current TestBNodeNoE into two new BNodes, removing and
     * inserting the middle object and a pointer to the new right
     * TestBNodeNoE into the parent. The left TestBNodeNoE (this TestBNodeNoE) will
     * contain everything to the left of the removed object and
     * the right TestBNodeNoE (new TestBNodeNoE) will be contain everything to
     * the right. Can only be run when the list is full.
     * <p>
     * WRITE: This method writes this changed BNodes to the RAF
     *
     * @return the parent of the two BNodes
     *
     * @throws IOException Reading/Writing to RAF may throw exception
     */
//	public long split() throws IOException {
//		//==== for better understanding when coming back to look at this method, I'm going to ====
//		//==== create an example split and show how it changes through comments:              ====
//		//Keys     -  a b c d e f g
//		//Pointers - 0 1 2 3 4 5 6 7
//
//		int originalN = keys.size();
//		TestBNodeNoE parentNode; //TODO: parent can be kept by BTree and passed in for performance increase
//
//
//		//remove key and two pointers right of middle and insert into new TestBNodeNoE 'splitRight':
//		//Keys     -  a b c d f g    |  e
//		//Pointers - 0 1 2 3   6 7   | 4 5
//		TestBNodeNoE splitRight = new TestBNodeNoE(keys.remove(keys.size()/2 + 1), BReadWrite.getNextAddress(), parent, children.remove(children.size()/2), children.remove(children.size()/2 + 1));
//
//		//starting just to the right of the middle of this TestBNodeNoE, continuously remove the pointers and keys
//		//at that position and insert them into splitRight:
//		//Keys     -  a b c d  |  e f g
//		//Pointers - 0 1 2 3   | 4 5 6 7
//		while((keys.size() - 1) != originalN/2) {
//			splitRight.insertNoWrite(keys.remove(originalN/2 + 1), children.remove(originalN/2 + 1));
//		}
//		n = keys.size() - 1;
//
//		//lastly, remove last node (original middle) from this and insert into parent with
//		//a pointer to splitRight
//		//Parent   -    .. x x d x x ..
//		//                    / \
//		//Keys     -  a b c d  |  e f g
//		//Pointers - 0 1 2 3   | 4 5 6 7
//
//		//if this is the ROOT, create new parent/root to insert into
//		//else read the parent and insert into
//		if(isRoot()) {
//			//                                           Already new node 'splitRight' at getNextAddress, so
//			//                                           have to compensate with additional offset getDiskSize
//			parentNode = new TestBNodeNoE(keys.removeLast(), BReadWrite.getNextAddress() + TestBNodeNoE.getDiskSize(), -1, address, splitRight.getAddress());
//			this.parent = splitRight.parent = parentNode.getAddress();
//		}
//		else {
//			parentNode = BReadWrite.readBNode(parent); //TODO: see other parentNode todo, prevent read here
//			parentNode.insertNoWrite(keys.removeLast(), splitRight.getAddress());
//		}
//
//		//write changed BNodes to RAF
//		BReadWrite.writeBNode(splitRight);
//		BReadWrite.writeBNode(this);
//		BReadWrite.writeBNode(parentNode);
//
//
//		return parent;
//	}

    //=================================================================================================================
    //                                           GET/SET/UTILITY METHODS
    //=================================================================================================================

    /**
     * Get the number of objects (n) in this TestBNodeNoE represented by
     * keys.size().
     *
     * @return Number of objects in this TestBNodeNoE
     */
    public int getN() {
        return n;
    }

    public void setN(int newN) {
        this.n = newN;
    }

    /**
     * Get the address of this TestBNodeNoE in the RAF.
     *
     * @return address of this in RAF
     */
    public long getAddress() {
        return address;
    }

    /**
     * Get the address of this TestBNodeNoE's parent in the RAF.
     *
     * @return address of parent in RAF
     */
    public long getParent() {
        return parent;
    }

    /**
     * Get this TestBNodeNoE's keys.
     *
     * @return LinkedList of TreeObjects
     */
    public TreeObjectNoE[] getKeys(){
        return keys;//copy maybe later for encapsulation
    }

    public TreeObjectNoE getKey(int index) {
        return keys[index];
    }

    public void setKey(int index, TreeObjectNoE newKey) {
        keys[index] = newKey;
    }

    /**
     * Get this TestBNodeNoE's children.
     *
     * @return LinkedList of longs
     */
    public long[] getChildren(){
        return children;
    }

    public int getNumOfChildren() {
        int num = 0;
        int i = 1;
        while (this.getChildren()[i] > 0) {
            i++;
            num++;
        }
        return num;
    }

    public void setChild(int index, long l) {
        if (index >= children.length) {
            return;
        }
        children[index] = l;
    }

    /**
     * Indicate if this node is a leaf or not
     *
     * @return true if leaf, false otherwise
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
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
     * Indicates whether or not this TestBNodeNoE is full (n = 2t - 1)
     *
     * @return true is full, false otherwise
     */
    public boolean isFull() {
        return ((2 * degree ) - 1) == keys.length;
    }

    //most likely temporary toString.
    //returns String value for each key in a single String separated by spaces
    @Override
    public String toString() {
        String s = "Keys: ";
        if (n > 0) {
            for (int i = 1; i <= n; i++) {
                if (keys[i] != null) {
                    s += "|" + keys[i].toString() + "| ";
                }
            }
        }
        s += "... Children: ";
        for (long l : children) {
            if (l > 0) {
                s += "|" + l + "|"
                        + " ";
            }
        }
        return s;
    }

    //=================================================================================================================
    //                                           STATIC METHODS
    //=================================================================================================================

    /**
     * Set the shared static degree of all BNodes.
     *
     * @param newDegree (t) How many keys/objects (t - 1 to 2t - 1)
     *               and children (t to 2t) BNodes can have
     */
    static public void setDegree(int newDegree) {
        degree = newDegree;
    }

    /**
     * Get the max size in bytes a TestBNodeNoE written to disk could
     * be. This will be the parent address (8), the number of
     * objects n (4), max number of objects ((2t - 1) * (8 + 4)),
     * and the max number of children (2t * 8) summed together.
     *
     * @return Max bytes a TestBNodeNoE will take up on the disk
     */
    static public int getDiskSize() {
        return Long.BYTES + Integer.BYTES + (((2 * degree) - 1) * (Integer.BYTES + Long.BYTES)) + (2 * degree * Long.BYTES);
    }
}