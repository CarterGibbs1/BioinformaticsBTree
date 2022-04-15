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
 * Contains static methods used to read and write to a Random
 * Access File. Used in conjunction with a BTree to allow the
 * storing of data in the computers storage.
 * 
 * @author  Mesa Greear
 * @version Spring 2022
 */
public class BReadWrite {

	private static FileChannel RAF = null;
	private static ByteBuffer buffer;
	
	/**
	 * Sets this class to read and write from the given Random
	 * Access File. If the RAF does not exist a new one will be
	 * created with the given name.
	 * <p>
	 * This method must be called at least once before using
	 * Write and Read methods. Not doing so will result in
	 * IllegalStateExceptions being thrown.
	 * 
	 * @param fileName Name of file to read from and possibly
	 *                 create as well
	 *                 
	 * @throws IOException Creating RAF may throw exception
	 */
	@SuppressWarnings("resource")
	static public void setRAF(String fileName) throws IOException {
		File file = new File(fileName);
		RandomAccessFile RAFRaw = null;
		
		try {
			//if file doesn't exist, create it
			if(!file.exists()) {
				file.createNewFile();
			}
			
			//get the Random Access File channel and store it
			RAFRaw = new RandomAccessFile(file, "rw");
			RAF = RAFRaw.getChannel();
		}
		catch (Exception e) {
			if(RAFRaw != null) {
				RAFRaw.close();
			}
			throw e;
		}
	}
	
	/**
	 * Set the capacity of the ByteBuffer, i.e. how many bytes are
	 * written/read from the RAF.
	 * <p>
	 * This method must be called at least once before using
	 * Write and Read methods if BTree was not read from RAF.
	 * Not doing so will result in IllegalStateExceptions being
	 * thrown.
	 * 
	 * @param capacity The buffer capacity in bytes
	 */
	static public void setBuffer(int capacity) {
		buffer = ByteBuffer.allocateDirect(capacity);
	}
	
	
	
