package cs321.btree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BTreeTest
{
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

    /**
     * Test that a BNode correctly inserts given elements in sorted order.
     */
    @Test
    public void singleBNode_TestInsertion() {
    	String inputLetters = "TGCATAAG";
    	
    	//instantiate and populate BNode with inputLetters
    	TestBNode<String> testNode = new TestBNode<String>(new TreeObject<String>(inputLetters.substring(0, 1), 1));
    	for(int i = 1; i < inputLetters.length(); i++) {
    		testNode.insert(new TreeObject<String>(inputLetters.substring(i, i + 1), 1));
    	}
    	
    	//see if the BNode contains AAACGGTT in int/byte value
    	assertEquals(testNode.toString(), "00012233");
    }
    
    /**
     * Test that a ROOT BNode correctly splits itself into three new nodes,
     * the new root and it's two children.
     */
    @Test
    public void BNode_TestSplit() {
    	String inputLetters = "ATGTC";
    	TestBNode<String> parent;
    	TestBNode<String> rightChild;
    	
    	//instantiate and populate BNode with inputLetters
    	TestBNode<String> testNode = new TestBNode<String>(new TreeObject<String>(inputLetters.substring(0, 1), 1));
    	for(int i = 1; i < inputLetters.length(); i++) {
    		testNode.insert(new TreeObject<String>(inputLetters.substring(i, i + 1), 1));
    	}
    	
    	//split BNode and save parent and rightChild
    	parent = testNode.split();
    	rightChild = parent.getSubtree(new TreeObject<String>("T", 1));
    	
    	assertEquals(parent.toString(), "2");      //parent = 2 (G)
    	assertEquals(testNode.toString(), "01");   //leftChild = 01 (AC)
    	assertEquals(rightChild.toString(), "33"); //rightChild = 33 (TT)
    }
    
    /**
     * Test that when a BNode is not full, isFull() will return false and
     * when BNode is full, isFull() will return true.
     */
    @Test
    public void BNode_TestIsFull() {
    	String inputLetters = "ATGTCTGACCGT";
    	int degree = 7;
    	
    	//instantiate and populate BNode with inputLetters
    	TestBNode<String> testNode = new TestBNode<String>(new TreeObject<String>(inputLetters.substring(0, 1), 1));
    	for(int i = 1; i < inputLetters.length(); i++) {
    		testNode.insert(new TreeObject<String>(inputLetters.substring(i, i + 1), 1));
    	}
    	
    	//testNode is not full
    	assert(!testNode.isFull(degree));
    	
    	//testNode is now full
    	testNode.insert(new TreeObject<String>("A", 1));
    	assert(testNode.isFull(degree));
    }
    
    /**
     * Test using a rudimentary BTree that the BNode methods isFull(), insert(key),
     * and split() function correctly resulting in a BTree with the correct number of
     * nodes and correct height.
     */
    @Test
    public void BNode_CorrectHeightAndNodeCount() {
    	String inputLetters = "ATGTCTGACCGTGACTTACGAAG";
    	int degree = 2;
    	
    	//instantiate and BNode root
    	TestBNode<String> root = new TestBNode<String>(new TreeObject<String>(inputLetters.substring(0, 1), 1));
    	TestBNode<String> currentNode;
    	
    	//create a rudimentary BTree to insert into while counting the total BNode amount and the height
    	int height = 1;
    	int totalNodes = 1;
    	TreeObject<String> key;
    	for(int i = 1; i < inputLetters.length(); i++) {
    		currentNode = root;
    		key = new TreeObject<String>(inputLetters.substring(i, i + 1), 1);
    		
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
}
