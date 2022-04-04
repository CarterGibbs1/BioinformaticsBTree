/**
 * This is the BTree class to construct a BTree
 * @author CS321 group 4 - Carter, Aaron, Mesa
 *
 */
public class BTree {

    //based on the reading on the lab writeup, the constructor will have a file parameter for a gbk file
    //upon looking at the gbk files, none of them have an 'N' character, only '//' indicating the end of a sequence
    //seems it'll be as simple as finding 'ORIGIN', and then stopping the scan when we reach '//'
    //another parameter for the constructor appears to be 'k', which represents the length of the subsequence
    //design question: do we have a default constructor with only the gbk file where we pick a default k value?
    //in the constructor, when "saving memory", it may be as simple as using 'replaceAll' to convert bases
    //this 'replaceAll' process can be in the treeobject class
    //doesn't seem like BTreeNodes will be uses directly in here, only as we want things referenced when using a driver

    //method to create a btree file should be in here as well:
    //Metadata storage. We need to store some metadata about the BTree on disk. For example, we can store the degree of
    // the tree, the byte offset of the root node (so we can find it), the number of nodes etc. This information could
    // be stored in separate metadata file or it can be stored at the beginning of the BTree file.
    //The B-Tree should be stored as a binary data file on the disk (and not as a text file). If the name of the
    // GeneBank file is xyz.gbk, the sequence length is k, the BTree degree is t, then the name of the btree file
    // should be xyz.gbk.btree.data.k.t.
}
