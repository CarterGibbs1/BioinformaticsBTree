package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

/**
 * Contains static methods used to read and write to a Random Access File. Used
 * in conjunction with a BTree to allow the storing of data in the computers
 * storage.
 *
 * @author Mesa Greear
 * @version Spring 2022
 */
public class BReadWriteAlt {

    private static FileChannel RAF = null;
    private static ByteBuffer buffer;

    /**
     * Sets this class to read and write from the given Random Access File. If the
     * file is to be replaced then it will be deleted and if the RAF does not exist
     * a new one will be created with the given name.
     * <p>
     * This method must be called at least once before using Write and Read methods.
     * Not doing so will result in IllegalStateExceptions being thrown.
     *
     * @param fileName Name of file to read from and possibly create as well
     * @param replace to delete the file and replace it with a new one if it exists
     *
     * @throws IOException Creating RAF may throw exception
     */
    @SuppressWarnings("resource")
    static public void setRAF(String fileName, boolean replace) throws IOException {
        File file = new File(fileName);
        RandomAccessFile RAFRaw = null;

        try {
            // if the file exists and is to be replaced, delete it
            if (file.exists() && replace) {
                if (!file.delete()) {
                    throw new IOException("File could not be deleted. Might be open"
                            + "elsewhere like inside a debugger");
                }
            }

            // if file doesn't exist, create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the Random Access File channel and store it
            RAFRaw = new RandomAccessFile(file, "rw");
            RAF = RAFRaw.getChannel();
        } catch (Exception e) {
            // close RAFRaw before throwing exception
            if (RAFRaw != null) {
                RAFRaw.close();
            }
            throw e;
        }
    }

    /**
     * Set the capacity of the ByteBuffer, i.e. how many bytes are written/read from
     * the RAF.
     * <p>
     * This method must be called at least once before using Write and Read methods
     * if BTree was not read from RAF. Not doing so will result in
     * IllegalStateExceptions being thrown.
     *
     * @param capacity The buffer capacity in bytes
     */
    static public void setBuffer(int capacity) {
        buffer = ByteBuffer.allocateDirect(capacity);
    }

