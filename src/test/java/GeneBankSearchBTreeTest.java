

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GeneBankSearchBTreeTest
{

	static private final String TESTS_FOLDER = "./results/tests/TEST_";
	static private final Random RANDOM = new Random();
	
//	static private Throwable ex = null;
//	static private int currentProgress = 0;
	static private String testName = "";
	
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
		return (RANDOM.nextInt(bound - origin) + origin);
	}
	
	/**
	 * Check that the produced query file from a test matches the expected query.
	 * 
	 * @param actualQuery   What the produced query should be identical to
	 * @param producedQuery The query created from running a test
	 * 
	 * @throws FileNotFoundException 
	 */
	private static void checkQuery(String actualQuery, String producedQuery) throws FileNotFoundException {
		// check that dump is correct
		Scanner scanAQ = new Scanner(new File(actualQuery));
		Scanner scanPQ = new Scanner(new File(producedQuery));
		String lineAQ;
		String linePQ;
		while(scanAQ.hasNext()) {
			lineAQ = scanAQ.nextLine();
			linePQ = scanPQ.nextLine();
			if (!lineAQ.equals(linePQ)) {
				assert (false);
			}
		}
		assert(!scanPQ.hasNextLine());
	}
	
	/**
	 * 
	 * 
	 * @throws Throwable 
	 */
	@Test
	public void performSearch() throws Throwable {
		 testName = new Object() {}.getClass().getEnclosingMethod().getName(); //get the name of this method
		 ArrayList<String> inputSequences = null;
		try {
			
			// delete old RAF and set new RAF, degree, and byteBuffer. Important that they
			// are done in this order
			BReadWrite.setRAF(TESTS_FOLDER + testName + ".gbk.btree.data", true);
			int degree = 2;
			BNode.setDegree(degree);
			BReadWrite.setBuffer(BNode.getDiskSize());
			
			//inputs and there frequencies
			inputSequences = new ArrayList<String>();
			inputSequences.add("aaa");
			String[] inputs = new String[]{"aaa", "aat", "act", "agt", "ccc", "cgt", "ctt", "gaa", "gag", "gtc", "tgc", "tgg", "ttg", "ttt"};
			int[] freq      = new int   []{  4,     6,     1,     3,     0,     4,      6,    19,    4,     5,     3,     3,     11,    12};
			
			// generate random placement
			for(int i = 0; i < inputs.length; i++) {
				for(; freq[i] > 0; freq[i]--) {
					inputSequences.add(getRand(0, inputSequences.size()) , inputs[i]);
				}
			}
			TreeObject.setSequenceLength(inputSequences.get(0).length());
			
			// create BTree
			BTree memoryTree = new BTree(new TreeObject(inputSequences.get(0)), degree, 5);

			// insert all sequences
			for (int i = 1; i < inputSequences.size(); i++) {
				memoryTree.insert(new TreeObject(inputSequences.get(i)));
			}
			
			//write memoryTree and perform search
			BReadWrite.writeBTree(memoryTree);
			GeneBankSearchBTree.main(new String[] {"0", BReadWrite.getRAFName(), "./results/tests/TEST_performSearch_query0", "1"});
			
			checkQuery("./results/tests/TEST_performSearch_query0-out", "./results/genebank/TEST_performSearch_query0"  + "_ON_" + BReadWrite.getRAFName().substring(BReadWrite.getRAFName().lastIndexOf('/') + 1, BReadWrite.getRAFName().length()));
		} catch (Throwable e) {
			throw e;
		}
	}
	
	@Test
	public void Random_performSearch() {
		
	}
	
}
