package cs321.btree;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Used to create BTree objects that hold Generic Type objects. Notable method
 * is insert() which is important in structuring the BTree.
 *
 * @author Carter Gibbs, Mesa Greear
 * @version Spring 2022
 *
 */
public class BTreeAaron {

    private TestBNodeNoE root;
    private int numNodes;
    private final int FREQUENCY;// substring sequence
    private final int DEGREE;
    private final int NODESIZE = 1000;
    // private final int NODEBITSIZE = TestBNodeNoE.getDiskSize();
    private int nextAddress = 1001;
    private int cacheSize = 0;
    private int finalAddressbeforeMax;

    // lls for cache
    LinkedList<TestBNodeNoE> cacheNodes;
    LinkedList<Long> cacheNodeAddresses;

    // =================================================================================================================
    // CONSTRUCTORS
    // =================================================================================================================

    /**
     * Constructor for read BTree...
     *
     * @param degree
     * @param k        frequency
     * @param root
     * @param filename
     * @throws IOException
     */
    public BTreeAaron(int degree, int k, TestBNodeNoE root, String filename) throws IOException {
        TestBNodeNoE.setDegree(degree);
        BReadWriteAlt.setRAF(filename, false);

        // instantiate variables
        DEGREE = degree;
        FREQUENCY = k;

        this.numNodes = 1;
        this.root = root;
    }

    // =================================================================================================================
    // BTREE FUNCTIONALITY METHODS
    // =================================================================================================================

    public void insert(TreeObjectNoE k) throws IOException {
        TestBNodeNoE r = root;// step 1
        if (r.getN() == 2 * DEGREE - 1) {// step 2
            TestBNodeNoE s = new TestBNodeNoE(nextAddress);// part of step 3
            nextAddress += NODESIZE;// also step 3, needed
            numNodes++;
            root = s;// step 4
            s.setLeaf(false);// step 5
            s.setN(0);// step 6
            s.setChild(1, r.getAddress());// step 7
            splitChild(s, 1);// step 8
            insertNonFull(s, k);// step 9
        } else {
            insertNonFull(r, k);// step 10
        }
    }

    public void insertNonFull(TestBNodeNoE x, TreeObjectNoE k)
            throws BufferOverflowException, IllegalStateException, IOException {
        int i = x.getN();// step 1
        if (x.isLeaf()) {// step 2
            if (x.getKeys()[i] != null && x.getKeys()[i].compare(k) == 0) {
                x.getKeys()[i].incrementFrequency();
                if (cacheSize == 0) {// step 8
                    BReadWriteAlt.setBuffer(NODESIZE);
                    BReadWriteAlt.writeBNode(x);
                } else {
                    diskWriteCheck(x);
                }
                return;
            }
            while (i >= 1 && k.compare(x.getKeys()[i]) < 0) {// step 3
                x.setKey(i + 1, x.getKeys()[i]);// step 4
                i--;// step 5
            }
            /*********************/
            if (x.getKeys()[i] != null && x.getKeys()[i].compare(k) == 0) {
                x.getKeys()[i].incrementFrequency();
                if (cacheSize == 0) {// step 8
                    BReadWriteAlt.setBuffer(NODESIZE);
                    BReadWriteAlt.writeBNode(x);
                } else {
                    diskWriteCheck(x);
                }
                return;
            }
            /**********************/
            x.setKey(i + 1, k);// step 6
            x.setN(x.getN() + 1);// step 7
            if (cacheSize == 0) {// step 8
                BReadWriteAlt.setBuffer(NODESIZE);
                BReadWriteAlt.writeBNode(x);
            } else {
                diskWriteCheck(x);
            }
        } else {
            while (i >= 1 && k.compare(x.getKeys()[i]) < 0) {// step 9
                i--;// step 10
            }
            if (x.getKeys()[i] != null && x.getKeys()[i].compare(k) == 0) {
                x.getKeys()[i].incrementFrequency();
                if (cacheSize == 0) {// step 8
                    BReadWriteAlt.setBuffer(NODESIZE);
                    BReadWriteAlt.writeBNode(x);
                } else {
                    diskWriteCheck(x);
                }
                return;
            }
            i++;// step 11
            TestBNodeNoE xCI = diskReadCheck(x.getChildren()[i]);
            if (xCI == null) {
                BReadWriteAlt.setBuffer(NODESIZE);
                xCI = BReadWriteAlt.readBNode(x.getChildren()[i]);// step 12
            }
            if (xCI.getN() == 2 * DEGREE - 1) {// step 13
                splitChild(x, i);// step 14
                if (x.getKeys()[i] != null && x.getKeys()[i].compare(k) == 0) {
                    x.getKeys()[i].incrementFrequency();
                    if (cacheSize == 0) {// step 8
                        BReadWriteAlt.setBuffer(NODESIZE);
                        BReadWriteAlt.writeBNode(x);
                    } else {
                        diskWriteCheck(x);
                    }
                    return;
                }
                if (k.compare(x.getKeys()[i]) > 0) {// step 15
                    i++;// step 16
                }
                /************/
                if (x.getKeys()[i] != null && x.getKeys()[i].compare(k) == 0) {
                    x.getKeys()[i].incrementFrequency();
                    if (cacheSize == 0) {// step 8
                        BReadWriteAlt.setBuffer(NODESIZE);
                        BReadWriteAlt.writeBNode(x);
                    } else {
                        diskWriteCheck(x);
                    }
                    return;
                }
                /************/
                xCI = diskReadCheck(x.getChildren()[i]);
                if (xCI == null) {
                    BReadWriteAlt.setBuffer(NODESIZE);
                    xCI = BReadWriteAlt.readBNode(x.getChildren()[i]);
                }
            }
            insertNonFull(xCI, k);// step 17
        } // end of else statement
    }// end of insertNonFull

