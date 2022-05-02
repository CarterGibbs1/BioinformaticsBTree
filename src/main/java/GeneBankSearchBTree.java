

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Driver class for performing searches on BTree RAFs.
 * 
 * @author  Mesa Greear, Aaron Goin
 * @version Spring 2022
 */
public class GeneBankSearchBTree
{

	/**
	 * Main: Perform a search on the given file using the given query
	 */
    public static void main(String[] args) throws Exception
    {
        GeneBankSearchBTreeArguments arguments = parseArgumentsAndHandleExceptions(args); //get args
        
        try {
        	//create Scanner for query file, set file to read BTree from, and instantiate BTree
        	Scanner qScan = new Scanner(new File(arguments.getQueryFileName()));
        	BReadWrite.setRAF(arguments.getBTreeFileName(), false);
	        BTree searchTree = BReadWrite.readBTree(arguments.getCacheSize());
	        
	        //if debug == 1, create new file to put query results in
	        boolean debug = arguments.getDebugLevel() == 1;
	        PrintStream result = null;
	        if(debug) {
		        File resultFile = new File("./results/genebank/" + arguments.getQueryFileName().substring(arguments.getQueryFileName().lastIndexOf('/') + 1, arguments.getQueryFileName().length()) 
		        		+ "_ON_" + arguments.getBTreeFileName().substring(arguments.getBTreeFileName().lastIndexOf('/') + 1, arguments.getBTreeFileName().length()));
		        if(resultFile.exists()) {
		        	if(!resultFile.delete()) {
		        		throw new IOException("Already existing result file '" + resultFile.getName() + "' could not be deleted, might be open elsewhere.");
		        	}
		        }
		        resultFile.createNewFile();
		        result = new PrintStream(resultFile);
	        }
	        
	        //scan each line from the query, printing the String and frequency of each String in result file
	        while (qScan.hasNext()) {
	            String qCurr = qScan.next().toLowerCase();
	            int resultFreq = searchTree.search(new TreeObject(qCurr));
	            
	            System.out.println(qCurr + " " + resultFreq);
	            if(debug)
	            	result.println(qCurr + " " + resultFreq);
	        }
        }
        catch(Exception e) {
        	printUsageAndExit(e.toString());
        }
    }

    /**
     * Parse the given command line arguments and handle exceptions that may
     * be thrown, printing messages to command line.
     * 
     * @param args String array of command line arguments
     * 
     * @return Object instance that contains parsed arguments
     */
    private static GeneBankSearchBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankSearchBTreeArguments geneBankSearchBTreeArguments = null;
        try {
            geneBankSearchBTreeArguments = parseArguments(args);
        } catch (ParseArgumentException e) {
            printUsageAndExit(e.getMessage());
        }
        return geneBankSearchBTreeArguments;
    }

    /**
     * Print the given error message to the console and exit.
     * 
     * @param errorMessage Message of throwable
     */
    private static void printUsageAndExit(String errorMessage) {
        System.err.println(errorMessage);
        System.exit(1);
    }

    /**
     * Parse the given command line arguments.
     * 
     * @param args String array of command line arguments
     * 
     * @return Object instance that contains parsed arguments
     * 
     * @throws ParseArgumentException 
     */
    public static GeneBankSearchBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
        if (args.length < 4 || args.length > 5) {
            System.out.println
                    ("Usage: java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]\n");
            System.out.println
                    ("<0/1(no/with Cache)>: 1 for run program with a Cache, 0 for without.");
            System.out.println
                    ("<btree file>: file name of the btree file.");
            System.out.println
                    ("<query file>: file name of the query.");
            System.out.println
                    ("[<cache size>]: if running program with cache, then must include the size of the cache in arguments");
            System.out.println
                    ("[<debug level>]: type of output wanted for program. 1 for a dump file output with console. 0 for just console.");
            throw new ParseArgumentException("Incorrect number of arguments");
        }
        int withOrWithoutCache = Integer.parseInt(args[0]);
        if (withOrWithoutCache != 0 && withOrWithoutCache != 1)
            throw new ParseArgumentException("With or Without Cache can only be 1 or 0.");
        String btreeFileName = args[1];
        if (!btreeFileName.contains(".gbk.btree.data")) {// check to see if this file format gets written
            throw new ParseArgumentException("BTree filename must contain\".gbk.btree.data\"");
        }
        String queryFileName = args[2];
        if (!queryFileName.contains("query")) {
            throw new ParseArgumentException("Query file must contain query.");
        }
        int cacheSize = 0;
        int debugLevel = 0;
        // Getting some warnings here, but seems fine. Probably logic error to come back to.
        if (withOrWithoutCache == 1 && args.length < 4) {
            throw new ParseArgumentException("If With/Without Cache is 1, then must provide Cache size.");
        } else if (args.length > 3){
        	if(withOrWithoutCache == 1) {
	            cacheSize = Integer.parseInt(args[3]);
	            if (cacheSize <= 0) throw new ParseArgumentException("Cache size must be greater than 0.");
	            if(args.length > 4) debugLevel = Integer.parseInt(args[4]);
        	}
        	else {
        		debugLevel = Integer.parseInt(args[3]);
        	}
        }

        return new GeneBankSearchBTreeArguments(withOrWithoutCache == 1, btreeFileName, queryFileName, cacheSize, debugLevel);
    }

}

