/**
 * This is a driver class to create the BTree
 * @author CS321 group 4 - Carter, Aaron, Mesa
 *
 */
public class GeneBankCreateBTree {
    //java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]

    //for degree: If the user specifies 0, then your program should choose the optimum degree based on a disk block
    //size of 4096 bytes and the size of your BTree node on disk.
    //not sure what that would be at the moment

    //gbk file: do we only use the ones in dr yeh's directory, or get more somehow from the ncbi website?

    //The sequence length is an integer that must be between 1 and 31 (inclusive).

    //cache size, from pa#1, 1 cache with either a size of 100 or 500, used for btreenode

    //debug level
    //opt 0: diagnostic messages, help and status messages printed on standard error stream (not sure what this is)
    //option 1...
    //note: query7_result and dump.6 have the same format, example of a line: ccactg: 2

    //additionally, this is where we can officially create a btree file, as it starts from a gbk file:
    //
    //Metadata storage. We need to store some metadata about the BTree on disk. For example, we can store the degree
    //of the tree, the byte offset of the root node (so we can find it), the number of nodes etc. This information
    // could be stored in separate metadata file or it can be stored at the beginning of the BTree file.
    //The B-Tree should be stored as a binary data file on the disk (and not as a text file). If the name of the
    // GeneBank file is xyz.gbk, the sequence length is k, the BTree degree is t, then the name of the btree file
    // should be xyz.gbk.btree.data.k.t.
    //
    // seems like it'd be a good idea to just write this at the beginning.
    // also, I couldn't find one, but it'd be cool to see an example of a btree file

}
