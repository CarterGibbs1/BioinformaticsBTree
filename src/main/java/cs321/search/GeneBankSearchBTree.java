package cs321.search;

import cs321.btree.BReadWrite;
import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import cs321.common.ParseArgumentUtils;
import cs321.create.GeneBankCreateBTreeArguments;

import java.io.File;
import java.util.Scanner;

public class GeneBankSearchBTree
{

    public static void main(String[] args) throws Exception
    {
        GeneBankSearchBTreeArguments arguments = parseArgumentsAndHandleExceptions(args);
        File qFile = new File(arguments.getQueryFileName());
        Scanner qScan = new Scanner(qFile);
        int cache = 0;
        if (arguments.getCacheSize() == 1) {
            cache = arguments.getCacheSize();
        }
        BTree searchedBT = BReadWrite.readBTree(cache);// is there a different way to read this,
        //or should it be through the .btree.data file
        while (qScan.hasNextLine()) {
            String qCurr = qScan.nextLine().toLowerCase();
            System.out.print(qCurr + ": ");// wonder if we don't print if the user doesn't provide debug level
            // I think the default value is zero, but I'm not sure for search specifically
            TreeObject tOToFind = new TreeObject(qCurr);
            int currFreq = searchedBT.search(tOToFind);
            System.out.println(currFreq);
        }
    }

    private static GeneBankSearchBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankSearchBTreeArguments geneBankSearchBTreeArguments = null;
        try {
            geneBankSearchBTreeArguments = parseArguments(args);
        } catch (ParseArgumentException e) {
            printUsageAndExit(e.getMessage());
        }
        return geneBankSearchBTreeArguments;
    }

    private static void printUsageAndExit(String errorMessage) {
        System.err.println(errorMessage);
        System.exit(1);
    }

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
        } else if (args.length >= 4){
            cacheSize = Integer.parseInt(args[3]);
            if (cacheSize <= 0) throw new ParseArgumentException("Cache size must be greater than 0.");
            if (args.length == 5) {
                debugLevel = Integer.parseInt(args[4]);
                if (debugLevel != 1 && debugLevel != 0) throw new ParseArgumentException("Debug level must be either 0 or 1");
                //possibly change to be just 0, could do 1 if we want, but I think project just requires 0
            }
        }

        return new GeneBankSearchBTreeArguments(withOrWithoutCache == 1, btreeFileName, queryFileName, cacheSize, debugLevel);
    }

}

