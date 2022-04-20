package cs321.btree;

import java.io.IOException;
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
    private final int FREQUENCY;
    private final int DEGREE;
    private final int NODESIZE = 1000;
    //private final int NODEBITSIZE = TestBNodeNoE.getDiskSize();
    private int nextAddress = 1000;
    private int finalAddressbeforeMax;

    // =================================================================================================================
    // CONSTRUCTORS
    // =================================================================================================================

    /**
     * Constructor for read BTree...
     *
     * @param degree
     * @param k
     * @param root
     * @throws IOException
     */
    public BTreeAaron(int degree, int k, TestBNodeNoE root, String filename) throws IOException {
        TestBNodeNoE.setDegree(degree);
        BReadWriteAlt.setRAF(filename, false);
        TestBNodeNoE.setDegree(degree);

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
            nextAddress += NODESIZE;//also step 3, needed
            root = s;//step 4
            s.setLeaf(false);//step 5
            s.setN(0);//step 6
            s.setChild(1, r.getAddress());//step 7
            splitChild(s, 1);// step 8
            insertNonFull(s, k);//step 9
        } else {
            insertNonFull(r, k);// step 10
        }
    }

    public void insertNonFull(TestBNodeNoE x, TreeObjectNoE k) throws BufferOverflowException, IllegalStateException, IOException {
        int i = x.getN();// step 1
        if (x.isLeaf()) {// step 2
            while (i >= 1 && k.compare(x.getKey(i)) < 0) {// step 3
                x.setKey(i + 1, x.getKeys()[i]);// step 4
                i--;// step 5
            }
            x.setKey(i + 1, k);//step 6
            x.setN(x.getN() + 1);//step 7
            BReadWriteAlt.setBuffer(NODESIZE);
            BReadWriteAlt.writeBNode(x);
        } else {
            /* check */
            while (i >= 1 && k.compare(x.getKey(i)) < 0) {//step 9
                i--;// step 10
            }
            i++;//step 11
            BReadWriteAlt.setBuffer(NODESIZE);
            TestBNodeNoE xCI = BReadWriteAlt.readBNode(x.getChildren()[i]);//step 12
            nextAddress += NODESIZE;
            if (xCI.getN() == 2 * DEGREE - 1) {//step 13
                splitChild(x, i);//step 14
                if (k.compare(x.getKey(i)) > 0) {//step 15
                    i++;//step 16
                    BReadWriteAlt.setBuffer(NODESIZE);
                    xCI = BReadWriteAlt.readBNode(x.getChildren()[i]);
                }
            }
            insertNonFull(xCI, k);//step 17
        }// end of else statement
    }

    private void splitChild(TestBNodeNoE x, int i) throws IOException {
        TestBNodeNoE z = new TestBNodeNoE(nextAddress);// step 1
        nextAddress += NODESIZE;//also step 1
        long address = x.getChildren()[i];
        BReadWriteAlt.setBuffer(NODESIZE);//buffer
        TestBNodeNoE y = BReadWriteAlt.readBNode(address);// initialize y
        z.setLeaf(y.isLeaf());//step 2
        z.setN(DEGREE - 1);//step 3
        for (int j = 1; j <= DEGREE - 1; j++) {//step 4
            z.setKey(j, y.getKey(j + DEGREE));//step 5
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
        x.setChild(i + 1, z.getAddress());//step 12
        for (int j = x.getN(); j >= i + 1; j--) {//step 13
            x.setKey(j + 1, x.getKey(j));//step 14
        }
        x.setKey(i, y.getKey(DEGREE));//step 15
        x.setN(x.getN() + 1);// step 16
        //saving x y and z
        //tests
        BReadWriteAlt.setBuffer(NODESIZE);
        BReadWriteAlt.writeBNode(y);//step 17
        BReadWriteAlt.setBuffer(NODESIZE);
        BReadWriteAlt.writeBNode(z);//step 18
        BReadWriteAlt.setBuffer(NODESIZE);
        BReadWriteAlt.writeBNode(x);//step 19
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

    public String toString() {
        return root.toString();
    }

    /********CHANGE*********/
    public TestBNodeNoE getNodeAtIndex(int index) throws BufferUnderflowException, IllegalStateException, IOException {
        if (index < 1) {
            return new TestBNodeNoE(-1);
        }
        Queue<TestBNodeNoE> queue = new LinkedList<>();
        queue.add(root);
        int idx = 1;
        while (!queue.isEmpty()) {
            TestBNodeNoE n = queue.remove();
            if (idx == index) {
                return n;
            }
            else {
                idx++;
            }
            if (!n.isLeaf()) {
                long[] cA = n.getChildren();
                for (int i = 1; i <= n.getChildren().length; i++) {
                    BReadWriteAlt.setBuffer(NODESIZE);//buffer
                    TestBNodeNoE child = BReadWriteAlt.readBNode(cA[i]);
                    queue.add(child);
                }
            }
        }
        return new TestBNodeNoE(-1);

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