package cs321.btree;

/**
 * Used to create BTree objects that hold Generic Type objects.
 * Notable method is insert() which is important in structuring the
 * BTree.
 *
 * @author  Carter Gibbs
 * @version Spring 2022
 *
 * @param <E> Generic Type for this BNode to hold
 */
public class BTree<E>
{

    private BNode<E> root;
    private final int degree;
    private int numNodes;

    public BTree(int degree) {
        root = new BNode<E>(degree, null);
        numNodes = 0;
        this.degree = degree;
    }

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

    public int getNumNodes() {
        return numNodes;
    }
}