    /**
     * Write the given BNode to the RAF at the specified address. Writes the n and
     * then the lists: child0, key0, child1, key1, child2 ... childn, keyn, childn +
     * 1 (long, long int, long, long int, long ....).
     *
     * @param node    BNode to write to RAF
     *
     * @throws IOException             Writing to RAF may throw exception
     * @throws BufferOverflowException Indicates buffer capacity was incorrect for
     *                                 what is was writing
     * @throws IllegalStateException   If thrown, it's likely RAF or buffer have not
     *                                 been set
     */
    static public void writeBNode(TestBNodeNoE node)// assumes every node is internal
            throws IOException, BufferOverflowException, IllegalStateException {
        try {
            System.out.println("\nIn the write method:");
            // start at address and make buffer ready to read
            RAF.position(node.getAddress());
            System.out.println("Address: " + node.getAddress());
            buffer.clear();

            // write parent
            buffer.putLong(node.getParent());
            System.out.println("Parent: " + node.getParent());
            // store and write n
            int n = node.getN();
            buffer.putInt(n);
            System.out.println("N: " + n);
            if (node.isLeaf()) {
                buffer.putInt(1);
                System.out.println("Is Leaf int: " + 1);
            } else {
                buffer.putInt(-1);
                System.out.println("Is Leaf int: " + -1);
            }

            // get children and keys
            long[] children = node.getChildren();
            TreeObjectNoE[] keys = node.getKeys();
            System.out.println("Node keys: " + keys.toString());

            // write the children and keys in alternating order
            if (!node.isLeaf()) {// if statement before to check if its leaf
                buffer.putLong(children[0]);
                System.out.println("Child 0: " + children[0]);
            }
            System.out.println("keys before for loop: " + keys.toString());
            System.out.println("first key: " + keys[0].getLongKey());
            for (int i = 0; i < n; i++) {
                buffer.putLong(keys[i].getLongKey());
                System.out.println("Key " + i + ": " + keys[i].getLongKey());
                buffer.putInt(keys[i].getFrequency());
                System.out.println("Freq: " + i + ": " + keys[i].getFrequency());
                if (!node.isLeaf()) {
                    buffer.putLong(children[i + 1]);
                    System.out.println("Child: " + (i + 1) + ": " + children[i + 1]);
                }
            }

            // make buffer ready to write and then write to RAF
            buffer.flip();
            RAF.write(buffer);
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new IllegalStateException(
                    e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
        }
    }

    /**
     * Reads and returns the BNode stored at the given address.
     *
     * @param address Address that BNode is located
     *
     * @return BNode stored in RAF at the given address
     *
     * @throws IOException              Reading RAF may throw exception
     * @throws BufferUnderflowException Indicates buffer capacity was incorrect for
     *                                  what is was reading
     * @throws IllegalStateException    If thrown, it's likely RAF or buffer have
     *                                  not been set
     */
    static public TestBNodeNoE readBNode(long address)
            throws IOException, BufferUnderflowException, IllegalStateException {
        try {
            System.out.println("\nIn the read method:\nAddress: " + address);
            // start at address and make buffer ready to read
            RAF.position(address);
            buffer.clear();
            RAF.read(buffer);
            buffer.flip();

            // get parent and n
            long parent = buffer.getLong();
            System.out.println("Parent: " + parent);
            int n = buffer.getInt();
            System.out.println("N: " + n);
            int isLeafInt = buffer.getInt();
            System.out.println("Is Leaf Int: " + isLeafInt);

            // get first key and two children
            long leftChild;
            if (isLeafInt == -1) {
                leftChild = buffer.getLong();
                System.out.println("Left Child: " + leftChild);
            } else {
                leftChild = -1;
            }

            long l = buffer.getLong();
            int in = buffer.getInt();
            TreeObjectNoE initialKey = new TreeObjectNoE(l, in);
            System.out.println("Initial Key long: " + l);
            System.out.println("Initial Key Freq: " + in);
            long rightChild;
            if (isLeafInt == -1) {
                rightChild = buffer.getLong();
                System.out.println("Right Child: " + rightChild);
            } else {
                rightChild = -1;
            }

            // construct the return BNode and insert the other n - 1 keys and children
            TestBNodeNoE retNode = new TestBNodeNoE(initialKey, address, parent, leftChild, rightChild);
            retNode.setN(n);
            if (isLeafInt == -1) {
                retNode.setLeaf(false);
            } else {
                retNode.setLeaf(true);
            }
            //System.out.println("retNode keys before for loop: " + retNode.toString() + ", and children: " + retNode.getChildren().toString());
            for (int i = 1; i < n; i++) {
                long l2 = buffer.getLong();
                System.out.println("new child object longkey: " + l2);
                int i2 = buffer.getInt();
                System.out.println("new child object freq: " + i2);
                // figure out logic here
                TreeObjectNoE newKey = new TreeObjectNoE(l2, i2);
                retNode.setKey(i, newKey);
                retNode.setChild(i - 1, l2);
                //System.out.println("Current retNode keys: " + retNode.toString() + ", And children: " + retNode.getChildren().toString());
            }

            return retNode;
        } catch (NullPointerException e) {
            throw new IllegalStateException(
                    e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
        }
    }

//	/**
//	 * Write the given BTree metadata to the RAF. Writes the root, degree,
//	 * frequency, and then numNodes Always rights to address 0. Automatically
//	 * sets the buffer capacity to the BNode disk size after running.
//	 *
//	 * @param tree BTree to write to RAF
//	 *
//	 * @throws IOException             Writing to RAF may throw exception
//	 * @throws BufferOverflowException Indicates buffer capacity was incorrect for
//	 *                                 what is was writing
//	 * @throws IllegalStateException   If thrown, it's likely RAF or buffer have not
//	 *                                 been set
//	 */
//	static public void writeBTree(BTreeAaron tree)
//			throws IOException, BufferOverflowException, IllegalStateException {
//		try {
//			// set buffer capacity to match BTree size
//			setBuffer(BTreeAaron.getDiskSize());
//
//			// start at 0 and make buffer ready to read
//			RAF.position(0);
//			buffer.clear();
//
//			//write metadata to RAF
//			buffer.putLong(tree.getRoot()); // root address
//			buffer.putInt(tree.getDegree());  // degree
//			buffer.putInt(tree.getFrequency());  // frequency
//			buffer.putInt(tree.getNumNodes());  //number of nodes
//
//			// make buffer ready to write and then write to RAF
//			buffer.flip();
//			RAF.write(buffer);
//
//			// reset buffer capacity to match BNode size
//			setBuffer(TestBNodeNoE.getDiskSize());
//		} catch (NullPointerException e) {
//			throw new IllegalStateException(
//					e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
//		}
//	}

    /**
     * Returns the BTree held at the beginning of the RAF and sets the static BNode
     * degree to match the BTree degree. Automatically sets the buffer capacity to
     * the BNode disk size after running.
     *
     *
     * @return BTree at beginning of RAF
     *
     * @throws IOException              Reading RAF may throw exception
     * @throws BufferUnderflowException Indicates buffer capacity was incorrect for
     *                                  what is was reading
     * @throws IllegalStateException    If thrown, it's likely RAF or buffer have
     *                                  not been set
     */
//	static public BTreeAaron readBTree() throws IOException, BufferUnderflowException, IllegalStateException {
//		try {
//			// set buffer capacity to match BTree size
//			setBuffer(BTreeAaron.getDiskSize());
//
//			// start at 0 and make buffer ready to read
//			RAF.position(0);
//			buffer.clear();
//			RAF.read(buffer);
//			buffer.flip();
//
//			//get metadata
//			long root = buffer.getInt();
//			int k = buffer.getInt(); //TODO: variables could be shorts, save barely on storage size
//			int t = buffer.getInt();
//			int numNodes = buffer.getInt();
//
//			// initialize BTree and return
//			BTreeAaron retTree = new BTreeAaron(t, k, numNodes, root);
//
//			// set static BNode degree
//			TestBNodeNoE.setDegree(retTree.getDegree());
//
//			// reset buffer capacity to match BNode size
//			setBuffer(TestBNodeNoE.getDiskSize());
//
//			return retTree;
//		} catch (NullPointerException e) {
//			throw new IllegalStateException(
//					e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
//		}
//	}

    /**
     * Get the next available address in the RAF, i.e. the end of the RAF.
     *
     * @return The next available address in the RAF.
     *
     * @throws IOException Reading RAF may throw exception
     */
    static public long getNextAddress() throws IOException {
        return RAF.size();
    }
}
