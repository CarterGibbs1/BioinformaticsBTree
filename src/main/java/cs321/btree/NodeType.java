package cs321.btree;

/**
 * Enumerator to represent what type a BNode is. ROOT
 * means the BNode is the first BNode of a BTree. INTERIOR
 * means the BNode has both a parent pointer and child
 * pointers. LEAF means a BNode has no children and is
 * at the bottom of a BTree.
 * 
 * @author  Mesa Greear
 * @version Spring 2022
 */
public enum NodeType {
	ROOT, INTERIOR, LEAF
}