    private void splitChild(TestBNodeNoE x, int i) throws IOException {
        TestBNodeNoE z = new TestBNodeNoE(nextAddress);// step 1
        nextAddress += NODESIZE;// also step 1
        numNodes++;
        long address = x.getChildren()[i];
        TestBNodeNoE y = diskReadCheck(address);
        if (y == null) {
            BReadWriteAlt.setBuffer(NODESIZE);// buffer
            y = BReadWriteAlt.readBNode(address);// initialize y
        }
        z.setLeaf(y.isLeaf());// step 2
        z.setN(DEGREE - 1);// step 3
        for (int j = 1; j <= DEGREE - 1; j++) {// step 4
            z.setKey(j, y.getKeys()[j + DEGREE]);// step 5
        }
        if (!z.isLeaf()) {// step 6
            for (int j = 1; j <= DEGREE; j++) {// step 7
                z.setChild(j, y.getChildren()[(j + DEGREE)]);// step 8
            }
        }
        y.setN(DEGREE - 1);// step 9
        for (int j = x.getN() + 1; j >= i + 1; j--) {// step 10
            x.setChild(j + 1, x.getChildren()[(j)]);// step 11
        }
        x.setChild(i + 1, z.getAddress());// step 12
        for (int j = x.getN(); j >= i; j--) {// step 13
            x.setKey(j + 1, x.getKeys()[j]);// step 14
        }
        x.setKey(i, y.getKeys()[DEGREE]);// step 15
        x.setN(x.getN() + 1);// step 16
        // saving x y and z
        // tests
        if (cacheSize == 0) {
            BReadWriteAlt.setBuffer(NODESIZE);
            BReadWriteAlt.writeBNode(y);
        } else {
            diskWriteCheck(y);
        }

        if (cacheSize == 0) {
            BReadWriteAlt.setBuffer(NODESIZE);
            BReadWriteAlt.writeBNode(z);
        } else {
            diskWriteCheck(z);
        }

        if (cacheSize == 0) {
            BReadWriteAlt.setBuffer(NODESIZE);
            BReadWriteAlt.writeBNode(x);
        } else {
            diskWriteCheck(x);
        }
    }

    // =================================================================================================================
    // GET/SET/UTILITY METHODS
    // =================================================================================================================

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
    public TestBNodeNoE getRoot() {
        return root;
    }

    public void setRoot(TestBNodeNoE root) {
        this.root = root;
    }

    public void finalAddressbeforeMax(int newFinal) {
        this.finalAddressbeforeMax = newFinal;
    }

    /**
     *
     * @return
     */
    public int getNumNodes() {
        return numNodes;
    }

    public void incrementNumNodes() {
        numNodes++;
    }

    public void setCacheSize(int newSize) {
        if (newSize < 0) {
            return;
        }
        cacheSize = newSize;
    }

    public String toString() {
        if (root != null) {
            return root.toString();
        }
        return "";
    }

