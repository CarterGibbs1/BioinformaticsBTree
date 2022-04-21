package cs321.btree;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BTreeTest {
	// folder location that RAFs and dumps go to
	static private final String TESTS_FOLDER = "./results/tests/TEST_";
	static private final Random random = new Random();
	static private final String VALID_LETTERS = "atcg";

	// how many times to run certain Tests, some of these drastically increase run time
	static private int[] timesToRun = new int[] {10, 20, 50, 100, 500, 1000, 5000};
	static private int run_BNode_RAF_RAFAppropriateSize = timesToRun[0];
	static private int run_BTree_RAF_IsSorted = timesToRun[0];
	static private int run_BTree_RAF_Search = timesToRun[1];
	//example
	static private int run_EXAMPLE_LOOPED_TEST = timesToRun[3];

	static private Throwable ex = null;
	static private int currentProgress = 0;
	static private String testName = "";
	
	// create progress bar to show things are working, destroy on completion (doesn't
	//really work on eclipse)
	static private final ProgressBar progress = new ProgressBar(15,
			run_BNode_RAF_RAFAppropriateSize +
			run_BTree_RAF_IsSorted +
			run_BTree_RAF_Search +
			BTreeTest.class.getDeclaredMethods().length - 6 //don't count methods that aren't tests i.e. utility methods
			);

	// =================================================================================================================
	//                                                Utility Methods
	// =================================================================================================================
	
	/**
	 * Gets a random number between the origin (inclusive) and bound
	 * (exclusive).
	 * 
	 * @param origin Minimum (inclusive) number
	 * @param bound  Maximum (exclusive) number
	 * 
	 * @return Random number in given range
	 */
	private static int getRand(int origin, int bound) {
		return (random.nextInt(bound - origin) + origin);
	}
	
	/**
	 * If a test is stopped in the middle due to an exception, this method
	 * will ensure that the exception is still thrown while updating the
	 * progress bar to compensate for the lost tests in a for loop.
	 * 
	 * @param excpectedRuns Times this test is expected to run/loop
	 * 
	 * @throws Throwable The exception thrown by the test
	 */
	private static void progressAndExceptionCheck(int excpectedRuns) throws Throwable {
		for (; progress.getProgress() < excpectedRuns + currentProgress;) {
			progress.increaseProgress();
		}
		
		if(ex != null) {
			throw ex;
		}
	}
	
	/**
	 * Generate a random number of sequences of random length in the given ranges.
	 * 
	 * @param minNumSeq    Minimum (inclusive) number of sequences
	 * @param maxNumSeq    Maximum (exclusive) number of sequences
	 * @param minseqLength Minimum (inclusive) length of sequences > 1
	 * @param maxSeqLength Maximum (exclusive) length of sequences < 33
	 * 
	 * @return List of randomly generated sequences
	 * @return
	 */
	static private ArrayList<String> generateRandomSequences(int minNumSeq, int maxNumSeq, int minseqLength,
			int maxSeqLength) {
		int numSeq = getRand(minNumSeq, maxNumSeq);
		int lengthSeq = getRand(minseqLength, maxSeqLength);
		ArrayList<String> sequences = new ArrayList<String>();

		// construct numSeq amount of random sequences
		String sequence;
		for (int i = 0; i < numSeq; i++) {
			sequence = "";

			for (int j = 0; j < lengthSeq; j++) {
				sequence = sequence + VALID_LETTERS.charAt(getRand(0, 4));
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
	static private void mergesortTreeObject(ArrayList<TreeObject> list) {
		// base case: less than 2 elements
		if (list.size() < 2) {
			return;
		}

		// general case
		ArrayList<TreeObject> left = new ArrayList<TreeObject>();
		ArrayList<TreeObject> right = new ArrayList<TreeObject>();

		// split the list into two equally sized lists
		while (!list.isEmpty()) {
			if (left.size() < list.size()) {
				left.add(list.remove(0));
			} else {
				right.add(list.remove(0));
			}
		}

		// recursively sort left and right
		mergesortTreeObject(left);
		mergesortTreeObject(right);

		// reconstructing the list
		/*
		 * while right has elements, "move" the first element in right to the end of the
		 * sorted list if it is greater than the first element of left. If left is
		 * greater, move it's first element to the end of the sorted list. If either
		 * left or right runs out of elements, move the rest of the elements of the
		 * remaining list to the sorted list.
		 */
		while (!right.isEmpty()) {
			if (!left.isEmpty()) {
				list.add(left.get(0).compare(right.get(0)) > 0 ? right.remove(0) : left.remove(0));
			} else {
				list.add(right.remove(0));
			}
		}
		while (!left.isEmpty()) {
			list.add(left.remove(0));
		}
	}

	/**
	 * Test that the methods used to randomly generate sequences and sort them
	 * function correctly.
	 * <p>
	 * NOTE: Not directly related to BTree, but if this test fails then other tests
	 * using random sequences will not function properly.
	 */
	@Test
	public void randomGenerator_mergesort() {
		try {
			ArrayList<String> randSeq = generateRandomSequences(30, 50, 10, 20);

			// test that there are between 5 and 9 sequences
			assert (randSeq.size() >= 30 && randSeq.size() < 50);

			int seqLength = randSeq.get(0).length();

			// test that all sequences are the same length
			for (int i = 0; i < randSeq.size(); i++) {
				assert (seqLength == randSeq.get(i).length());
			}

			// create ArrayList of TreeObjects from sequences
			ArrayList<TreeObject> treeObjects = new ArrayList<TreeObject>();
			for (int i = 0; i < randSeq.size(); i++) {
				treeObjects.add(new TreeObject(randSeq.get(i), 1));
			}

			// sort TreeObjects
			mergesortTreeObject(treeObjects);

			// test that treeObjects is sorted in increasing order
			for (int i = 0; i < treeObjects.size() - 1; i++) {
				assert (treeObjects.get(i).compare(treeObjects.get(i + 1)) <= 0);
			}
			progress.increaseProgress();
		} catch (Exception e) {
			progress.increaseProgress();
			throw e;
		}
	}
	
	// =================================================================================================================
	//                                              Testing BNodes in Memory Only
	// =================================================================================================================

	/**
	 * Test that a BNode correctly inserts given elements in sorted order.
	 */
	@Test
	public void singleBNode_TestInsertion() {
		try {

			// 57 12 8 59 7 10 44
			String inputSequences = "TGC ATA AGA TGT ACT AGG GTA".toLowerCase();

			// instantiate and populate BNode with inputLetters
			TestBNode<String> testNode = new TestBNode<String>(new TreeObject(inputSequences.substring(0, 3), 1));
			for (int i = 4; i < inputSequences.length(); i += 4) {
				testNode.insert(new TreeObject(inputSequences.substring(i, i + 3), 1));
			}

			// see if the BNode contains sequences in order in long value
			assertEquals(testNode.toString(), "7 8 10 12 44 57 59");

			progress.increaseProgress();
		} catch (Exception e) {
			progress.increaseProgress();
			throw e;
		}
	}

	/**
	 * Test that a ROOT BNode correctly splits itself into three new nodes, the new
	 * root and it's two children.
	 */
	@Test
	public void BNode_TestSplit() {
		try {
			// 59 108 14 103 46
			String inputSequences = "ATGT CGTA AATG CGCT AGTG".toLowerCase();
			TestBNode<String> parent;
			TestBNode<String> rightChild;

			// instantiate and populate BNode with inputLetters
			TestBNode<String> testNode = new TestBNode<String>(new TreeObject(inputSequences.substring(0, 4), 1));
			for (int i = 5; i < inputSequences.length(); i += 5) {
				testNode.insert(new TreeObject(inputSequences.substring(i, i + 4), 1));
			}

			// split BNode and save parent and rightChild
			parent = testNode.split();
			rightChild = parent.getSubtree(new TreeObject("tttt", 1));

			assertEquals(parent.toString(), "59");
			assertEquals(testNode.toString(), "14 46");
			assertEquals(rightChild.toString(), "103 108");

			progress.increaseProgress();
		} catch (Exception e) {
			progress.increaseProgress();
			throw e;
		}
	}

	/**
	 * Test that when a BNode is not full, isFull() will return false and when BNode
	 * is full, isFull() will return true.
	 */
	@Test
	public void BNode_TestIsFull() {
		try {
			String inputSequences = "ATGTCTGACCGT".toLowerCase();
			int degree = 7;

			// instantiate and populate BNode with inputLetters
			TestBNode<String> testNode = new TestBNode<String>(new TreeObject(inputSequences.substring(0, 1), 1));
			for (int i = 1; i < inputSequences.length(); i++) {
				testNode.insert(new TreeObject(inputSequences.substring(i, i + 1), 1));
			}

			// testNode is not full
			assert (!testNode.isFull(degree));

			// testNode is now full
			testNode.insert(new TreeObject("a", 1));
			assert (testNode.isFull(degree));

			progress.increaseProgress();
		} catch (Exception e) {
			progress.increaseProgress();
			throw e;
		}
	}

	/**
	 * Test using a rudimentary BTree that the BNode methods isFull(), insert(key),
	 * and split() function correctly resulting in a BTree with the correct number
	 * of nodes and correct height.
	 */
	@Test
	public void BNode_CorrectHeightAndNodeCount() {
		try {
			String inputSequences = "ATGTCTGACCGTGACTTACGAAG".toLowerCase();
			int degree = 2;

			// instantiate and BNode root
			TestBNode<String> root = new TestBNode<String>(new TreeObject(inputSequences.substring(0, 1), 1));
			TestBNode<String> currentNode;

			// create a rudimentary BTree to insert into while counting the total BNode
			// amount and the height
			int height = 1;
			int totalNodes = 1;
			TreeObject key;
			for (int i = 1; i < inputSequences.length(); i++) {
				currentNode = root;
				key = new TreeObject(inputSequences.substring(i, i + 1), 1);

				if (root.isFull(degree)) {
					root = currentNode = currentNode.split();
					totalNodes += 2;
					height++;
				}

				// get to appropriate leaf BNode
				while (!currentNode.isLeaf()) {
					currentNode = currentNode.getSubtree(key);

					// if the currentNode is full, split it
					if (currentNode.isFull(degree)) {
						currentNode = currentNode.split();
						totalNodes++;
					}
				}

				// once at LEAF, insert key
				currentNode.insert(key);

				// TODO: might need to check and split the leaf node we just inserted into
//    		if(currentNode.isFull()) {
//    			currentNode = currentNode.split();
//    			totalNodes++;
//    		}
			}

			assertEquals(totalNodes, 16); // 18 if we perform a split on the leaf nodes we just inserted into
			assertEquals(height, 4);
			
			progress.increaseProgress();
		} catch (Exception e) {
			progress.increaseProgress();
			throw e;
		}
	}

	// =================================================================================================================
	//                                          Testing TreeObjects in Memory Only
	// =================================================================================================================

	/**
	 * Test most public methods for TreeObject with a variety of cases
	 */
	@Test
	public void TreeObject_PublicMethods() {
		try {
			TreeObject tO = new TreeObject("tcacgaggtc", 5);
			long key = Long.parseLong("11010001100010101101", 2);
			assertEquals(tO.getKey(), key);
//			assertEquals(tO.withZeros(), "11010001100010101101");
			assertEquals(tO.getFrequency(), 5);

			tO.setFrequency(6);
			assertEquals(tO.getFrequency(), 6);
			tO.setFrequency(5);
			TreeObject tOTwo = new TreeObject("tcacgaggta", 5);
			assert (tO.compare(tOTwo) > 0);
//			assertEquals(tO.toString(), "tcacgaggtc: 5");

			progress.increaseProgress();
		} catch (Exception e) {
			progress.increaseProgress();
			throw e;
		}
	}

	// =================================================================================================================
	//                                           Testing BNodes using RAF
	// =================================================================================================================

	/**
	 * Test that a single BNode with a few keys inserted and that is then written to
	 * the RAF is the same as the one stored in memory.
	 * 
	 * @throws Throwable 
	 */
	@Test
	public void BNode_RAF_InsertWriteRead() throws Throwable {
		testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method
		try {
			// 57 12 8 59 7 10 44
			String inputSequences = "TGC ATA AGA TGT ACT AGG GTA".toLowerCase();

			// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
			// are done in this order
			BReadWrite.setRAF(TESTS_FOLDER + testName, true);
			BNode.setDegree(10);
			BReadWrite.setBuffer(BNode.getDiskSize());

			// instantiate and populate BNode with inputLetters
			BNode memoryNode = new BNode(new TreeObject(inputSequences.substring(0, 3), 1), BTree.getDiskSize());
			for (int i = 4; i < inputSequences.length(); i += 4) {
				memoryNode.insert(new TreeObject(inputSequences.substring(i, i + 3), 1));
			}

			// read BNode in RAF
			BNode readNode = BReadWrite.readBNode(BTree.getDiskSize());

			// see if memoryNode contains sequences in order in long value,
			assertEquals(memoryNode.toString(), "7 8 10 12 44 57 59");
			// then check if readNode from the RAF is the same as the memoryNode
			assertEquals(readNode.toString(), memoryNode.toString());

			progress.increaseProgress();
		} catch (Throwable e) {
			progress.increaseProgress();
			throw e;
		}
	}

	/**
	 * Test that a split BNode correctly writes the new root and new child to the
	 * RAF, maintaining the correct keys, children, and properties.
	 * 
	 * @throws Throwable 
	 */
	@Test
	public void BNode_RAF_SplitWriteRead() throws Throwable {
		testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method
		try {
			// 180 59 108 14 103 46 118
			String inputSequences = "GTCA ATGT CGTA AATG CGCT AGTG CTCG".toLowerCase();
			BNode readNode;
			BNode readParent;
			BNode readRight;

			// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
			// are done in this order
			BReadWrite.setRAF(TESTS_FOLDER + testName, true);
			BNode.setDegree(4);
			BReadWrite.setBuffer(BNode.getDiskSize());

			// instantiate and populate BNode with inputLetters
			BNode memoryNode = new BNode(new TreeObject(inputSequences.substring(0, 4), 1), BTree.getDiskSize());
			for (int i = 5; i < inputSequences.length(); i += 5) {
				memoryNode.insert(new TreeObject(inputSequences.substring(i, i + 4), 1));
			}

			// perform split and read the returned address (the new parent/root)
			readParent = BReadWrite.readBNode(memoryNode.split());
			// read the left and right children from parentNode
			readNode = BReadWrite.readBNode(memoryNode.getAddress());
//			readRight = BReadWrite.readBNode(readParent.getChildren().get(1));
			readRight = BReadWrite.readBNode(readParent.getChild(1));

			// see if memoryNode contains sequences in order in long value,
			assertEquals(memoryNode.toString(), "14 46 59");

			// then check if the read nodes contain the correct elements
			assertEquals(readNode.toString(), memoryNode.toString());
			assertEquals(readParent.toString(), "103");
			assertEquals(readRight.toString(), "108 118 180");

			// check that the read nodes contain the correct properties
			assert (memoryNode.isLeaf() && !memoryNode.isRoot() && !memoryNode.isFull() && memoryNode.getN() == 3);
			assert (readNode.isLeaf() && !readNode.isRoot() && !readNode.isFull() && readNode.getN() == 3);
			assert (readRight.isLeaf() && !readRight.isRoot() && !readRight.isFull() && readRight.getN() == 3);
			assert (!readParent.isLeaf() && readParent.isRoot() && !readParent.isFull() && readParent.getN() == 1);
			
			progress.increaseProgress();
		} catch (Throwable e) {
			progress.increaseProgress();
			throw e;
		}
	}

	/**
	 * Tests that after inserting a random number of sequences into a rudimentary
	 * BTree of a random degree, the size of the RAF is close to the number of nodes
	 * * BNode.getDiskSize. This indicates that methods such as insert and split
	 * don't overwrite other BNodes.
	 * <p>
	 * RANDOM: This test is random and thus, the RAFs will change every run.
	 * 
	 * @throws Throwable 
	 */
	@Test
	public void BNode_RAF_RAFAppropriateSize() throws Throwable {
		currentProgress = progress.getProgress();
		ex = null;
		testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method

		try {
			for (int k = 0; k < run_BNode_RAF_RAFAppropriateSize; k++) {// <--- THIS WILL TAKE A LONG TIME IF REALLY BIG

				ArrayList<String> inputSequences = generateRandomSequences(20000 / 3, 30000 / 3, 2, 32);// <--- THIS
																										// WILL TAKE A
																										// LONG TIME IF
																										// REALLY BIG
				ArrayList<TreeObject> insertedSequences = new ArrayList<TreeObject>();

				// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
				// are done in this order
				BReadWrite.setRAF(TESTS_FOLDER + testName + k, true);
				BNode.setDegree(getRand(3, 7));
				BReadWrite.setBuffer(BNode.getDiskSize());

				// create a rudimentary BTree to insert into while counting the total BNode
				// amount
				insertedSequences.add(new TreeObject(inputSequences.get(0), 1));
				long root = BTree.getDiskSize();

				// create and write initial BNode to RAF
				BNode currentNode = null;
				BReadWrite.writeBNode(new BNode(insertedSequences.get(0), root));
				int numNodes = 1;
				long nextNode;

				// debugging variables
				ArrayList<BNode> x = new ArrayList<BNode>();
				int y = 0;

				insertLoop: for (int i = 1; i < inputSequences.size(); i++) {
					y = 0;
					x.clear();
					currentNode = BReadWrite.readBNode(root);
					insertedSequences.add(new TreeObject(inputSequences.get(i), 1));
					x.add(currentNode);

					// if currentNode(root) is full, split it
					if (currentNode.isFull()) {
						root = currentNode.split();
						currentNode = BReadWrite.readBNode(root);
						numNodes += 2;
						y++;
					}

					// get to appropriate leaf BNode
					while (!currentNode.isLeaf()) {
						// if the object to insert is in currentNode, break and insert it
						nextNode = currentNode.getElementLocation(insertedSequences.get(i));
						if (nextNode == currentNode.getAddress()) {
							continue insertLoop;
						}
						currentNode = BReadWrite.readBNode(nextNode);

						x.add(currentNode);

						// if the currentNode is full, split it
						if (currentNode.isFull()) {
							currentNode = BReadWrite.readBNode(currentNode.split());
							numNodes++;
							y++;
						}
					}

					// once at LEAF, insert key
					currentNode.insert(insertedSequences.get(i));
				}

				// the RAF size should be the DiskSize of a numNodes amount of BNodes with a MOE
				// of 1 BNode
				assert (BReadWrite.getRAFSize() > ((numNodes - 1) * BNode.getDiskSize() + BTree.getDiskSize())
						&& BReadWrite.getRAFSize() < ((numNodes + 1) * BNode.getDiskSize() + BTree.getDiskSize()));

//		    	for(int i =0; i< currentNode.getN(); i++) {
//		    		System.out.println(currentNode.getKey(i).toString());
//		    	}
//		    	currentNode = BReadWrite.readBNode(root);
//		    	System.out.println();
//		    	for(int i =0; i< currentNode.getN(); i++) {
//		    		System.out.println(currentNode.getKey(i).toString());
//		    	}
//		    	System.out.println("\n\n");

				progress.increaseProgress();
			}
		} catch (Throwable e) {
			ex = e;
			progressAndExceptionCheck(run_BTree_RAF_Search);
		}
	}
	
	// =================================================================================================================
	//                                           Testing BTrees using RAF
	// =================================================================================================================
	
	/**
	 * Insert a random number of sequences of random length into a BTree, then write
	 * the BTree, and lastly check if both the memory held node and the read node are
	 * sorted correctly.
	 * <p>
	 * RANDOM: This test is random and thus, the RAFs will change every run.
	 * 
	 * @throws Throwable 
	 */
	@Test
	public void BTree_RAF_IsSorted() throws Throwable {
		ex = null;
		currentProgress = progress.getProgress();
		testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method
		
		try {
			
			for (int k = 0; k < run_BTree_RAF_IsSorted; k++) {// <--- THIS WILL TAKE A LONG TIME IF REALLY BIG
				
				// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
				// are done in this order
				BReadWrite.setRAF(TESTS_FOLDER + testName + k, true);
				int degree = getRand(3, 7);
				BNode.setDegree(degree);
				BReadWrite.setBuffer(BNode.getDiskSize());
				
				//generate random sequences and create BTree
				ArrayList<String> inputSequences = generateRandomSequences(20000/5, 30000/5, 5, 15);// <--- THIS WILL TAKE A LONG TIME IF REALLY BIG
				BTree memoryTree = new BTree(new TreeObject(inputSequences.get(0), 1), degree, 5);
				
				//insert all sequences
				for(int i = 1; i < inputSequences.size(); i++) {
					memoryTree.insert(new TreeObject(inputSequences.get(i), 1));
				}
				
				//write BTree and then read
				BReadWrite.writeBTree(memoryTree);
				BTree readTree = BReadWrite.readBTree();
				
				//check that both BTrees are sorted
				assert(BTree.isSorted(memoryTree.getRoot()));
				assert(BTree.isSorted(readTree.getRoot()));
				
				progress.increaseProgress();
			}
			
		} catch (Throwable e) {
			ex = e;
			progressAndExceptionCheck(run_BTree_RAF_IsSorted);
		}
	}
	
	/**
	 * Insert a random number of sequences of random length into a BTree, then insert
	 * the same random sequence a random number of times at random positions. A
	 * following search for the sequence should return a frequency equal to the number
	 * of sequences added.
	 * <p>
	 * RANDOM: This test is random and thus, the RAFs will change every run.
	 * 
	 * @throws Throwable 
	 */
	@Test
	public void BTree_RAF_Search() throws Throwable {
		ex = null;
		currentProgress = progress.getProgress();
		testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method
		
		try {
			
			for (int k = 0; k < run_BTree_RAF_Search; k++) {// <--- THIS WILL TAKE A LONG TIME IF REALLY BIG
				
				// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
				// are done in this order
				BReadWrite.setRAF(TESTS_FOLDER + testName + k, true);
				int degree = getRand(3, 7);
				BNode.setDegree(degree);
				BReadWrite.setBuffer(BNode.getDiskSize());
				
				//generate random sequences
				ArrayList<String> inputSequences = generateRandomSequences(20000/5, 30000/5, 5, 15);// <--- THIS WILL TAKE A LONG TIME IF REALLY BIG
				
				//generate the same random sequence a random number of times and insert at random spots
				int numNewSeq = getRand(20, 100);
				String newSeq = inputSequences.get(getRand(0, inputSequences.size()));
				for(;inputSequences.remove(newSeq););//remove all instances of newSeq
				for(int i = 0; i < numNewSeq; i++) {
					inputSequences.add(getRand(0, inputSequences.size()), newSeq);
				}
				
				//create BTree
				BTree memoryTree = new BTree(new TreeObject(inputSequences.get(0), 1), degree, 5);
				
				//debugging variable
				int y = 0;
				
				//insert all sequences
				for(int i = 1; i < inputSequences.size(); i++) {
					memoryTree.insert(new TreeObject(inputSequences.get(i), 1));
					if(inputSequences.get(i).equals(newSeq)) {
						y++;
					}
				}
				
				if(y != memoryTree.search(new TreeObject(newSeq, 0))) {
					y = y;
				}
				
				//write BTree and then read
				BReadWrite.writeBTree(memoryTree);
				BTree readTree = BReadWrite.readBTree();
				
				//check that both BTrees are sorted
				assert(memoryTree.search(new TreeObject(newSeq, 0)) == numNewSeq);
				assert(readTree.search(new TreeObject(newSeq, 0)) == numNewSeq);
				
				progress.increaseProgress();
				
			}
		} catch (Throwable e) {
			ex = e;
			progressAndExceptionCheck(run_BTree_RAF_Search);
		}
	}
	
	
	
	
	/**
	 * 
	 * 
	 * @throws Throwable
	 */
	public void EXAMPLE_TEST() throws Throwable {
		//you can thank stackoverflow for this one: https://stackoverflow.com/questions/442747/getting-the-name-of-the-currently-executing-method
		testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method
		try {
			
			// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
			// are done in this order
			BReadWrite.setRAF(TESTS_FOLDER + testName, true);
			int degree = getRand(3, 7);
			BNode.setDegree(degree);
			BReadWrite.setBuffer(BNode.getDiskSize());
			
			// code goes here
			
			progress.increaseProgress();
		} catch (Throwable e) {
			progress.increaseProgress();
			throw e;
		}
	}

	/**
	 * 
	 * 
	 * @throws Throwable 
	 */
	public void EXAMPLE_LOOPED_TEST() throws Throwable {
		ex = null;
		currentProgress = progress.getProgress();
		testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method
		
		try {
			
			for (int k = 0; k < run_EXAMPLE_LOOPED_TEST; k++) {// <--- THIS WILL TAKE A LONG TIME IF REALLY BIG
				
				// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
				// are done in this order
				BReadWrite.setRAF(TESTS_FOLDER + testName + k, true);
				int degree = getRand(3, 7);
				BNode.setDegree(degree);
				BReadWrite.setBuffer(BNode.getDiskSize());
				
				// code goes here
				
				progress.increaseProgress();
			}
			
		} catch (Throwable e) {
			ex = e;
			progressAndExceptionCheck(run_BTree_RAF_Search);
		}
	}

}
