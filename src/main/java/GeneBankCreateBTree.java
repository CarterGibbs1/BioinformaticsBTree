import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Driver class to create a GeneBank BTree, parse a valid gbk file, fill a
 * SQL database, and output a dump file.
 *
 * @author  Carter Gibbs, Mesa Greear, Aaron Goin
 * @version Spring 2022
 */
public class GeneBankCreateBTree {

    /**
     * Main method of driver
     * @param args - arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = parseArgumentsAndHandleExceptions(args);
        // Read in from file
        BTree bT;
        Connection connection = null;
        try {
        	
        	//if the desired location doesn't exist, create RAF in main directory
        	String RAFLocation = "./data/files_gbk_btree_rafs_databases/";
        	if(!Files.exists(Path.of(RAFLocation))) {
        		RAFLocation = "./";
        	}
        	
            BReadWrite.setRAF(RAFLocation + geneBankCreateBTreeArguments.getGbkFileName().substring(geneBankCreateBTreeArguments.getGbkFileName().lastIndexOf('/') + 1, geneBankCreateBTreeArguments.getGbkFileName().length()) + ".btree.data." + geneBankCreateBTreeArguments.getSubsequenceLength() + "." + geneBankCreateBTreeArguments.getDegree(), true);
            TreeObject.setSequenceLength(geneBankCreateBTreeArguments.getSubsequenceLength());
            
            //create Scanner for file
            File file = new File(geneBankCreateBTreeArguments.getGbkFileName());
            Scanner fileScan = new Scanner(file);
            int lineCount = 0;
            
            //get number of scannable lines in file, lil inefficient, but it works
            boolean flag = false;
            String line = "";
            while(fileScan.hasNextLine()) {
            	line = fileScan.nextLine();
            	
            	if(line.contains("ORIGIN")) {
            		flag = true;
            	}
            	else if(line.contains("//")) {
            		flag = false;
            	}
            	
            	lineCount += flag ? 1 : 0;
            }
            fileScan = new Scanner(file);
            
            //create a progress bar to show that things are happening
            ProgressBar progress = new ProgressBar(30, lineCount + 1);
            
            
            bT = (geneBankCreateBTreeArguments.getDebugLevel() == 1) ?
                    new BTree(geneBankCreateBTreeArguments.getDegree(), geneBankCreateBTreeArguments.getSubsequenceLength(), geneBankCreateBTreeArguments.getCacheSize()) : // with cache
                    new BTree(geneBankCreateBTreeArguments.getDegree(), geneBankCreateBTreeArguments.getSubsequenceLength()); // without cache
            boolean newSegment = true;
            while (fileScan.hasNextLine()) {
                String fileLine = fileScan.nextLine();
                while (newSegment && fileScan.hasNextLine() && !fileLine.contains("ORIGIN")) {
                    fileLine = fileScan.nextLine();
                }
                
                newSegment = false;
                if (!fileScan.hasNextLine()) break;
                
                //holds an entire sequence from ORIGIN to //, ORIGIN to n, n to //, etc.
                StringBuilder seq = new StringBuilder();
                
                fileLine = fileScan.nextLine();
                while (!fileLine.contains("//") && fileScan.hasNextLine()) { // while has another sequence and is not end of segment
                	progress.increaseProgress();
                	
                	//get rid of all spaces and numbers in the line
                	fileLine = fileLine.substring(10);
                	fileLine = fileLine.replaceAll(" ", "");

                	//add all the characters of this line to the sequence
                    for (int i = 0; i < fileLine.length(); i++) {
                    	//if an n is detected, parse the current sequence and clear it
                        if (fileLine.charAt(i) == 'n') {
                        	parseDNASequence(seq.toString(), bT, geneBankCreateBTreeArguments);
                        	seq.setLength(0);
                        	//move past n's
                        	while(i < fileLine.length() && fileLine.charAt(i) == 'n') {
                        		i++;
                        	}
                        	//if at end of line, break
                        	if(i >= fileLine.length()) {
                        		break;
                        	}
                        }
                        seq.append(fileLine.charAt(i));
                    }
                	fileLine = fileScan.nextLine();
                }
                //parse the last sequence
                parseDNASequence(seq.toString(), bT, geneBankCreateBTreeArguments);
                newSegment = true;
                
                
            }// end of while
            
            //write BTree to RAF
            bT.emptyBCache();
            BReadWrite.writeBTree(bT);

			// if the desired location doesn't exist for dumps, create dumps in main directory
			String DLocation = "./data/files_gbk_actual_results/";
			if (!Files.exists(Path.of(DLocation))) {
				DLocation = "./";
			}

            // print dump to console if there are elements in the BTree
            if(bT.getRoot().getN() > 0) {
	            String dump = bT.dump();
	            System.out.println(dump);
	            
	            //print dump to file if debug level is 1
	            if(geneBankCreateBTreeArguments.isUseCache()) {
	            	
	            	File dumpFile = new File(DLocation + geneBankCreateBTreeArguments.getGbkFileName().substring(geneBankCreateBTreeArguments.getGbkFileName().lastIndexOf('/') + 1, geneBankCreateBTreeArguments.getGbkFileName().length()) + ".btree.dump." + bT.getFrequency());
	            	dumpFile.createNewFile();
	            	
	            	PrintStream fileOut = new PrintStream(dumpFile);
	            	fileOut.print(dump);
	            	fileOut.close();
	            }



//        bT.emptyBCache();
//        String dumpData = "";
//        if (bT.getRoot().getN() != 0) {
//        dumpData = bT.dump();
//        }
//        if (geneBankCreateBTreeArguments.getDebugLevel() == 1) {
//        String dumpFilename = geneBankCreateBTreeArguments.getGbkFileName() + ".btree.dump."
//        + geneBankCreateBTreeArguments.getSubsequenceLength();
//        PrintStream pS = new PrintStream(dumpFilename);
//        PrintStream stdout = System.out;
//        pS.append(dumpData);
//        System.setOut(pS);
//        System.setOut(stdout);
//        }
//        BReadWrite.writeBTree(bT); // need to verify with searchBTree
        // IMPORTANT: BOTH CREATEBTREE AND SEARCHDATABASE NEED THE FOLLOWING COMMAND IN
        // THE DIR IN TERMINAL BEFORE RUNNING PROGRAM:
        // jar xf sqlite-jdbc-3.36.0.3.jar
        //Connection connection = null;
		        try {
			        // create a database connection
		        	Class.forName("org.sqlite.JDBC");
			        connection = DriverManager.getConnection("jdbc:sqlite:" + RAFLocation + geneBankCreateBTreeArguments.getGbkFileName().substring(geneBankCreateBTreeArguments.getGbkFileName().lastIndexOf('/') + 1, geneBankCreateBTreeArguments.getGbkFileName().length())
			        		+  "btree.database." + geneBankCreateBTreeArguments.getSubsequenceLength() + "." + geneBankCreateBTreeArguments.getDegree() + ".db");

			        Statement statement = connection.createStatement();
			        statement.setQueryTimeout(30); // set timeout to 30 sec.
			        statement.executeUpdate("drop table if exists btree;");
			        statement.executeUpdate("create table btree (dnaseq varchar(255), freq int);");

			        System.out.println("Creating Database...");
//			        progress = new ProgressBar(30, dump.length() - dump.replace("\n", "").length());

			        Scanner scanDump = new Scanner(dump);
			        String dumpLine;

			        while (scanDump.hasNextLine()) {
				        dumpLine = scanDump.nextLine().replace(':', ' ');

				        statements.append("insert into btree (dnaseq, freq) values (\'" + dumpLine.substring(0, dumpLine.indexOf(' '))
				        + "\', " + dumpLine.substring(dumpLine.lastIndexOf(' ') + 1) + ");");


//				        statement.executeUpdate(
//				        "insert into btree (dnaseq, freq) values (\'" + dumpLine.substring(0, dumpLine.indexOf(' '))
//				        + "\', " + dumpLine.substring(dumpLine.lastIndexOf(' ') + 1) + ");");

//				        progress.increaseProgress();
			        } // end of while

			        statement.executeUpdate(statements.toString());
			        scanDump.close();
		        } catch (SQLException e) {
		        	// if the error message is "out of memory",
		        	// it probably means no database file is found
		        	System.err.println(e.getMessage());
		        } finally {
		        	try {
		        		if (connection != null)
		        				connection.close();
		        	} catch (SQLException e) {
		        		// connection close failed.
		        		System.err.println(e.getMessage());
		        	}
		        }

            }

        } // end of try
        catch (FileNotFoundException fe) {
        printUsageAndExit("File Not Found.");
        }
    }

//Below is some of the implementation needed to create a database in createbtree

//IMPORTANT: BOTH CREATEBTREE AND SEARCHDATABASE NEED THE FOLLOWING COMMAND IN THE DIR IN TERMINAL BEFORE RUNNING PROGRAM:
// jar xf sqlite-jdbc-3.36.0.3.jar

    /**
     * This method will in order traversal of a DNA Sequence.
     *
     * @param s - string of DNA subsequence
     * @param statement - SQL statement
     * @param args - arguments of the driver
     * @throws SQLException
     */
    private static void inOrderT (String s, Statement statement, GeneBankCreateBTreeArguments args) throws SQLException {// s from dump() before
        int idx = 0;
        while (idx < s.length()) {
            String thisSeq = "";
            while (thisSeq.length() < args.getSubsequenceLength()) {
                thisSeq += s.charAt(idx);
                idx++;
            }
            idx = idx + 3;// three spaces for space, colon, then space in toString
            int freq = Integer.parseInt("" + s.charAt(idx));// better way to do this probs
            statement.executeUpdate("insert into btree (dnaseq, freq) values (\'" + thisSeq + "\', " + freq + ");");
            idx++;// /n char is just one char
        }  // end of while
    }

    /**
     *
     * @param dnaSequence - String that holds DNA sequence
     * @param bT - BTree
     * @param args - arguments of the driver class.
     * @throws IOException
     */
    public static void parseDNASequence(String dnaSequence, BTree bT, GeneBankCreateBTreeArguments args) throws IOException {
        // Parse data in dnaSequence
        int index = 0;
        
        if (dnaSequence.length() >= args.getSubsequenceLength()) {
            while (index < dnaSequence.length() - args.getSubsequenceLength() + 1) {
                String thisSeq = "";
                thisSeq += dnaSequence.substring(index, index + args.getSubsequenceLength());
                
                TreeObject x = new TreeObject(thisSeq);
                
                bT.insert(x);
                index++;
            }
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