    public TestBNodeNoE getNodeAtIndex(int index) throws BufferUnderflowException, IllegalStateException, IOException {
        if (index < 1) {
            return new TestBNodeNoE(-1);
        }

        int i = 1;
        Queue<TestBNodeNoE> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TestBNodeNoE n = queue.remove();
            if (i == index) {
                return n;
            } else {
                i++;
            }
            if (!n.isLeaf()) {
                long[] c = n.getChildren();
                int numChildren = n.getNumOfChildren();
                for (int j = 1; j <= numChildren; j++) {
                    BReadWriteAlt.setBuffer(NODESIZE);// buffer
                    TestBNodeNoE child = BReadWriteAlt.readBNode(c[j]);
                    queue.add(child);
                }
            }
        }
        return new TestBNodeNoE(-1);
    }

    // cache

    public void createCaches() {
        cacheNodes = new LinkedList<TestBNodeNoE>();
        cacheNodeAddresses = new LinkedList<Long>();
    }

    public TestBNodeNoE diskReadCheck(long address) {
        TestBNodeNoE returnNode = null;
        if (cacheNodeAddresses.contains(address)) {
            int idx = cacheNodeAddresses.indexOf(address);
            returnNode = cacheNodes.get(idx);
            cacheNodes.remove(idx);
            cacheNodeAddresses.remove(idx);
            cacheNodes.addFirst(returnNode);
            cacheNodeAddresses.addFirst(returnNode.getAddress());
            return returnNode;
        }
        return returnNode;
    }

    public void diskWriteCheck(TestBNodeNoE writeNode)
            throws BufferOverflowException, IllegalStateException, IOException {
        if (cacheNodes.size() == cacheSize) {
            TestBNodeNoE removedNode = cacheNodes.removeLast();
            cacheNodeAddresses.removeLast();
            BReadWriteAlt.setBuffer(NODESIZE);
            BReadWriteAlt.writeBNode(removedNode);
        }
        /* Dunno if you add the node in this portion */
        if (!cacheNodeAddresses.contains(writeNode.getAddress())) {
            cacheNodes.addFirst(writeNode);
            cacheNodeAddresses.addFirst(writeNode.getAddress());
        } else {
            int idx = cacheNodeAddresses.indexOf(writeNode.getAddress());
            cacheNodes.remove(idx);
            cacheNodeAddresses.remove(idx);
            cacheNodes.addFirst(writeNode);
            cacheNodeAddresses.addFirst(writeNode.getAddress());
        }

    }

    public void doneWithBTree() throws BufferOverflowException, IllegalStateException, IOException {
        for (int i = 0; i < cacheNodes.size(); i++) {
            BReadWriteAlt.setBuffer(NODESIZE);
            BReadWriteAlt.writeBNode(cacheNodes.get(i));
        }
    }

    //possible dump method
//	public void dump(String filename) throws FileNotFoundException {// calls in order on root
//		PrintStream pS = new PrintStream(filename);
//		PrintStream stdout = System.out;
//		inOrderTraversal(root, pS);
//		System.setOut(pS);
//		System.setOut(stdout);
//	}
//
//	private void inOrderTraversal(TestBNodeNoE n, PrintStream pS) {
//		TreeObjectNoE[] keys = n.getKeys();
//		if (n.isLeaf()) {
//			for (int i = 0; i <= n.getN(); i++) {
//				pS.append(keys[i].getStringKey() + ": " + keys[i].getFrequency());
//			}
//		} else {
//			for (int i = 1; i <= n.getN(); i++) {
//				TestBNodeNoE tN = new TestBNodeNoE(n.getChildren()[i]);
//				inOrderTraversal(tN, pS);
//				pS.append(keys[i].getStringKey());
//			}
//			TestBNodeNoE tNN = new TestBNodeNoE(n.getChildren()[n.getN() + 1]);
//			inOrderTraversal(tNN, pS);
//		}
//	}

    /**
     * Recursively get a String in dump format of this BTree.
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
     * Recursively get a String in dump format of the subtree that starts at the
     * given root.
     * <p>
     * FORMAT (for each key) - key : frequency | "agcctgc 18"
     *
     * @param rootAddress The address of the root of this subtree
     *
     * @return String in dump format
     *
     * @throws IOException Reading/Writing to RAF may throw exception
     */
    public String dump(long rootAddress) throws IOException {
        // base case, the given root is non-existent
        if (rootAddress == -1) {
            return "";
        }

        // return string and read this BNode
        StringBuilder ret = new StringBuilder();
        TestBNodeNoE root = BReadWriteAlt.readBNode(rootAddress);

        // recursively construct the string
        ret.append(dump(root.getChildren()[1]));
        for (int i = 1; i <= root.getN(); i++) {
            ret.append(root.getKeys()[i].toString() + "\n");
            ret.append(dump(root.getChildren()[i + 1]));
        }

        return ret.toString();
    }

    // =================================================================================================================
    // STATIC METHODS
    // =================================================================================================================

    /**
     * Get the max size in bytes a BTree written to disk could be. This will be the
     * degree (4), frequency (4), root address (8), and number of nodes (4) summed
     * together.
     *
     * @return Max disk size of a BTree's metadata
     */
    static public int getDiskSize() {
        return Integer.BYTES + Integer.BYTES + Long.BYTES + Integer.BYTES;
    }
}