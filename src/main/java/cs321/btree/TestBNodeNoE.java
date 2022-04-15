package cs321.btree;


import java.util.LinkedList;

/**
 * Similar to BNode.java, but references to other BNodes are now pointers stored
 * in memory instead of addresses to a RAF. Useful for testing that BTree's are
 * working.
 *
 * @author Mesa Greear, Aaron Goin
 * @version Spring 2022
 *
 */
public class TestBNodeNoE {

    // child0 <= key0 <= child1 <= key1 <= child2 ... childn <= keyn <= childn + 1
    private LinkedList<TreeObjectNoE> keys; // objects/keys in this node, also size() = n
    private LinkedList<String> children; // children in this node
    private String parent; // parent pointer
    private boolean isLeaf;
    private long location;// in Random Access File

    // =================================================================================================================
    // CONSTRUCTORS
    // =================================================================================================================

    /**
     * Constructor: Create BNode with one key initialKey, a parent pointer parent,
     * two children leftChild and rightChild, and the degree (t) of this BNode.
     * BNode type is determined automatically by parameters.
     *
     * @param initialKey  the initial object in this BNode
     * @param parent     pointer to the parent of this BNode
     * @param leftChild  pointer to the child left of initialKey
     * @param rightChild pointer to the child right of initialKey
     */
    /*
     * Example Demonstration:
     *
     *
     * new BNode(?, a, ?, /, \)
     *
     * result - a / \
     */
    public TestBNodeNoE(TreeObjectNoE initialKey, String parent, String leftChild, String rightChild) {
        // initialize instance variables
        keys = new LinkedList<TreeObjectNoE>();
        children = new LinkedList<String>();

        keys.add(initialKey);
        children.add(leftChild);
        children.add(rightChild);

        this.parent = parent;
        this.isLeaf = true;
        this.location = -1;// indicate it's not written to disk yet
    }

    /**
     * Constructor: Create LEAF BNode with one key initialKey and a parent pointer
     * parent. Degree (t) is determined by the parent pointer.
     *
     * @param initialKey the initial object in this BNode
     * @param parent    pointer to the parent of this BNode
     */
    public TestBNodeNoE(TreeObjectNoE initialKey, String parent) {
        this(initialKey, parent, null, null);
    }

    /**
     * Constructor: Create ROOT BNode with one key initialKey and the degree (t).
     *
     * @param initialKey the initial object in this BNode
     */
    public TestBNodeNoE(TreeObjectNoE initialKey) {
        this(initialKey, null, null, null);
    }

    // =================================================================================================================
    // BTREE FUNCTIONALITY METHODS
    // =================================================================================================================

//	/**
//	 * Insert the given key into this BNode and insert the given child to the right
//	 * of the inserted key.
//	 *
//	 * @param key   TreeObjectNoE containing Object to insert
//	 * @param child Child related to key to insert
//	 */
//	/*
//	 * Example Demonstration:
//	 *
//	 *
//	 * keys - a b d e f children - # # # # # #
//	 *
//	 * insert(c, *)
//	 *
//	 * keys - a b c d e f children - # # # * # # #
//	 */
//	public void insert(TreeObjectNoE key, String child) {
//
//		// get to the index of the first k less than key
//		int i;
//		for (i = (keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--) {
//		}
//
//		// add new key and child to lists
//		keys.add(i + 1, key);
//		children.add(i + 2, child);
//
//		// TODO: write to disk, probably in BTree.java
//	}

    /**
     * Insert the given key into this BNode. Should only be used on LEAF nodes. If a
     * node y is in the right subtree of a node x, then key[y] > key[x].
     *
     * @param key TreeObjectNoE containing Object to insert
     */
    public void insert(TreeObjectNoE key) {
        int i = 0;
        while (i < keys.size()) {
            if (key.compare(keys.get(i)) > 0) {
                i++;
            } else {// satisfies the condition in javadoc
                keys.add(i, key);
            }
        }
    }

