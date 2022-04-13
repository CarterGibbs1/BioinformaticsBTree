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
    }
}
