package cs321.btree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Contains static methods used to read and write to a Random Access File. Used
 * in conjunction with a BTree to allow the storing of data in the computers
 * storage.
 * 
 * @author  Mesa Greear
 * @version Spring 2022
 */
public class BReadWrite {

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
	 * @param fileName Name of file to read from and to possibly create as well
	 * @param replace  True to delete the file and replace it with a new one if it
	 *                 exists
	 * 
	 * @throws IOException Creating RAF may throw exception
	 */
	@SuppressWarnings("resource") //TODO: memory leak? Talk to tutor
	static public void setRAF(String fileName, boolean replace) throws IOException {
		File file = new File(fileName);
		RandomAccessFile RAFRaw = null;

		try {
			//if the file exists and is to be replaced, delete it
			if(file.exists() && replace) {
				if(!file.delete()) {
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
			//close RAFRaw before throwing exception
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
	 * @param address Address to write this BNode to
	 * 
	 * @throws IOException             Writing to RAF may throw exception
	 * @throws BufferOverflowException Indicates buffer capacity was incorrect for
	 *                                 what is was writing
	 * @throws IllegalStateException   If thrown, it's likely RAF or buffer have not
	 *                                 been set
	 */
	static public void writeBNode(BNode node) throws IOException {
		try {
			// start at address and make buffer ready to read
			RAF.position(node.getAddress());
			buffer.clear();

			// write parent
			buffer.putLong(node.getParent());
			// store and write n
			int n = node.getN();
			buffer.putInt(n);
			
			buffer.putLong(node.getChild(0));
			for (int i = 0; i < n; i++) {
				buffer.putLong(node.getKey(i).getKey());
				buffer.putInt(node.getKey(i).getFrequency());

				buffer.putLong(node.getChild(i + 1));
			}

			// make buffer ready to write and then write to RAF
			buffer.flip();
			RAF.write(buffer);
		} catch (NullPointerException e) {
			throw new IllegalStateException(
					e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}catch(Exception e) {
			throw e;
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
	static public BNode readBNode(long address) throws IOException {
		try {
			// start at address and make buffer ready to read
			RAF.position(address);
			buffer.clear();
			RAF.read(buffer);
			buffer.flip();

			// get parent and n
			long parent = buffer.getLong();
			int n = buffer.getInt();

			// get first key and two children
			long leftChild = buffer.getLong();
			TreeObject initialKey = new TreeObject(buffer.getLong(), buffer.getInt());
			long rightChild = buffer.getLong();

			// construct the return BNode and insert the other n - 1 keys and children
			BNode retNode = new BNode(initialKey, address, parent, leftChild, rightChild);
			for (int i = 1; i < n; i++) {
				retNode.insert(new TreeObject(buffer.getLong(), buffer.getInt()), buffer.getLong());
			}

			return retNode;
		} catch (NullPointerException e) {
			throw new IllegalStateException(
					e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}catch(Exception e) {
			throw e;
		}
	}

	/**
	 * Write the given BTree metadata to the RAF. Writes the root, degree,
	 * frequency, and then numNodes Always rights to address 0. Automatically
	 * sets the buffer capacity to the BNode disk size after running.
	 * 
	 * @param tree BTree to write to RAF
	 * 
	 * @throws IOException             Writing to RAF may throw exception
	 * @throws BufferOverflowException Indicates buffer capacity was incorrect for
	 *                                 what is was writing
	 * @throws IllegalStateException   If thrown, it's likely RAF or buffer have not
	 *                                 been set
	 */
	static public void writeBTree(BTree tree) throws IOException {
		try {
			// set buffer capacity to match BTree size
			setBuffer(BTree.getDiskSize());

			// start at 0 and make buffer ready to read
			RAF.position(0);
			buffer.clear();

			//write metadata to RAF
			buffer.putLong(tree.getRoot().getAddress());
			buffer.putInt(tree.getDegree());
			buffer.putShort(tree.getFrequency());
			buffer.putInt(tree.getNumNodes());
			buffer.putShort(tree.getHeight());

			// make buffer ready to write and then write to RAF
			buffer.flip();
			RAF.write(buffer);

			// reset buffer capacity to match BNode size
			setBuffer(BNode.getDiskSize());
			//write the root
			writeBNode(tree.getRoot());
		} catch (NullPointerException e) {
			throw new IllegalStateException(
					e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}
	}

	/**
	 * Returns the BTree held at the beginning of the RAF and sets the static BNode
	 * degree to match the BTree degree. Automatically sets the buffer capacity to
	 * the BNode disk size after running.
	 * 
	 * @param cache The cache size of this BTree, <= 0 for no cache
	 * 
	 * @return BTree at beginning of RAF
	 * 
	 * @throws IOException              Reading RAF may throw exception
	 * @throws BufferUnderflowException Indicates buffer capacity was incorrect for
	 *                                  what is was reading
	 * @throws IllegalStateException    If thrown, it's likely RAF or buffer have
	 *                                  not been set
	 */
	static public BTree readBTree(int cache) throws IOException {
		try {
			// set buffer capacity to match BTree size
			setBuffer(BTree.getDiskSize());

			// start at 0 and make buffer ready to read
			RAF.position(0);
			buffer.clear();
			RAF.read(buffer);
			buffer.flip();

			//get metadata
			long root = buffer.getLong();
			int t = buffer.getInt();
			short k = buffer.getShort();
			int numNodes = buffer.getInt();
			short height = buffer.getShort();

			// reset buffer capacity to match BNode size
			setBuffer(BNode.getDiskSize());
			
			// set static BNode degree
			BNode.setDegree(t);
			
			// initialize BTree and return
			return new BTree(root, t, k, numNodes, height, cache);
		} catch (NullPointerException e) {
			throw new IllegalStateException(
					e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}
	}
	
	/**
	 * Get the size of the RAF in bytes.
	 * 
	 * @return The size of the RAF.
	 * 
	 * @throws IOException Reading RAF may throw exception
	 */
	static public long getRAFSize() throws IOException {
		return RAF.size();
	}
}