    /**
     * Get the child of this BNode where the given key should be inserted or would
     * be located. Does NOT insert the key, only returns the subtree that it belongs
     * to.
     *
     * @param key Object to use to locate the appropriate subtree
     *
     * @return subtree (child of this BNode) that key belongs to
     */
    public String getSubtree(TreeObjectNoE key) {

        // get to the index of the first k less than key
        int i;
        for (i = (keys.size() - 1); i >= 0 && key.compare(keys.get(i)) <= 0; i--) {
        }

        return children.get(i + 1);
    }

//	/**
//	 * Splits the current BNode into two new BNodes, removing and
//	 * inserting the middle object and a pointer to the new right
//	 * BNode into the parent. The left BNode (this BNode) will
//	 * contain everything to the left of the removed object and
//	 * the right BNode (new BNode) will be contain everything to
//	 * the right. Can only be run when the list is full.
//	 *
//	 * @return the parent of the two BNodes
//	 */
//	public TestBNodeNoE split() throws IllegalStateException{
//		//==== for better understanding when coming back to look at this method, I'm going to ====
//		//==== create an example split and show how it changes through comments:              ====
//		//Keys     -  a b c d e f g
//		//Pointers - 0 1 2 3 4 5 6 7
//
//		int originalN = keys.size();
//
//
//		//remove key and two pointers right of middle and insert into new BNode 'splitRight':
//		//Keys     -  a b c d f g    |  e
//		//Pointers - 0 1 2 3   6 7   | 4 5
//		TestBNodeNoE splitRight = new TestBNodeNoE(keys.remove(keys.size()/2 + 1), parent, children.remove(children.size()/2), children.remove(children.size()/2 + 1));
//
//
//		//starting just to the right of the middle of this BNode, continuously remove the pointers and keys
//		//at that position and insert them into splitRight:
//		//Keys     -  a b c d  |  e f g
//		//Pointers - 0 1 2 3   | 4 5 6 7
//		while((keys.size() - 1) != originalN/2) {
//			splitRight.insert(keys.remove(originalN/2 + 1), children.remove(originalN/2 + 1));
//		}
//
//
//		//lastly, remove last node (original middle) from this and insert into parent with
//		//a pointer to splitRight
//		//Parent   -    .. x x d x x ..
//		//                    / \
//		//Keys     -  a b c d  |  e f g
//		//Pointers - 0 1 2 3   | 4 5 6 7
//
//		//if this is the ROOT, create new parent/root to insert into and update types
//		if(isRoot()) {
//			TestBNodeNoE newRoot = new TestBNodeNoE(keys.removeLast(), null, this, splitRight);
//			this.parent = splitRight.parent = newRoot;
//
//			return newRoot;
//		}
//
//		parent.insert(keys.removeLast(), splitRight);
//		return parent;
//	}

    // =================================================================================================================
    // GET/SET/UTILITY METHODS
    // =================================================================================================================
    /**
     * Gets the first key for the root. Used and needed for B-Tree insert.
     *
     * @return the key of the root
     */
    public TreeObjectNoE rootKey() {
        return keys.getFirst();
    }

    /**
     * @param index the index of the key to be set
     * @param newKey the new key of the key being set
     */
    public void setKey(int index, String newKey) {
        keys.get(index).setKey(newKey);
    }

    /**
     * Get the number of objects (n) in this BNode represented by keys.size().
     *
     * @return Number of objects in this BNode
     */
    public int getN() {
        return keys.size();
    }

    /**
     * Indicate if this node is a leaf or not
     *
     * @return true if leaf, false otherwise
     */
    public boolean isLeaf() {
        return isLeaf;
    }

    /**
     * @param isLeaf the new indicator of whether something is a leaf or not
     */
    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    /**
     * Indicate if this node is the root or not
     *
     * @return true if root, false otherwise
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Indicates whether or not this BNode is full (n = 2t - 1)
     *
     *
     * @param degree the degree of this BTree
     *
     * @return true is full, false otherwise
     */
    public boolean isFull(int degree) {
        return ((2 * degree) - 1) == keys.size();
    }

    /**
     * @return the location of this node in memory
     */
    public long getLocation() {
        return location;
    }

    /**
     * @param location where the location of the node is on disk
     */
    public void setLocation(int location) {
        this.location = location;
    }

    // returns long value for each key in a single String seperated by spaces
    @Override
    public String toString() {
        StringBuilder retString = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            retString.append(keys.get(i).getKey() + " "); // TODO: return letters instead
        }

        return retString.toString().substring(0, retString.length() - 1);
    }
}