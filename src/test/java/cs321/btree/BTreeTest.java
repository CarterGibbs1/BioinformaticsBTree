package cs321.btree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class BTreeTest
{
	//folder location that RAFs and dumps go to
	static private final String TESTS_FOLDER = "./results/tests/TEST_";
	static private final Random random = new Random();
	static private final String VALID_LETTERS = "atcg";
	
	//=================================================================================================================
	//                                  Utility Methods
	//=================================================================================================================
	
	/**
	 * Generate a random number of sequences of random length in the given range.
	 * Sequences can be 10 to 30 letters long.
	 * 
	 * @param minNumSeq Minimum (inclusive) number of sequences
	 * @param maxNumSeq Maximum (exclusive) number of sequences
	 * 
	 * @return List of randomly generated sequences
	 */
	static private ArrayList<String> generateRandomSequences(int minNumSeq, int maxNumSeq){
		int numSeq = random.nextInt(minNumSeq, maxNumSeq);
		int lengthSeq = random.nextInt(10, 31);
		ArrayList<String> sequences = new ArrayList<String>();
		
		//construct numSeq amount of random sequences
		String sequence;
		for(int i = 0; i < numSeq; i++) {
			sequence = "";
			
			for(int j = 0; j < lengthSeq; j++) {
				sequence = sequence + VALID_LETTERS.charAt(random.nextInt(0, 4));
			}
			
			sequences.add(sequence);
		}
		return sequences;
	}
	
	/**
	 * Recursively mergesort the given list of TreeObjects in increasing order.
	 * 
	 * @param list ArrayList to sort
	 */
	static private void mergesortTreeObject(ArrayList<TreeObject<String>> list){
		//base case: less than 2 elements
		if(list.size() < 2) {
			return;
		}
		
		//general case
		ArrayList<TreeObject<String>> left = new ArrayList<TreeObject<String>>();
		ArrayList<TreeObject<String>> right = new ArrayList<TreeObject<String>>();
		
		//split the list into two equally sized lists
		while(!list.isEmpty()) {
			if(left.size() < list.size()) {
				left.add(list.remove(0));
			}
			else {
				right.add(list.remove(0));
			}
		}
		
		//recursively sort left and right
		mergesortTreeObject(left);
		mergesortTreeObject(right);
		
		//reconstructing the list
		/* while right has elements, "move" the first element in right to the end of the sorted list if it is greater than
		 * the first element of left. If left is greater, move it's first element to the end of the sorted list. If either
		 * left or right runs out of elements, move the rest of the elements of the remaining list to the sorted list.
		 */
		while(!right.isEmpty()) {
			if(!left.isEmpty()) {
				list.add(left.get(0).compare(right.get(0)) > 0 ? right.remove(0) : left.remove(0));
			}
			else {
				list.add(right.remove(0));
			}
		}
		while(!left.isEmpty()) {
			list.add(left.remove(0));
		}
	}
	
	/**
	 * Test that the methods used to randomly generate sequences and sort them
	 * function correctly.
	 * <p>
	 * NOTE: Not directly related to BTree, but if this test fails then other
	 * tests using random sequences will not function properly.
	 */
	@Test
	public void randomGenerator_mergesort() {
		ArrayList<String> randSeq = generateRandomSequences(5, 10);
		
		//test that there are between 5 and 9 sequences
		assert(randSeq.size() > 4 && randSeq.size() < 10);
		
		
		int seqLength = randSeq.get(0).length();
		
		//test that all sequences are the same length
		for(int i = 0; i < randSeq.size(); i++) {
			assert(seqLength == randSeq.get(i).length());
		}
		
		//create ArrayList of TreeObjects from sequences
		ArrayList<TreeObject<String>> treeObjects = new ArrayList<TreeObject<String>>();
		for(int i = 0; i < randSeq.size(); i++) {
			treeObjects.add(new TreeObject<String>(randSeq.get(i), 1));
		}
		
		//sort TreeObjects
		mergesortTreeObject(treeObjects);
		
		//test that treeObjects is sorted in increasing order
		for(int i = 0; i < treeObjects.size() - 1; i++) {
			assert(treeObjects.get(i).compare(treeObjects.get(i + 1)) <= 0);
		}
	}
	
	//test 1
//	@Test
//	public void btreeTestOne() {
//		BTreeAaron bT = new BTreeAaron(2);
//		bT.insert("t");
//		bT.insert("g");
//		bT.insert("c");
//		assertEquals(3, bT.getNumNodes());
//		assertEquals("cgt", bT.toString());
//	}
	//test 2
	
    // HINT:
    //  instead of checking all intermediate states of constructing a tree
    //  you can check the final state of the tree and
    //  assert that the constructed tree has the expected number of nodes and
    //  assert that some (or all) of the nodes have the expected values
    @Test
    public void btreeDegree4Test()
    {
//        //TODO instantiate and populate a bTree object
//        int expectedNumberOfNodes = TBD;
//
//        // it is expected that these nodes values will appear in the tree when
//        // using a level traversal (i.e., root, then level 1 from left to right, then
//        // level 2 from left to right, etc.)
//        String[] expectedNodesContent = new String[]{
//                "TBD, TBD",      //root content
//                "TBD",           //first child of root content
//                "TBD, TBD, TBD", //second child of root content
//        };
//
//        assertEquals(expectedNumberOfNodes, bTree.getNumberOfNodes());
//        for (int indexNode = 0; indexNode < expectedNumberOfNodes; indexNode++)
//        {
//            // root has indexNode=0,
//            // first child of root has indexNode=1,
//            // second child of root has indexNode=2, and so on.
//            assertEquals(expectedNodesContent[indexNode], bTree.getArrayOfNodeContentsForNodeIndex(indexNode).toString());
//        }
    }
	//end of BTree tests
    
    //=================================================================================================================
	//                                  Testing BNodes in Memory Only
	//=================================================================================================================

    /**
     * Test that a BNode correctly inserts given elements in sorted order.
     */
    @Test
    public void singleBNode_TestInsertion() {
    	//                       57  12  8   59  7   10  44
    	String inputSequences = "TGC ATA AGA TGT ACT AGG GTA".toLowerCase();
    	
    	//instantiate and populate BNode with inputLetters
    	TestBNode<String> testNode = new TestBNode<String>(new TreeObject<String>(inputSequences.substring(0, 3), 1));
    	for(int i = 4; i < inputSequences.length(); i += 4) {
    		testNode.insert(new TreeObject<String>(inputSequences.substring(i, i + 3), 1));
    	}
    	
    	//see if the BNode contains sequences in order in long value
    	assertEquals(testNode.toString(), "7 8 10 12 44 57 59");
    }
    
    /**
     * Test that a ROOT BNode correctly splits itself into three new nodes,
     * the new root and it's two children.
     */
    @Test
    public void BNode_TestSplit() {
    	//                     59   108  14   103  46
    	String inputSequences = "ATGT CGTA AATG CGCT AGTG".toLowerCase();
    	TestBNode<String> parent;
    	TestBNode<String> rightChild;
    	
    	//instantiate and populate BNode with inputLetters
    	TestBNode<String> testNode = new TestBNode<String>(new TreeObject<String>(inputSequences.substring(0, 4), 1));
    	for(int i = 5; i < inputSequences.length(); i += 5) {
    		testNode.insert(new TreeObject<String>(inputSequences.substring(i, i + 4), 1));
    	}
    	
    	//split BNode and save parent and rightChild
    	parent = testNode.split();
    	rightChild = parent.getSubtree(new TreeObject<String>("tttt", 1));
    	
    	assertEquals(parent.toString(), "59");
    	assertEquals(testNode.toString(), "14 46");
    	assertEquals(rightChild.toString(), "103 108");
    }
    
    /**
     * Test that when a BNode is not full, isFull() will return false and
     * when BNode is full, isFull() will return true.
     */
    @Test
    public void BNode_TestIsFull() {
    	String inputSequences = "ATGTCTGACCGT".toLowerCase();
    	int degree = 7;
    	
    	//instantiate and populate BNode with inputLetters
    	TestBNode<String> testNode = new TestBNode<String>(new TreeObject<String>(inputSequences.substring(0, 1), 1));
    	for(int i = 1; i < inputSequences.length(); i++) {
    		testNode.insert(new TreeObject<String>(inputSequences.substring(i, i + 1), 1));
    	}
    	
    	//testNode is not full
    	assert(!testNode.isFull(degree));
    	
    	//testNode is now full
    	testNode.insert(new TreeObject<String>("a", 1));
    	assert(testNode.isFull(degree));
    }
    
    /**
     * Test using a rudimentary BTree that the BNode methods isFull(), insert(key),
     * and split() function correctly resulting in a BTree with the correct number of
     * nodes and correct height.
     */
    @Test
    public void BNode_CorrectHeightAndNodeCount() {
    	String inputSequences = "ATGTCTGACCGTGACTTACGAAG".toLowerCase();
    	int degree = 2;
    	
    	//instantiate and BNode root
    	TestBNode<String> root = new TestBNode<String>(new TreeObject<String>(inputSequences.substring(0, 1), 1));
    	TestBNode<String> currentNode;
    	
    	//create a rudimentary BTree to insert into while counting the total BNode amount and the height
    	int height = 1;
    	int totalNodes = 1;
    	TreeObject<String> key;
    	for(int i = 1; i < inputSequences.length(); i++) {
    		currentNode = root;
    		key = new TreeObject<String>(inputSequences.substring(i, i + 1), 1);
    		
    		if(root.isFull(degree)) {
	    		root = currentNode = currentNode.split();
	    		totalNodes += 2;
	    		height++;
    		}
    		
    		//get to appropriate leaf BNode
    		while(!currentNode.isLeaf()) {
    			currentNode = currentNode.getSubtree(key);
    				
    			//if the currentNode is full, split it
    			if(currentNode.isFull(degree)) {
	    			currentNode = currentNode.split();
	    			totalNodes++;
    			}
    		}
    		
    		//once at LEAF, insert key
    		currentNode.insert(key);
    		
    		//TODO: might need to check and split the leaf node we just inserted into
//    		if(currentNode.isFull()) {
//    			currentNode = currentNode.split();
//    			totalNodes++;
//    		}
    	}
    	
    	assertEquals(totalNodes, 16); //18 if we perform a split on the leaf nodes we just inserted into
    	assertEquals(height, 4);
    }
    
    
    //=================================================================================================================
	//                               Testing TreeObjects in Memory Only
	//=================================================================================================================

    
	/**
	 * Test most public methods for TreeObject with a variety of cases
	 */
	@Test
	public void TreeObject_PublicMethods() {
		TreeObject<String> tO = new TreeObject<String>("tcacgaggtc", 5);
		long key = Long.parseLong("11010001100010101101", 2);
		assertEquals(tO.getKey(), key);
		assertEquals(tO.withZeros(), "11010001100010101101");
		assertEquals(tO.getFrequency(), 5);

		tO.setFrequency(6);
		assertEquals(tO.getFrequency(), 6);
		tO.setFrequency(5);
		TreeObject<String> tOTwo = new TreeObject<String>("tcacgaggta", 5);
		assert(tO.compare(tOTwo) > 0);
		assertEquals(tO.toString(), "tcacgaggtc: 5");
	}
	
	
    //=================================================================================================================
	//                                  Testing BNodes using RAF
	//=================================================================================================================
	
	/**
	 * Test that a single BNode with a few keys inserted and that is then written
	 * to the RAF is the same as the one stored in memory.
	 */
	@Test
	public void BNode_RAF_InsertWriteRead() {
		try {
	    	//                       57  12  8   59  7   10  44
	    	String inputSequences = "TGC ATA AGA TGT ACT AGG GTA".toLowerCase();
	    	
	    	//delete old RAF and set new RAF, degree, and byteBuffer. Important that they are done in this order
	    	BReadWrite.setRAF(TESTS_FOLDER + "BNode_RAF_InsertWriteRead", true);
	    	BNode.setDegree(10);
	    	BReadWrite.setBuffer(BNode.getDiskSize());
	    	
	    	//instantiate and populate BNode with inputLetters
	    	BNode memoryNode = new BNode(new TreeObject<String>(inputSequences.substring(0, 3), 1), BTree.getDiskSize());
	    	for(int i = 4; i < inputSequences.length(); i += 4) {
	    		memoryNode.insert(new TreeObject<String>(inputSequences.substring(i, i + 3), 1));
	    	}
	    	
	    	//read BNode in RAF
	    	BNode readNode = BReadWrite.readBNode(BTree.getDiskSize());
	    	
	    	//see if memoryNode contains sequences in order in long value,
	    	assertEquals(memoryNode.toString(), "7 8 10 12 44 57 59");
	    	//then check if readNode from the RAF is the same as the memoryNode
	    	assertEquals(readNode.toString(), memoryNode.toString());
		}
		catch(IOException e) {
			assert(false);
		}
	}
	
	/**
	 * Test that a split BNode correctly writes the new root and new child to the
	 * RAF, maintaining the correct keys, children, and properties.
	 */
	@Test
	public void BNode_RAF_SplitWriteRead() {
		try {
			//                       180  59   108  14   103  46   118
			String inputSequences = "GTCA ATGT CGTA AATG CGCT AGTG CTCG".toLowerCase();
			BNode readNode;
			BNode readParent;
			BNode readRight;
	    	
	    	//delete old RAF and set new RAF, degree, and byteBuffer. Important that they are done in this order
	    	BReadWrite.setRAF(TESTS_FOLDER + "BNode_RAF_SplitWriteRead", true);
	    	BNode.setDegree(4);
	    	BReadWrite.setBuffer(BNode.getDiskSize());
	    	
	    	//instantiate and populate BNode with inputLetters
	    	BNode memoryNode = new BNode(new TreeObject<String>(inputSequences.substring(0, 4), 1), BTree.getDiskSize());
	    	for(int i = 5; i < inputSequences.length(); i += 5) {
	    		memoryNode.insert(new TreeObject<String>(inputSequences.substring(i, i + 4), 1));
	    	}
	    	
	    	//perform split and read the returned address (the new parent/root)
	    	readParent = BReadWrite.readBNode(memoryNode.split());
	    	//read the left and right children from parentNode
	    	readNode = BReadWrite.readBNode(memoryNode.getAddress());
	    	readRight = BReadWrite.readBNode(readParent.getChildren().get(1));
	    	
	    	//see if memoryNode contains sequences in order in long value,
	    	assertEquals(memoryNode.toString(), "14 46 59");
	    	
	    	//then check if the read nodes contain the correct elements
	    	assertEquals(readNode.toString(), memoryNode.toString());
	    	assertEquals(readParent.toString(), "103");
	    	assertEquals(readRight.toString(), "108 118 180");
	    	
	    	//check that the read nodes contain the correct properties
	    	assert(memoryNode.isLeaf() && !memoryNode.isRoot() && !memoryNode.isFull() && memoryNode.getN() == 3);
	    	assert(readNode.isLeaf() && !readNode.isRoot() && !readNode.isFull() && readNode.getN() == 3);
	    	assert(readRight.isLeaf() && !readRight.isRoot() && !readRight.isFull() && readRight.getN() == 3);
	    	assert(!readParent.isLeaf() && readParent.isRoot() && !readParent.isFull() && readParent.getN() == 1);
		}
		catch(IOException e) {
			assert(false);
		}
	}
	
	/**
	 * Tests that after inserting a random number of sequences into a
	 * rudimentary BTree of a random degree, the size of the RAF is
	 * close to the number of nodes * BNode.getDiskSize. This indicates
	 * that methods such as insert and split don't overwrite other BNodes.
	 * <p>
	 * RANDOM: This test is random and thus, the RAF will change every run.
	 */
	@Test
	public void BNode_RAF_RAFAppropriateSize() {
		try {
			ArrayList<String> inputSequences = generateRandomSequences(20000, 30000);
			ArrayList<TreeObject<String>> insertedSequences = new ArrayList<TreeObject<String>>();
	    	
	    	//delete old RAF and set new RAF, degree, and byteBuffer. Important that they are done in this order
	    	BReadWrite.setRAF(TESTS_FOLDER + "BNode_RAF_RAFAppropriateSize", true);
	    	BNode.setDegree(random.nextInt(3, 7));
	    	BReadWrite.setBuffer(BNode.getDiskSize());
	    	
	    	
	    	//create a rudimentary BTree to insert into while counting the total BNode amount
	    	insertedSequences.add(new TreeObject<String>(inputSequences.get(0), 1));
	    	long root = BTree.getDiskSize();
	    	
	    	//create and write initial BNode to RAF
	    	BNode currentNode = null;
	    	BReadWrite.writeBNode(new BNode(insertedSequences.get(0), root));
	    	int numNodes = 1; ArrayList<BNode> x = new ArrayList<BNode>();
	    	int y = 0;
	    	
	    	for(int i = 1; i < inputSequences.size(); i++) {
	    		y = 0;
	    		x.clear();
	    		currentNode = BReadWrite.readBNode(root);
	    		insertedSequences.add(new TreeObject<String>(inputSequences.get(i), 1));
	    		x.add(currentNode);
	    		
	    		//if currentNode(root) is full, split it
	    		if(currentNode.isFull()) {
	    			root = currentNode.split();
		    		currentNode = BReadWrite.readBNode(root);
		    		numNodes += 2;
		    		y++;
	    		}
	    		
	    		//get to appropriate leaf BNode
	    		while(!currentNode.isLeaf()) {
	    			if(currentNode.getKeys().contains(insertedSequences.get(i))) {
	    				break;
	    			}
	    			
	    			currentNode = BReadWrite.readBNode(currentNode.getSubtree(insertedSequences.get(i)));
	    			x.add(currentNode);
	
	    			//if the currentNode is full, split it
	    			if(currentNode.isFull()) {
		    			currentNode = BReadWrite.readBNode(currentNode.split());
		    			numNodes++;
		    			y++;
	    			}
	    		}
	    		
	    		//once at LEAF, insert key
	    		currentNode.insert(insertedSequences.get(i));
	    	}
	    	
	    	//the RAF size should be the DiskSize of a numNodes amount of BNodes with a MOE of 1 BNode
	    	assert(BReadWrite.getRAFSize() > ((numNodes - 1) * BNode.getDiskSize() + BTree.getDiskSize()) && 
	    		   BReadWrite.getRAFSize() < ((numNodes + 1) * BNode.getDiskSize() + BTree.getDiskSize()));
	    	
	    	for(int i =0; i< currentNode.getN(); i++) {
	    		System.out.println(currentNode.getKeys().get(i).toString());
	    	}
	    	currentNode = BReadWrite.readBNode(root);
	    	System.out.println();
	    	for(int i =0; i< currentNode.getN(); i++) {
	    		System.out.println(currentNode.getKeys().get(i).toString());
	    	}
		}
		catch(IOException e) {
			System.out.println(e);
			assert(false);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
