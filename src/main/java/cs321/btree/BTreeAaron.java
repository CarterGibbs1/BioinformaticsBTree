package cs321.btree;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * Used to create BTree objects that hold Generic Type objects. Notable method
 * is insert() which is important in structuring the BTree. Aaron's version,
 * using Carter's code as baseline.
 *
 * @author Aaron Goin
 * @version Spring 2022
 *
 */
public class BTreeAaron {
    private TestBNodeNoE root;
    private final int degree;
    private int numNodes;
    private RandomAccessFile rAF;

    /**
     * Create an initialized BTree with null root.
     *
     * @param degree the degree of the BTree (minimum number of children for
     *               internal nodes other than the root)
     * @param filename name of file
     * @throws FileNotFoundException
     */
    public BTreeAaron(int degree, String filename) throws FileNotFoundException {
        rAF = new RandomAccessFile(filename, "rw");
        root = new TestBNodeNoE(degree);
        numNodes = 0;
        this.degree = degree;
    }

    /**
     * Inserts an element into the list. sections are labeled BInsert and
     * BInsertNotFull to show which sections somewhat match the pseudo code.
     *
     * @param k to insert into BTree
     * @throws IOException
     */
    public void insert(String key, long k) throws IOException {
        TestBNodeNoE r = root;// step 1 of pseudo code

        // ====BInsert==================
        // if root is full, split it and reassign to the new root
        if (r.isFull(degree)) {// step 2
//            TestBNodeNoE s = null; // part of step 3, need to allocate
//            root = s; // step 4, step 5 and 6 may already be covered
//            s.insert(r.rootKey(), r);// step 7?
//            s = r.split();//step 8 //TODO: write to disk could be dealt in BNode or in BTree
//            insertNonFull(s, k);
//            //increment numNodes
//            numNodes++;
        } else {
            root = insertNonFull(r, key, k);
        }

    }

    /**
     * Inserts key into nonfull node
     *
     * @param x the node that isn't full
     * @param k the key that is being inserted
     * @throws IOException
     */
    private TestBNodeNoE insertNonFull(TestBNodeNoE x, String key, long k) throws IOException {
        // starting at root, search down BTree to find correct leaf node to insert into
        int i = x.getN();// step 1
        if (x.isLeaf()) {// step 2
            while (i >= 1 && k < x.getLongKey(i)) {// step 3
                x.setKey(i + 1, x.getStringKey(i), x.getLongKey(i));// step 4
                i--;//step 5
            }
            x.setKey(i + 1, key, k);//step 6
//			TreeObjectNoE = new TreeObjectNoE(key, k, );
            x.setN(x.getN() + 1);//step 7
//			diskWrite(x);//step 8, for test 2
            numNodes++;
        }
//		} else {
////			// if the current node is full, split it and assign currentNode to parent
////			if (x.isFull(degree)) {
////				x = x.split(); // TODO: write to disk could be dealt in BNode or in BTree
////			}
//			// once at leaf node, make treeobject insert key
////			insertNonFull();
//		}
        return x;
    }

    /**
     * Creates a long value from a treeObjectKey in String form
     *
     * @param s the treeObjectKey in String form
     * @return the corresponding long value according to 2-bit keys: a = 00, c = 01,
     *         g = 10, t = 11
     */
    private long byteShift(String s) {
        long b = 0;
        for (int i = 0; i < s.length(); i++) {
            b += charToByteVal(s.charAt(i));
            if (i < s.length() - 1) {
                b = b << 2;
            }
        }
        return b;
    }
//
//	private void diskWrite(TestBNodeNoE x) throws IOException {
//		ByteBuffer b = x.serializeByteArray();
//		byte[] bA = b.array();
//		rAF.readFully(bA);
//		rAF.close();
//	}

    /**
     * A key for byteShift to determine the correct value for each letter
     *
     * @param c the letter being evaluated
     * @return the corresponding 2-bit binary value
     */
    private long charToByteVal(char c) {
        if (c == 'a') {
            return 0;
        }
        if (c == 'c') {
            return 1;
        }
        if (c == 'g') {
            return 2;
        }
        if (c == 't') {
            return 3;
        }
        return -1;
    }

    public TestBNodeNoE getRoot() {
        return root;
    }

    public int getNumNodes() {
        return numNodes;
    }

//	public TestBNodeNoE getNodeAtIndex(int index) {
//	//params TreeObjectNoE initialKey, String parent, String leftChild, String rightChild
//		if (index == 1) {
//			return null;
//		}
//		int i = 1;
//		Queue q = new Queue();
//
//	}

    public String toString() {
        return root.toString();
    }
}

