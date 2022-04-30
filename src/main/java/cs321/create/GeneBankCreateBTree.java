package cs321.create;

import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class GeneBankCreateBTree {

    public static void main(String[] args) throws Exception {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = parseArgumentsAndHandleExceptions(args);
        // Read in from file
        try {
            Scanner fileScan = new Scanner(new File(geneBankCreateBTreeArguments.getGbkFileName()));
            BTree bT = (geneBankCreateBTreeArguments.getDebugLevel() == 1) ?
                    new BTree(geneBankCreateBTreeArguments.getDegree(), 1, geneBankCreateBTreeArguments.getCacheSize()) : // with cache
                    new BTree(geneBankCreateBTreeArguments.getDegree(), 1); // without cache
            boolean newSegment = true;
            while (fileScan.hasNextLine()) {
                while (newSegment && fileScan.hasNextLine() && !fileScan.nextLine().contains("ORIGIN"));
                newSegment = false;
                if (!fileScan.hasNextLine()) break;
                String line = fileScan.next();
                while (isNumber(line)) line = fileScan.next();
                String dnaSequence = ""; // full DNA sequence
                while (!line.contains("//") && fileScan.hasNext()) { // while has another sequence and is not end of segment
                    if (isNumber(line)) {
                        line = fileScan.next();
                        continue;
                    }
                    int locN = line.indexOf("n"); // location of N in line
                    if (locN != -1) { // if we have an N in sequence, add up to n, then parse sequence.
                        dnaSequence += line.substring(0, locN);
                        parseDNASequence(dnaSequence, bT, geneBankCreateBTreeArguments);
                        int ind = locN;
                        while (fileScan.hasNext() && !line.contains("//")) { // if we ended dna sequence from 'n' instead of end of sequence.
                            if (line.charAt(ind) != 'n') {
                                dnaSequence = line.substring(ind, line.length());
                                break;
                            }
                            if (++ind == line.length()) {
                                ind = 0;
                                line = fileScan.next();
                            }
                        }
                    } else {
                        dnaSequence += line;
                    }
                    line = fileScan.next();
                }
                parseDNASequence(dnaSequence, bT, geneBankCreateBTreeArguments);
                newSegment = true;
            }// end of while
            //deal with dump and db stuff in notes go here
        }// end of try
        catch (FileNotFoundException fe) {
            printUsageAndExit("File Not Found.");
        }



        // Deal with Dump file
        /***/
        //imo, best to have this no matter what, even if debug level is zero, as it's something needed for db
        //String dumpData = bT.dump();
        /***/
        //if (args.getDebugLevel() == 1) {
            //String dumpFilename = "";
            //dumpFilename += filename + ".btree.dump." + bT.getFrequency();
            //PrintStream pS = new PrintStream(filename);
            //PrintStream stdout = System.out;
            //pS.append(dumpData);
            //System.setOut(pS);
            //System.setOut(stdout);
        //}end of if statement
        //bT.emptyCache();
        /***/
        // not sure if we need the part below, somehow this'll be the filename of the btree file
        //String bTreeFilename += args.getGbkFileName() + ".btree.data." + args.getSubsequenceLength + args.getDegree();
        // guess: setRAF(bTreeFilename, true) // wonder if it goes before or after you write the bTree
        //bT.writeBTree();
        /***/
//Below is some of the implementation needed to create a database in createbtree
/***/
//IMPORTANT: BOTH CREATEBTREE AND SEARCHDATABASE NEED THE FOLLOWING COMMAND IN THE DIR IN TERMINAL BEFORE RUNNING PROGRAM:
// jar xf sqlite-jdbc-3.36.0.3.jar
/***/
//below is a method that could go atop the createbtree class **/

//
//      private void inOrderT(String s, Statement statement)// s from dump() before
//          String seqs = s;
//          *parse each seq of length sequenceLength and insert it with following:*
//            int idx = 0;
//			  while (idx < seqs.length()) {
//                String thisSeq = "";
//                while (thisSeq.length() < args.getSubSequenceLength()) {
//                    thisSeq += overallSeqs.charAt(idx);
//                    idx++;
//                }
//                idx = idx + 3;// three spaces for space, colon, then space in toString
//                int freq = Integer.parseInt(overallSeqs.charAt(idx));// better way to do this probs
//                statement.executeUpdate("insert into btree (dnaseq, freq) values (\'" + thisSeq + "\', " + freq + ");");
//                idx++;// /n char is just one char
//          }// end of while
//
/***/
//this part also goes in main, after BTree is created
//    Connection connection = null;
//    try
//    {
//
//
//        /*Test for a driver*/
//        /*********/
//        // create a database connection
//        connection = DriverManager.getConnection("jdbc:sqlite:btree.db");
//        Statement statement = connection.createStatement();
//        statement.setQueryTimeout(30);  // set timeout to 30 sec.
//        statement.executeUpdate("drop table if exists btree;");
//        statement.executeUpdate("create table btree (dnaseq varchar(255), freq int);");
//

//in order traversal here
//        inOrderT(dumpData, statement);
//
//    }
//    catch(SQLException e)
//    {
//        // if the error message is "out of memory",
//        // it probably means no database file is found
//        System.err.println(e.getMessage());
//    }
//    finally
//    {
//        try
//        {
//            if(connection != null)
//                connection.close();
//        }
//        catch(SQLException e)
//        {
//            // connection close failed.
//            System.err.println(e.getMessage());
//        }
//    }
//}// end of main
//}
    }

    public static void parseDNASequence(String dnaSequence, BTree bT, GeneBankCreateBTreeArguments args) throws IOException {
        // Parse data in dnaSequence
        int index = 0;
        while (index < dnaSequence.length() - args.getSubsequenceLength()) {
            String thisSeq = "";
            thisSeq += dnaSequence.substring(index, index + args.getSubsequenceLength());
            bT.insert(new TreeObject(thisSeq));
            index++;
        }
    }

    /**
     * Catches any ParseArgumentExceptions and returns a GeneBankCreateBTreeArguments object
     */
    private static GeneBankCreateBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = null;
        try {
            geneBankCreateBTreeArguments = parseArguments(args);
        } catch (ParseArgumentException e) {
            printUsageAndExit(e.getMessage());
        }
        return geneBankCreateBTreeArguments;
    }

    /**
     * Parses the command-line arguments and handles exceptions
     * @param args - array of Strings of command-line arguments
     * @return - a GeneBankCreateBTreeArguments object holding the arguments of the command line
     * @throws ParseArgumentException - if any issues with arguments, throws this exception
     */
    public static GeneBankCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
        if (args.length < 4 || args.length > 6) {
            System.out.println
                    ("Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]\n");
            System.out.println
                    ("<0/1(no/with Cache)>: 1 for run program with a Cache, 0 for without.");
            System.out.println
                    ("<degree>: degree of the BTree.");
            System.out.println
                    ("<gbk file>: file name of the gene bank.");
            System.out.println
                    ("<sequence length>: sequence length of each DNA strand. Integer between 1 and 31 inclusive.");
            System.out.println
                    ("[<cache size>]: if running program with cache, then must include the size of the cache in arguments");
            System.out.println
                    ("[<debug level>]: type of output wanted for program. 1 for a dump file output with console. 0 for just console.");
            throw new ParseArgumentException("Incorrect number of arguments");
        }
        int withOrWithoutCache = Integer.parseInt(args[0]);
        if (withOrWithoutCache != 0 && withOrWithoutCache != 1)
            throw new ParseArgumentException("With or Without Cache can only be 1 or 0.");
        int degree = Integer.parseInt(args[1]);
        if (degree < 0) throw new ParseArgumentException("Degree must be 0 or greater, cannot be negative.");
        String gbkFileName = args[2];
        int sequenceLength = Integer.parseInt(args[3]);
        if (sequenceLength < 1 || sequenceLength > 31) throw new ParseArgumentException("Sequence length must be between 1 and 31 inclusive.");
        int cacheSize = 0;
        int debugLevel = 0;
        if (withOrWithoutCache == 1 && args.length < 5) {
            throw new ParseArgumentException("If With/Without Cache is 1, then must provide Cache size.");
        } else if (args.length >= 5){
            cacheSize = Integer.parseInt(args[4]);
            if (cacheSize <= 0) throw new ParseArgumentException("Cache size must be greater than 0.");
            if (args.length == 6) {
                debugLevel = Integer.parseInt(args[5]);
                if (debugLevel != 1 && debugLevel != 0) throw new ParseArgumentException("Debug level must be either 0 or 1");
            }
        }

        return new GeneBankCreateBTreeArguments(withOrWithoutCache == 1, degree, gbkFileName, sequenceLength, cacheSize, debugLevel);
    }

    /**
     * Parses a file formated as a GBK, then fills a string with the DNA sequence of the file.
     * @param args - arguments of the tree, includes gbkfile name
     * @return - a string containing the entire dna sequence. Ends character before n or end of sequence.

    public static String readGBKFile(GeneBankCreateBTreeArguments args) {
        try {
            Scanner fileScan = new Scanner(new File(args.getGbkFileName()));
            while (!fileScan.nextLine().contains("ORIGIN"));
            String line = fileScan.next();
            while (isNumber(line)) line = fileScan.next();
            //BTree tree = new BTree(args.getDegree(),args.getSubsequenceLength(), new TreeObject(new String(charSeq), args.getSubsequenceLength()));
            String dnaSequence = ""; // full DNA sequence
            while (!line.contains("//") && fileScan.hasNext()) { // while has another sequence
                if (isNumber(line)) {
                    line = fileScan.next();
                    continue;
                }
                int locN = line.indexOf("n"); // location of N in line
                if (locN != -1) {
                    dnaSequence += line.substring(0, locN);
                    break;
                }
                dnaSequence += line;
                line = fileScan.next();
            }

            return dnaSequence;

        }
        catch (FileNotFoundException fe) {
            printUsageAndExit("File Not Found.");
        }
        return null;
    }
*/


    // HELPER METHODS FOR DRIVER

    /**
     * Takes in a string and returns true if the string is a number and false if it isn't
     */
    private static boolean isNumber(String line) {
        try {
            Integer.parseInt(line);
            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Print error message and exits program.
     * @param errorMessage
     */
    private static void printUsageAndExit(String errorMessage) {
        System.err.println(errorMessage);
        System.exit(1);
    }
}

/***/
//Below is a suggested implementation. String DNA seqs now
//appear in the dump method. The frequencies match
//in the dump file test3.gbk.btree.dump.6
//errors will be thrown in the database implementation (at parseint for freq) due to the dna string not
// appearing in the dump method
/***/
//import java.io.*;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Connection;
//import java.sql.Statement;
//import java.util.Scanner;
//
//public class GeneBankCreateBTree {
//
//	public static void main(String[] args) throws Exception {
//		GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = parseArgumentsAndHandleExceptions(args);
//		String dnaSequence = ""; // full DNA sequence
//      TreeObject.setSequenceLength(geneBankCreateBTreeArguments.getSubsequenceLength());
//		// Read in from file
//		try {
//			Scanner fileScan = new Scanner(new File(geneBankCreateBTreeArguments.getGbkFileName()));
//			// not sure if we need the part below, somehow this'll be the filename of the
//			// btree file
//			String bTreeFilename = "";
//			bTreeFilename += geneBankCreateBTreeArguments.getGbkFileName() + ".btree.data."
//					+ geneBankCreateBTreeArguments.getSubsequenceLength() + "."
//					+ geneBankCreateBTreeArguments.getDegree();
//			BReadWrite.setRAF(bTreeFilename, true); // wonder if it goes before or after you write the bTree
//			BTree bT = (geneBankCreateBTreeArguments.getDebugLevel() == 1) ? new BTree(
//					geneBankCreateBTreeArguments.getDegree(), 1, geneBankCreateBTreeArguments.getCacheSize()) : // with
//																												// cache
//					new BTree(geneBankCreateBTreeArguments.getDegree(), 1); // without cache
//			boolean newSegment = true;
//			while (fileScan.hasNextLine()) {
//				while (newSegment && fileScan.hasNextLine() && !fileScan.nextLine().contains("ORIGIN"))
//					;
//				newSegment = false;
//				if (!fileScan.hasNextLine())
//					break;
//				String line = fileScan.next();
//				while (isNumber(line))
//					line = fileScan.next();
//				while (!line.contains("//") && fileScan.hasNext()) { // while has another sequence and is not end of
//																		// segment
//					if (isNumber(line)) {
//						line = fileScan.next();
//						continue;
//					}
//					int locN = line.indexOf("n"); // location of N in line
//					if (locN != -1) { // if we have an N in sequence, add up to n, then parse sequence.
//						dnaSequence += line.substring(0, locN);
//						// parseDNASequence(dnaSequence, bT, geneBankCreateBTreeArguments);
//						int ind = locN;
//						while (fileScan.hasNext() && !line.contains("//")) { // if we ended dna sequence from 'n'
//																				// instead of end of sequence.
//							if (line.charAt(ind) != 'n') {
//								// dnaSequence = line.substring(ind, line.length());
//								break;
//							}
//							if (++ind == line.length()) {
//								ind = 0;
//								line = fileScan.next();
//							}
//						}
//					} else {
//						dnaSequence += line;
//					}
//					line = fileScan.next();
//				}
//				parseDNASequence(dnaSequence, bT, geneBankCreateBTreeArguments);
//				dnaSequence = "";
//				newSegment = true;
//			} // end of while
//				// deal with dump and db stuff in notes go here
//			if (geneBankCreateBTreeArguments.getDebugLevel() == 1) {
//				bT.emptyBCache();
//			}
//			String dumpData = bT.dump();// Strings aren't showing up here, just colon and number
//			if (geneBankCreateBTreeArguments.getDebugLevel() == 1) {
//				String dumpFilename = geneBankCreateBTreeArguments.getGbkFileName() + ".btree.dump."
//						+ geneBankCreateBTreeArguments.getSubsequenceLength();
//				PrintStream pS = new PrintStream(dumpFilename);
//				PrintStream stdout = System.out;
//				pS.append(dumpData);
//				System.setOut(pS);
//				System.setOut(stdout);
//			}
//			// IMPORTANT: BOTH CREATEBTREE AND SEARCHDATABASE NEED THE FOLLOWING COMMAND IN
//			// THE DIR IN TERMINAL BEFORE RUNNING PROGRAM:
//			// jar xf sqlite-jdbc-3.36.0.3.jar
//			Connection connection = null;
//			try {
//				// create a database connection
//				connection = DriverManager.getConnection("jdbc:sqlite:btree.db");
//				Statement statement = connection.createStatement();
//				statement.setQueryTimeout(30); // set timeout to 30 sec.
//				statement.executeUpdate("drop table if exists btree;");
//				statement.executeUpdate("create table btree (dnaseq varchar(255), freq int);");
//	            int idx = 0;
//				  while (idx < dumpData.length()) {
//	                String thisSeq = "";
//	                while (thisSeq.length() < geneBankCreateBTreeArguments.getSubsequenceLength()) {
//	                    thisSeq += dumpData.charAt(idx);
//	                    idx++;
//	                }
//	               idx = idx + 3;// three spaces for space, colon, then space in toString
//                 if toString changes in TreeObject, this will be idx = idx + 2;
//                  String stringF = "";
//					while (Character.isDigit(dumpData.charAt(idx))) {
//                            stringF += dumpData.charAt(idx);
//                            idx++;
//                            }
//                            int freq = Integer.parseInt(stringF);// better way to do this probs
//	               statement.executeUpdate("insert into btree (dnaseq, freq) values (\'" + thisSeq + "\', " + freq + ");");
//	               idx++;// /n char is just one char
//	          }// end of while
//
//
//			} catch (SQLException e) {
//				// if the error message is "out of memory",
//				// it probably means no database file is found
//				System.err.println(e.getMessage());
//			} finally {
//				try {
//					if (connection != null)
//						connection.close();
//				} catch (SQLException e) {
//					// connection close failed.
//					System.err.println(e.getMessage());
//				}
//			}
//
//		} // end of try
//		catch (FileNotFoundException fe) {
//			printUsageAndExit("File Not Found.");
//		}
//	}
//
//	public static void parseDNASequence(String dnaSequence, BTree bT, GeneBankCreateBTreeArguments args)
//			throws IOException {
//		// Parse data in dnaSequence
//		int index = 0;
//		while (index < dnaSequence.length() - args.getSubsequenceLength()) {
//			String thisSeq = "";
//			thisSeq += dnaSequence.substring(index, index + args.getSubsequenceLength());
//			bT.insert(new TreeObject(thisSeq));
//			index++;
//		}
//	}
//
//	/**
//	 * Catches any ParseArgumentExceptions and returns a
//	 * GeneBankCreateBTreeArguments object
//	 */
//	private static GeneBankCreateBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
//		GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = null;
//		try {
//			geneBankCreateBTreeArguments = parseArguments(args);
//		} catch (ParseArgumentException e) {
//			printUsageAndExit(e.getMessage());
//		}
//		return geneBankCreateBTreeArguments;
//	}
//
//	/**
//	 * Parses the command-line arguments and handles exceptions
//	 *
//	 * @param args - array of Strings of command-line arguments
//	 * @return - a GeneBankCreateBTreeArguments object holding the arguments of the
//	 *         command line
//	 * @throws ParseArgumentException - if any issues with arguments, throws this
//	 *                                exception
//	 */
//	public static GeneBankCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
//		if (args.length < 4 || args.length > 6) {
//			System.out.println(
//					"Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]\n");
//			System.out.println("<0/1(no/with Cache)>: 1 for run program with a Cache, 0 for without.");
//			System.out.println("<degree>: degree of the BTree.");
//			System.out.println("<gbk file>: file name of the gene bank.");
//			System.out.println(
//					"<sequence length>: sequence length of each DNA strand. Integer between 1 and 31 inclusive.");
//			System.out.println(
//					"[<cache size>]: if running program with cache, then must include the size of the cache in arguments");
//			System.out.println(
//					"[<debug level>]: type of output wanted for program. 1 for a dump file output with console. 0 for just console.");
//			throw new ParseArgumentException("Incorrect number of arguments");
//		}
//		int withOrWithoutCache = Integer.parseInt(args[0]);
//		if (withOrWithoutCache != 0 && withOrWithoutCache != 1)
//			throw new ParseArgumentException("With or Without Cache can only be 1 or 0.");
//		int degree = Integer.parseInt(args[1]);
//		if (degree < 0)
//			throw new ParseArgumentException("Degree must be 0 or greater, cannot be negative.");
//		String gbkFileName = args[2];
//		int sequenceLength = Integer.parseInt(args[3]);
//		if (sequenceLength < 1 || sequenceLength > 31)
//			throw new ParseArgumentException("Sequence length must be between 1 and 31 inclusive.");
//		int cacheSize = 0;
//		int debugLevel = 0;
//		if (withOrWithoutCache == 1 && args.length < 5) {
//			throw new ParseArgumentException("If With/Without Cache is 1, then must provide Cache size.");
//		} else if (args.length >= 5) {
//			cacheSize = Integer.parseInt(args[4]);
//			if (cacheSize <= 0)
//				throw new ParseArgumentException("Cache size must be greater than 0.");
//			if (args.length == 6) {
//				debugLevel = Integer.parseInt(args[5]);
//				if (debugLevel != 1 && debugLevel != 0)
//					throw new ParseArgumentException("Debug level must be either 0 or 1");
//			}
//		}
//
//		return new GeneBankCreateBTreeArguments(withOrWithoutCache == 1, degree, gbkFileName, sequenceLength, cacheSize,
//				debugLevel);
//	}
//
//	/**
//	 * Parses a file formated as a GBK, then fills a string with the DNA sequence of
//	 * the file.
//	 *
//	 * @param args - arguments of the tree, includes gbkfile name
//	 * @return - a string containing the entire dna sequence. Ends character before
//	 *         n or end of sequence.
//	 *
//	 *         public static String readGBKFile(GeneBankCreateBTreeArguments args) {
//	 *         try { Scanner fileScan = new Scanner(new
//	 *         File(args.getGbkFileName())); while
//	 *         (!fileScan.nextLine().contains("ORIGIN")); String line =
//	 *         fileScan.next(); while (isNumber(line)) line = fileScan.next();
//	 *         //BTree tree = new
//	 *         BTree(args.getDegree(),args.getSubsequenceLength(), new
//	 *         TreeObject(new String(charSeq), args.getSubsequenceLength())); String
//	 *         dnaSequence = ""; // full DNA sequence while (!line.contains("//") &&
//	 *         fileScan.hasNext()) { // while has another sequence if
//	 *         (isNumber(line)) { line = fileScan.next(); continue; } int locN =
//	 *         line.indexOf("n"); // location of N in line if (locN != -1) {
//	 *         dnaSequence += line.substring(0, locN); break; } dnaSequence += line;
//	 *         line = fileScan.next(); }
//	 *
//	 *         return dnaSequence;
//	 *
//	 *         } catch (FileNotFoundException fe) { printUsageAndExit("File Not
//	 *         Found."); } return null; }
//	 */
//
//	// HELPER METHODS FOR DRIVER
//
//	/**
//	 * Takes in a string and returns true if the string is a number and false if it
//	 * isn't
//	 */
//	private static boolean isNumber(String line) {
//		try {
//			Integer.parseInt(line);
//			return true;
//		} catch (NumberFormatException nfe) {
//			return false;
//		}
//	}
//
//	/**
//	 * Print error message and exits program.
//	 *
//	 * @param errorMessage
//	 */
//	private static void printUsageAndExit(String errorMessage) {
//		System.err.println(errorMessage);
//		System.exit(1);
//	}
//}