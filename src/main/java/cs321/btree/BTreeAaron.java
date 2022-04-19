package cs321.btree;

import java.io.IOException;
import java.nio.BufferOverflowException;
/**
 * Used to create BTree objects that hold Generic Type objects. Notable method
 * is insert() which is important in structuring the BTree.
 *
 * @author Carter Gibbs, Mesa Greear, Aaron Goin
 * @version Spring 2022
 *
 */
public class BTreeAaron {

    private TestBNodeNoE root;
    private int numNodes;
    private final int FREQUENCY;
    private final int DEGREE;
    private final int NODESIZE = 1000;
    private final int NODEBITSIZE = TestBNodeNoE.getDiskSize();
    private int nextAddress = 0;
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
            nextAddress += NODESIZE;//also step 3, needed
            root = s;//step 4
            s.setLeaf(false);//step 5
            s.setN(0);//step 6
            s.setChild(0, r.getAddress());//step 7
            splitChild(s, 0);// step 8
            root = insertNonFull(s, k);//step 9
        } else {
            root = insertNonFull(r, k);// step 10
        }
    }

    public TestBNodeNoE insertNonFull(TestBNodeNoE x, TreeObjectNoE k) throws BufferOverflowException, IllegalStateException, IOException {
        int i = x.getN() - 1;// step 1
        if (x.isLeaf()) {// step 2
            while (i >= 0 && k.compare(x.getKey(i)) < 1) {// step 3
                x.setKey(i + 1, x.getKeys()[i]);// step 4
                i--;// step 5
            }
            x.setKey(i + 1, k);//step 6
            x.setN(x.getN() + 1);//step 7
            BReadWriteAlt.setBuffer(NODEBITSIZE);
            BReadWriteAlt.writeBNode(x);
            return x;
        } else {

        }
        return x;
    }

    private void splitChild(TestBNodeNoE x, int i) throws IOException {
        TestBNodeNoE z = new TestBNodeNoE(nextAddress);// step 1
        nextAddress += NODESIZE;//also step 1
        long address = x.getChildren()[i];
        BReadWriteAlt.setBuffer(NODEBITSIZE);//buffer
        TestBNodeNoE y = BReadWriteAlt.readBNode(address);// initialize y
        System.out.println("\nIn the splitChild method.");
        System.out.println("Initial node z: " + z.toString());
        System.out.println("Initial node y: " + y.toString());
        System.out.println("Initial node x: " + x.toString());
        z.setLeaf(y.isLeaf());//step 2
        z.setN(DEGREE - 1);//step 3
        for (int j = 0; j < DEGREE - 1; j++) {//step 4
            z.setKey(j, y.getKey(j + DEGREE));//step 5
        }
        if (!z.isLeaf()) {// step 6
            for (int j = 0; j < DEGREE - 1; j++) {// step 7
                z.setChild(j, y.getChildren()[(j + DEGREE)]);// step 8
            }
        }
        y.setN(DEGREE - 1);// step 9
        for (int j = x.getN(); j > i; j--) {// step 10
            x.setChild(j + 1, x.getChildren()[(j)]);// step 11
        }
        x.setChild(i + 1, z.getAddress());//step 12
        for (int j = x.getN() - 1; j > i; j--) {//step 13
            x.setKey(j + 1, x.getKey(j));//step 14
        }
        x.setKey(i, y.getKey(DEGREE));//step 15
        x.setN(x.getN() + 1);// step 16
        //saving x y and z
        //tests
        System.out.println("Node y keys: " + y.toString());
        System.out.println("Node y children: " + y.getChildren().toString());
        System.out.println("Node z: " + z.toString());
        System.out.println("Node z children: " + z.getChildren().toString());
        System.out.println("Node x: " + x.toString());
        System.out.println("Node x children: " + x.getChildren().toString());
        BReadWriteAlt.setBuffer(NODEBITSIZE);
        System.out.println("\nWriting y");
        BReadWriteAlt.writeBNode(y);//step 17
        BReadWriteAlt.setBuffer(NODEBITSIZE);
        System.out.println("\nWriting z");
        BReadWriteAlt.writeBNode(z);//step 18
        BReadWriteAlt.setBuffer(NODEBITSIZE);
        System.out.println("\nWriting x");
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

    /**
     *
     * @return
     */
    public int getNumNodes() {
        return numNodes;
    }

    public String toString() {
        return root.toString();
    }

//	public TestBNodeNoE getNodeAtIndex(int index) throws BufferUnderflowException, IllegalStateException, IOException {
//		if (index < 0) {
//			return new TestBNodeNoE(-1);
//		}
//		Queue<TestBNodeNoE> queue = new LinkedList<>();
//		queue.add(root);
//		int idx = 0;
//		while (!queue.isEmpty()) {
//			TestBNodeNoE n = queue.remove();
//			if (idx == index) {
//				return n;
//			}
//			if (!n.isLeaf()) {
//				for (long c : n.getChildren()) {
//					TestBNodeNoE child = BReadWriteAlt.readBNode(c);
//					queue.add(child);
//				}
//			}
//		}
//		return new TestBNodeNoE(-1);
//
//	}

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