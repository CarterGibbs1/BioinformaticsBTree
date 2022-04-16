package cs321.btree;

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

    // =================================================================================================================
    // CONSTRUCTORS
    // =================================================================================================================

    /**
     * Constructor for read BTree...
     *
     * @param degree
     * @param k
     * @param root
     */
    public BTreeAaron(int degree, int k, TestBNodeNoE root) {
        // instantiate variables
        DEGREE = degree;
        FREQUENCY = k;

        this.numNodes = 1;
        this.root = root;
    }

    // =================================================================================================================
    // BTREE FUNCTIONALITY METHODS
    // =================================================================================================================

    public void insert(TreeObjectNoE k) {
        TestBNodeNoE r = root;// step 1
        if (r.getN() == 2 * DEGREE - 1) {// step 2
            // future steps
        } else {
            root = insertNonFull(r, k);
        }
    }

    public TestBNodeNoE insertNonFull(TestBNodeNoE x, TreeObjectNoE k) {
        int i = x.getN() - 1;// step 1
        if (x.isLeaf()) {// step 2
            while (i >= 0 && k.compare(x.getKey(i)) < 1) {// step 3
                x.setKey(i + 1, x.getKeys().get(i));// step 4
                i--;// step 5
            }
            x.setKey(i + 1, k);
            return x;
        } else {

        }
        return x;
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