	/**
	 * Write the given BNode to the RAF at the specified address.
	 * Writes the n and then the lists: child0, key0, child1, key1,
	 * child2 ... childn, keyn, childn + 1.
	 * 
	 * @param <E>     Generic type this BTree holds
	 * @param node    BNode to write to RAF
	 * @param address Address to write this BNode to
	 * 
	 * @throws IOException Writing to RAF may throw exception
	 * @throws BufferOverflowException Indicates buffer capacity was incorrect for what is was writing
	 * @throws IllegalStateException If thrown, it's likely
	 *                               RAF or buffer have not
	 *                               been set
	 */
	static public <E> void writeBNode(BNode<E> node, long address) throws IOException, BufferOverflowException, IllegalStateException{
		try {
			//start at address and make buffer ready to read
			RAF.position(address);
			buffer.clear();
			
			//write parent
			buffer.putLong(address);
			//store and write n
			int n = node.getN();
			buffer.putInt(n);
			
			//get children and keys
			LinkedList<Long> children = node.getChildren();
			LinkedList<TreeObject<E>> keys = node.getKeys();
			
			//write the children and keys in alternating order
			buffer.putLong(children.get(0));
			for(int i = 0; i < n; i++) {
				buffer.putLong(keys.get(i).getKey());
				buffer.putInt(keys.get(i).getFrequency());
				
				buffer.putLong(children.get(i + 1));
			}
			
			//make buffer ready to write and then write to RAF
			buffer.flip();
			RAF.write(buffer);
		}
		catch(NullPointerException e) {
			throw new IllegalStateException(e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}
	}
	
	/**
	 * Reads and returns the BNode stored at the given address.
	 * 
	 * @param <E>     Generic type this BTree holds
	 * @param address Address that BNode is located
	 * 
	 * @return BNode stored in RAF at the given address
	 * 
	 * @throws IOException Reading RAF may throw exception
	 * @throws BufferUnderflowException Indicates buffer capacity was incorrect for what is was reading
	 * @throws IllegalStateException If thrown, it's likely
	 *                               RAF or buffer have not
	 *                               been set
	 */
	static public <E> BNode<E> readBNode(long address) throws IOException, BufferUnderflowException, IllegalStateException {
		try {
			//start at address and make buffer ready to read
			RAF.position(address);
			buffer.clear();
			RAF.read(buffer);
			buffer.flip();
			
			//get parent and n
			long parent = buffer.getLong();
			int n = buffer.getInt();
			
			//get first key and two children
			long leftChild = buffer.getLong();
			TreeObject<E> initialKey = new TreeObject<E>(null, buffer.getInt()); //TODO: additional TreeObject constructor
			long rightChild = buffer.getLong();
			
			//construct the return BNode and insert the other n - 1 keys and children
			BNode<E> retNode = new BNode<E>(initialKey, parent, leftChild, rightChild);
			for(int i = 1; i < n; i++) {
				retNode.insert(new TreeObject<E>(null, buffer.getInt()), buffer.getLong());
			}
			
			return retNode;
		}
		catch(NullPointerException e) {
			throw new IllegalStateException(e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}
	}
	
	/**
	 * Write the given BTree to the RAF. Always rights to
	 * address 0. Automatically sets the buffer capacity to
	 * the BNode disk size after running.
	 * 
	 * @param <E>  Generic type this BTree holds
	 * @param tree BTree to write to RAF
	 * 
	 * @throws IOException Reading RAF may throw exception
	 * @throws BufferOverflowException Indicates buffer capacity was incorrect for what is was writing
	 * @throws IllegalStateException If thrown, it's likely
	 *                               RAF or buffer have not
	 *                               been set
	 */
	static public <E> void writeBTree(BTree<E> tree) throws IOException, BufferOverflowException, IllegalStateException{
		try {
			//set buffer capacity to match BTree size TODO: static BTree disk size method
			
			//start at 0 and make buffer ready to read
			RAF.position(0);
			buffer.clear();
			
			//write to RAF: TODO: BTree get methods & maybe more info to write
			buffer.putLong(-1); //root address
			buffer.putInt(-1);  //degree
			buffer.putInt(-1);  //frequency
	//		buffer.putInt(-1);  //number of nodes? Height?
			
			//make buffer ready to write and then write to RAF
			buffer.flip();
			RAF.write(buffer);
			
			//reset buffer capacity to match BNode size
			setBuffer(BNode.getDiskSize());
		}
		catch(NullPointerException e) {
			throw new IllegalStateException(e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}
	}
	
	/**
	 * Returns the BTree held at the beginning of the RAF
	 * and sets the static BNode degree to match the BTree
	 * degree. Automatically sets the buffer capacity to
	 * the BNode disk size after running.
	 * 
	 * @param <E> Generic type this BTree holds
	 * 
	 * @return BTree at beginning of RAF
	 * 
	 * @throws IOException Reading RAF may throw exception
	 * @throws BufferUnderflowException Indicates buffer capacity was incorrect for what is was reading
	 * @throws IllegalStateException If thrown, it's likely
	 *                               RAF or buffer have not
	 *                               been set
	 */
	static public <E> BTree<E> readBTree() throws IOException, BufferUnderflowException, IllegalStateException {
		try {
			//set buffer capacity to match BTree size TODO: static BTree disk size method
			
			//start at 0 and make buffer ready to read
			RAF.position(0);
			buffer.clear();
			RAF.read(buffer);
			buffer.flip();
			
			//get root address, sequence, and degree TODO: might be in different order/more stuff
			long root = buffer.getInt();
			int k = buffer.getInt();
			int t = buffer.getInt();
			
			//initialize BTree and return TODO: no proper constructor
			BTree<E> retTree = new BTree<E>(t);
			
			//set static BNode degree
			BNode.setDegree(t);
			
			//reset buffer capacity to match BNode size
			setBuffer(BNode.getDiskSize());
			
			return retTree;
		}
		catch(NullPointerException e) {
			throw new IllegalStateException(e.getClass() + " thrown, may indicate that RAF or buffer have not been set properly.");
		}
	}
}
















