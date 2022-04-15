package cs321.btree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

public class BReadWrite {

	private static FileChannel RAF;
	private static ByteBuffer buffer;
	
	
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
	 */
	static public <E> void writeBNode(BNode<E> node, long address) throws IOException{
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
	
	/**
	 * Reads and returns the BNode stored at the given address.
	 * 
	 * @param <E>     Generic type this BTree holds
	 * @param address Address that BNode is located
	 * 
	 * @return BNode stored in RAF at the given address
	 * 
	 * @throws IOException Reading RAF may throw exception
	 */
	static public <E> BNode<E> readBNode(long address) throws IOException {
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
	
	/**
	 * Write the given BTree to the RAF. Always rights to
	 * address 0.
	 * 
	 * @param <E>  Generic type this BTree holds
	 * @param tree BTree to write to RAF
	 * 
	 * @throws IOException Reading RAF may throw exception
	 */
	static public <E> void writeBTree(BTree<E> tree) throws IOException{
		//set buffer capacity to match BTree size TODO: static BTree disk size method
//		buffer = ByteBuffer.allocateDirect(BNode.getDiskSize(BTree.));
		
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
		buffer = ByteBuffer.allocateDirect(BNode.getDiskSize());
	}
	
	/**
	 * Returns the BTree held at the beginning of the RAF
	 * and sets the static BNode degree to match the BTree
	 * degree.
	 * 
	 * @param <E> Generic type this BTree holds
	 * 
	 * @return BTree at beginning of RAF
	 * 
	 * @throws IOException Reading RAF may throw exception
	 */
	static public <E> BTree<E> readBTree() throws IOException {
		//set buffer capacity to match BTree size TODO: static BTree disk size method
//		buffer = ByteBuffer.allocateDirect(BNode.getDiskSize(BTree.));
		
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
		buffer = ByteBuffer.allocateDirect(BNode.getDiskSize());
		
		return null;
	}
}
















